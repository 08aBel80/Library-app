package org.abel.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.abel.library.LibraryStorage


fun Application.configureRouting(library: LibraryStorage) {
    routing {
        staticResources("static", "static")

        get("/library/get-members") {
            call.respond(library.getMembers())
        }

        get("/library/get-books") {
            call.respond(library.getBooks())
        }

        get("/library/get-member") {
            try {
                val memberId = (call.request.queryParameters["memberId"] ?: throw IllegalArgumentException()).toInt()
                if (memberId < 0) throw NumberFormatException()
                call.respond(library.findMemberById(memberId))
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Parameter 'memberId' not found")
            } catch (_: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, "Parameter 'memberId' must be a positive Integer")
            }
        }

        get("/library/get-book") {
            try {
                val bookId = (call.request.queryParameters["bookId"] ?: throw IllegalArgumentException()).toInt()
                if (bookId < 0) throw NumberFormatException()
                call.respond(library.findBookById(bookId))
            } catch (_: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, "Parameter 'bookId' not found")
            } catch (_: NumberFormatException) {
                call.respond(HttpStatusCode.BadRequest, "Parameter 'bookId' must be a positive Integer")
            }
        }
    }
}


