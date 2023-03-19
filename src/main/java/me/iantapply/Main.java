package me.iantapply;


import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;
import java.net.InetAddress;

public class Main {
    public static InetAddress[] robotAddress = {null};


    public static void main(String[] args) throws Exception {
        // Create mock service that a roboRIO would have
//        JmDNS jmdns = JmDNS.create();
//        ServiceInfo serviceInfo = ServiceInfo.create("_ni-rt._tcp.local.", "roboRIO-1114-FRC", 1735, "Mock roboRIO service info");
//        jmdns.registerService(serviceInfo);

        // Print looking for rio and then start finding it
        System.out.println("Looking for roboRIO...");
        findRobotIP(1114);
    }

    public static void findRobotIP(int teamNumber) throws Exception {
        // Create new JmDNS instance
        JmDNS jmdns = JmDNS.create();

        // Service info to look for
        String serviceType = "_ni-rt._tcp.local.";
        String serviceName = String.format("roboRIO-%d-FRC.%s", teamNumber, serviceType);

        ServiceListener listener = new ServiceListener() {
            // Stop printing cannot find roboRIO when it has found one
            boolean addressFound = false;

            @Override
            public void serviceAdded(ServiceEvent event) {}

            @Override
            public void serviceRemoved(ServiceEvent event) {}

            @Override
            public void serviceResolved(ServiceEvent event) {
                String fullServiceName = event.getInfo().getQualifiedName();
                if (fullServiceName.equalsIgnoreCase(serviceName) && !addressFound) {
                    InetAddress[] addresses = event.getInfo().getInetAddresses();
                    if (addresses.length > 0) {
                        addressFound = true;
                        robotAddress[0] = addresses[0];
                        System.out.println("Found roboRIO at: " + robotAddress[0].getHostAddress());
                    }
                }
            }
        };

        jmdns.addServiceListener(serviceType, listener);

        /**
         * Wait 5 seconds to give it time to look through all addresses and find the service name.
         * I'm just sleeping the main thread and not running this on a separate thread because
         * there shouldn't be any other processes running on the main thread .
         */
        Thread.sleep(5000);

        jmdns.unregisterAllServices();
        jmdns.close();

        // If no address has been defined as the roboRIO's, then say that it can't find it.
        if (robotAddress[0] == null) {
            System.out.println("Cannot find roboRIO");
        }
    }

}