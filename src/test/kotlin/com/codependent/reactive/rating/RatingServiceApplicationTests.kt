package com.codependent.reactive.rating

import com.codependent.reactive.rating.controller.Rating
import com.codependent.reactive.rating.controller.RatingRequest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.messaging.rsocket.RSocketRequester
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import java.net.URI

@SpringBootTest
@TestInstance(PER_CLASS)
class RatingServiceApplicationTests(@Autowired private val builder: RSocketRequester.Builder) {

    private val requester: RSocketRequester = builder
            .connectWebSocket(URI.create("http://localhost:8080/rating-ws"))
            .block()!!

    @Test
    fun testRequestGetsResponse() {
        val ratingRequest = RatingRequest("songId")
        val result: Mono<Rating> = requester!!
                .route("request-rating")
                .data(ratingRequest)
                .retrieveMono(Rating::class.java)
        result.test()
                .expectNextMatches {
                    it.songId == "songId"
                }.verifyComplete()
    }

}
