package org.abel.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table

class DatabaseManager(
    private val name: String,
    private val jdbcURL: String = "jdbc:sqlite:%s.db".format(name),
    private val driverClass: String = "org.sqlite.JDBC",
    private val maxPoolSize: Int = 10
) {


    private lateinit var dataSource: HikariDataSource

    init {
        val config = HikariConfig().apply {
            jdbcUrl = jdbcURL
            driverClassName = driverClass
            maximumPoolSize = maxPoolSize
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_SERIALIZABLE"
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

