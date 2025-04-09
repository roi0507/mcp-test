package com.example.api

import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import org.slf4j.LoggerFactory

class MainVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)
    
    override suspend fun start() {
        val router = Router.router(vertx)
        
        // Configure CORS
        val corsHandler = CorsHandler.create("*")
            .allowedMethods(setOf(
                HttpMethod.GET, 
                HttpMethod.POST, 
                HttpMethod.PUT, 
                HttpMethod.DELETE, 
                HttpMethod.OPTIONS
            ))
            .allowedHeaders(setOf(
                "Content-Type", 
                "Authorization", 
                "X-Requested-With", 
                "Accept"
            ))
            .allowCredentials(true)
        
        router.route().handler(corsHandler)
        router.route().handler(BodyHandler.create())
        
        // Health check endpoint
        router.get("/api/health").handler { ctx ->
            ctx.response()
                .putHeader("content-type", "application/json")
                .end("""{"status":"UP"}""")
        }
        
        // Sample data endpoint
        router.get("/api/data").handler { ctx ->
            ctx.response()
                .putHeader("content-type", "application/json")
                .end("""{"items":[{"id":1,"name":"Item 1"},{"id":2,"name":"Item 2"},{"id":3,"name":"Item 3"}]}""")
        }
        
        // Start HTTP server
        val port = 8080
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(port)
            .await()
        
        logger.info("HTTP server started on port $port")
    }
}

fun main() {
    val vertx = Vertx.vertx()
    vertx.deployVerticle(MainVerticle())
} 