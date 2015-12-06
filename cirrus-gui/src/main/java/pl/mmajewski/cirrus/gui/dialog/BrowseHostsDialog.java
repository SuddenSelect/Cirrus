package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.model.HostPanel;
import pl.mmajewski.cirrus.gui.storage.HostStoragePanel;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class BrowseHostsDialog extends JDialog {
    private JPanel contentPane;
    private HostStoragePanel hostStoragePanel;
    private HostPanel hostPanel;

    private CirrusBasicApp cirrusBasicApp = null;
    private RefreshingThread refreshingThread = null;

    public void setCirrusBasicApp(CirrusBasicApp cirrusBasicApp) {
        this.cirrusBasicApp = cirrusBasicApp;
        hostStoragePanel.apply(cirrusBasicApp.getAppEventHandler().getHostStorage());
    }

    public void setRefreshingThread(RefreshingThread refreshingThread) {
        this.refreshingThread = refreshingThread;
        this.refreshingThread.register(hostStoragePanel);
        this.refreshingThread.register(hostPanel);
    }

    public BrowseHostsDialog() {
        setContentPane(contentPane);
        setModal(true);

        // call onClose() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
                                               @Override
                                               public void actionPerformed(ActionEvent e) {
                                                   BrowseHostsDialog.this.onClose();
                                               }
                                           },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        hostStoragePanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                hostPanel.apply((Host) e.getNewValue());
            }
        });
    }

    private void onClose() {
        if(refreshingThread!=null){
            refreshingThread.unregister(hostStoragePanel);
            refreshingThread.unregister(hostPanel);
        }
        dispose();
    }

    public static void main(String[] args) {
        BrowseHostsDialog dialog = new BrowseHostsDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
