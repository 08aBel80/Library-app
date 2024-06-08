package org.abel.database

import org.abel.database.Books.title
import org.abel.errors.BookNotAvailableException
import org.abel.errors.BookNotFoundException
import org.abel.errors.BookNotInPossessionException
import org.abel.errors.MemberNotFoundException
import org.abel.library.Book
import org.abel.library.LibraryStorage
import org.abel.library.Member
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class SqlLibraryStorage(private val db: DatabaseManager) : LibraryStorage {
    init {
        transaction(db.getDatabase()) {
            SchemaUtils.create(Books, Members)
        }
    }

    override fun addMember(name: String) {
        transaction {
            Members.insert {
                it[Members.name] = name
            }
        }
    }


    override fun findMemberById(id: Int): Member {
        return try {
            transaction {
                val member = Members.selectAll().where { Members.id eq id }.single()
                val books = Books.selectAll().where { Books.borrowerId eq id }.map {
                    Book(it[Books.id], it[Books.title], it[Books.author], false)
                }

                Member(
                    member[Members.id],
                    member[Members.name],
                    books.toMutableList()
                )
            }
        } catch (e: NoSuchElementException) {
            throw MemberNotFoundException(id)
        }
    }

    override fun addBook(title: String, author: String) {
        transaction {
            Books.insert {
                it[Books.title] = title
                it[Books.author] = author
            }
        }
    }

    override fun borrowBook(memberId: Int, bookId: Int) {
        transaction {
            try {
                Members.selectAll().where { Members.id eq memberId }.single()
            } catch (e: NoSuchElementException) {
                throw MemberNotFoundException(memberId)
            }

            val book = try {
                Books.selectAll().where { Books.id eq bookId }.single()
            } catch (e: NoSuchElementException) {
                throw BookNotFoundException(bookId)
            }

            if (book[Books.borrowerId] != null) {
                throw BookNotAvailableException(book[title])
            }

            Books.update({ Books.id eq bookId }) {
                it[borrowerId] = memberId
            }
        }
    }


    override fun returnBook(memberId: Int, bookId: Int) {
        transaction {
            try {
                Members.selectAll().where { Members.id eq memberId }.single()
            } catch (e: NoSuchElementException) {
                throw MemberNotFoundException(memberId)
            }

            val book = try {
                Books.selectAll().where { Books.id eq bookId }.single()
            } catch (e: NoSuchElementException) {
                throw BookNotFoundException(bookId)
            }

            if (book[Books.borrowerId] != memberId) {
                throw BookNotInPossessionException(book[title])
            }

            Books.update({ Books.id eq bookId }) {
                it[borrowerId] = null
            }
        }
    }

    override fun findBookById(id: Int): Book {
        return try {
            transaction {
                val book = Books.selectAll().where { Books.id eq id }.single()
                Book(book[Books.id], book[Books.title], book[Books.author], book[Books.borrowerId] == null)
            }
        } catch (e: NoSuchElementException) {
            throw BookNotFoundException(id)
        }
    }

    override fun getBooks(): List<Book> {
        return transaction {
            Books.selectAll().map {
                Book(it[Books.id], it[Books.title], it[Books.author], it[Books.borrowerId] == null)
            }
        }
    }

    override fun getMembers(): List<Member> {
        return transaction {
            Members.selectAll().map { m ->
                val books = Books.selectAll().where { Books.borrowerId eq m[Members.id] }.map { b ->
                    Book(b[Books.id], b[Books.title], b[Books.author], false)
                }

                Member(
                    m[Members.id],
                    m[Members.name],
                    books.toMutableList()
                )
            }
        }
    }
}