// A small command line so you can try the bank by hand:   kotlin MainKt --shell
//
// execute() takes one line and gives back one reply as a String, which also makes it easy to test.
// Put "double quotes" around names that have spaces in them.
class BankShell(private val bank: Bank) {

    // Only used for mistakes in what the user typed (missing argument, bad amount...).
    private class UsageError(message: String) : Exception(message)

    fun run() {
        println("Welcome to ${bank.name}. Type 'help' for commands, 'quit' to leave.")
        while (true) {
            print("bank> ")
            val line = readlnOrNull() ?: break
            if (line.trim().lowercase() in setOf("quit", "exit")) break
            val reply = execute(line)
            if (reply.isNotEmpty()) println(reply)
        }
        println("Goodbye.")
    }

    fun execute(line: String): String {
        val words = tokenize(line)
        if (words.isEmpty()) return ""
        val args = words.drop(1)
        return try {
            when (words.first().lowercase()) {
                "help" -> HELP
                "open" -> open(args)
                "list" -> bank.summary()
                "info" -> account(args, 0).info()
                "report" -> account(args, 0).balanceReport()
                "history" -> account(args, 0).statement()
                "deposit" -> show(bank.deposit(text(args, 0), money(args, 1)))
                "withdraw" -> show(bank.withdraw(text(args, 0), money(args, 1)))
                "transfer" -> show(bank.transfer(text(args, 0), text(args, 1), money(args, 2)))
                "activate" -> account(args, 0).let { it.activate(); "${it.number} is now active." }
                "deactivate" -> account(args, 0).let { it.deactivate(); "${it.number} is now inactive." }
                "month" -> bank.applyMonthlyChanges().toString()
                "project" -> project(args)
                "payoff" -> payoff(args)
                "networth" -> "Net worth of ${text(args, 0)}: ${bank.netWorthOf(text(args, 0))}"
                "events" -> bank.events.joinToString("\n") { "  ${it.message}" }.ifEmpty { "No events yet." }
                else -> "Unknown command '${words.first()}'. Type 'help'."
            }
        } catch (e: UsageError) {
            "ERROR: ${e.message}"
        }
    }

    // ---- the commands that are too long to fit in the big `when` above ----

    private fun open(args: List<String>): String {
        val owner = text(args, 1)
        val account = when (text(args, 0).lowercase()) {
            "payroll" -> bank.openPayrollAccount(owner)
            "debit" -> if (args.size > 2) bank.openDebitAccount(owner, money(args, 2)) else bank.openDebitAccount(owner)
            "credit" -> if (args.size > 2) bank.openCreditAccount(owner, money(args, 2)) else bank.openCreditAccount(owner)
            else -> fail("account type must be payroll, debit or credit")
        }
        return "Opened ${account.number}: ${account.type} account for ${account.owner}."
    }

    private fun project(args: List<String>): String {
        val account = account(args, 0)
        val bearing = account as? InterestBearing ?: fail("${account.type} accounts have no interest")
        val months = text(args, 1).toIntOrNull()?.takeIf { it in 1..600 } ?: fail("months must be a whole number from 1 to 600")
        val lines = bearing.projectBalances(months).mapIndexed { i, balance -> "  month ${i + 1}: $balance" }
        return "Projection for ${account.number} at ${bearing.rate}, no other activity:\n" + lines.joinToString("\n")
    }

    private fun payoff(args: List<String>): String {
        val account = account(args, 0) as? CreditAccount ?: fail("payoff only works on credit accounts")
        val payment = money(args, 1)
        val months = account.monthsToPayOff(payment)
        return if (months == null) "Paying $payment a month never clears ${account.balance}: it does not even cover the interest."
        else "Paying $payment a month clears ${account.balance} in $months month(s)."
    }

    // ---- small helpers for reading the arguments ----

    private fun show(outcome: Outcome) = (if (outcome.succeeded) "OK: " else "REJECTED: ") + outcome.message

    private fun text(args: List<String>, index: Int): String = args.getOrNull(index) ?: fail("missing argument #${index + 1}; type 'help'")

    private fun money(args: List<String>, index: Int): Money =
        Money.parseOrNull(text(args, index)) ?: fail("'${args[index]}' is not a valid amount (use at most 2 decimals)")

    private fun account(args: List<String>, index: Int): Account =
        bank[text(args, index)] ?: fail("no account with number ${args[index]}")

    private fun fail(message: String): Nothing = throw UsageError(message)

    // Splits on spaces, but keeps "quoted text" together as one word.
    private fun tokenize(line: String): List<String> =
        Regex("\"([^\"]*)\"|(\\S+)").findAll(line).map { it.groups[1]?.value ?: it.groups[2]!!.value }.toList()

    private companion object {
        val HELP = """
            Commands (amounts like 1500 or 1,500.50; names with spaces in "quotes"):
              open payroll <owner>                 open debit <owner> [opening balance]
              open credit <owner> [credit limit]   list
              info <acct>      report <acct>       history <acct>
              deposit <acct> <amount>              withdraw <acct> <amount>
              transfer <from> <to> <amount>
              activate <acct>  deactivate <acct>   month   (apply the monthly changes)
              project <acct> <months>              payoff <credit acct> <monthly payment>
              networth <owner> events              help    quit
        """.trimIndent()
    }
}
