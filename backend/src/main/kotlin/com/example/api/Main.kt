package com.example.api

import com.example.api.model.ItemDetail
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import org.slf4j.LoggerFactory

class MainVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)
    private val mapper = jacksonObjectMapper()
    
    override suspend fun start() {
        // Register Jackson module for Kotlin with Vert.x
        DatabindCodec.mapper().registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
        DatabindCodec.prettyMapper().registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
        
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
        
        // Item detail endpoint
        router.get("/api/items/:id").handler { ctx ->
            val id = ctx.pathParam("id").toIntOrNull()
            
            if (id == null) {
                ctx.response()
                    .setStatusCode(400)
                    .putHeader("content-type", "application/json")
                    .end("""{"error":"Invalid item ID"}""")
                return@handler
            }
            
            // Mock data - in a real app, this would come from a database
            val itemDetail = when (id) {
                1 -> ItemDetail(1, "Item 1", "Warehouse A", 150)
                2 -> ItemDetail(2, "Item 2", "Warehouse B", 75)
                3 -> ItemDetail(3, "Item 3", "Warehouse C", 320)
                else -> null
            }
            
            if (itemDetail == null) {
                ctx.response()
                    .setStatusCode(404)
                    .putHeader("content-type", "application/json")
                    .end("""{"error":"Item not found"}""")
                return@handler
            }
            
            // Convert the ItemDetail object to JSON using Jackson
            val jsonString = mapper.writeValueAsString(itemDetail)
            
            ctx.response()
                .putHeader("content-type", "application/json")
                .end(jsonString)
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