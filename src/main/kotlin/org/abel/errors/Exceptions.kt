package org.abel.errors

import org.abel.cli.CommandTypes
import org.abel.library.Book

class BookNotFoundException(id: Int) : Exception("Book with id $id not found")

class BookNotAvailableException(title: String) : Exception("Book '${title}' is not available") {
    constructor(book: Book) : this(book.title)
}

class BookNotInPossessionException(title: String) :
    Exception("Member doesn't have book '$title' in their possession") {
    constructor(book: Book) : this(book.title)
}

class MemberNotFoundException(id: Int) : Exception("Member with id $id not found")

class UnknownCommandException(command: String) : Exception("Unknown command: '$command'")

class InvalidNumberOfArgumentsException(command: CommandTypes, numArgs: Int) :
    Exception("Invalid number of arguments for command: '${command.command}'\nexpected ${command.numArgs}, but got $numArgs")