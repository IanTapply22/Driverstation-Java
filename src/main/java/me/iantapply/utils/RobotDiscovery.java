package me.iantapply.utils;

import me.iantapply.Main;
import me.iantapply.driverToRobot.DriverToRobot;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;
import java.io.IOException;
import java.net.InetAddress;

public class RobotDiscovery {
    static boolean searchOn = true;
    static int searchCount = 0;
    static boolean found = false;
    static InetAddress[] searchedAddresses = {null};

    public static void findRobotIP(int teamNumber) throws IOException, InterruptedException {
        while(searchOn) {

            if (searchedAddresses[0] == null) {
                // Create a JmDNS instance
                JmDNS jmdns = JmDNS.create(InetAddress.getLocalHost());

                // Specify the service type and name to look for
                String serviceType = "_ni-rt._tcp.local.";
                String serviceName = "roboRIO-" + teamNumber + "-FRC._ni-rt._tcp.local.";

                // Create a ServiceListener to handle service events
                ServiceListener listener = new ServiceListener() {
                    // Stop printing "cannot find roboRIO" when one is found

                    @Override
                    public void serviceAdded(ServiceEvent event) {
                    }

                    @Override
                    public void serviceRemoved(ServiceEvent event) {
                    }

                    @Override
                    public void serviceResolved(ServiceEvent event) {
                        String fullName = event.getName() + "." + event.getType();
                        if (fullName.equalsIgnoreCase(serviceName) && !found) {
                            InetAddress[] addresses = event.getInfo().getInet4Addresses();
                            if (addresses.length > 0) {
                                DriverStationConstants.setConnectionStatus("Robot Connected With IP: " + addresses[0].getHostAddress());
                                found = true;
                                searchedAddresses[0] = addresses[0];
                                Main.robotAddress[0] = addresses[0];
                                System.out.println("Found roboRIO at: " + addresses[0].getHostAddress());

                                try {
                                    DriverToRobot.sendPacketToBuffer();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                searchOn = false;
                            }
                        }
                    }
                };

                // Add the ServiceListener to the JmDNS instance
                jmdns.addServiceListener(serviceType, listener);

                // Wait for the specified timeout period for a roboRIO to be discovered
                Thread.sleep(4000);
                searchCount ++;

                // Remove the ServiceListener and close the JmDNS instance
                jmdns.removeServiceListener(serviceType, listener);
                jmdns.close();

                if(searchCount >= 5) {
                    searchOn = false;
                    System.out.println("Failed after 5 attempts!");
                }


                // If no address has been defined as the roboRIO's, then say that it can't find it.
                if (searchedAddresses[0] == null) {
                    System.out.println("Cannot find roboRIO");
                }
            }
        }
    }
}
