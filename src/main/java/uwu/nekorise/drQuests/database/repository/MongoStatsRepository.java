package uwu.nekorise.drQuests.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.quest.model.QuestStats;

import java.util.Optional;

public class MongoStatsRepository implements StatsRepository {
    private final MongoCollection<Document> collection;

    public MongoStatsRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getDatabase().getCollection("stats");
    }

    @Override
    public void createIfNone(String nickname) {
        collection.updateOne(
                Filters.eq("nickname", nickname),
                new Document("$setOnInsert",
                        new Document("nickname", nickname).append("totalCompleted", 0)
                ),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void save(QuestStats stats) {
        Document doc = new Document()
                .append("nickname", stats.getNickname())
                .append("totalCompleted", stats.getTotalCompleted());

        collection.replaceOne(
                Filters.eq("nickname", stats.getNickname()),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public void delete(String nickname) {
        collection.deleteOne(
                Filters.eq("nickname", nickname)
        );
    }

    @Override
    public Optional<QuestStats> find(String nickname) {
        Document doc = collection.find(Filters.eq("nickname", nickname)).first();
        if (doc == null) return Optional.empty();
        return Optional.of(map(doc));
    }

    private QuestStats map(Document doc) {
        return new QuestStats(
                doc.getString("nickname"),
                doc.getInteger("totalCompleted")
        );
    }
}
