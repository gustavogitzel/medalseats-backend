package com.medalseats.adapter.http.query.match

import com.medalseats.adapter.http.query.match.response.toMatchResponse
import com.medalseats.application.query.match.FindMatchByIdQuery
import com.medalseats.application.query.match.FindMatchByIdQueryHandler
import com.unicamp.medalseats.match.exception.MatchException
import com.unicamp.medalseats.match.toMatchId
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import java.util.UUID

class MatchHttpHandler(
    private val findMatchByIdQueryHandler: FindMatchByIdQueryHandler
) {
    suspend fun findMatchById(req: ServerRequest): ServerResponse {
        val response = with(UUID.fromString(req.pathVariable("matchId"))) {
            val query = FindMatchByIdQuery(
                matchId = this
            )

            findMatchByIdQueryHandler.handle(query).toMatchResponse()
                ?: throw MatchException.MatchNotFoundException(this.toMatchId())
        }

        return ServerResponse.ok().bodyValueAndAwait(response)
    }
}
