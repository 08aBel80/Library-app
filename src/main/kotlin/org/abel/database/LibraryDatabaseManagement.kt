package org.abel.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.cdimascio.dotenv.Dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

object DatabaseFactory {
    private val logger = LoggerFactory.getLogger(DatabaseFactory::class.java)

    private val dotenv = Dotenv.load();
    private var testDatabase: Database? = null
    private var testCliDatabase: Database? = null
    private var devDatabase: Database? = null

    fun getDevDatabase(): Database {
        return devDatabase ?: synchronized(this) {
            devDatabase ?: initializeDatabase(
                jdbcUrl = dotenv["DB_URL"],
                driverClassName = dotenv["DB_DRIVER_CLASS"],
                username = dotenv["DB_USER"],
                password = dotenv["DB_PASSWORD"]
            ).also { devDatabase = it }
        }
    }

    fun getTestDatabase(): Database {
        return testDatabase ?: synchronized(this) {
            testDatabase ?: initializeDatabase(
                "jdbc:sqlite:build/testLibrary.db",
                "org.sqlite.JDBC",
                "",
                ""
            ).also { testDatabase = it }
        }
    }

    fun getTestCliDatabase(): Database {
        return testCliDatabase ?: synchronized(this) {
            testCliDatabase ?: initializeDatabase(
                "jdbc:sqlite:build/testCliLibrary.db",
                "org.sqlite.JDBC",
                "",
                ""
            ).also { testCliDatabase = it }
        }
    }

    fun clearTestCliDatabase() {
        try {
            transaction(getTestCliDatabase()) {
                SchemaUtils.drop(Books, Members)
            }
        } catch (e: Exception) {
            logger.error("Could not clear test cli database: ${e.cause}")
        }
    }

    fun clearTestDatabase() {
        try {
            transaction(getTestDatabase()) {
                Books.deleteAll()
                Members.deleteAll()
            }
        } catch (e: Exception) {
            logger.error("Could not clear test database: ${e.cause}")
        }
    }

    private fun initializeDatabase(
        jdbcUrl: String,
        driverClassName: String,
        username: String,
        password: String
    ): Database {
        val config = HikariConfig().apply {
            this.jdbcUrl = jdbcUrl
            this.driverClassName = driverClassName
            this.username = username
            this.password = password
            this.maximumPoolSize = 10
            this.isAutoCommit = false
            this.transactionIsolation = "TRANSACTION_SERIALIZABLE"
        }
        val dataSource = HikariDataSource(config)
        return Database.connect(dataSource)
    }
}

object Books : Table() {
    val id = integer("id").autoIncrement()
    val title = varchar("title", 50)
    val author = varchar("author", 50)
    val borrowerId = reference("borrower_id", Members.id).nullable()
    override val primaryKey = PrimaryKey(id)
}

object Members : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 50)
    override val primaryKey = PrimaryKey(id)
}