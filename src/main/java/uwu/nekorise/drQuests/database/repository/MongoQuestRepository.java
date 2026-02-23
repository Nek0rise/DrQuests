package uwu.nekorise.drQuests.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.bson.conversions.Bson;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoQuestRepository implements QuestRepository {
    private final MongoCollection<Document> collection;

    public MongoQuestRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getDatabase().getCollection("quests");
        this.collection.createIndex(
                new Document("nickname", 1).append("questId", 1),
                new IndexOptions().unique(true)
        );
    }

    @Override
    public void save(QuestProgress progress) {
        Document doc = new Document()
                .append("nickname", progress.getNickname())
                .append("questId", progress.getQuestId())
                .append("progress", progress.getProgress())
                .append("completed", progress.isCompleted());

        collection.replaceOne(
                Filters.and(
                        Filters.eq("nickname", progress.getNickname()),
                        Filters.eq("questId", progress.getQuestId())
                ),
                doc,
                new ReplaceOptions().upsert(true)
        );
    }

    @Override
    public void addProgress(String nickname, String questId, int amount) {
        List<Bson> updated = List.of(
                // create if not exists
                new Document("$set",
                        new Document("nickname", nickname)
                                .append("questId", questId)
                                .append("completed",
                                        new Document("$ifNull",
                                                List.of("$completed", false)))
                                .append("progress",
                                        new Document("$ifNull",
                                                List.of("$progress", 0)))
                ),

                // update if completed false
                new Document("$set",
                        new Document("progress",
                                new Document("$cond", List.of(
                                        new Document("$eq",
                                                List.of("$completed", true)),
                                        "$progress",
                                        new Document("$add",
                                                List.of("$progress", amount))
                                ))
                        )
                )
        );

        collection.updateOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                ),
                updated,
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void addBiome(String nickname, String questId, String biomeKey) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                ),
                new Document("$addToSet",
                        new Document("visitedBiomes", biomeKey))
                        .append("$setOnInsert",
                                new Document("nickname", nickname)
                                        .append("questId", questId)
                                        .append("completed", false)
                        ),
                new UpdateOptions().upsert(true)
        );
    }

    @Override
    public void setCompleted(String nickname, String questId, boolean isCompleted) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                ),
                new Document("$set",
                        new Document("completed", isCompleted)
                )
        );
    }

    @Override
    public void delete(String nickname, String questId) {
        collection.deleteOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                )
        );
    }

    @Override
    public Optional<QuestProgress> find(String nickname, String questId) {
        Document doc = collection.find(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                )).first();

        if (doc == null) return Optional.empty();
        return Optional.of(map(doc));
    }

    @Override
    public List<QuestProgress> findAll(String nickname) {
        List<QuestProgress> progresses = new ArrayList<>();

        for (Document doc : collection.find(Filters.eq("nickname", nickname))) {
            progresses.add(map(doc));
        }

        return progresses;
    }

    private QuestProgress map(Document doc) {
        List<String> visitedBiomes = (List<String>) doc.get("visitedBiomes", List.class);
        if (visitedBiomes == null) visitedBiomes = new ArrayList<>();

        return new QuestProgress(
                doc.getString("nickname"),
                doc.getString("questId"),
                doc.getInteger("progress", 0),
                doc.getBoolean("completed"),
                visitedBiomes
        );
    }
}
