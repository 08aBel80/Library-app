package org.abel.errors

import org.abel.cli.CommandTypes
import org.abel.library.Book

class BookNotFoundException(id: Int) : Exception("Book with id $id not found")

class BookNotAvailableException(book: Book) : Exception("Book '${book.title}' is not available")

class BookNotInPossessionException(book: Book) :
    Exception("member doesn't have book '${book.title}' in their possession")

class MemberNotFoundException(id: Int) : Exception("Member with id $id not found")

class UnknownCommandException(command: String) : Exception("Unknown command: '$command'")

class InvalidNumberOfArgumentsException(command: CommandTypes, numArgs: Int) :
    Exception("Invalid number of arguments for command: '${command.command}'\nexpected ${command.numArgs}, but got $numArgs")

class CommandNotImplementedException(command: CommandTypes) : Exception("Command not implemented: '${command.command}'")
