package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.common.persistance.ContentStorage;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.action.AdaptPanel;
import pl.mmajewski.cirrus.gui.model.ContentMetadataPanel;
import pl.mmajewski.cirrus.gui.storage.ContentStoragePanel;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.appevents.CommitContentCirrusAppEvent;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AdaptDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private AdaptPanel adaptPanel;
    private ContentMetadataPanel contentMetadataPanel;
    private ContentStoragePanel contentStoragePanel;
    private JSpinner redundancySpinner;

    private CirrusBasicApp cirrusBasicApp = null;
    private RefreshingThread refreshingThread = null;

    public void setCirrusBasicApp(CirrusBasicApp cirrusBasicApp) {
        this.cirrusBasicApp = cirrusBasicApp;
        contentStoragePanel.apply(cirrusBasicApp.getAppEventHandler().getContentStorage());
        adaptPanel.setContentAdapter(new ContentAdapterImplPlainFile(cirrusBasicApp.getAppEventHandler()));
    }

    public void setRefreshingThread(RefreshingThread refreshingThread) {
        this.refreshingThread = refreshingThread;
        refreshingThread.register(contentMetadataPanel);
        refreshingThread.register(contentStoragePanel);
        refreshingThread.register(adaptPanel);
    }
    private void unregister(){
        if(refreshingThread!=null) {
            refreshingThread.unregister(contentMetadataPanel);
            refreshingThread.unregister(contentStoragePanel);
            refreshingThread.register(adaptPanel);
        }
    }

    public AdaptDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdaptDialog.this.onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdaptDialog.this.onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(
                new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       AdaptDialog.this.onCancel();
                   }
                },
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        contentStoragePanel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if(cirrusBasicApp != null && e.getNewValue()!=null) {
                    ContentStorage contentStorage = cirrusBasicApp.getAppEventHandler().getContentStorage();
                    ContentMetadata contentMetadata = contentStorage.getContentMetadata((String) e.getNewValue());
                    contentMetadataPanel.apply(contentMetadata);
                }else{
                    contentMetadataPanel.apply(null);
                }
            }
        });

        redundancySpinner.setValue(1);
        redundancySpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int maxValue = cirrusBasicApp.getAppEventHandler().getHostStorage().size();
                int value = (Integer) redundancySpinner.getValue();
                if(value < 1){
                    redundancySpinner.setValue(1);
                }
                if(value > maxValue){
                    redundancySpinner.setValue(maxValue);
                }
            }
        });
    }

    private void commit() throws EventHandlerClosingCirrusException {
        CommitContentCirrusAppEvent event = new CommitContentCirrusAppEvent();
        event.setDiffusionRedundancy((Integer) redundancySpinner.getValue());
        cirrusBasicApp.accept(event);
    }
    private void onOK() {
        try {
            commit();
        } catch (EventHandlerClosingCirrusException e) {
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
            cirrusBasicApp.getAppEventHandler().getCoreEventHandler().pushFailure(e.getMessage());
            e.printStackTrace();
        }
        unregister();
        dispose();
    }

    private void rollback(){
        cirrusBasicApp.getAppEventHandler().resetStorage();
    }
    private void onCancel() {
        rollback();
        unregister();
        dispose();
    }

    public static void main(String[] args) {
        AdaptDialog dialog = new AdaptDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
