package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.gui.RefreshingThread;
import pl.mmajewski.cirrus.gui.action.AdaptPanel;
import pl.mmajewski.cirrus.gui.model.ContentMetadataPanel;
import pl.mmajewski.cirrus.gui.storage.ContentStoragePanel;
import pl.mmajewski.cirrus.impl.content.adapters.ContentAdapterImplPlainFile;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.appevents.CommitContentCirrusAppEvent;

import javax.swing.*;
import java.awt.event.*;

public class AdaptDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private AdaptPanel adaptPanel;
    private ContentMetadataPanel contentMetadataPanel;
    private ContentStoragePanel contentStoragePanel;

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
    }
    private void unregister(){
        if(refreshingThread!=null) {
            refreshingThread.unregister(contentMetadataPanel);
            refreshingThread.unregister(contentStoragePanel);
        }
    }

    public AdaptDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void commit() throws EventHandlerClosingCirrusException {
        CommitContentCirrusAppEvent event = new CommitContentCirrusAppEvent();
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
