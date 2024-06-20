package org.abel

import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.abel.database.DatabaseFactory
import org.abel.database.SqlLibraryStorage
import org.abel.plugins.configureRouting
import org.abel.plugins.configureSerialization

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureRouting(SqlLibraryStorage(DatabaseFactory.getDevDatabase()))
}

