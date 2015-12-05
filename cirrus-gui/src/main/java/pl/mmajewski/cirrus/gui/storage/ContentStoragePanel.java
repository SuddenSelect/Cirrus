package pl.mmajewski.cirrus.gui.storage;

import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.util.Collections;
import java.util.Set;
import java.util.Vector;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class ContentStoragePanel implements RefreshablePanel {
    private JPanel contentStoragePanel;
    private JList contentList;

    private ContentStorage contentStorage = null;

    public void apply(ContentStorage contentStorage){
        this.contentStorage = contentStorage;

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
