package pl.mmajewski.cirrus.common.persistance;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Interface for general utility class caring for the content information preservance and accessibility.
 * Responsibilities of the implementer:
 * - automatic backups in the runtime
 * - retrieval upon startup
 * - checking validity of the Content
 * - binding ContentMetadata with ContentPieces
 * Pieces stored temporarily should NOT be backed up.
 * Every retrieved collection contains both temporary and persistent pieces.
 * Created by Maciej Majewski on 09/11/14.
 */
public interface ContentStorage {
    /* Parametrization */
    public void setMaxTemporarilyStoredPieces(int maxTemporaryPieces);
    public int  getMaxTemporarilyStoredPieces();
    public void setMaxPersistentStoredPieces(int maxPersistentPieces);
    public int getMaxPersistentStoredPieces();
    /* *************** */

    /**
     * Retrieves Metadata about every content in the system.
     * @return set of all stored ContentMetadata
     */
    public Set<ContentMetadata> getAllContentMetadata();

    /**
     * Retrieves Metadata about content with given ID.
     * @return ContentMetadata with given ID
     */
    public ContentMetadata getContentMetadata(String contentId);

    /**
     * Updates status of the ContentMetadata or adds if not existent.
     * Should in separate thread verify correctness.
     * @param metadata set of ContentMetadata to be updated
     */
    public void updateContentMetadata(Set<ContentMetadata> metadata);

    /**
     * Removes ContentMetadata and all pieces tied to it.
     * Invoked only when deleting content entirely.
     * @param metadata
     */
    public void deleteContent(ContentMetadata metadata);

    /**
     * After checking correctness, content with CORRUPTED status should be retrievable through
     * this method.
     * @return collection of Content known to be CORRUPTED
     */
    public Iterable<ContentMetadata> getCorruptedContentMetadata();

    /**
     * Adds given ContentPiece to local, persistent storage.
     * Should in separate thread verify correctness.
     * @param contentPiece ContentPiece to be stored locally
     */
    public void storeContentPiece(ContentPiece contentPiece) throws IOException;

    /**
     * Stores ContentPiece in temporary non-persistent space that
     * behaves as FIFO/Queue - when limit is exceeded, oldest gets deleted.
     * Unless stored ContentPiece was not retrieved yet - then limit may be exceeded.
     * @param contentPiece
     */
    public void storeContentPieceTemporarily(ContentPiece contentPiece);

    /**
     * Deletes given ContentPiece from local storage.
     * @param contentPiece ContentPiece not to be stored locally anymore
     */
    public void deleteContentPiece(ContentPiece contentPiece);

    /**
     * Retrieves every ContentPiece that has status CORRUPTED for given ContentMetadata.
     * @param contentMetadata
     * @return set of ContentPieces that have different checksums than expected
     */
    public Iterable<ContentPiece> getCorruptedContentPieces(ContentMetadata contentMetadata);

    /**
     * Retrieves all ContentPieces having status CORRUPTED.
     * @return set of ContentPieces that have different checksums than expected
     */
    public Iterable<ContentPiece> getCorruptedContentPieces();

    /**
     * Retrieves sequence numbers of ContentPieces tied to given
     * ContentMetadata that are NOT stored locally.
     * @param contentMetadata
     * @return sequence numbers of ContentPieces not stored locally
     */
    public Set<Integer> getMissingContentPieceSequenceNumbers(ContentMetadata contentMetadata);

    /**
     * Retrieves every available piece for given Content
     * @param contentMetadata
     * @return
     */
    public ArrayList<ContentPiece> getAvailablePieces(ContentMetadata contentMetadata);

}
