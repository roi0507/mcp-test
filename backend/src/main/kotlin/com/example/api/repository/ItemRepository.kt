package com.example.api.repository

import com.example.api.config.DatabaseConfig
import com.example.api.model.ItemDetail
import io.vertx.core.json.JsonArray
import io.vertx.ext.sql.ResultSet
import io.vertx.ext.sql.SQLConnection
import io.vertx.kotlin.coroutines.awaitResult
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ItemRepository(private val dbConfig: DatabaseConfig) {
    private val logger = LoggerFactory.getLogger(ItemRepository::class.java)
    private val jdbcClient = dbConfig.jdbcClient

    suspend fun getAllItems(): List<ItemDetail> {
        logger.debug("Fetching all items from database")
        
        val connection = awaitResult<SQLConnection> { jdbcClient.getConnection(it) }
        
        return try {
            val result = awaitResult<ResultSet> { 
                connection.query("SELECT id, name, warehouse, amount, created_at, updated_at FROM items", it) 
            }
            
            result.rows.map { row ->
                ItemDetail(
                    id = row.getInteger("id"),
                    name = row.getString("name"),
                    warehouse = row.getString("warehouse"),
                    amount = row.getInteger("amount"),
                    createdAt = parseDateTime(row.getString("created_at")),
                    updatedAt = parseDateTime(row.getString("updated_at"))
                )
            }
        } catch (e: Exception) {
            logger.error("Error fetching all items: ${e.message}", e)
            emptyList()
        } finally {
            connection.close()
        }
    }

    suspend fun getItemById(id: Int): ItemDetail? {
        logger.debug("Fetching item with id: $id")
        
        val connection = awaitResult<SQLConnection> { jdbcClient.getConnection(it) }
        
        return try {
            val params = JsonArray().add(id)
            val result = awaitResult<ResultSet> { 
                connection.queryWithParams(
                    "SELECT id, name, warehouse, amount, created_at, updated_at FROM items WHERE id = ?", 
                    params,
                    it
                ) 
            }
            
            if (result.rows.isNotEmpty()) {
                val row = result.rows.first()
                ItemDetail(
                    id = row.getInteger("id"),
                    name = row.getString("name"),
                    warehouse = row.getString("warehouse"),
                    amount = row.getInteger("amount"),
                    createdAt = parseDateTime(row.getString("created_at")),
                    updatedAt = parseDateTime(row.getString("updated_at"))
                )
            } else {
                null
            }
        } catch (e: Exception) {
            logger.error("Error fetching item with id $id: ${e.message}", e)
            null
        } finally {
            connection.close()
        }
    }

    suspend fun saveItem(item: ItemDetail): ItemDetail? {
        logger.debug("Saving item: $item")
        
        val connection = awaitResult<SQLConnection> { jdbcClient.getConnection(it) }
        
        return try {
            if (item.id > 0) {
                // Update existing item
                val params = JsonArray()
                    .add(item.name)
                    .add(item.warehouse)
                    .add(item.amount)
                    .add(item.id)
                    
                val updateResult = awaitResult<io.vertx.ext.sql.UpdateResult> { 
                    connection.updateWithParams(
                        "UPDATE items SET name = ?, warehouse = ?, amount = ? WHERE id = ?",
                        params,
                        it
                    )
                }
                
                if (updateResult.updated > 0) {
                    getItemById(item.id)
                } else {
                    null
                }
            } else {
                // Insert new item
                val params = JsonArray()
                    .add(item.name)
                    .add(item.warehouse)
                    .add(item.amount)
                    
                val insertResult = awaitResult<io.vertx.ext.sql.UpdateResult> { 
                    connection.updateWithParams(
                        "INSERT INTO items (name, warehouse, amount) VALUES (?, ?, ?)",
                        params,
                        it
                    )
                }
                
                if (insertResult.updated > 0) {
                    val id = insertResult.keys.getInteger(0)
                    getItemById(id)
                } else {
                    null
                }
            }
        } catch (e: Exception) {
            logger.error("Error saving item: ${e.message}", e)
            null
        } finally {
            connection.close()
        }
    }

    suspend fun deleteItem(id: Int): Boolean {
        logger.debug("Deleting item with id: $id")
        
        val connection = awaitResult<SQLConnection> { jdbcClient.getConnection(it) }
        
        return try {
            val params = JsonArray().add(id)
            val result = awaitResult<io.vertx.ext.sql.UpdateResult> { 
                connection.updateWithParams(
                    "DELETE FROM items WHERE id = ?",
                    params,
                    it
                )
            }
            
            result.updated > 0
        } catch (e: Exception) {
            logger.error("Error deleting item with id $id: ${e.message}", e)
            false
        } finally {
            connection.close()
        }
    }
    
    private fun parseDateTime(dateStr: String?): LocalDateTime? {
        if (dateStr == null) return null
        return try {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            LocalDateTime.parse(dateStr, formatter)
        } catch (e: Exception) {
            logger.warn("Failed to parse date: $dateStr")
            null
        }
    }
} 