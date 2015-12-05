package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.action.SignupPanel;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.coreevents.network.SendSignupCirrusEvent;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class SignupDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private SignupPanel signupPanel;

    private CirrusBasicApp cirrusBasicApp = null;

    public CirrusBasicApp getCirrusBasicApp() {
        return cirrusBasicApp;
    }

    public SignupDialog() {
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

    private void onOK() {
        try {
            signup();
        } catch (UnknownHostException|EventHandlerClosingCirrusException e) {
            JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
            e.printStackTrace();
        }
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        SignupDialog dialog = new SignupDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void signup() throws UnknownHostException, EventHandlerClosingCirrusException {
        Host host = new Host();
        if(!signupPanel.isStartingNew()) {
            host.setPhysicalAddress(InetAddress.getByName(signupPanel.getRemoteAddress()));
            host.setPort(signupPanel.getRemotePort());
            host.setCirrusId(signupPanel.getRemoteCirrusId());
        }

        cirrusBasicApp = new CirrusBasicApp(InetAddress.getByName(signupPanel.getLocalAddress()));

        if(!signupPanel.isStartingNew()) {
            SendSignupCirrusEvent event = new SendSignupCirrusEvent();
            event.setHost(host);
            cirrusBasicApp.accept(event);
        }
    }
}
