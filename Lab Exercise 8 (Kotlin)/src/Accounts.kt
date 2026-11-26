import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale

/*
 * How this file is put together (to make it easier to read):
 *
 * What an account can DO is described by small interfaces: Withdrawable, Depositable,
 * TransferSource and InterestBearing. PayrollAccount just doesn't implement Depositable or
 * TransferSource, so the compiler itself stops you from depositing into it. No if-statements
 * needed for that rule.
 *
 * The rules every account shares (is the amount valid? is the account active? don't move any
 * money if the transfer would fail halfway) are written once, in Account. Each account type
 * then only has to answer four small questions: can this much money leave, what happens to my
 * balance when it does, can this much money arrive, and what happens to my balance when it does.
 *
 * Payroll and debit accounts hold money the owner HAS. A credit account holds money the owner
 * OWES, so its answers are the mirror image of the others.
 */

// ---- what an account can do ----

interface Withdrawable {
    fun withdraw(amount: Money): Outcome
}

interface Depositable {
    fun deposit(amount: Money): Outcome
}

interface TransferSource {
    fun transferTo(target: Account, amount: Money): Outcome
}

interface InterestBearing {
    val rate: InterestRate

    // The balance after each of the next `months` months, if nothing else happens.
    fun projectBalances(months: Int): List<Money>
}

// ---- Account (the base class) ----

abstract class Account(val number: String, val owner: String) {

    init {
        require(owner.isNotBlank()) { "Owner name cannot be blank" }
    }

    // "Payroll", "Debit" or "Credit"
    abstract val type: String

    // Money held (payroll, debit) or money owed (credit). Only the account itself can change it.
    var balance: Money = Money.ZERO
        protected set

    var status: Status = Status.Active
        private set

    val isActive: Boolean get() = status is Status.Active

    // How this account counts toward the owner's net worth: savings are positive, debt is negative.
    open val netPosition: Money get() = balance

    // An inactive account can't send money out, but it can still receive money. I did it this way
    // so a debit account that dropped under its required balance can be topped up and turned back
    // on at the next month-end. (If it couldn't receive anything it would stay inactive forever.)
    // Override this with false if you want an account that is completely frozen.
    protected open val acceptsIncomingWhenInactive: Boolean get() = true

    private val ledger = mutableListOf<Transaction>()
    val history: List<Transaction> get() = ledger.toList()

    private val listeners = mutableListOf<AccountListener>()

    fun addListener(listener: AccountListener) {
        listeners += listener
    }

    // ---- the four questions every account type answers ----
    // These are only called after the shared checks (valid amount, active account) already passed.

    // Can `amount` leave this account (withdrawal or outgoing transfer)?
    // Return null if yes, otherwise the reason why not.
    protected abstract fun checkRelease(amount: Money): Rejection?

    // The money really leaves, so update the balance.
    protected abstract fun release(amount: Money)

    // Can `amount` come into this account (deposit or incoming transfer)? Same idea as above.
    protected abstract fun checkReceive(amount: Money): Rejection?

    // The money really arrives, so update the balance.
    protected abstract fun receive(amount: Money)

    // The extra rows of the balance report that depend on the account type.
    protected abstract fun reportLines(): List<Pair<String, String>>

    // ---- month-end ----

    // By default nothing happens at month-end (that's what payroll does).
    // Debit and credit override these two.
    protected open fun applyInterest(): Money = Money.ZERO

    protected open fun reviewStatus() {}

    // The bank calls this once a month on every account: interest first,
    // then check whether the account should still be active.
    fun applyMonthlyChanges(): MonthlyResult {
        val before = status
        val interest = applyInterest()
        reviewStatus()
        return MonthlyResult(number, owner, type, interest, before, status, balance)
    }

    // Adds this month's interest to the balance. For a credit account that means
    // the amount owed goes up.
    protected fun postInterest(rate: InterestRate): Money {
        val interest = rate.interestOn(balance)
        if (interest.isPositive) {
            balance += interest
            record(TransactionKind.INTEREST, interest, "Monthly interest")
            publish(AccountEvent.InterestPosted(number, interest))
        }
        return interest
    }

    // ---- the operations ----
    // The checks are the same for every account type, so they live here and not in the subclasses.

    protected fun performWithdrawal(amount: Money): Outcome {
        val problem = invalidAmount(amount) ?: inactiveProblem("withdraw") ?: checkRelease(amount)
        if (problem != null) return Outcome.Failure(problem)
        release(amount)
        return Outcome.Success(record(TransactionKind.WITHDRAWAL, amount))
    }

    protected fun performDeposit(amount: Money): Outcome {
        val problem = receiveProblem(amount)
        if (problem != null) return Outcome.Failure(problem)
        receive(amount)
        return Outcome.Success(record(TransactionKind.DEPOSIT, amount))
    }

