import java.time.Instant
import java.util.Locale

// This file has all the "answer" types: what an operation gives back, why something was
// refused, the account status, one line of history, notifications and the month-end report.

// ---- results of operations ----

// The reason something was refused. Every reason carries the numbers involved (how much was
// asked for, how much is available...) so the caller can use them, or just print `message`.
// It's a sealed interface, so a `when` has to cover every case and the compiler tells us
// if we ever forget one.
sealed interface Rejection {
    val message: String

    data class InvalidAmount(val amount: Money) : Rejection {
        override val message get() = "Amount must be greater than zero (got $amount)."
    }

    data class AccountInactive(val account: String, val action: String, val reason: String) : Rejection {
        override val message get() = "Account $account is inactive ($reason), so it cannot $action."
    }

    data class NotSupported(val accountType: String, val action: String) : Rejection {
        override val message get() = "$accountType accounts cannot $action."
    }

    data class InsufficientFunds(val requested: Money, val available: Money) : Rejection {
        override val message get() = "Insufficient funds: $requested requested but only $available available."
    }

    data class CreditLimitExceeded(val requested: Money, val availableCredit: Money, val limit: Money) : Rejection {
        override val message get() =
            "Credit limit exceeded: $requested requested but only $availableCredit of the $limit limit is left."
    }

    data class Overpayment(val payment: Money, val owed: Money) : Rejection {
        override val message get() = "Payment of $payment is more than the $owed currently owed."
    }

    data class SameAccount(val account: String) : Rejection {
        override val message get() = "Cannot transfer from account $account to itself."
    }

    data class UnknownAccount(val number: String) : Rejection {
        override val message get() = "No account with number $number."
    }
}

// Every deposit, withdrawal and transfer gives back one of these. The account itself never
// prints or throws anything, the caller decides what to do with a Success or a Failure.
sealed interface Outcome {
    data class Success(val transaction: Transaction) : Outcome
    data class Failure(val rejection: Rejection) : Outcome

    val succeeded: Boolean get() = this is Success

    val message: String
        get() = when (this) {
            is Success -> "${transaction.kind.label} of ${transaction.amount} done; balance is now ${transaction.balanceAfter}."
            is Failure -> rejection.message
        }
}

inline fun Outcome.onSuccess(action: (Transaction) -> Unit): Outcome {
    if (this is Outcome.Success) action(transaction)
    return this
}

inline fun Outcome.onFailure(action: (Rejection) -> Unit): Outcome {
    if (this is Outcome.Failure) action(rejection)
    return this
}

// ---- status ----

// An account is either Active or Inactive. When it's inactive we also remember why,
// and whether the system did it or a person did.
sealed interface Status {
    data object Active : Status

    // bySystem = true means the month-end check froze it, which also means the
    // month-end check is allowed to unfreeze it later.
    data class Inactive(val reason: String, val bySystem: Boolean = false) : Status {
        override fun toString() = "Inactive ($reason)"
    }
}

// ---- history ----

enum class TransactionKind(val label: String) {
    OPENING("Opening balance"),
    DEPOSIT("Deposit"),
    WITHDRAWAL("Withdrawal"),
    TRANSFER_IN("Transfer in"),
    TRANSFER_OUT("Transfer out"),
    INTEREST("Interest")
}

// One line in an account's history.
data class Transaction(
    val sequence: Int,
    val kind: TransactionKind,
    val amount: Money,
    val balanceAfter: Money,
    val note: String = "",
    val timestamp: Instant = Instant.now()
)

// ---- notifications ----

sealed interface AccountEvent {
    val accountNumber: String
    val message: String

    data class StatusChanged(override val accountNumber: String, val from: Status, val to: Status) : AccountEvent {
        override val message get() = "Account $accountNumber: $from -> $to"
    }

    data class InterestPosted(override val accountNumber: String, val amount: Money) : AccountEvent {
        override val message get() = "Account $accountNumber: interest of $amount posted"
    }
}

fun interface AccountListener {
    fun onEvent(event: AccountEvent)
}

// ---- month-end ----

// What happened to one account during the monthly run.
data class MonthlyResult(
    val accountNumber: String,
    val owner: String,
    val type: String,
    val interest: Money,
    val statusBefore: Status,
    val statusAfter: Status,
    val balanceAfter: Money
) {
    val statusChanged: Boolean get() = statusBefore != statusAfter
}

// The result for the whole bank. Printing it gives a table.
data class MonthlyReport(val month: Int, val results: List<MonthlyResult>) {
    val statusChanges: List<MonthlyResult> get() = results.filter { it.statusChanged }

    override fun toString(): String = buildString {
        appendLine("=== MONTH $month CLOSED ===")
        appendLine(String.format(Locale.US, "  %-9s %-8s %-14s %14s %16s  %s", "Account", "Type", "Owner", "Interest", "Balance", "Status change"))
        for (r in results) {
            val change = if (r.statusChanged) "${r.statusBefore.label()} -> ${r.statusAfter.label()}" else "-"
            appendLine(String.format(Locale.US, "  %-9s %-8s %-14s %14s %16s  %s", r.accountNumber, r.type, r.owner, r.interest, r.balanceAfter, change))
        }
        append("  ${statusChanges.size} status change(s)")
    }

    private fun Status.label() = if (this is Status.Active) "Active" else "Inactive"
}
