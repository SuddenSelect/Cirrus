package pl.mmajewski.cirrus.gui.action;

import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Maciej Majewski on 01/12/15.
 */
public class SignupPanel {
    private JPanel signupPanel;
    private JComboBox localIpComboBox;
    private JTextField remoteCirrusIdTextField;
    private JFormattedTextField remoteAddressFormattedTextField;
    private JComboBox ipVersionComboBox;

    private Pattern addressPattern = Pattern.compile("(.+):(\\d{1,5})");
    private InputVerifier addressVerifier = new InputVerifier() {
        @Override
        public boolean verify(JComponent input) {
            boolean result = false;
            JFormattedTextField addressField = (JFormattedTextField) input;
            Matcher matcher = addressPattern.matcher(addressField.getText());

            if(matcher.matches()){
                String address = matcher.group(1);
                String port = matcher.group(2);

                try {
                    InetAddress.getByName(address);
                    Integer portInt = Integer.parseInt(port);

                    result = portInt > 0 && portInt < 65536;

                } catch (UnknownHostException|NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            addressField.setBackground(result?Color.WHITE:Color.PINK);
            return result;
        }
    };

    public SignupPanel() {
        remoteAddressFormattedTextField.setInputVerifier(addressVerifier);
        fillAdressesComboBox();
        ipVersionComboBox.addActionListener(e -> fillAdressesComboBox());
    }

    private void fillAdressesComboBox(){
        try {
            List<String> activeIPs = getActiveIPs((String) ipVersionComboBox.getSelectedItem());
            localIpComboBox.setModel(new DefaultComboBoxModel(activeIPs.toArray()));
        } catch (SocketException|UnknownHostException e) {
            e.printStackTrace();
        }
    }


    private List<String> getActiveIPs(String ipVersion) throws SocketException, UnknownHostException {
        List<String> addrList = new ArrayList<>();
        if("IPv4".equals(ipVersion)){
            addrList.add(Inet4Address.getByName("0.0.0.0").getHostAddress());
        }
        if("IPv6".equals(ipVersion)){
            addrList.add(Inet6Address.getByName("::").getHostAddress());
        }
        Enumeration<NetworkInterface> allInterfaces = NetworkInterface.getNetworkInterfaces();
        while (allInterfaces.hasMoreElements()) {
            NetworkInterface ifc = allInterfaces.nextElement();
            if (ifc.isUp()) {
                Enumeration<InetAddress> enumAdds = ifc.getInetAddresses();
                while (enumAdds.hasMoreElements()) {
                    InetAddress addr = enumAdds.nextElement();
                    if(("IPv4".equals(ipVersion) && addr instanceof Inet4Address)
                     ||("IPv6".equals(ipVersion) && addr instanceof Inet6Address)){
                        addrList.add(addr.getHostAddress());
                    }
                }
            }
        }
        return addrList;
    }
}