    // Everything is checked on BOTH accounts before any money moves,
    // so if a transfer fails, nothing has changed anywhere.
    protected fun performTransfer(target: Account, amount: Money): Outcome {
        val problem = if (target === this) {
            Rejection.SameAccount(number)
        } else {
            invalidAmount(amount) ?: inactiveProblem("transfer funds") ?: checkRelease(amount) ?: target.receiveProblem(amount)
        }
        if (problem != null) return Outcome.Failure(problem)

        release(amount)
        target.receive(amount)
        target.record(TransactionKind.TRANSFER_IN, amount, "from $number (${owner})")
        return Outcome.Success(record(TransactionKind.TRANSFER_OUT, amount, "to ${target.number} (${target.owner})"))
    }

    private fun invalidAmount(amount: Money): Rejection? =
        if (amount.isPositive) null else Rejection.InvalidAmount(amount)

    private fun inactiveProblem(action: String): Rejection? =
        (status as? Status.Inactive)?.let { Rejection.AccountInactive(number, action, it.reason) }

    private fun receiveProblem(amount: Money): Rejection? =
        invalidAmount(amount)
            ?: (if (acceptsIncomingWhenInactive) null else inactiveProblem("receive funds"))
            ?: checkReceive(amount)

    // ---- activate / deactivate ----

    // These two are for a person (bank staff) to use.
    fun activate() = changeStatus(Status.Active)

    fun deactivate(reason: String = "deactivated by the bank") = changeStatus(Status.Inactive(reason, bySystem = false))

    // Used by the month-end check. It never touches an account that a person deactivated.
    protected fun deactivateBySystem(reason: String) {
        if (isActive) changeStatus(Status.Inactive(reason, bySystem = true))
    }

    // Also for the month-end check. It only lifts a freeze that the system itself put there.
    protected fun reactivateBySystem() {
        val current = status
        if (current is Status.Inactive && current.bySystem) changeStatus(Status.Active)
    }

    private fun changeStatus(new: Status) {
        if (new == status) return
        val old = status
        status = new
        publish(AccountEvent.StatusChanged(number, old, new))
    }

    // ---- history and notifications ----

    // Call this AFTER the balance changed, because the history line stores the new balance.
    protected fun record(kind: TransactionKind, amount: Money, note: String = ""): Transaction {
        val transaction = Transaction(ledger.size + 1, kind, amount, balance, note)
        ledger += transaction
        return transaction
    }

    protected fun publish(event: AccountEvent) = listeners.forEach { it.onEvent(event) }

    // ---- reports ----

    // Owner name, account type and whether it's active.
    fun info(): String = renderTable(
        "ACCOUNT INFORMATION",
        listOf("Account number" to number, "Owner" to owner, "Account type" to type, "Status" to status.text())
    )

    fun balanceReport(): String = renderTable(
        "BALANCE REPORT",
        listOf("Account" to "$number ($type)", "Owner" to owner, "Status" to status.text()) + reportLines()
    )

    fun statement(): String = buildString {
        appendLine("=== STATEMENT: $number ($owner) ===")
        if (ledger.isEmpty()) appendLine("  (no transactions yet)")
        for (t in ledger) {
            appendLine(
                String.format(Locale.US, "  %2d  %-16s %16s  balance %16s  %s", t.sequence, t.kind.label, t.amount, t.balanceAfter, t.note).trimEnd()
            )
        }
    }.trimEnd()

    fun showInfo() = println(info())
    fun showBalanceReport() = println(balanceReport())
    fun showStatement() = println(statement())

    override fun toString() = "$number $type account of $owner"
}

private fun Status.text(): String = if (this is Status.Active) "Active" else toString()

private fun renderTable(title: String, rows: List<Pair<String, String>>): String {
    val labelWidth = rows.maxOf { it.first.length }
    val lines = rows.map { (label, value) -> "  ${label.padEnd(labelWidth)} : $value" }
    val width = maxOf(title.length + 8, lines.maxOf { it.length })
    return buildString {
        appendLine("=== $title ".padEnd(width, '='))
        lines.forEach { appendLine(it) }
        append("=".repeat(width))
    }
}

// ---- accounts that hold money the owner HAS (payroll and debit) ----

abstract class CashAccount(number: String, owner: String, openingBalance: Money = Money.ZERO) : Account(number, owner) {

    init {
        require(!openingBalance.isNegative) { "Opening balance cannot be negative" }
        if (openingBalance.isPositive) {
            balance = openingBalance
            record(TransactionKind.OPENING, openingBalance, "Account opened")
        }
    }

    override fun checkRelease(amount: Money): Rejection? =
        if (amount > balance) Rejection.InsufficientFunds(amount, balance) else null

    override fun release(amount: Money) {
        balance -= amount
    }

    override fun checkReceive(amount: Money): Rejection? = null

    override fun receive(amount: Money) {
        balance += amount
    }

    override fun reportLines(): List<Pair<String, String>> = listOf("Balance" to balance.toString())
}

// Can withdraw and can be paid by transfer. It does NOT implement Depositable or TransferSource,
// which is exactly what "payroll accounts can't deposit or transfer" means.
class PayrollAccount(number: String, owner: String) : CashAccount(number, owner), Withdrawable {
    override val type = "Payroll"

