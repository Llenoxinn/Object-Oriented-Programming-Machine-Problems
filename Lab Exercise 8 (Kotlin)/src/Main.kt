// Demo of the whole system.
//   kotlin MainKt            runs the scripted tour below
//   kotlin MainKt --shell    lets you type commands yourself
fun main(args: Array<String>) {
    if ("--shell" in args) {
        BankShell(Bank("Lab Seven Bank")).run()
    } else {
        runDemo()
    }
}

private fun heading(text: String) = println("\n--- $text ".padEnd(78, '-'))

// Prints what we tried and whether the bank accepted it.
private fun step(label: String, outcome: Outcome) =
    println("  ${if (outcome.succeeded) "[ ok ]" else "[FAIL]"} $label\n           ${outcome.message}")

private fun runDemo() {
    val bank = Bank("Lab Seven Bank")
    bank.addListener { println("  (notification) ${it.message}") }

    val acme = bank.openDebitAccount("Acme Corp", openingBalance = 100_000.php)
    val ben = bank.openPayrollAccount("Ben Cruz")
    val carla = bank.openDebitAccount("Carla Reyes", openingBalance = 1_000.php, minimumBalance = 500.php)
    val danaSavings = bank.openDebitAccount("Dana Lim", openingBalance = 2_000.php)
    val danaCard = bank.openCreditAccount("Dana Lim", creditLimit = 10_000.php, rate = 24.percentPerYear)

    heading("1. Payroll: can be paid and can withdraw, nothing else")
    step("Acme pays Ben's salary of 25,000", bank.transfer(acme.number, ben.number, 25_000.php))
    step("Ben withdraws 3,000", ben.withdraw(3_000.php))
    // ben.deposit(500.php)  <- this doesn't even compile, PayrollAccount isn't Depositable
    step("Ben tries to deposit 500", bank.deposit(ben.number, 500.php))
    step("Ben tries to transfer 100 to Carla", bank.transfer(ben.number, carla.number, 100.php))

    heading("2. Debit: cannot overdraw, cannot pay a debt that does not exist")
    step("Carla deposits 2,500", carla.deposit(2_500.php))
    step("Carla withdraws 10,000", carla.withdraw(10_000.php))
    step("Carla withdraws 0", carla.withdraw(0.php))
    step("Carla pays 1,000 into Dana's card (balance owed is 0)", carla.transferTo(danaCard, 1_000.php))

    heading("3. Credit: the balance is what you owe, and it has a ceiling")
    step("Dana withdraws 4,000 on her card", danaCard.withdraw(4_000.php))
    step("Dana withdraws 7,000 (only 6,000 left)", danaCard.withdraw(7_000.php))
    step("Dana deposits 1,000 (pays the card down)", danaCard.deposit(1_000.php))
    step("Dana's card sends 2,000 to Carla (increases what she owes)", danaCard.transferTo(carla, 2_000.php))
    step("Dana's card sends 6,000 to Carla", danaCard.transferTo(carla, 6_000.php))
    danaCard.showBalanceReport()

    heading("4. Month-end: interest, deactivation and re-activation")
    step("Carla withdraws 5,200, leaving less than her required balance", carla.withdraw(5_200.php))
    ben.deactivate("payroll on hold")
    println(bank.applyMonthlyChanges())
    step("Carla (now inactive) tries to withdraw 100", carla.withdraw(100.php))
    step("...but she can still deposit 1,000", carla.deposit(1_000.php))
    ben.withdraw(100.php).let { println("  Ben, deactivated by hand: ${it.message}") }
    println(bank.applyMonthlyChanges())
    println("  Ben stays inactive because a person froze him; Carla was un-frozen by the system.")

    heading("5. Account information and reports")
    carla.showInfo()
    carla.showBalanceReport()
    danaCard.showStatement()
    println(bank.summary())

    heading("6. Extra features")
    println("  What-if: Acme's balance over the next 3 months: ${acme.projectBalances(3).joinToString()}")
    val months = danaCard.monthsToPayOff(500.php)
    println("  Paying 500 a month clears Dana's ${danaCard.balance} card in $months months.")
    println("  Net worth of Dana (savings minus card debt): ${bank.netWorthOf("Dana Lim")}")
    println("  Notifications recorded by the bank: ${bank.events.size}")
    println("\nRun with --shell to try it yourself.")
}
