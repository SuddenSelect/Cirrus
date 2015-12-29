package pl.mmajewski.cirrus.impl.persistance;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.ObjectLockingIndexedCollection;
import com.googlecode.cqengine.index.compound.CompoundIndex;
import com.googlecode.cqengine.index.hash.HashIndex;
import com.googlecode.cqengine.persistence.disk.DiskPersistence;
import com.googlecode.cqengine.persistence.offheap.OffHeapPersistence;
import pl.mmajewski.cirrus.common.exception.InvalidPersistanceStoragePathCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

/**
 * Created by Maciej Majewski on 14/09/15.
 */
public class PersistentContentStorage extends AbstractContentStorage implements ContentStorage {
    private File piecesSubfolder;
    private String pieceExtension = ".piece";
    private IndexedCollection<ContentMetadata> metadatas;
    private final Map<String/*contentId*/,IndexedCollection<ContentPiece>> pieces = new TreeMap<>();

    private File getPieceFile(String contentId){
        return new File(piecesSubfolder, contentId+pieceExtension);
    }
    private IndexedCollection<ContentPiece> newPiecesCollection(String contentId) throws IOException {
        File piecesFile = getPieceFile(contentId);
        if(!piecesFile.exists()){
            piecesFile.createNewFile();
        }
        return new ObjectLockingIndexedCollection<>(
                DiskPersistence.onPrimaryKeyInFile(
                        ContentPiece.IDX_SEQUENCE,
                        piecesFile));
    }

    public PersistentContentStorage(File persistancePath) throws InvalidPersistanceStoragePathCirrusException, IOException {
        if(persistancePath.isFile()||!persistancePath.canWrite()){
            throw new InvalidPersistanceStoragePathCirrusException(persistancePath);
        }
        this.piecesSubfolder = new File(persistancePath, "pieces");
        File metadataFile = new File(persistancePath, "metadata");
        if(!metadataFile.exists()) {
            metadataFile.createNewFile();
        }
        metadatas = new ObjectLockingIndexedCollection<ContentMetadata>(DiskPersistence.onPrimaryKeyInFile(ContentMetadata.IDX_CONTENT_ID, metadataFile)){{
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
        contentMetadatas = metadatas;

        persistentContentPieces =
                new ConcurrentIndexedCollection<ContentPiece>(OffHeapPersistence.onPrimaryKey(ContentPiece.IDX_UNIQUE_ID)) {{
                    addIndex(CompoundIndex.onAttributes(
                            ContentPiece.IDX_CONTENT_ID,
                            ContentPiece.IDX_CONTENT_STATUS,
                            ContentPiece.IDX_SEQUENCE));
                    addIndex(HashIndex.onAttribute(ContentPiece.IDX_CONTENT_STATUS));
                }};

        load();
    }

    private void load() throws IOException {
        FilenameFilter filter = (dir, name) -> name.endsWith(pieceExtension);
        for(String pieceFilename : piecesSubfolder.list(filter)){
            String contentId = pieceFilename.split(Pattern.quote(pieceExtension))[0];
            pieces.put(contentId, newPiecesCollection(contentId));
        }
        super.updateContentMetadata(metadatas);
        for(IndexedCollection<ContentPiece> piecesCollection : pieces.values()){
            for(ContentPiece piece : piecesCollection){
                super.storeContentPiece(piece);
            }
        }
    }

    @Override
    public void deleteContent(ContentMetadata metadata) {
        super.deleteContent(metadata);
        if(pieces.containsKey(metadata.getContentId())){
            pieces.remove(metadata.getContentId());
            File piecesFile = getPieceFile(metadata.getContentId());
            piecesFile.delete();
        }
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) throws IOException {
        super.storeContentPiece(contentPiece);
        if(!pieces.containsKey(contentPiece.getContentId())){
            pieces.put(contentPiece.getContentId(), newPiecesCollection(contentPiece.getContentId()));
        }
        pieces.get(contentPiece.getContentId()).add(contentPiece);
    }

    @Override
    public void deleteContentPiece(ContentPiece contentPiece) {
        super.deleteContentPiece(contentPiece);
        IndexedCollection<ContentPiece> piecesCollection = pieces.get(contentPiece.getContentId());
        piecesCollection.remove(contentPiece);
        if(piecesCollection.isEmpty()){
            pieces.remove(contentPiece.getContentId());
            File piecesFile = getPieceFile(contentPiece.getContentId());
            piecesFile.delete();
        }
    }
}
