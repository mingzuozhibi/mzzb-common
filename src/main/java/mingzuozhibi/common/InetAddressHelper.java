package mingzuozhibi.common;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.Optional;

@Slf4j
public abstract class InetAddressHelper {

    public static Optional<String> getHostAddress() {
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

    private static boolean isNotLocalAddress(InetAddress address) {
        return !address.isLoopbackAddress()
                && !address.isAnyLocalAddress()
                && !address.isLinkLocalAddress()
                && !address.isMulticastAddress();
    }

}
