package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.model.HostPanel;
import pl.mmajewski.cirrus.gui.storage.HostStoragePanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class BrowseHostsDialog extends JDialog {
    private JPanel contentPane;
    private HostStoragePanel hostStoragePanel;
    private HostPanel hostPanel;

    private RefreshingThread refreshingThread = null;

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
        contentPane.registerKeyboardAction(e -> onClose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        hostStoragePanel.addPropertyChangeListener(e -> hostPanel.apply((Host) e.getNewValue()));
    }

    private void onClose() {
        if(refreshingThread!=null){
            refreshingThread.unregister(hostStoragePanel);
            refreshingThread.register(hostPanel);
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
