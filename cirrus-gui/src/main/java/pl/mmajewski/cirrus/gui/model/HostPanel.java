package pl.mmajewski.cirrus.gui.model;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Created by Maciej Majewski on 01/12/15.
 */
public class HostPanel implements RefreshablePanel {
    private JPanel hostPanel;
    private JTextField cirrusIdTextField;
    private JTextField addressTextField;
    private JTextField firstSeenTextField;
    private JTextField lastSeenTextField;
    private JTextField lastUpdatedTextField;
    private JTree propertiesTree;

    private Host host = null;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    synchronized public void apply(Host host){
        this.host = host;

        cirrusIdTextField.setText(host.getCirrusId());

        addressTextField.setText(
                host.getPhysicalAddress().getHostAddress() +
                        ":" + host.getPort() + " [" + host.getLatency() +"ms]");

        if(host.getFirstSeen()!=null) {
            firstSeenTextField.setText(host.getFirstSeen().format(dateTimeFormatter));
        }else{
            firstSeenTextField.setText("");
        }
        if(host.getLastSeen()!=null) {
            lastSeenTextField.setText(host.getLastSeen().format(dateTimeFormatter));
        }else{
            lastSeenTextField.setText("");
        }
        if(host.getLastUpdated()!=null) {
            lastUpdatedTextField.setText(host.getLastUpdated().format(dateTimeFormatter));
        }else{
            lastUpdatedTextField.setText("");
        }

        constructPropertiesTree(host);
    }

    @Override
    public void refresh(){
        if(host!=null) {
            apply(host);
        }
    }

    private void constructPropertiesTree(Host host){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Host Properties");

        DefaultMutableTreeNode tags = new DefaultMutableTreeNode("Tags");
        for(String tag : host.getTags()){
            tags.add(new DefaultMutableTreeNode(tag));
        }
        root.add(tags);

        DefaultMutableTreeNode availableContent = new DefaultMutableTreeNode("Avaliable Content");
        for(String content : host.getAvailableContent()){
            availableContent.add(new DefaultMutableTreeNode(content));
        }
        root.add(availableContent);

        DefaultMutableTreeNode sharedPieces = new DefaultMutableTreeNode("Shared Pieces");
        for(Map.Entry entry : host.getSharedPiecesMap().entrySet()){
            sharedPieces.add(new DefaultMutableTreeNode(entry));
        }
        root.add(sharedPieces);

        propertiesTree.setModel(new DefaultTreeModel(root));
    }

}
