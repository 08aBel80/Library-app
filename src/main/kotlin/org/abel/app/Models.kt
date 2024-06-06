package org.abel.app

import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotInPossessionException


data class Book(
    val id: Int,
    val title: String,
    val author: String,
    var isAvailable: Boolean = true
)

data class Member(
    val id: Int,
    val name: String,
    val borrowedBooks: MutableList<Book> = mutableListOf()
) {
    fun borrowBook(book: Book) {
        if (!book.isAvailable) throw BookNotAvailableException(book)
        borrowedBooks.add(book)
        book.isAvailable = false
    }

    fun returnBook(book: Book) {
        for (b in borrowedBooks) {
            if (b.id == book.id) {
                borrowedBooks.remove(b)
                book.isAvailable = true
                return
            }
        }
        throw BookNotInPossessionException(book)
    }

    override fun toString(): String {
        return "Member(id=$id, name='$name', books=$borrowedBooks)"
    }

}

