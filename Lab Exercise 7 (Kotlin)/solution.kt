package booksBorrowing

import java.time.LocalDate
import java.time.temporal.ChronoUnit

interface BorrowableItem{

    fun uniqueItemId(): Int
    fun commonName(): String

    fun loanPeriodDays(): Int = 0
}

class Page (
    private val sectionHeader: String,
    private val body: String
)

class Book (
    private val bookId: Int,
    private val title: String,
    private val author: String,
    private val publishDate: LocalDate,
    private val pages: List<Page>
): BorrowableItem {

    public fun coverInfo(): String {
        return "$title by $author"
    }

    override fun uniqueItemId(): Int {
        return bookId
    }

    override fun commonName(): String {
        return coverInfo()
    }

    override fun loanPeriodDays(): Int {
        return 7
    }
}

class Periodical (
    private val periodicalId: Int,
    private val title: String,
    private val issue: LocalDate,
    private val pages: List<Page>
): BorrowableItem {

    override fun uniqueItemId(): Int {
        return periodicalId
    }

    override fun commonName(): String {
        return "$title issue: ${issue.year}-${issue.monthValue}-${issue.dayOfMonth}"
    }

    override fun loanPeriodDays(): Int {
        return 1
    }
}

class PC (
    private val pcID: Int
): BorrowableItem {

    override fun uniqueItemId(): Int {
        return pcID
    }

    override fun commonName(): String {
        return "PC$pcID"
    }

    override fun loanPeriodDays(): Int {
        return 0
    }
}

class LibraryCard (
    private val idNumber: Int,
    private val name: String,
    private val borrowedItems: MutableMap<BorrowableItem, LocalDate>
) {

    private companion object {
        const val PENALTY_PER_DAY = 3.5f
    }

    public fun borrowItem(item: BorrowableItem, date: LocalDate) {
        borrowedItems[item] = date
    }

    public fun borrowerReport(): String {
        var r = "$name\n"
        for ((item, date) in borrowedItems) {
            r += "${item.commonName()}, borrow date: $date"
        }
        return r
    }

    public fun returnItem(b: BorrowableItem) {
        borrowedItems.remove(b)
    }

    private fun dueDate(item: BorrowableItem, borrowDate: LocalDate): LocalDate {
        return borrowDate.plusDays(item.loanPeriodDays().toLong())
    }

    public fun penalty(b: BorrowableItem, today: LocalDate): Float {
        val borrowDate = borrowedItems[b] ?: return 0f
        val daysOverdue = ChronoUnit.DAYS.between(dueDate(b, borrowDate), today)
        return if (daysOverdue > 0) daysOverdue * PENALTY_PER_DAY else 0f
    }

    public fun itemsDue(today: LocalDate): List<BorrowableItem> {
        val due = mutableListOf<BorrowableItem>()
        for ((item, borrowDate) in borrowedItems) {
            if (!today.isBefore(dueDate(item, borrowDate))) {
                due.add(item)
            }
        }
        return due
    }

    public fun totalPenalty(today: LocalDate): Float {
        var total = 0f
        for (item in borrowedItems.keys) {
            total += penalty(item, today)
        }
        return total
    }
}
