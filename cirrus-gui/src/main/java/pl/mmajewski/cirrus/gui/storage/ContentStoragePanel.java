package pl.mmajewski.cirrus.gui.storage;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class ContentStoragePanel implements RefreshablePanel {
    private JPanel contentStoragePanel;
    private JList contentList;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    private ContentStorage contentStorage = null;

    public ContentStoragePanel() {
        contentList.addListSelectionListener(new ListSelectionListener() {
            private Object previousSelection = null;
            @Override
            public void valueChanged(ListSelectionEvent e) {
                propertyChangeSupport.firePropertyChange("selectedContentId", previousSelection, contentList.getSelectedValue());
                previousSelection=contentList.getSelectedValue();
            }
        });
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public void apply(ContentStorage contentStorage){
        this.contentStorage = contentStorage;
        if(contentStorage==null){
            return;
        }

        Set<ContentMetadata> allContentMetadata = contentStorage.getAllContentMetadata();
        Vector<String> contentIds = new Vector<>(allContentMetadata.size());
        for(ContentMetadata metadata : allContentMetadata){
            contentIds.add(metadata.getContentId());
        }
        Collections.sort(contentIds);
        contentList.setListData(contentIds);
    }

    @Override
    public void refresh() {
        if(contentList!=null){
            apply(contentStorage);
        }
    }
}
