package pl.mmajewski.cirrus.gui.action;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.ContentMetadata;
import pl.mmajewski.cirrus.content.ContentAccessor;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class DownloadPanel implements RefreshablePanel{
    private JPanel downloadFilePanel;
    private JFormattedTextField filePathTextField;
    private JButton downloadButton;
    private JProgressBar progressBar;
    private JButton cancelButton;
    private JButton chooseFileButton;

    private JFileChooser fileChooser = new JFileChooser();
    private ContentAccessor contentAccessor = null;
    private ContentMetadata contentMetadata = null;

    public DownloadPanel() {
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setMultiSelectionEnabled(false);

        filePathTextField.setVerifyInputWhenFocusTarget(false);
        filePathTextField.setInputVerifier(new InputVerifier() {
            @Override
            public boolean verify(JComponent input) {
                JFormattedTextField formattedTextField = (JFormattedTextField) input;
                File file = new File(formattedTextField.getText());
                boolean ok = !file.exists() && contentAccessor!=null && contentMetadata!=null;
                formattedTextField.setBackground(ok? Color.WHITE:Color.PINK);
                downloadButton.setEnabled(ok);
                return ok;
            }
        });

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = fileChooser.showSaveDialog(chooseFileButton);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        filePathTextField.setText(selectedFile.getAbsolutePath());
                        downloadButton.setEnabled(contentAccessor != null && contentMetadata != null);
                        progressBar.setValue(0);
                    }
                }
            }
        });

        downloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentAccessor.setContentMetadata(contentMetadata);
                try {
                    contentAccessor.saveAsFile(filePathTextField.getText());
                    cancelButton.setEnabled(true);
                } catch (FileNotFoundException | EventHandlerClosingCirrusException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), e1.getMessage());
                    e1.printStackTrace();
                    cancelButton.setEnabled(false);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contentAccessor.cancel();
                cancelButton.setEnabled(false);
                downloadButton.setEnabled(true);
            }
        });
    }

    public void setDownloadObjects(ContentAccessor contentAccessor, ContentMetadata contentMetadata) {
        this.contentAccessor = contentAccessor;
        this.contentMetadata = contentMetadata;
        if(contentAccessor==null || contentMetadata==null){
            throw new RuntimeException("Missing download objects: Accessor "+
                    (contentAccessor==null?"missing":"present")+", Metadata "+
                    (contentMetadata==null?"missing":"present"));
        }
    }

    @Override
    public void refresh() {
        if(contentAccessor!=null) {
            progressBar.setMaximum(contentAccessor.getMaxProgress());
            progressBar.setValue(contentAccessor.getProgress());
        }else{
            progressBar.setMaximum(100);
            progressBar.setValue(0);
        }
    }
}
