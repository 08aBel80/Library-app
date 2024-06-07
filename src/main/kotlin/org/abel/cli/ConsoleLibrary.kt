package org.abel.cli

import org.abel.errors.InvalidNumberOfArgumentsException
import org.abel.errors.UnknownCommandException
import org.abel.library.ILibrary
import org.abel.library.Library

class ConsoleLibrary(private val library: ILibrary = Library()) {
    private var programRunning: Boolean = true;

    fun start() {
        println("Welcome to Library!")
        println("type 'help' to see available commands")
        while (programRunning) {
            val input = readlnOrNull()
            if (input != null) println(executeCommand(input))
        }

    }

    fun executeCommand(command: String): String {
        try {
            val (commandType, args) = analyzeInput(command)
            return commandType.function.invoke(library, args)
        } catch (e: Exception) {
            return e.message!!
        }
    }

    @Throws(UnknownCommandException::class, InvalidNumberOfArgumentsException::class)
    private fun analyzeInput(input: String): Pair<CommandTypes, Array<String>> {
        val inputParts = tokenize(input)

        val commandType = findCommand(inputParts[0])

        val args = inputParts.drop(1).toTypedArray()
        if (args.size != commandType.numArgs) {
            throw InvalidNumberOfArgumentsException(commandType, args.size)
        }

        return Pair(commandType, args)
    }

    @Throws(UnknownCommandException::class)
    private fun findCommand(command: String): CommandTypes {
        val result = CommandTypes.entries.find { it.command == command }
        if (result == null) throw UnknownCommandException(command)
        return result
    }

    private fun tokenize(input: String): List<String> {
        val tokens = mutableListOf<String>()
        var currentToken = StringBuilder()
        var inQuotes = false
        //remove multiple spaces and spaces at the start and end
        val trimmedInput = input.trim().replace(Regex("\\s+"), " ")

        for (char in trimmedInput.toCharArray()) {
            when {
                char == '"' -> {
                    inQuotes = !inQuotes
                    if (!inQuotes) {
                        tokens.add(currentToken.toString())
                        currentToken = StringBuilder()
                    }
                }

                char.isWhitespace() && !inQuotes -> {
                    if (currentToken.isNotEmpty()) {
                        tokens.add(currentToken.toString())
                        currentToken = StringBuilder()
                    }
                }

                else -> {
                    currentToken.append(char)
                }
            }
        }

        if (currentToken.isNotEmpty()) {
            tokens.add(currentToken.toString())
        }

        return tokens
    }
}

enum class CommandTypes(
    val command: String,
    val description: String,
    val numArgs: Int,
    val function: (ILibrary, Array<String>) -> String
) {
    HELP(
        "help",
        "Show available commands or see description",
        0,
        CommandFunctions::help
    ),
    ADD_BOOK(
        "add_book",
        """Add a book to the library: add_book "<title>" "<author>"""",
        2,
        CommandFunctions::addBook
    ),
    ADD_MEMBER(
        "add_member",
        """Add a member to the library: add_member "<name>"""",
        1,
        CommandFunctions::addMember
    ),
    BORROW_BOOK(
        "borrow_book",
        "Borrow a book: borrow_book <member_id> <book_id>",
        2,
        CommandFunctions::borrowBook
    ),
    RETURN_BOOK(
        "return_book",
        "Return a book: return_book <member_id> <book_id>",
        2,
        CommandFunctions::returnBook
    ),
    FIND_MEMBER(
        "find_member",
        "Find member by id: find_member <member_id>",
        1,
        CommandFunctions::findMember
    ),
    FIND_BOOK(
        "find_book",
        "Find book by id: find_member <book_id>",
        1,
        CommandFunctions::findBook
    ),
    LIST_BOOKS(
        "list_books",
        "List all books",
        0,
        CommandFunctions::listBooks
    ),
    LIST_MEMBERS(
        "list_members",
        "List all members",
        0,
        CommandFunctions::listMembers
    );

    override fun toString(): String {
        return "$command - $description"
    }


}

private class CommandFunctions() {
    companion object {
        fun help(library: ILibrary, args: Array<String>): String {
            return CommandTypes.entries.joinToString(separator = "\n")
        }

        fun addBook(library: ILibrary, args: Array<String>): String {
            try {
                library.addBook(args[0], args[1])
                return "Book added Successfully!"
            } catch (e: Exception) {
                return e.message!!
            }
        }

        fun addMember(library: ILibrary, args: Array<String>): String {
            try {
                library.addMember(args[0])
                return "Member added Successfully!"
            } catch (e: Exception) {
                return e.message!!
            }
        }

        fun borrowBook(library: ILibrary, args: Array<String>): String {
            try {
                library.borrowBook(args[0].toInt(), args[1].toInt())
                return "Book borrowed Successfully!"
            } catch (e: Exception) {
                return e.message!!
            }
        }

        fun returnBook(library: ILibrary, args: Array<String>): String {
            try {
                library.returnBook(args[0].toInt(), args[1].toInt())
                return "Book returned Successfully!"
            } catch (e: Exception) {
                return e.message!!
            }
        }

        fun findMember(library: ILibrary, args: Array<String>): String {
            return try {
                library.findMemberById(args[0].toInt()).toString()
            } catch (e: Exception) {
                e.message!!
            }
        }

        fun findBook(library: ILibrary, args: Array<String>): String {
            return try {
                library.findBookById(args[0].toInt()).toString()
            } catch (e: Exception) {
                e.message!!
            }
        }

        fun listBooks(library: ILibrary, args: Array<String>): String {
            try {
                val books = library.getBooks()
                if (books.isEmpty()) return "No books in the library!"
                val sb = StringBuilder()
                sb.append("Books in the library:\n")
                sb.append(books.joinToString(separator = "\n"))
                return sb.toString()
            } catch (e: Exception) {
                return e.message!!
            }
        }

        fun listMembers(library: ILibrary, args: Array<String>): String {
            try {
                val members = library.getMembers()
                if (members.isEmpty()) return "No members in the library!"
                val sb = StringBuilder()
                sb.append("Members in the library:\n")
                sb.append(members.joinToString(separator = "\n"))
                return sb.toString()
            } catch (e: Exception) {
                return e.message!!
            }
        }
    }
}


