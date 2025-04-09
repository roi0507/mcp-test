package com.example.api

import com.example.api.config.DatabaseConfig
import com.example.api.model.ItemDetail
import com.example.api.repository.ItemRepository
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.vertx.core.Vertx
import io.vertx.core.http.HttpMethod
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.CorsHandler
import io.vertx.ext.web.handler.StaticHandler
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class MainVerticle : CoroutineVerticle() {
    private val logger = LoggerFactory.getLogger(MainVerticle::class.java)
    private val mapper = jacksonObjectMapper()
    private lateinit var itemRepository: ItemRepository
    
    override suspend fun start() {
        // Register Jackson module for Kotlin with Vert.x
        DatabindCodec.mapper().registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
        DatabindCodec.prettyMapper().registerModule(com.fasterxml.jackson.module.kotlin.KotlinModule.Builder().build())
        
        // Initialize database
        val dbConfig = DatabaseConfig(vertx)
        try {
            dbConfig.initializeDatabase()
            itemRepository = ItemRepository(dbConfig)
        } catch (e: Exception) {
            logger.error("Failed to initialize database", e)
            throw e
        }
        
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
        
        // Get all items from database
        router.get("/api/data").handler { ctx ->
            launch(vertx.dispatcher()) {
                try {
                    val items = itemRepository.getAllItems()
                    val response = JsonObject().put("items", items.map { 
                        JsonObject()
                            .put("id", it.id)
                            .put("name", it.name)
                    })
                    
                    ctx.response()
                        .putHeader("content-type", "application/json")
                        .end(response.encode())
                } catch (e: Exception) {
                    logger.error("Error getting all items", e)
                    ctx.response()
                        .setStatusCode(500)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Internal server error"}""")
                }
            }
        }
        
        // Item detail endpoint
        router.get("/api/items/:id").handler { ctx ->
            launch(vertx.dispatcher()) {
                val id = ctx.pathParam("id").toIntOrNull()
                
                if (id == null) {
                    ctx.response()
                        .setStatusCode(400)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Invalid item ID"}""")
                    return@launch
                }
                
                val itemDetail = itemRepository.getItemById(id)
                
                if (itemDetail == null) {
                    ctx.response()
                        .setStatusCode(404)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Item not found"}""")
                    return@launch
                }
                
                // Convert the ItemDetail object to JSON using Jackson
                val jsonString = mapper.writeValueAsString(itemDetail)
                
                ctx.response()
                    .putHeader("content-type", "application/json")
                    .end(jsonString)
            }
        }
        
        // Create a new item
        router.post("/api/items").handler { ctx ->
            launch(vertx.dispatcher()) {
                try {
                    val body = ctx.body().asJsonObject()
                    val name = body.getString("name")
                    val warehouse = body.getString("warehouse")
                    val amount = body.getInteger("amount")
                    
                    if (name.isNullOrBlank() || warehouse.isNullOrBlank() || amount == null) {
                        ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Name, warehouse and amount are required fields"}""")
                        return@launch
                    }
                    
                    val newItem = ItemDetail(
                        id = 0, // Will be assigned by the database
                        name = name,
                        warehouse = warehouse,
                        amount = amount
                    )
                    
                    val savedItem = itemRepository.saveItem(newItem)
                    
                    if (savedItem != null) {
                        ctx.response()
                            .setStatusCode(201)
                            .putHeader("content-type", "application/json")
                            .end(mapper.writeValueAsString(savedItem))
                    } else {
                        ctx.response()
                            .setStatusCode(500)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Failed to create item"}""")
                    }
                } catch (e: Exception) {
                    logger.error("Error creating item", e)
                    ctx.response()
                        .setStatusCode(500)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Internal server error"}""")
                }
            }
        }
        
        // Update an existing item
        router.put("/api/items/:id").handler { ctx ->
            launch(vertx.dispatcher()) {
                try {
                    val id = ctx.pathParam("id").toIntOrNull()
                    
                    if (id == null) {
                        ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Invalid item ID"}""")
                        return@launch
                    }
                    
                    val body = ctx.body().asJsonObject()
                    val name = body.getString("name")
                    val warehouse = body.getString("warehouse")
                    val amount = body.getInteger("amount")
                    
                    if (name.isNullOrBlank() || warehouse.isNullOrBlank() || amount == null) {
                        ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Name, warehouse and amount are required fields"}""")
                        return@launch
                    }
                    
                    val existingItem = itemRepository.getItemById(id)
                    
                    if (existingItem == null) {
                        ctx.response()
                            .setStatusCode(404)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Item not found"}""")
                        return@launch
                    }
                    
                    val updatedItem = existingItem.copy(
                        name = name,
                        warehouse = warehouse,
                        amount = amount
                    )
                    
                    val savedItem = itemRepository.saveItem(updatedItem)
                    
                    if (savedItem != null) {
                        ctx.response()
                            .putHeader("content-type", "application/json")
                            .end(mapper.writeValueAsString(savedItem))
                    } else {
                        ctx.response()
                            .setStatusCode(500)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Failed to update item"}""")
                    }
                } catch (e: Exception) {
                    logger.error("Error updating item", e)
                    ctx.response()
                        .setStatusCode(500)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Internal server error"}""")
                }
            }
        }
        
        // Delete an item
        router.delete("/api/items/:id").handler { ctx ->
            launch(vertx.dispatcher()) {
                try {
                    val id = ctx.pathParam("id").toIntOrNull()
                    
                    if (id == null) {
                        ctx.response()
                            .setStatusCode(400)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Invalid item ID"}""")
                        return@launch
                    }
                    
                    val deleted = itemRepository.deleteItem(id)
                    
                    if (deleted) {
                        ctx.response()
                            .setStatusCode(204)
                            .end()
                    } else {
                        ctx.response()
                            .setStatusCode(404)
                            .putHeader("content-type", "application/json")
                            .end("""{"error":"Item not found"}""")
                    }
                } catch (e: Exception) {
                    logger.error("Error deleting item", e)
                    ctx.response()
                        .setStatusCode(500)
                        .putHeader("content-type", "application/json")
                        .end("""{"error":"Internal server error"}""")
                }
            }
        }
        
        // Serve static frontend files if they exist in the resources/webroot directory
        router.route("/*").handler(StaticHandler.create("webroot").setIndexPage("index.html"))
        
        // SPA fallback - redirect to index.html for any routes not matched
        router.route("/*").handler { ctx ->
            if (!ctx.request().path().startsWith("/api/")) {
                ctx.reroute("/index.html")
            } else {
                ctx.next()
            }
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