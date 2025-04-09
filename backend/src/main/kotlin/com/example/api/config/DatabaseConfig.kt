package com.example.api.config

import io.vertx.core.Vertx
import io.vertx.core.json.JsonObject
import io.vertx.ext.jdbc.JDBCClient
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.awaitResult
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import java.sql.Connection
import java.sql.DriverManager

class DatabaseConfig(private val vertx: Vertx) {
    private val logger = LoggerFactory.getLogger(DatabaseConfig::class.java)
    private val dbUrl = "jdbc:mysql://localhost:3306/database3?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
    private val dbUser = "root"
    private val dbPassword = "mypassword"
    
    private val dbConfig = JsonObject()
        .put("url", dbUrl)
        .put("driver_class", "com.mysql.cj.jdbc.Driver")
        .put("user", dbUser)
        .put("password", dbPassword)
        .put("max_pool_size", 30)

    val jdbcClient: JDBCClient by lazy {
        JDBCClient.create(vertx, dbConfig)
    }

    suspend fun initializeDatabase() {
        try {
            // Run migrations with Flyway
            logger.info("Running database migrations...")
            val flyway = Flyway.configure()
                .dataSource(dbUrl, dbUser, dbPassword)
                .load()

            flyway.migrate()
            logger.info("Database migrations completed successfully")

            // Test connection
            val connection = awaitResult<io.vertx.ext.sql.SQLConnection> { 
                jdbcClient.getConnection(it) 
            }
            
            try {
                // Test query
                val result = awaitResult<io.vertx.ext.sql.ResultSet> {
                    connection.query("SELECT 1", it)
                }
                logger.info("Database connection test successful: ${result.rows}")
            } finally {
                connection.close()
            }
            
            logger.info("Database connection established successfully")
        } catch (e: Exception) {
            logger.error("Failed to initialize database: ${e.message}", e)
            throw e
        }
    }
} 