package org.abel.plugins

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.abel.library.LibraryStorage


fun Application.configureRouting(library: LibraryStorage) {
    routing {
        staticResources("static", "static")

        get("/library") {
            call.respondText("Hello World!")
        }
        get("/library/get-members") {
            call.respond(library.getMembers())

        }
        get("/library/get-books") {
            call.respond(library.getBooks())

        }
        get("/library/get-member") {
            call.respond(
                library.findMemberById(
                    (call.request.queryParameters["memberId"]
                        ?: throw IllegalArgumentException("Parameter memberId not found")).toInt()
                )
            )
        }
        get("/library/get-book") {
            call.respond(
                library.findBookById(
                    (call.request.queryParameters["bookId"]
                        ?: throw IllegalArgumentException("Parameter bookId not found")).toInt()
                )
            )
        }
    }
}


