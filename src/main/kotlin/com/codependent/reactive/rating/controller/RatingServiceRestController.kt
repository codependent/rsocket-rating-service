package com.codependent.reactive.rating.controller

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class RatingServiceRestController {

    private val logger = LoggerFactory.getLogger(javaClass)

    @MessageMapping("request-rating")
    fun getRatingWebSocket(ratingRequest: RatingRequest): Mono<Rating> {
        return Mono.just(Rating(ratingRequest.songId, (0..10).random())).log()
                .doOnNext {
                    logger.info("Next {}", it)
                }
                .doOnCancel {
                    logger.info("Cancel")
                }
                .doOnSuccess {
                    logger.info("Success {}", it)
                }
                .doOnError { throwable ->
                    logger.error("Error {}", throwable)
                }
                .doOnTerminate { logger.info("Terminate") }
    }
}
