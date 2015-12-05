package pl.mmajewski.cirrus.gui.action;

import pl.mmajewski.cirrus.common.event.GenericCirrusEventThread;
import pl.mmajewski.cirrus.content.ContentAdapter;
import pl.mmajewski.cirrus.exception.ContentAdapterCirrusException;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class AdaptPanel implements RefreshablePanel {
    private JPanel adaptFilePanel;
    private JFormattedTextField filePathTextField;
    private JButton chooseFileButton;
    private JButton adaptButton;
    private JProgressBar progressBar;
    private JButton cancelButton;

    private JFileChooser fileChooser = new JFileChooser();
    private ContentAdapter contentAdapter = null;
    private GenericCirrusEventThread adaptingThread = null;

    public AdaptPanel() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        filePathTextField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JFormattedTextField formattedTextField = (JFormattedTextField) input;
                File file = new File(formattedTextField.getText());
                boolean ok = file.exists() && file.isFile() && file.canRead();
                if(contentAdapter!=null){
                    ok = ok && contentAdapter.isSupported(file.getAbsolutePath());
                }
                formattedTextField.setBackground(ok? Color.WHITE:Color.PINK);
                adaptButton.setEnabled(ok);
                return ok;
            }
        });
        chooseFileButton.addActionListener(e -> {
            int result = fileChooser.showOpenDialog(chooseFileButton);
            if(result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    filePathTextField.setText(selectedFile.getAbsolutePath());
                    adaptButton.setEnabled(true);
                    progressBar.setValue(0);
                }
            }
        });
        cancelButton.addActionListener(e -> {
            if(adaptingThread!=null){
                adaptingThread.terminate();
            }
        });

        adaptButton.addActionListener(e -> {
            adaptingThread = new ContentAdapterThread();
            new Thread(adaptingThread).run();
        });
    }

    public void setContentAdapter(ContentAdapter contentAdapter) {
        this.contentAdapter = contentAdapter;
    }

    private class ContentAdapterThread extends GenericCirrusEventThread{

        public Integer getMaxProgress(){
            return contentAdapter.getMaxProgress();
        }

        @Override
        public Integer getProgress() {
            return contentAdapter.getProgress();
        }

        @Override
        public void run() {
            try {
                adaptButton.setEnabled(false);
                cancelButton.setEnabled(true);
                contentAdapter.adapt(filePathTextField.getText());

            } catch (ContentAdapterCirrusException e) {
                JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
                e.printStackTrace();
            } finally {
                cancelButton.setEnabled(false);
                adaptButton.setEnabled(true);
            }
        }
    }

    @Override
    public void refresh() {
        if(contentAdapter!=null){
            progressBar.setMaximum(100);
            progressBar.setValue(0);
        }else{
            progressBar.setMaximum(contentAdapter.getMaxProgress());
            progressBar.setValue(contentAdapter.getProgress());
        }
    }
}
