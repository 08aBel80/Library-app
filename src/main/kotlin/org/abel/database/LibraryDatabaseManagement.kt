package org.abel.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

class DatabaseManager(
    private val jdbcURL: String,
    private val dbUsername: String,
    private val dbPassword: String,
    private val driverClass: String,
    private val maxPoolSize: Int = 10
) {
    /**
     * Constructor for a SQLite database
     */
    constructor(
        name: String
    ) : this("jdbc:sqlite:%s.db".format(name), "", "", "org.sqlite.JDBC")

    private lateinit var dataSource: HikariDataSource

    init {
        val config = HikariConfig().apply {
            jdbcUrl = jdbcURL
            username = dbUsername
            password = dbPassword
            driverClassName = driverClass
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
//            transactionIsolation = "TRANSACTION_SERIALIZABLE"
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
        }

        dataSource = HikariDataSource(config)
    }

    fun getDatabase(): Database {
        return Database.connect(dataSource)
    }

    fun closeConnection() {
        dataSource.close()
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

