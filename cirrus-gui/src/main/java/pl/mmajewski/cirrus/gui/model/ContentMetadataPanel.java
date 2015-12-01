package pl.mmajewski.cirrus.gui.model;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by Maciej Majewski on 01/12/15.
 */
public class ContentMetadataPanel implements RefreshablePanel {
    private JPanel contentMetadataPanel;
    private JTextField contentIdTextField;
    private JTextField commiterCirrusIdTextField;
    private JTextField availableSinceTextField;
    private JTextField lastUpdatedTextField;
    private JTextField contentChecksumTextField;
    private JTextField statusTextField;
    private JTextField piecesAmountTextField;
    private JList piecesChecksumsList;

    private ContentMetadata contentMetadata = null;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    synchronized public void apply(ContentMetadata contentMetadata){
        this.contentMetadata = contentMetadata;

        contentIdTextField.setText(contentMetadata.getContentId());
        commiterCirrusIdTextField.setText(contentMetadata.getCommiterCirrusId());
        if(contentMetadata.getAvailableSince()!=null){
            availableSinceTextField.setText(contentMetadata.getAvailableSince().format(dateTimeFormatter));
        }else{
            availableSinceTextField.setText("");
        }
        if(contentMetadata.getLastUpdated()!=null){
            lastUpdatedTextField.setText(contentMetadata.getLastUpdated().format(dateTimeFormatter));
        }else{
            lastUpdatedTextField.setText("");
        }
        contentChecksumTextField.setText(contentMetadata.getContentChecksum());
        statusTextField.setText(contentMetadata.getStatus().toString());
        piecesAmountTextField.setText(contentMetadata.getPiecesAmount().toString());

        Set<Integer> sortedSequences = new TreeSet<>(contentMetadata.getPiecesChecksums().keySet());
        Vector<String> listData = new Vector<>(sortedSequences.size());
        for(Integer seq : sortedSequences){
            listData.add(seq+" - "+contentMetadata.getPiecesChecksums().get(seq));
        }
        piecesChecksumsList.setListData(listData);
    }

    @Override
    public void refresh() {
        if(contentMetadata!=null){
            apply(contentMetadata);
        }
    }
}
