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
import java.util.function.Predicate;

import static com.googlecode.cqengine.query.QueryFactory.*;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class MemoryContentStorage extends AbstractContentStorage implements ContentStorage {

    public MemoryContentStorage() {
        persistentContentPieces =
                new ConcurrentIndexedCollection<ContentPiece>(OffHeapPersistence.onPrimaryKey(ContentPiece.IDX_UNIQUE_ID)) {{
                    addIndex(CompoundIndex.onAttributes(
                            ContentPiece.IDX_CONTENT_ID,
                            ContentPiece.IDX_CONTENT_STATUS,
                            ContentPiece.IDX_SEQUENCE));
                    addIndex(HashIndex.onAttribute(ContentPiece.IDX_CONTENT_STATUS));
                }};

        contentMetadatas =
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
    }
}
