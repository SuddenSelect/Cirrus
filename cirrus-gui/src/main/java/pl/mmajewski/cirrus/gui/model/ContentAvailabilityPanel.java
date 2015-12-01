package pl.mmajewski.cirrus.gui.model;

import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Maciej Majewski on 01/12/15.
 */
public class ContentAvailabilityPanel implements RefreshablePanel {
    private JPanel contentAvailabilityPanel;
    private JTextField cirrusIdTextField;
    private JTextField contentIdTextField;
    private JList sequenceNumbersList;

    private ContentAvailability contentAvailability = null;

    synchronized public void apply(ContentAvailability contentAvailability){
        this.contentAvailability = contentAvailability;

        cirrusIdTextField.setText(contentAvailability.getHolderCirrusId());
        contentIdTextField.setText(contentAvailability.getContentId());
        Set numbers = new TreeSet<>(contentAvailability.getPiecesSequenceNumbers());
        sequenceNumbersList.setListData(numbers.toArray());
    }

    @Override
    public void refresh(){
        if(contentAvailability!=null){
            apply(contentAvailability);
        }
    }
}
