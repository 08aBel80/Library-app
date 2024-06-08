package org.abel.cli

import org.abel.cli.LibraryCommandFunctions.LibraryCommandFunction
import org.abel.errors.InvalidNumberOfArgumentsException
import org.abel.errors.UnknownCommandException
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage

class ConsoleLibrary(private val library: LibraryStorage = InMemoryLibraryStorage()) {
    fun start() {
        println("Welcome to Library!")
        println("type 'help' to see available commands")
        while (true) {
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
    val function: LibraryCommandFunction
) {
    HELP(
        "help",
        "Show available commands or see description",
        0,
        LibraryCommandFunctions.help
    ),
    ADD_BOOK(
        "add_book",
        """Add a book to the library: add_book "<title>" "<author>"""",
        2,
        LibraryCommandFunctions.addBook
    ),
    ADD_MEMBER(
        "add_member",
        """Add a member to the library: add_member "<name>"""",
        1,
        LibraryCommandFunctions.addMember
    ),
    BORROW_BOOK(
        "borrow_book",
        "Borrow a book: borrow_book <member_id> <book_id>",
        2,
        LibraryCommandFunctions.borrowBook
    ),
    RETURN_BOOK(
        "return_book",
        "Return a book: return_book <member_id> <book_id>",
        2,
        LibraryCommandFunctions.returnBook
    ),
    FIND_MEMBER(
        "find_member",
        "Find member by id: find_member <member_id>",
        1,
        LibraryCommandFunctions.findMember
    ),
    FIND_BOOK(
        "find_book",
        "Find book by id: find_member <book_id>",
        1,
        LibraryCommandFunctions.findBook
    ),
    LIST_BOOKS(
        "list_books",
        "List all books",
        0,
        LibraryCommandFunctions.listBooks
    ),
    LIST_MEMBERS(
        "list_members",
        "List all members",
        0,
        LibraryCommandFunctions.listMembers
    );

    override fun toString(): String {
        return "$command - $description"
    }


}

class LibraryCommandFunctions() {
    fun interface LibraryCommandFunction {
        fun invoke(library: LibraryStorage, args: Array<String>): String
    }

    companion object {
        val help = LibraryCommandFunction { _, _ ->
            CommandTypes.entries.joinToString(separator = "\n")
        }

        val addBook = LibraryCommandFunction { library, args ->
            try {
                library.addBook(args[0], args[1])
                "Book added Successfully!"
            } catch (e: Exception) {
                e.message!!
            }
        }

        val addMember = LibraryCommandFunction { library, args ->
            try {
                library.addMember(args[0])
                "Member added Successfully!"
            } catch (e: Exception) {
                e.message!!
            }
        }

        val borrowBook = LibraryCommandFunction { library, args ->
            try {
                library.borrowBook(args[0].toInt(), args[1].toInt())
                "Book borrowed Successfully!"
            } catch (e: Exception) {
                e.message!!
            }
        }

        val returnBook = LibraryCommandFunction { library, args ->
            try {
                library.returnBook(args[0].toInt(), args[1].toInt())
                "Book returned Successfully!"
            } catch (e: Exception) {
                e.message!!
            }
        }

        val findMember = LibraryCommandFunction { library, args ->
            try {
                library.findMemberById(args[0].toInt()).toString()
            } catch (e: Exception) {
                e.message!!
            }
        }

        val findBook = LibraryCommandFunction { library, args ->
            try {
                library.findBookById(args[0].toInt()).toString()
            } catch (e: Exception) {
                e.message!!
            }
        }

        val listBooks = LibraryCommandFunction { library, _ ->
            try {
                val books = library.getBooks()
                when {
                    books.isEmpty() -> "No books in the library!"
                    else -> {
                        val sb = StringBuilder()
                        sb.append("Books in the library:\n")
                        sb.append(books.joinToString(separator = "\n"))
                        sb.toString()
                    }
                }
            } catch (e: Exception) {
                e.message!!
            }
        }

        val listMembers = LibraryCommandFunction { library, _ ->
            try {
                val members = library.getMembers()
                when {
                    members.isEmpty() -> "No members in the library!"
                    else -> {
                        val sb = StringBuilder()
                        sb.append("Members in the library:\n")
                        sb.append(members.joinToString(separator = "\n"))
                        sb.toString()
                    }
                }
            } catch (e: Exception) {
                e.message!!
            }
        }
    }
}


