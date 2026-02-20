package uwu.nekorise.drQuests.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import uwu.nekorise.drQuests.database.MongoManager;
import uwu.nekorise.drQuests.quest.model.QuestProgress;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MongoQuestRepository implements QuestRepository {
    private final MongoCollection<Document> collection;

    public MongoQuestRepository(MongoManager mongoManager) {
        this.collection = mongoManager.getDatabase().getCollection("quests");
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

    public void addProgress(String nickname, String questId, int value) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                ),
                new Document("$inc", new Document("progress", value))
                        .append("$setOnInsert",
                                new Document("nickname", nickname)
                                        .append("questId", questId)
                                        .append("completed", false)
                        ),
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
    public void setCompleted(String nickname, String questId) {
        collection.updateOne(
                Filters.and(
                        Filters.eq("nickname", nickname),
                        Filters.eq("questId", questId)
                ),
                new Document("$set",
                        new Document("completed", true)
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
