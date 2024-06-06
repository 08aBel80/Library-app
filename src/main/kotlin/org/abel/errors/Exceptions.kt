package org.abel.errors

import org.abel.app.Book

class BookNotFoundException(id: Int) : Exception("Book with id $id not found")

class BookNotAvailableException(book: Book) : Exception("Book '${book.title}' is not available")

class BookNotInPossessionException(book: Book) :
    Exception("member doesn't have book '${book.title}' in their possession")

class MemberNotFoundException(id: Int) : Exception("Member with id $id not found")