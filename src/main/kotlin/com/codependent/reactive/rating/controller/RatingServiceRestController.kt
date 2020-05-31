package com.codependent.reactive.rating.controller

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.retry.Backoff
import reactor.retry.Repeat
import java.time.Duration


@RestController
class RatingServiceRestController {

    private val FAIL_RATE = 0
    private val logger = LoggerFactory.getLogger(javaClass)

    @GetMapping("/requestRating")
    fun getRatingHttp(ratingRequest: RatingRequest): Mono<Rating> {
        return generateRating(ratingRequest)
    }

    @MessageMapping("request-rating")
    fun getRatingWebSocket(ratingRequest: RatingRequest): Mono<Rating> {
        return generateRating(ratingRequest)
    }

    private fun generateRating(ratingRequest: RatingRequest): Mono<Rating> {
        return doGenerateRating(ratingRequest)
                .doOnNext {
                    logger.info("Next1 {}", it)
                }
                .doOnCancel {
                    logger.info("Cancel1")
                }
                .doOnSuccess {
                    logger.info("Success1 {}", it)
                }
                .doOnError { throwable ->
                    logger.error("Error1 {}", throwable)
                }
                .doOnTerminate {
                    logger.info("Terminate1")
                }
                .repeatWhenEmpty(Repeat.onlyIf<Any> { true }.backoff(Backoff.fixed(Duration.ofSeconds(1))))
                .doOnNext {
                    logger.info("Next2 {}", it)
                }
                .doOnCancel {
                    logger.info("Cancel2")
                }
                .doOnSuccess {
                    logger.info("Success2 {}", it)
                }
                .doOnError { throwable ->
                    logger.error("Error2 {}", throwable)
                }
                .doOnTerminate {
                    logger.info("Terminate2")
                }
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
