package org.abel.cli

import org.abel.database.DatabaseManager
import org.abel.database.SqlLibraryStorage
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage

private const val DATABASE_NAME = "library"
fun main() {
    val consoleLibrary = ConsoleLibrary(getSqlLibraryCLI())
    consoleLibrary.start()
}


fun getInMemoryLibraryCLI(): LibraryStorage {
    return InMemoryLibraryStorage()
}

fun getSqlLibraryCLI(): LibraryStorage {
    return SqlLibraryStorage(DatabaseManager(DATABASE_NAME))
}