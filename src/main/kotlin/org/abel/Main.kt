package org.abel

import io.github.cdimascio.dotenv.Dotenv
import org.abel.cli.ConsoleLibrary
import org.abel.database.DatabaseManager
import org.abel.database.SqlLibraryStorage
import org.abel.library.LibraryStorage
import org.abel.simple.InMemoryLibraryStorage

private const val DATABASE_NAME = "library"
fun main() {
    val consoleLibrary = ConsoleLibrary(getMYSQLLibraryCLI())
    consoleLibrary.start()
}

fun getInMemoryLibraryCLI(): LibraryStorage {
    return InMemoryLibraryStorage()
}

fun getSqlLibraryCLI(): LibraryStorage {
    return SqlLibraryStorage(DatabaseManager(DATABASE_NAME))
}

fun getMYSQLLibraryCLI(): LibraryStorage {
    val config = ConfigLoader.loadConfig()
    print(config)
    return SqlLibraryStorage(DatabaseManager(config.dbUrl, config.dbUser, config.dbPassword, config.driverClass))
}

data class Config(
    val dbUrl: String,
    val dbUser: String,
    val dbPassword: String,
    val driverClass: String,
)

object ConfigLoader {
    private val dotenv = Dotenv.load()

    fun loadConfig(): Config {
        return Config(
            dbUrl = dotenv["DB_URL"],
            dbUser = dotenv["DB_USER"],
            dbPassword = dotenv["DB_PASSWORD"],
            driverClass = dotenv["DB_DRIVER_CLASS"]
        )
    }
}