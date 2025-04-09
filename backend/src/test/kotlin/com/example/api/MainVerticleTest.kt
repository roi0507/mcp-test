package com.example.api

import io.vertx.core.Vertx
import io.vertx.ext.web.client.WebClient
import io.vertx.ext.web.client.WebClientOptions
import io.vertx.junit5.VertxExtension
import io.vertx.junit5.VertxTestContext
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(VertxExtension::class)
class MainVerticleTest {

    @BeforeEach
    fun setup(vertx: Vertx, testContext: VertxTestContext) {
        vertx.deployVerticle(MainVerticle(), testContext.succeedingThenComplete())
    }

    @Test
    fun `health endpoint should return UP status`(vertx: Vertx, testContext: VertxTestContext) {
        val client = WebClient.create(vertx, WebClientOptions().setDefaultPort(8080))
        
        client.get("/api/health")
            .send()
            .onComplete(testContext.succeeding { response ->
                testContext.verify {
                    assertEquals(200, response.statusCode())
                    assertEquals("""{"status":"UP"}""", response.bodyAsString())
                    testContext.completeNow()
                }
            })
    }
} 