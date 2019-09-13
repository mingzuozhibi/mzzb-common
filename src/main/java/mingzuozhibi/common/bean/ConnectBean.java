package mingzuozhibi.common.bean;

import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsConnect;
import mingzuozhibi.common.jms.JmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;

@Slf4j
@Component
public class ConnectBean implements CommandLineRunner {

    @Value("${server.port}")
    private int port;

    @Autowired
    private JmsConnect jmsConnect;

    @Autowired
    private JmsMessage jmsMessage;

    public void run(String... args) {
        Optional<String> hostAddress = getHostAddress();
        if (hostAddress.isPresent()) {
            jmsConnect.connect(hostAddress.get() + ":" + port);
        } else {
            jmsMessage.warning("Can't get network address");
        }
    }

    private Optional<String> getHostAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress address = interfaceAddress.getAddress();
                    if (isNotLocalAddress(address)) {
                        return Optional.of(address.getHostAddress());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Can't get network address", e);
        }
        log.warn("Can't get network address, no error");
        return Optional.empty();
    }

    private boolean isNotLocalAddress(InetAddress address) {
        return !address.isLoopbackAddress()
                && !address.isAnyLocalAddress()
                && !address.isLinkLocalAddress()
                && !address.isMulticastAddress();
    }

}
