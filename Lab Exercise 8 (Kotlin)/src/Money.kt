import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode
import java.util.Locale

// Money is just a BigDecimal under the hood, but giving it its own type means we can't
// accidentally mix it up with a normal number, and we get nice operators:
//
//     val left = 25_000.php - 3_000.php     // PHP 22,000.00
//     if (left > 500.php) { ... }
//
// It always has exactly 2 decimal places (centavos).
@JvmInline
value class Money private constructor(val amount: BigDecimal) : Comparable<Money> {

    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun minus(other: Money) = Money(amount - other.amount)
    operator fun unaryMinus() = Money(amount.negate())

    val isPositive: Boolean get() = amount.signum() > 0
    val isNegative: Boolean get() = amount.signum() < 0
    val isZero: Boolean get() = amount.signum() == 0

    override fun compareTo(other: Money): Int = amount.compareTo(other.amount)

    override fun toString(): String =
        (if (isNegative) "-" else "") + "PHP " + String.format(Locale.US, "%,.2f", amount.abs())

    companion object {
        val ZERO = Money(BigDecimal.ZERO.setScale(2))

        // Only accepts amounts that already fit in centavos. 10.005 is an error on purpose,
        // I didn't want it quietly turning into 10.01.
        fun exact(value: BigDecimal): Money {
            require(value.stripTrailingZeros().scale() <= 2) { "$value has more than 2 decimal places" }
            return Money(value.setScale(2))
        }

        // For numbers we calculate ourselves (like interest) we do have to round.
        // Half-up, so 0.005 goes to 0.01.
        fun rounded(value: BigDecimal): Money = Money(value.setScale(2, RoundingMode.HALF_UP))

        // Turns text like "1500", "1,500.50" or "20_000" into Money, or null if it isn't a valid amount.
        fun parseOrNull(text: String): Money? =
            try {
                exact(BigDecimal(text.trim().replace(",", "").replace("_", "")))
            } catch (e: IllegalArgumentException) { // NumberFormatException is a kind of IllegalArgumentException
                null
            }
    }
}

// Lets us write amounts like 5_000.php or 250.75.php
val Number.php: Money get() = Money.exact(BigDecimal(toString()))

// An interest rate written per YEAR (like 3% a year). Each month we apply 1/12 of it.
// It compounds, meaning next month's interest is worked out on the balance that already
// includes this month's interest.
data class InterestRate(val annualPercent: BigDecimal) {
    init {
        require(annualPercent.signum() >= 0) { "Interest rate cannot be negative" }
    }

    // The fraction added each month. For example 3% a year -> 0.0025
    val monthlyFraction: BigDecimal get() = annualPercent.divide(BigDecimal(1200), MathContext.DECIMAL64)

    fun interestOn(balance: Money): Money = Money.rounded(balance.amount.multiply(monthlyFraction))

    // The balance after each of the next `months` months if nothing else happens.
    // This is for the "what if" feature, it doesn't change anything.
    fun compound(start: Money, months: Int): List<Money> {
        require(months >= 0) { "Months cannot be negative" }
        var running = start
        return List(months) {
            running += interestOn(running)
            running
        }
    }

    override fun toString(): String =
        String.format(Locale.US, "%.2f%% per year (%.4f%% per month)", annualPercent, annualPercent.divide(BigDecimal(12), MathContext.DECIMAL64))
}

// Same idea as .php: 3.percentPerYear or 24.5.percentPerYear
val Number.percentPerYear: InterestRate get() = InterestRate(BigDecimal(toString()))