    override fun withdraw(amount: Money) = performWithdrawal(amount)
}

// Can do everything. Every month the balance grows by `rate` (compounding). It has to keep at
// least `minimumBalance` at month-end or the account gets deactivated. Once the balance is back
// up, the next month-end turns it on again (unless someone deactivated it by hand).
class DebitAccount(
    number: String,
    owner: String,
    openingBalance: Money = Money.ZERO,
    override val rate: InterestRate = DEFAULT_RATE,
    val minimumBalance: Money = DEFAULT_MINIMUM
) : CashAccount(number, owner, openingBalance), Withdrawable, Depositable, TransferSource, InterestBearing {

    init {
        require(!minimumBalance.isNegative) { "Required balance cannot be negative" }
    }

    override val type = "Debit"

    val meetsMinimum: Boolean get() = balance >= minimumBalance

    override fun withdraw(amount: Money) = performWithdrawal(amount)
    override fun deposit(amount: Money) = performDeposit(amount)
    override fun transferTo(target: Account, amount: Money) = performTransfer(target, amount)

    override fun projectBalances(months: Int) = rate.compound(balance, months)

    override fun applyInterest() = postInterest(rate)

    override fun reviewStatus() {
        if (meetsMinimum) reactivateBySystem()
        else deactivateBySystem("balance is below the required minimum of $minimumBalance")
    }

    override fun reportLines() = super.reportLines() + listOf(
        "Required balance" to "$minimumBalance (${if (meetsMinimum) "met" else "NOT met"})",
        "Interest rate" to rate.toString()
    )

    companion object {
        val DEFAULT_RATE = 3.percentPerYear
        val DEFAULT_MINIMUM = 500.php
    }
}

// ---- the account that holds money the owner OWES ----

// Here `balance` is the amount owed. Withdrawing or transferring out raises it, but never past
// the credit limit. Depositing or receiving a transfer lowers it, but never below zero.
//
// Monthly interest raises what is owed. We always charge the full interest, even if that pushes
// the balance over the limit (the bank is allowed to charge it). When that happens the account
// is frozen until it's paid back down.
class CreditAccount(
    number: String,
    owner: String,
    val creditLimit: Money = DEFAULT_LIMIT,
    override val rate: InterestRate = DEFAULT_RATE
) : Account(number, owner), Withdrawable, Depositable, TransferSource, InterestBearing {

    init {
        require(creditLimit.isPositive) { "Credit limit must be greater than zero" }
    }

    override val type = "Credit"

    override val netPosition: Money get() = -balance

    val availableCredit: Money get() = maxOf(creditLimit - balance, Money.ZERO)

    val isOverLimit: Boolean get() = balance > creditLimit

    fun utilizationPercent(): BigDecimal =
        balance.amount.multiply(BigDecimal(100)).divide(creditLimit.amount, 1, RoundingMode.HALF_UP)

    override fun withdraw(amount: Money) = performWithdrawal(amount)
    override fun deposit(amount: Money) = performDeposit(amount)
    override fun transferTo(target: Account, amount: Money) = performTransfer(target, amount)

    override fun projectBalances(months: Int) = rate.compound(balance, months)

    // How many months it takes to clear the debt if you pay `monthlyPayment` every month
    // (interest gets added first each month). Returns null if the payment is too small to ever get there.
    fun monthsToPayOff(monthlyPayment: Money, maxMonths: Int = 1200): Int? {
        require(monthlyPayment.isPositive) { "Monthly payment must be greater than zero" }
        var owed = balance
        var months = 0
        while (owed.isPositive) {
            if (months == maxMonths) return null
            owed += rate.interestOn(owed)
            owed = if (monthlyPayment >= owed) Money.ZERO else owed - monthlyPayment
            months++
        }
        return months
    }

    override fun checkRelease(amount: Money): Rejection? =
        if (amount > availableCredit) Rejection.CreditLimitExceeded(amount, availableCredit, creditLimit) else null

    override fun release(amount: Money) {
        balance += amount
    }

    override fun checkReceive(amount: Money): Rejection? =
        if (amount > balance) Rejection.Overpayment(amount, balance) else null

    override fun receive(amount: Money) {
        balance -= amount
    }

    override fun applyInterest() = postInterest(rate)

    override fun reviewStatus() {
        if (isOverLimit) deactivateBySystem("balance is over the credit limit of $creditLimit")
        else reactivateBySystem()
    }

    override fun reportLines() = listOf(
        "Credit balance (owed)" to balance.toString(),
        "Credit limit" to creditLimit.toString(),
        "Available credit" to availableCredit.toString(),
        "Utilization" to "${utilizationPercent()}%",
        "Interest rate" to rate.toString()
    )

    companion object {
        val DEFAULT_LIMIT = 20_000.php
        val DEFAULT_RATE = 24.percentPerYear
    }
}
