package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.persistence.offheap.OffHeapPersistence;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class MemoryContentStorage implements ContentStorage{
    private final IndexedCollection<ContentPiece> persistentContentPieces =
            new ConcurrentIndexedCollection<ContentPiece>(OffHeapPersistence.onPrimaryKey(ContentPiece.IDX_UNIQUE_ID)) {{
                addIndex(CompoundIndex.onAttributes(
                        ContentPiece.IDX_CONTENT_ID,
                        ContentPiece.IDX_CONTENT_STATUS,
                        ContentPiece.IDX_SEQUENCE));
                addIndex(HashIndex.onAttribute(ContentPiece.IDX_CONTENT_STATUS));
            }};
    private final IndexedCollection<ContentPiece> temporaryContentPieces =
            new ConcurrentIndexedCollection<ContentPiece>(OffHeapPersistence.onPrimaryKey(ContentPiece.IDX_UNIQUE_ID)) {{
                addIndex(CompoundIndex.onAttributes(
                        ContentPiece.IDX_CONTENT_ID,
                        ContentPiece.IDX_CONTENT_STATUS,
                        ContentPiece.IDX_SEQUENCE));
                addIndex(HashIndex.onAttribute(ContentPiece.IDX_CONTENT_STATUS));
            }};

    private final IndexedCollection<ContentMetadata> contentMetadatas =
            new ConcurrentIndexedCollection<ContentMetadata>(OffHeapPersistence.onPrimaryKey(ContentMetadata.IDX_CONTENT_ID)) {{
                addIndex(CompoundIndex.onAttributes(
                        ContentMetadata.IDX_CONTENT_ID,
                        ContentMetadata.IDX_CONTENT_STATUS));
                addIndex(CompoundIndex.onAttributes(
                        ContentMetadata.IDX_COMMITER_CIRRUS_ID,
                        ContentMetadata.IDX_CONTENT_ID,
                        ContentMetadata.IDX_AVAILABLE_SINCE));
                addIndex(CompoundIndex.onAttributes(
                        ContentMetadata.IDX_CONTENT_STATUS,
                        ContentMetadata.IDX_LAST_UPDATED));
            }};
    private int maxPersistentPieces;
    private int maxTemporaryPieces;


    @Override
    public void setMaxTemporarilyStoredPieces(int maxTemporaryPieces) {
        this.maxTemporaryPieces = maxTemporaryPieces;
    }

    @Override
    public int getMaxTemporarilyStoredPieces() {
        return maxTemporaryPieces;
    }

    @Override
    public void setMaxPersistentStoredPieces(int maxPieces) {
        this.maxPersistentPieces = maxPieces;
    }

    @Override
    public int getMaxPersistentStoredPieces() {
        return maxPersistentPieces;
    }

    @Override
    public ResultSet<ContentMetadata> getAllContentMetadata() {
        Query<ContentMetadata> query = all(ContentMetadata.class);
        return contentMetadatas.retrieve(query);
    }

    @Override
    public ContentMetadata getContentMetadata(String contentId) {
        Query<ContentMetadata> query = equal(ContentMetadata.IDX_CONTENT_ID, contentId);
        for(ContentMetadata metadata : contentMetadatas.retrieve(query)){
            return metadata;
        }
        return null;
    }

    @Override
    public void updateContentMetadata(Set<ContentMetadata> metadata) {
        //removal necessary for updating indexes
        contentMetadatas.removeAll(metadata);
        contentMetadatas.addAll(metadata);
    }

    @Override
    public void deleteContent(ContentMetadata metadata) {
        contentMetadatas.remove(metadata);
        Predicate<ContentPiece> predicate = contentPiece -> contentPiece.getContentId().equals(metadata.getContentId());
        persistentContentPieces.removeIf(predicate);
        temporaryContentPieces.removeIf(predicate);
    }

    @Override
    public ResultSet<ContentMetadata> getCorruptedContentMetadata() {
        Query<ContentMetadata> query = equal(ContentMetadata.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED);
        return contentMetadatas.retrieve(query);
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) throws IOException {
        persistentContentPieces.add(contentPiece);
        //TODO saving to storage file
        //TODO remove if more than max
    }

    @Override
    public void storeContentPieceTemporarily(ContentPiece contentPiece) {
        temporaryContentPieces.add(contentPiece);
        //TODO remove if more than max
    }

    @Override
    public void deleteContentPiece(ContentPiece contentPiece) {
        persistentContentPieces.remove(contentPiece);
        //TODO deleting from storage file
    }

    private Set<ContentPiece> getPersistentAndTemporaryPieces(Query<ContentPiece> query){
        ResultSet<ContentPiece> persistent = persistentContentPieces.retrieve(query);
        ResultSet<ContentPiece> temporary = temporaryContentPieces.retrieve(query);
        Set<ContentPiece> queriedPieces = new TreeSet<>();
        for(ContentPiece piece : persistent){
            queriedPieces.add(piece);
        }
        for(ContentPiece piece : temporary){
            queriedPieces.add(piece);
        }
        return queriedPieces;
    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces(ContentMetadata contentMetadata) {
        Query<ContentPiece> query = and(
                equal(ContentPiece.IDX_CONTENT_ID,contentMetadata.getContentId()),
                equal(ContentPiece.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED));
        return getPersistentAndTemporaryPieces(query);
    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces() {
        Query<ContentPiece> query = equal(ContentPiece.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED);
        return getPersistentAndTemporaryPieces(query);
    }

    @Override
    public Set<Integer> getMissingContentPieceSequenceNumbers(ContentMetadata contentMetadata) {
        Set<Integer> missingPieces = new TreeSet<>();
        ArrayList<ContentPiece> availablePieces = getAvailablePieces(contentMetadata);
        for (int i = 0; i < contentMetadata.getPiecesAmount(); i++) {
            if(availablePieces.get(i)==null){
                missingPieces.add(i);
            }
        }
        return missingPieces;
    }

    @Override
    public ArrayList<ContentPiece> getAvailablePieces(ContentMetadata contentMetadata) {
        Query<ContentPiece> query = and(
                equal(ContentPiece.IDX_CONTENT_ID, contentMetadata.getContentId()),
                equal(ContentPiece.IDX_CONTENT_STATUS, ContentStatus.CORRECT));
        Set<ContentPiece> availablePieces = getPersistentAndTemporaryPieces(query);
        ArrayList<ContentPiece> availablePiecesList = new ArrayList<>(contentMetadata.getPiecesAmount());
        for(ContentPiece piece : availablePieces){
            availablePiecesList.add(piece.getSequence(), piece);
        }
        return availablePiecesList;
    }
}
