package pl.mmajewski.cirrus.gui.storage;

import pl.mmajewski.cirrus.common.model.ContentAvailability;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class AvailabilityStoragePanel implements RefreshablePanel{
    private JPanel availabilityStoragePanel;
    private JTable availabilityTable;

    private AvailabilityStorage availabilityStorage = null;
    private String contentId = null;
    private Vector<String> columnNames = new Vector<String>(3){{
        add("Content ID");
        add("Holder Cirrus ID");
        add("Pieces Sequence Numbers");
    }};

    public void setAvailabilityStorage(AvailabilityStorage availabilityStorage) {
        this.availabilityStorage = availabilityStorage;
    }

    public void apply(String contentId){
        this.contentId = contentId;
        if(contentId==null){
            return;
        }

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnCount(3);
        tableModel.setColumnIdentifiers(columnNames);

        for(ContentAvailability availability : availabilityStorage.getContentAvailability(contentId)){
            Vector data = new Vector(3);
            data.add(availability.getContentId());
            data.add(availability.getHolderCirrusId());
            data.add(availability.getPiecesSequenceNumbers());

            tableModel.addRow(data);
        }
        availabilityTable.setModel(tableModel);
    }

    @Override
    public void refresh() {
        if(availabilityStorage!=null && contentId != null){
            apply(contentId);
        }
    }
}
