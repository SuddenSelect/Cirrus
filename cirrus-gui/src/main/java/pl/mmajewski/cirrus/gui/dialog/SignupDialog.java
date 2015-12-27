package pl.mmajewski.cirrus.gui.dialog;

import pl.mmajewski.cirrus.common.exception.EventHandlerClosingCirrusException;
import pl.mmajewski.cirrus.common.model.Host;
import pl.mmajewski.cirrus.gui.action.SignupPanel;
import pl.mmajewski.cirrus.main.CirrusBasicApp;
import pl.mmajewski.cirrus.main.coreevents.send.SendSignupCirrusEvent;
import pl.mmajewski.cirrus.network.event.HostCirrusEvent;

import javax.swing.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

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

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignupDialog.this.onOK();
            }
        });
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SignupDialog.this.onCancel();
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
                       SignupDialog.this.onCancel();
                   }
                },
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
        Set<Host> hosts = new HashSet<>();
        Host remoteHost = new Host();
        if(!signupPanel.isStartingNew()) {
            remoteHost.setPhysicalAddress(InetAddress.getByName(signupPanel.getRemoteAddress()));
            remoteHost.setPort(signupPanel.getRemotePort());
            remoteHost.setCirrusId(signupPanel.getRemoteCirrusId());

            hosts.add(remoteHost);
        }

        cirrusBasicApp = new CirrusBasicApp(InetAddress.getByName(signupPanel.getLocalAddress()));
        hosts.add(cirrusBasicApp.getAppEventHandler().getHostStorage().fetchLocalHost());

        HostCirrusEvent hostEvent = new HostCirrusEvent();
        hostEvent.setSharedHosts(hosts);
        cirrusBasicApp.accept(hostEvent);

        if(!signupPanel.isStartingNew()) {
            SendSignupCirrusEvent event = new SendSignupCirrusEvent();
            event.setHost(remoteHost);
            cirrusBasicApp.accept(event);
        }
    }
}
