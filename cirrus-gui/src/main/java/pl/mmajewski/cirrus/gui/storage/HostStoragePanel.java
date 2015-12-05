package pl.mmajewski.cirrus.gui.storage;

import pl.mmajewski.cirrus.common.persistance.HostStorage;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class HostStoragePanel implements RefreshablePanel{
    private JPanel hostStoragePanel;
    private JList hostList;

    private HostStorage hostStorage = null;

    public void apply(HostStorage hostStorage){
        this.hostStorage = hostStorage;

        hostList.setListData(hostStorage.fetchAllHosts().toArray());
    }

    @Override
    public void refresh() {
        if(hostStorage != null){
            apply(hostStorage);
        }
    }
}
