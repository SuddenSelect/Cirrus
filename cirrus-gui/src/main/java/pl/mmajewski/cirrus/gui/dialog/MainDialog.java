package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.event.CirrusEventHandler;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.AvailabilityStorage;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.model.ContentMetadataPanel;
import pl.mmajewski.cirrus.gui.storage.AvailabilityStoragePanel;
import pl.mmajewski.cirrus.gui.storage.ContentStoragePanel;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import javax.swing.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MainDialog extends JDialog {
    private JPanel contentPane;
    private JButton signupButton;
    private JButton adaptFileButton;
    private JButton downloadButton;
    private JButton browseHostsButton;
    private ContentStoragePanel contentStoragePanel;
    private ContentMetadataPanel contentMetadataPanel;
    private FailuresPanel failuresPanel;
    private AvailabilityStoragePanel availabilityStoragePanel;
    private JLabel buildLabel;

    private CirrusBasicApp cirrusBasicApp = null;
    private RefreshingThread refreshingThread = new RefreshingThread();
    private Thread runningRefreshingThread = null;

    public MainDialog() {
        buildLabel.setText(getBuild());

        runningRefreshingThread = new Thread(refreshingThread);
        runningRefreshingThread.start();

        setContentPane(contentPane);
        setModal(true);

        // call onExit() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onExit() on ESCAPE
        contentPane.registerKeyboardAction(
                new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       MainDialog.this.onExit();
                   }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        contentStoragePanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if(cirrusBasicApp != null && e.getNewValue()!=null) {
                    CirrusEventHandler coreEventHandler = cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
                    ContentStorage contentStorage = coreEventHandler.getContentStorage();
                    ContentMetadata contentMetadata = contentStorage.getContentMetadata((String) e.getNewValue());
                    contentMetadataPanel.apply(contentMetadata);

                    AvailabilityStorage availabilityStorage = cirrusBasicApp.getAppEventHandler().getAvailabilityStorage();
                    availabilityStoragePanel.setAvailabilityStorage(availabilityStorage);
                    availabilityStoragePanel.apply((String) e.getNewValue());
                } else {
                    contentMetadataPanel.apply(null);
                    availabilityStoragePanel.apply(null);
                }
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignupDialog signupDialog = new SignupDialog();
                signupDialog.pack();
                signupDialog.setModal(true);
                signupDialog.setVisible(true);
                cirrusBasicApp = signupDialog.getCirrusBasicApp();
                if (cirrusBasicApp != null) {
                    signupButton.setEnabled(false);
                    adaptFileButton.setEnabled(true);
                    downloadButton.setEnabled(true);
                    browseHostsButton.setEnabled(true);

                    CirrusEventHandler coreEventHandler = cirrusBasicApp.getAppEventHandler().getCoreEventHandler();
                    contentStoragePanel.apply(coreEventHandler.getContentStorage());
                }
            }
        });

        adaptFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdaptDialog adaptDialog = new AdaptDialog();
                adaptDialog.setCirrusBasicApp(cirrusBasicApp);
                adaptDialog.setRefreshingThread(refreshingThread);
                adaptDialog.pack();
                adaptDialog.setVisible(true);
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadDialog downloadDialog = new DownloadDialog();
                downloadDialog.setCirrusBasicApp(cirrusBasicApp);
                downloadDialog.setRefreshingThread(refreshingThread);
                downloadDialog.pack();
                downloadDialog.setVisible(true);
            }
        });

        browseHostsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BrowseHostsDialog browseHostsDialog = new BrowseHostsDialog();
                browseHostsDialog.setRefreshingThread(refreshingThread);
                browseHostsDialog.pack();
                browseHostsDialog.setVisible(true);
            }
        });

        refreshingThread.register(contentStoragePanel);
        refreshingThread.register(availabilityStoragePanel);
        refreshingThread.register(contentMetadataPanel);
        refreshingThread.register(failuresPanel);
    }

    private void onExit() {
        refreshingThread.unregister(contentStoragePanel);
        refreshingThread.unregister(availabilityStoragePanel);
        refreshingThread.unregister(contentMetadataPanel);
        refreshingThread.unregister(failuresPanel);
        runningRefreshingThread.interrupt();
        try {
            runningRefreshingThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dispose();
    }

    public static void main(String[] args) {
        MainDialog dialog = new MainDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private Properties guiProperties = null;
    private void loadGuiProperties(){
        guiProperties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("gui.properties");
        try {
            guiProperties.load( inputStream );
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getBuild(){
        loadGuiProperties();
        return guiProperties.getProperty("gui.version");
    }
}
