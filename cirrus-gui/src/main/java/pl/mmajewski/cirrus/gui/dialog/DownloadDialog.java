package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.content.ContentAccessor;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.action.DownloadPanel;
import pl.mmajewski.cirrus.gui.storage.ContentStoragePanel;
import pl.mmajewski.cirrus.impl.content.accessors.ContentAccessorImplPlainBQueue;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class DownloadDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonClose;
    private ContentStoragePanel contentStoragePanel;
    private DownloadPanel downloadPanel;

    private CirrusBasicApp cirrusBasicApp = null;
    private RefreshingThread refreshingThread = null;

    public void setCirrusBasicApp(CirrusBasicApp cirrusBasicApp) {
        this.cirrusBasicApp = cirrusBasicApp;
        CirrusEventHandler coreEventHandler = cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
        contentStoragePanel.apply(coreEventHandler.getContentStorage());
    }

    public void setRefreshingThread(RefreshingThread refreshingThread) {
        this.refreshingThread = refreshingThread;
        refreshingThread.register(contentStoragePanel);
    }

    public DownloadDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonClose);

        buttonClose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadDialog.this.onClose();
            }
        });

        // call onClose() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onClose();
            }
        });

        // call onClose() on ESCAPE
        contentPane.registerKeyboardAction(
                new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       DownloadDialog.this.onClose();
                   }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        contentStoragePanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                ContentStorage contentStorage = cirrusBasicApp.getAppEventHandler().getCoreEventHandler().getContentStorage();
                ContentMetadata contentMetadata = contentStorage.getContentMetadata((String) e.getNewValue());
                ContentAccessor contentAccessor = new ContentAccessorImplPlainBQueue(contentMetadata, cirrusBasicApp.getAppEventHandler().getCoreEventHandler());
                downloadPanel.setDownloadObjects(contentAccessor, contentMetadata);
            }
        });
    }

    private void onClose() {
        refreshingThread.unregister(contentStoragePanel);
        dispose();
    }

    public static void main(String[] args) {
        DownloadDialog dialog = new DownloadDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
