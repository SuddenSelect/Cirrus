package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.gui.RefreshablePanel;
import pl.mmajewski.cirrus.main.CirrusBasicApp;

import javax.swing.*;

/**
 * Created by Maciej Majewski on 05/12/15.
 */
public class FailuresPanel implements RefreshablePanel{
    private JPanel failuresPanel;
    private JTextPane failuresTextPane;

    private StringBuilder failures = new StringBuilder();
    private CirrusBasicApp cirrusBasicApp = null;

    public void setCirrusBasicApp(CirrusBasicApp cirrusBasicApp) {
        this.cirrusBasicApp = cirrusBasicApp;
    }

    @Override
    public void refresh() {
        if(cirrusBasicApp!=null){
            String fail;
            while((fail = cirrusBasicApp.getAppEventHandler().popFailure())!=null){
                failures.append(fail).append("\n");
            }
            while((fail = cirrusBasicApp.getAppEventHandler().getCoreEventHandler().popFailure())!=null){
                failures.append(fail).append("\n");
            }
            failuresTextPane.setText(failures.toString());
        }
    }
}
