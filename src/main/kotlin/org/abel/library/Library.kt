package org.abel.library

import org.abel.errors.BookNotFoundException
import org.abel.errors.MemberNotFoundException

class Library(
    private val members: MutableList<Member> = mutableListOf(),
    private val books: MutableList<Book> = mutableListOf()
) : ILibrary {
    override fun addMember(name: String) {
        members.add(Member(members.size + 1, name))
    }

    @Throws(MemberNotFoundException::class)
    override fun findMemberById(id: Int): Member {
        val member = members.find { member -> member.id == id }
        if (member == null) throw MemberNotFoundException(id)
        return member
    }

    override fun addBook(name: String, author: String) {
        books.add(Book(books.size + 1, name, author))
    }

    @Throws(BookNotFoundException::class, MemberNotFoundException::class)
    override fun borrowBook(memberId: Int, bookId: Int) {
        val member = findMemberById(memberId)
        val book = findBookById(bookId)

        member.borrowBook(book)
    }

    @Throws(BookNotFoundException::class, MemberNotFoundException::class)
    override fun returnBook(memberId: Int, bookId: Int) {
        val member = findMemberById(memberId)
        val book = findBookById(bookId)

        member.returnBook(book)
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