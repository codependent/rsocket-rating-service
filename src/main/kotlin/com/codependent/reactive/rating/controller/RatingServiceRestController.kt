package com.codependent.reactive.rating.controller

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.retry.Repeat
import reactor.util.context.Context
import java.time.Duration

@RestController
class RatingServiceRestController {

    private val FAIL_RATE = 40
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/requestRating")
    fun getRatingHttp(ratingRequest: RatingRequest): Mono<Rating> {
        return generateRating(ratingRequest)
    }

    @MessageMapping("request-rating")
    fun getRatingWebSocket(ratingRequest: RatingRequest): Mono<Rating> {
        return generateRating(ratingRequest)
                .subscriberContext(Context.empty())
    }

    private fun generateRating(ratingRequest: RatingRequest): Mono<Rating> {
        return doGenerateRating(ratingRequest)
                .repeatWhenEmpty(10000) {
                    it.delayElements(Duration.ofMillis(100))
                            .doOnNext { logger.info("repeating {}", it) }
                }
                //XXX This operator oesn't work: .repeatWhenEmpty(Repeat.times<Context>(0L).withApplicationContext(Context.empty()))
    }

    private fun doGenerateRating(ratingRequest: RatingRequest): Mono<Rating> {
        return Mono.defer {
            val random = (0..100).random()
            if (random <= FAIL_RATE) {
                Mono.empty()
            } else {
                Mono.just(Rating(ratingRequest.songId, (0..10).random()))
            }
        }
    }
}
