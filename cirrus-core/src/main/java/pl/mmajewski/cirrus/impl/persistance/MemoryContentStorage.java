package pl.mmajewski.cirrus.impl.persistance;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.*;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class MemoryContentStorage implements ContentStorage{ //TODO implement
    private Set<ContentMetadata> metadatas = new HashSet<>();
    private Map<String /*contentID*/,ContentMetadata> contentMetadataMap = new HashMap<>();
    private Map<String /*contentID*/,HashMap<Integer,ContentPiece>> piecesMap = new HashMap<>();

    @Override
    public void setMaxTemporarilyStoredPieces(int maxTemporaryPieces) {

    }

    @Override
    public int getMaxTemporarilyStoredPieces() {
        return 0;
    }

    @Override
    public void setMaxStoredPieces(int maxPieces) {

    }

    @Override
    public int getMaxStoredPieces() {
        return 0;
    }

    @Override
    public Set<ContentMetadata> getAllContentMetadata() {
        return metadatas;
    }

    @Override
    public ContentMetadata getContentMetadata(String contentId) {
        return contentMetadataMap.get(contentId);
    }

    @Override
    public void updateContentMetadata(Set<ContentMetadata> metadata) {
        metadatas.addAll(metadata);
        for(ContentMetadata meta : metadata){
            contentMetadataMap.put(meta.getContentId(), meta);
        }
    }

    @Override
    public void deleteContent(ContentMetadata metadata) {
        metadatas.remove(metadata);
        contentMetadataMap.remove(metadata.getContentId());
    }

    @Override
    public Set<ContentMetadata> getCorruptedContentMetadata() {
        return null;
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) {
        if(!piecesMap.containsKey(contentPiece.getContentId())){
            piecesMap.put(contentPiece.getContentId(), new HashMap<>());
        }
        piecesMap.get(contentPiece.getContentId()).put(contentPiece.getSequence(), contentPiece);
    }

    @Override
    public void storeContentPieceTemporarily(ContentPiece contentPiece) {

    }

    @Override
    public void deleteContentPiece(ContentPiece contentPiece) {

    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces(ContentMetadata contentMetadata) {
        return null;
    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces() {
        return null;
    }

    @Override
    public Set<Integer> getMissingContentPieceSequenceNumbers(ContentMetadata contentMetadata) {
        return null;
    }

    @Override
    public ArrayList<ContentPiece> getAvailablePieces(ContentMetadata contentMetadata) {
        ArrayList<ContentPiece> arr = new ArrayList<>(contentMetadata.getPiecesAmount());
        HashMap<Integer,ContentPiece> pieces = piecesMap.get(contentMetadata.getContentId());
        if(pieces!=null){
            for (int i = 0; i < contentMetadata.getPiecesAmount(); i++) {
                if(pieces.containsKey(i)) {
                    arr.add(pieces.get(i));
                }else{
                    arr.add(null);
                }
            }
        }
        return arr;
    }
}
