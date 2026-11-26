import java.util.Locale

// The bank opens accounts, finds them by account number, and does the things that involve more
// than one account (transfers) or all of them (the monthly run).
//
// Everything here returns an Outcome, so an unknown account number or an operation the account
// type doesn't support is just another reason for a Rejection, not a crash.
class Bank(val name: String) {

    private val accounts = linkedMapOf<String, Account>()
    private var nextSerial = 1
    private var monthsElapsed = 0
    private val eventLog = mutableListOf<AccountEvent>()
    private val subscribers = mutableListOf<AccountListener>()

    val allAccounts: List<Account> get() = accounts.values.toList()

    // Every notification from every account of this bank (status changes, interest), oldest first.
    val events: List<AccountEvent> get() = eventLog.toList()

    // Get told about every event from every account. Could be used to send an SMS, for example.
    fun addListener(listener: AccountListener) {
        subscribers += listener
    }

    // ---- opening accounts ----

    fun openPayrollAccount(owner: String): PayrollAccount =
        register("PAY") { PayrollAccount(it, owner) }

    fun openDebitAccount(
        owner: String,
        openingBalance: Money = Money.ZERO,
        rate: InterestRate = DebitAccount.DEFAULT_RATE,
        minimumBalance: Money = DebitAccount.DEFAULT_MINIMUM
    ): DebitAccount = register("DEB") { DebitAccount(it, owner, openingBalance, rate, minimumBalance) }

    fun openCreditAccount(
        owner: String,
        creditLimit: Money = CreditAccount.DEFAULT_LIMIT,
        rate: InterestRate = CreditAccount.DEFAULT_RATE
    ): CreditAccount = register("CRD") { CreditAccount(it, owner, creditLimit, rate) }

    // All three open* functions above use this: make the account number, hook up the
    // notifications and save the account.
    private fun <A : Account> register(prefix: String, create: (number: String) -> A): A {
        val serial = nextSerial++
        val account = create("$prefix-${serial.toString().padStart(4, '0')}")
        account.addListener { event ->
            eventLog += event
            subscribers.forEach { it.onEvent(event) }
        }
        accounts[account.number] = account
        return account
    }

    // ---- looking things up ----

    // bank["DEB-0002"]. It doesn't matter if you type it in lowercase.
    operator fun get(number: String): Account? = accounts[number.trim().uppercase()]

    fun accountsOf(owner: String): List<Account> = accounts.values.filter { it.owner.equals(owner.trim(), ignoreCase = true) }

    // Everything the owner has saved minus everything they owe, across all of their accounts.
    fun netWorthOf(owner: String): Money = accountsOf(owner).fold(Money.ZERO) { total, account -> total + account.netPosition }

    // ---- operations by account number ----

    // This is where the interfaces get used: `when (account) { is Depositable -> ... }`.
    // If the account type doesn't have that ability we give back a NotSupported rejection
    // instead of throwing an exception.

    fun deposit(number: String, amount: Money): Outcome = withAccount(number) { account ->
        when (account) {
            is Depositable -> account.deposit(amount)
            else -> notSupported(account, "deposit")
        }
    }

    fun withdraw(number: String, amount: Money): Outcome = withAccount(number) { account ->
        when (account) {
            is Withdrawable -> account.withdraw(amount)
            else -> notSupported(account, "withdraw")
        }
    }

    fun transfer(fromNumber: String, toNumber: String, amount: Money): Outcome {
        val source = this[fromNumber] ?: return Outcome.Failure(Rejection.UnknownAccount(fromNumber))
        val target = this[toNumber] ?: return Outcome.Failure(Rejection.UnknownAccount(toNumber))
        return when (source) {
            is TransferSource -> source.transferTo(target, amount)
            else -> notSupported(source, "transfer funds")
        }
    }

    private inline fun withAccount(number: String, action: (Account) -> Outcome): Outcome {
        val account = this[number] ?: return Outcome.Failure(Rejection.UnknownAccount(number))
        return action(account)
    }

    private fun notSupported(account: Account, action: String): Outcome =
        Outcome.Failure(Rejection.NotSupported(account.type, action))

    // ---- month-end ----

    // Run this once a month: interest first, then every account's status gets checked.
    fun applyMonthlyChanges(): MonthlyReport {
        monthsElapsed++
        return MonthlyReport(monthsElapsed, accounts.values.map { it.applyMonthlyChanges() })
    }

    // ---- printing ----

    fun summary(): String = buildString {
        appendLine("=== $name: ${accounts.size} account(s) ===")
        appendLine(String.format(Locale.US, "  %-9s %-8s %-14s %16s  %s", "Account", "Type", "Owner", "Net position", "Status"))
        for (a in accounts.values) {
            val status = if (a.status is Status.Active) "Active" else a.status.toString()
            appendLine(String.format(Locale.US, "  %-9s %-8s %-14s %16s  %s", a.number, a.type, a.owner, a.netPosition, status))
        }
    }.trimEnd()
}
