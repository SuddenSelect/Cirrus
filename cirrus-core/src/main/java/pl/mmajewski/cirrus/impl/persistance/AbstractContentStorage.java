package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.persistence.offheap.OffHeapPersistence;
import com.googlecode.cqengine.query.Query;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by Maciej Majewski on 29/12/15.
 */
/*package*/ class AbstractContentStorage implements ContentStorage {
    protected IndexedCollection<ContentPiece> persistentContentPieces;
    protected IndexedCollection<ContentPiece> temporaryContentPieces =
            new ConcurrentIndexedCollection<ContentPiece>(OffHeapPersistence.onPrimaryKey(ContentPiece.IDX_UNIQUE_ID)) {{
                addIndex(CompoundIndex.onAttributes(
                        ContentPiece.IDX_CONTENT_ID,
                        ContentPiece.IDX_CONTENT_STATUS,
                        ContentPiece.IDX_SEQUENCE));
                addIndex(HashIndex.onAttribute(ContentPiece.IDX_CONTENT_STATUS));
            }};
    protected IndexedCollection<ContentMetadata> contentMetadatas;

//    private int maxPersistentPieces;
//    private int maxTemporaryPieces;


//    @Override
//    public void setMaxTemporarilyStoredPieces(int maxTemporaryPieces) {
//        this.maxTemporaryPieces = maxTemporaryPieces;
//    }
//
//    @Override
//    public int getMaxTemporarilyStoredPieces() {
//        return maxTemporaryPieces;
//    }
//
//    @Override
//    public void setMaxPersistentStoredPieces(int maxPieces) {
//        this.maxPersistentPieces = maxPieces;
//    }
//
//    @Override
//    public int getMaxPersistentStoredPieces() {
//        return maxPersistentPieces;
//    }

    @Override
    public Set<ContentMetadata> getAllContentMetadata() {
        return new HashSet<>(contentMetadatas);
    }

    @Override
    public ContentMetadata getContentMetadata(String contentId) {
        Query<ContentMetadata> query = equal(ContentMetadata.IDX_CONTENT_ID, contentId);
        return contentMetadatas.retrieve(query).uniqueResult();
    }

    @Override
    public void updateContentMetadata(Set<ContentMetadata> metadata) {
        //removal necessary for updating indexes
        Set<ContentMetadata> metadatas = new HashSet<>(metadata);
        contentMetadatas.removeAll(metadatas);
        contentMetadatas.addAll(metadatas);
    }

    @Override
    public void updateContentPieces(Set<ContentPiece> contentPieces){
        for(ContentPiece contentPiece : contentPieces) {
            Query<ContentPiece> query = and(
                    equal(ContentPiece.IDX_CONTENT_ID, contentPiece.getContentId()),
                    equal(ContentPiece.IDX_SEQUENCE, contentPiece.getSequence())
            );
            ContentPiece toDel = persistentContentPieces.retrieve(query).uniqueResult();
            persistentContentPieces.remove(toDel);
        }
        persistentContentPieces.addAll(contentPieces);
    }

    @Override
    public void deleteContent(ContentMetadata metadata) {
        contentMetadatas.remove(metadata);
        Query<ContentPiece> query = equal(ContentPiece.IDX_CONTENT_ID, metadata.getContentId());
        Set<ContentPiece> toDel = new HashSet<>();
        for(ContentPiece contentPiece : persistentContentPieces.retrieve(query)){
            toDel.add(contentPiece);
        }
        persistentContentPieces.removeAll(toDel);
        toDel.clear();

        for(ContentPiece contentPiece : temporaryContentPieces.retrieve(query)){
            toDel.add(contentPiece);
        }
        temporaryContentPieces.removeAll(toDel);
    }

    @Override
    public Iterable<ContentMetadata> getCorruptedContentMetadata() {
        Query<ContentMetadata> query = equal(ContentMetadata.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED);
        return contentMetadatas.retrieve(query);
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) throws IOException {
        persistentContentPieces.add(contentPiece);
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
    }

    private Iterable<ContentPiece> getPersistentAndTemporaryPieces(Query<ContentPiece> query){
        Set<ContentPiece> queriedPieces = new TreeSet<>();
        for(ContentPiece contentPiece : persistentContentPieces.retrieve(query)){
            queriedPieces.add(contentPiece);
        }
        for(ContentPiece contentPiece : temporaryContentPieces.retrieve(query)){
            queriedPieces.add(contentPiece);
        }
        return queriedPieces;
    }

    @Override
    public Iterable<ContentPiece> getCorruptedContentPieces(ContentMetadata contentMetadata) {
        Query<ContentPiece> query = and(
                equal(ContentPiece.IDX_CONTENT_ID,contentMetadata.getContentId()),
                equal(ContentPiece.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED));
        return getPersistentAndTemporaryPieces(query);
    }

    @Override
    public Iterable<ContentPiece> getCorruptedContentPieces() {
        Query<ContentPiece> query = equal(ContentPiece.IDX_CONTENT_STATUS, ContentStatus.CORRUPTED);
        return getPersistentAndTemporaryPieces(query);
    }

    @Override
    public Set<Integer> getMissingContentPieceSequenceNumbers(ContentMetadata contentMetadata) {
        Set<Integer> missingPieces = new TreeSet<>();
        ArrayList<ContentPiece> availablePieces = getAvailablePieces(contentMetadata);
        for (int i = 0; i < contentMetadata.getPiecesAmount(); i++) {
            try {
                if (availablePieces.get(i) == null) {
                    missingPieces.add(i);
                }
            }catch (IndexOutOfBoundsException e){
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
        Iterable<ContentPiece> availablePieces = getPersistentAndTemporaryPieces(query);
        ArrayList<ContentPiece> availablePiecesList = new ArrayList<>(contentMetadata.getPiecesAmount());
        for(ContentPiece piece : availablePieces){
            availablePiecesList.add(piece.getSequence(), piece);
        }
        return availablePiecesList;
    }
}
