package uwu.nekorise.drQuests.database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;

public class MongoManager {
    @Getter private final MongoClient client;
    @Getter private final MongoDatabase database;

    public MongoManager(String settings, String database) {
        this.client = MongoClients.create(settings);
        this.database = client.getDatabase(database);
    }

    public void shutdown() {
        client.close();
    }
}
