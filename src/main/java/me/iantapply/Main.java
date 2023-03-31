package me.iantapply;


import me.iantapply.utils.RobotDiscovery;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Main {
    public static InetAddress[] robotAddress = {null};

    public static void main(String[] args) throws Exception {
        // Create mock service that a roboRIO would have
//        JmDNS jmdns = JmDNS.create();
//        ServiceInfo serviceInfo = ServiceInfo.create("_ni-rt._tcp.local.", "roboRIO-1114-FRC", 1735, "Mock roboRIO service info");
//        jmdns.registerService(serviceInfo);

        // Print looking for rio and then start finding it
        System.out.println("Looking for roboRIO...");
        new DriverStation();

        RobotDiscovery.findRobotIP(1114);
    }

    /**
     * Retrieves the team number set on the roboRIO via a socket connection to the roboRIO.
     */
    public static void sendTeamNumber() throws IOException {
//        try {
//            // Creates a socket connection to the rio via port 1735
        Socket socket = new Socket(robotAddress[0].getHostName(), 1735);
//
//            // Create datastream to send a retrieve data through
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();
//
//            // Initialize and send byte array
//            byte[] request = new byte[]{(short) 15};
//            System.out.println(request.length);
//            out.write(request);
//
//            // Read and print response I get back from the rio after I send the byte array
        byte[] response = new byte[1024];
        int bytesRead = in.read(response);
//            if (bytesRead >= 4 && response[0] == 0x0E && response[1] == 0x02) {
        ByteBuffer buffer = ByteBuffer.wrap(response, 2, bytesRead - 2);
//                buffer.order(ByteOrder.BIG_ENDIAN);
        //out.write(buffer);

        //buffer.putSho

        // Receiving
//                var seq = buffer.getShort();
//                var commVersion = buffer.get();
//                var status = buffer.get();
//                var trace = buffer.get();
//                double battery;
//                {
//                    var intc =buffer.get();
//                    var decc = buffer.get();
//                    battery = intc + decc / 255.0;
//                }
//                boolean requestTime = buffer.get() == 1;
//                int bytesLeft = bytesRead - 8;
//                while (bytesLeft > 0 ){
//                    int len = buffer.get();
//                    byte code = buffer.get();
//                    var tagBuffer = ByteBuffer.wrap(response, 7, len);
//                    bytesLeft -= len;
//                    switch (code){
//                        case 0x01 -> {
//
//                        }
//                        default ->  {
//                            throw new RuntimeException("Invalid code: " + code);
//                        }
//                    }
    }


    // Sending
//            buffer.putShort((short)12); // short = 2 bytes (length 2)
//            buffer.put(1); (comm version; always 1)
//            buffer.put(-1); // control
//            buffer.put(-1); // request
//            buffer.put(-1); // alience

    // -1 placeholder


//                int teamNumber = buffer.getShort();
//                System.out.println("Team number: " + teamNumber);
//            }
//            socket.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
}