package mingzuozhibi.common.bean;

import lombok.extern.slf4j.Slf4j;
import mingzuozhibi.common.jms.JmsConnect;
import mingzuozhibi.common.jms.JmsMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
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

    private String moduleAddr;

    public void run(String... args) {
        Optional<String> hostAddress = getHostAddress();
        if (hostAddress.isPresent()) {
            moduleAddr = hostAddress.get() + ":" + port;
            jmsMessage.success("应用已启动 => " + moduleAddr);
            sendConnect();
        } else {
            jmsMessage.warning("Can't get network address");
        }
    }

    @Scheduled(cron = "0 0/10 * * * ?")
    public void sendConnect() {
        if (moduleAddr != null) {
            jmsConnect.connect(moduleAddr);
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
