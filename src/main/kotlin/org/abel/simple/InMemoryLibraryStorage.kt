package org.abel.simple

import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotFoundException
import org.abel.errors.BookNotInPossessionException
import org.abel.errors.MemberNotFoundException
import org.abel.library.Book
import org.abel.library.LibraryStorage
import org.abel.library.Member

class InMemoryLibraryStorage(
    private val members: MutableList<Member> = mutableListOf(),
    private val books: MutableList<Book> = mutableListOf()
) : LibraryStorage {
    override fun addMember(name: String) {
        members.add(Member(members.size + 1, name))
    }

    @Throws(MemberNotFoundException::class)
    override fun findMemberById(id: Int): Member {
        val member = members.find { member -> member.id == id }
        if (member == null) throw MemberNotFoundException(id)
        return member
    }

    override fun addBook(title: String, author: String) {
        books.add(Book(books.size + 1, title, author))
    }

    @Throws(BookNotFoundException::class, MemberNotFoundException::class)
    override fun borrowBook(memberId: Int, bookId: Int) {
        val member = findMemberById(memberId)
        val book = findBookById(bookId)

        if (!book.isAvailable) throw BookNotAvailableException(book)
        member.borrowedBooks.add(book)
        book.isAvailable = false
    }

    @Throws(BookNotFoundException::class, MemberNotFoundException::class)
    override fun returnBook(memberId: Int, bookId: Int) {
        val member = findMemberById(memberId)
        val book = findBookById(bookId)

        for (b in member.borrowedBooks) {
            if (b.id == book.id) {
                member.borrowedBooks.remove(b)
                book.isAvailable = true
                return
            }
        }
        throw BookNotInPossessionException(book)
    }

    @Throws(BookNotFoundException::class)
    override fun findBookById(id: Int): Book {
        val book = books.find { book -> book.id == id }
        if (book == null) throw BookNotFoundException(id)
        return book
    }

    override fun getBooks(): List<Book> {
        return books
    }

    override fun getMembers(): List<Member> {
        return members
    }
}