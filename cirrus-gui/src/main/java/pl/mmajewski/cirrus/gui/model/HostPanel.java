package pl.mmajewski.cirrus.gui.model;

import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.RefreshablePanel;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
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

    public HostPanel() {
        propertiesTree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("<none>")));
    }

    synchronized public void apply(Host host){
        this.host = host;
        if(host==null){
            return;
        }

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

    private TreePath tagsPath = null;
    private boolean tagsExpanded = false;

    private TreePath availPath = null;
    private boolean availExpanded = false;

    private TreePath piecesPath = null;
    private boolean piecesExpanded = false;

    @Override
    public void refresh(){
        if(host!=null) {
            if(tagsPath!=null){
                tagsExpanded = propertiesTree.isExpanded(tagsPath);
            }
            if(availPath!=null){
                availExpanded = propertiesTree.isExpanded(availPath);
            }
            if(piecesPath!=null){
                piecesExpanded = propertiesTree.isExpanded(piecesPath);
            }

            apply(host);

            if(tagsExpanded) {
                propertiesTree.expandPath(tagsPath);
            }
            if(availExpanded) {
                propertiesTree.expandPath(availPath);
            }
            if(piecesExpanded) {
                propertiesTree.expandPath(piecesPath);
            }
        }
    }

    private void constructPropertiesTree(Host host){
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("Host Properties");

        DefaultMutableTreeNode tags = new DefaultMutableTreeNode("Tags");
        for(String tag : host.getTags()){
            tags.add(new DefaultMutableTreeNode(tag));
        }
        root.add(tags);
        tagsPath = new TreePath(tags.getPath());

        DefaultMutableTreeNode availableContent = new DefaultMutableTreeNode("Avaliable Content");
        for(String content : host.getAvailableContent()){
            availableContent.add(new DefaultMutableTreeNode(content));
        }
        root.add(availableContent);
        availPath = new TreePath(availableContent.getPath());

        DefaultMutableTreeNode sharedPieces = new DefaultMutableTreeNode("Shared Pieces");
        for(Map.Entry entry : host.getSharedPiecesMap().entrySet()){
            sharedPieces.add(new DefaultMutableTreeNode(entry));
        }
        root.add(sharedPieces);
        piecesPath = new TreePath(sharedPieces.getPath());

        propertiesTree.setModel(new DefaultTreeModel(root));
    }

}
