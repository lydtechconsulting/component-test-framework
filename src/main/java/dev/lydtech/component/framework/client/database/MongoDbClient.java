package dev.lydtech.component.framework.client.database;

import java.util.Optional;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MongoDbClient {

    private static MongoDbClient instance;
    private static String dbUrl;

    private MongoDbClient(){
        String postgresHost = Optional.ofNullable(System.getProperty("docker.host"))
                .orElse("localhost");
        String postgresPort = Optional.ofNullable(System.getProperty("mongodb.mapped.port"))
                .orElseThrow(() -> new RuntimeException("mongodb.mapped.port property not found"));
        dbUrl = "mongodb://" + postgresHost + ":" + postgresPort + "/";
    }

    public synchronized static MongoDbClient getInstance() {
        if(instance==null) {
            instance = new MongoDbClient();
        }
        return instance;
    }

    /**
     * Connect to MongoDB with the database name passed in.
     */
    public static MongoClient getMongoClient() {
        ConnectionString connectionString = new ConnectionString(dbUrl);
        log.debug("Connected to MongoDB at: "+dbUrl);
        return MongoClients.create(connectionString);
    }

    /**
     * Close the MongoDB connection.
     */
    public static void close(MongoClient mongoClient) {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    public static String getDbUrl() {
        return dbUrl;
    }
}
