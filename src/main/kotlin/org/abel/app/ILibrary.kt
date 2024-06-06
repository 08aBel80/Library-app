package org.abel.app

import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotFoundException
import org.abel.errors.BookNotInPossessionException
import org.abel.errors.MemberNotFoundException

interface ILibrary {
    fun addMember(name: String)

    @Throws(MemberNotFoundException::class)
    fun findMemberById(id: Int): Member

    fun addBook(name: String, author: String)

    @Throws(MemberNotFoundException::class, BookNotFoundException::class, BookNotAvailableException::class)
    fun borrowBook(memberId: Int, bookId: Int)

    @Throws(MemberNotFoundException::class, BookNotFoundException::class, BookNotInPossessionException::class)
    fun returnBook(memberId: Int, bookId: Int)

    @Throws(BookNotFoundException::class)
    fun findBookById(id: Int): Book

    fun getBooks(): List<Book>

    fun getMembers(): List<Member>
}