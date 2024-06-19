package org.abel

import org.abel.cli.ConsoleLibrary
import org.abel.database.DatabaseFactory
import org.abel.database.SqlLibraryStorage
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage

fun main() {
    val consoleLibrary = ConsoleLibrary(getMYSQLLibraryCLI())
    consoleLibrary.start()
}

fun getInMemoryLibraryCLI(): LibraryStorage {
    return InMemoryLibraryStorage()
}

fun getMYSQLLibraryCLI(): LibraryStorage {
    return SqlLibraryStorage(DatabaseFactory.getDevDatabase())
}
