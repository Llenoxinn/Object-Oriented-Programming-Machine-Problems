package booksBorrowing

import java.time.LocalDate

fun debugMain() {
    val card = LibraryCard(1, "Rubelito Abella", mutableMapOf())
    val book = Book(
        15,
        "Necronomicon",
        "Hermes Trismegistus",
        LocalDate.of(1800, 12, 12),
        listOf()
    )
    card.borrowItem(book, LocalDate.of(2025, 10, 10))
    println(card.borrowerReport())
}

//this file is for runnning debug only. delete or rename the main function when evaluating.
//if you keep the main function here it will be in conflict with the tester
