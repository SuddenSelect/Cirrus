package pl.mmajewski.cirrus.impl.persistance;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
public class MemoryContentStorage implements ContentStorage{ //TODO implement
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
        return null;
    }

    @Override
    public ContentMetadata getContentMetadata(String contentId) {
        return null;
    }

    @Override
    public void updateContentMetadata(Set<ContentMetadata> metadata) {

    }

    @Override
    public void deleteContent(ContentMetadata metadata) {

    }

    @Override
    public Set<ContentMetadata> getCorruptedContentMetadata() {
        return null;
    }

    @Override
    public void storeContentPiece(ContentPiece contentPiece) {

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
        return null;
    }
}
