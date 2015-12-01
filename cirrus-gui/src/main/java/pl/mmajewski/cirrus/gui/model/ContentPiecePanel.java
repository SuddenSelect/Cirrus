package pl.mmajewski.cirrus.gui.model;

import pl.mmajewski.cirrus.common.model.ContentPiece;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by Maciej Majewski on 01/12/15.
 */
public class ContentPiecePanel implements RefreshablePanel {
    private JPanel contentPiecePanel;
    private JTextField contentIdTextField;
    private JTextField sequenceTextField;
    private JTextField expectedChecksumTextField;
    private JTextField statusTextField;
    private JTextPane contentHexTextPane;
    private JTextPane contentTextPane;

    private ContentPiece contentPiece = null;

    synchronized public void apply(ContentPiece contentPiece){
        this.contentPiece = contentPiece;

        contentIdTextField.setText(contentPiece.getContentId());
        sequenceTextField.setText(contentPiece.getSequence().toString());
        expectedChecksumTextField.setText(contentPiece.getExpectedChecksum());
        statusTextField.setText(contentPiece.getStatus().toString());

        ByteBuffer byteBuffer = contentPiece.getContent().asReadOnlyBuffer();
        contentTextPane.setText(toCharsetString(byteBuffer, StandardCharsets.UTF_8));
        byteBuffer.rewind();

        contentHexTextPane.setText(toHexString(byteBuffer));
        byteBuffer.rewind();

    }

    @Override
    public void refresh(){
        if(contentPiece!=null) {
            apply(contentPiece);
        }
    }

    private String toCharsetString(ByteBuffer byteBuffer, Charset charset){
        byte[] bytes;
        if(byteBuffer.hasArray()) {
            bytes = byteBuffer.array();
        } else {
            bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
        }
        return new String(bytes, charset);
    }

    private String toHexString(ByteBuffer byteBuffer){
        StringBuilder result = new StringBuilder();
        while(byteBuffer.hasRemaining()) {
            if(byteBuffer.position() % 8 == 0){
                result.append(String.format("%04X:   ",byteBuffer.position()));
            }
            result.append(String.format("%02X ", byteBuffer.get()));
            result.append(" "); // delimiter
            if(byteBuffer.position() % 8 == 0){
                result.append("\n");
            }
        }
        return result.toString();
    }
}
