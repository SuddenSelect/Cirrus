package pl.mmajewski.cirrus.impl.persistance;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.*;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class MemoryContentStorage implements ContentStorage{ //TODO implement temporaries
    private Set<ContentMetadata> metadatas = new HashSet<>();
    private Map<String /*contentID*/,ContentMetadata> contentMetadataMap = new HashMap<>();
    private Map<String /*contentID*/,HashMap<Integer,ContentPiece>> piecesMap = new HashMap<>();
    private int maxTemporaryPieces = Integer.MAX_VALUE;
    private int maxStoredPieces = Integer.MAX_VALUE;

    @Override
    public void setMaxTemporarilyStoredPieces(int maxTemporaryPieces) {
        this.maxTemporaryPieces =maxTemporaryPieces;
    }

    @Override
    public int getMaxTemporarilyStoredPieces() {
        return maxTemporaryPieces;
    }

    @Override
    public void setMaxStoredPieces(int maxPieces) {
        this.maxStoredPieces = maxPieces;
    }

    @Override
    public int getMaxStoredPieces() {
        return maxStoredPieces;
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
        piecesMap.remove(metadata.getContentId());
    }

    @Override
    public Set<ContentMetadata> getCorruptedContentMetadata() {
        Set<ContentMetadata> corrupted = new TreeSet<>();
        for(ContentMetadata metadata : metadatas){
            if(!getCorruptedContentPieces(metadata).isEmpty()){
                corrupted.add(metadata);
            }
        }
        return corrupted;
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) {
        //TODO handle maxStoredPieces
        if(!piecesMap.containsKey(contentPiece.getContentId())){
            piecesMap.put(contentPiece.getContentId(), new HashMap<>());
        }
        piecesMap.get(contentPiece.getContentId()).put(contentPiece.getSequence(), contentPiece);
    }

    @Override
    public void storeContentPieceTemporarily(ContentPiece contentPiece) {
        //TODO handle maxTemporaryPieces
    }

    @Override
    public void deleteContentPiece(ContentPiece contentPiece) {
        HashMap<Integer,ContentPiece> pieces = piecesMap.get(contentPiece.getContentId());
        if(pieces!=null) {
            pieces.remove(contentPiece.getSequence());
        }
    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces(ContentMetadata contentMetadata) {
        Set<ContentPiece> corrupted = new TreeSet<>();
        for(ContentPiece piece : piecesMap.get(contentMetadata.getContentId()).values()){
            if(piece.getStatus().equals(ContentStatus.CORRUPTED)){
                corrupted.add(piece);
            }
        }
        return corrupted;
    }

    @Override
    public Set<ContentPiece> getCorruptedContentPieces() {
        Set<ContentPiece> corrupted = new TreeSet<>();
        for(ContentMetadata metadata : metadatas){
            corrupted.addAll(getCorruptedContentPieces(metadata));
        }
        return corrupted;
    }

    @Override
    public Set<Integer> getMissingContentPieceSequenceNumbers(ContentMetadata contentMetadata) {
        Set<Integer> missing = new TreeSet<>();
        HashMap<Integer, ContentPiece> presentPieces = piecesMap.get(contentMetadata.getContentId());
        for (int i = 0; i < contentMetadata.getPiecesAmount(); i++) {
            if(!presentPieces.keySet().contains(i)){
                missing.add(i);
            }
        }
        return missing;
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
