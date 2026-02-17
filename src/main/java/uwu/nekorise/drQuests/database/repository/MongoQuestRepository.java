package uwu.nekorise.drQuests.database.repository;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
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
        Document document = new Document()
                .append("nickname", progress.getNickname())
                .append("questId", progress.getQuestId())
                .append("progress", progress.getProgress())
                .append("completed", progress.isCompleted());

        collection.replaceOne(
                Filters.and(
                        Filters.eq("nickname", progress.getNickname()),
                        Filters.eq("questId", progress.getQuestId())
                ),
                document,
                new ReplaceOptions().upsert(true)
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
        return new QuestProgress(
                doc.getString("playerId"),
                doc.getString("questId"),
                doc.getInteger("progress"),
                doc.getBoolean("completed")
        );
    }
}
