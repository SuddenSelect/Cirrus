package pl.mmajewski.cirrus.impl.content.adapters;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.common.model.ContentStatus;
import pl.mmajewski.cirrus.common.util.CirrusChecksum;
import pl.mmajewski.cirrus.common.util.CirrusIdGenerator;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Maciej Majewski on 2015-02-03.
 */
/*package*/ class ContentFactory {
    private CirrusChecksum totalChecksum = new CirrusChecksum();
    private CirrusChecksum pieceChecksum = new CirrusChecksum();
    private int sequence = 0;

    private ContentMetadata metadata = new ContentMetadata();
    private List<ContentPiece> pieces;
    private Map<Integer,String> piecesChecksums = new HashMap<>();

    /**
     * Constructor for processing static content (known length)
     * @param piecesNum total number of pieces
     */
    public ContentFactory(int piecesNum){
        metadata.setPiecesAmount(piecesNum);
        metadata.setStatus(ContentStatus.UNCHECKED);

            String owner = System.getProperty("user.name","-anon-");
            String cirrusId = CirrusIdGenerator.generateContentId(owner);
        metadata.setContentId(cirrusId);
        metadata.setPiecesChecksums(piecesChecksums);

        pieces = new ArrayList<>(piecesNum);
    }

    public void feed(ByteBuffer[] chunks){
        for(ByteBuffer chunk : chunks){
            pieceChecksum.reset();
            pieceChecksum.update(chunk);
            totalChecksum.update(chunk);

            ContentPiece newPiece = new ContentPiece();
            newPiece.setContent(chunk);
            newPiece.setExpectedChecksum(pieceChecksum.getCirrusChecksum());
            newPiece.setContentId(metadata.getContentId());
            newPiece.setSequence(sequence++);
            newPiece.setStatus(ContentStatus.CORRECT);//Well, just got calculated

            pieces.add(newPiece);
            piecesChecksums.put(newPiece.getSequence(), newPiece.getExpectedChecksum());
        }
    }

    public ContentMetadata getMetadata(){
        if(pieces.size() < metadata.getPiecesAmount()) {
            metadata.setStatus(ContentStatus.CALCULATING);
        }else{
            metadata.setStatus(ContentStatus.CORRECT);
            metadata.setContentChecksum(totalChecksum.getCirrusChecksum());
        }
        return metadata;
    }

    public List<ContentPiece> getPieces(){
        return pieces;
    }
}
