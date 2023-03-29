package me.iantapply.driverToRobot;

import me.iantapply.Main;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DriverToRobot {

    public static int sequenceNumber = 1;
    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    public static void sendPacketToBuffer(DriverToRobotCorePacket driverToRobotCorePacket) throws IOException {
        DatagramSocket socket = new DatagramSocket(1110);

        // Schedule the code to run every 20ms
        executor.scheduleAtFixedRate(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            // Put core data in buffer
            buffer.putShort(driverToRobotCorePacket.getSequenceNumber());
            buffer.put(driverToRobotCorePacket.getCommVersion());
            buffer.put(driverToRobotCorePacket.getControlByte());
            //buffer.put(driverToRobotCorePacket.getRequestByte());
            buffer.put((byte) 0x00000000); // Alliance station

            // Construct packet being sent
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), Main.robotAddress[0], 1110);

            // Send packet and update sequence number
            long startTime = System.currentTimeMillis();
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            sequenceNumber++;
            long endTime = System.currentTimeMillis();
            long difference = endTime - startTime;
            System.out.println("Packet time to send: " + difference + " milliseconds");
            //driverToRobotCorePacket.printPacketDetails();
        }, 0, 20, TimeUnit.MILLISECONDS);
    }

    public static void getPacketFromBuffer() throws SocketException {
        // Open datagram sending socket on UDP port 1150
        DatagramSocket socket = new DatagramSocket(1150);

        // Schedule the code to run every 20ms
        executor.scheduleAtFixedRate(() -> {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length); // create a packet to hold the received data


            try {
                socket.receive(packet); // receive the data into the packet
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            System.out.println("Recieved packet with length: " + packet.getLength());

// now you can extract the data from the packet
            ByteBuffer bb = ByteBuffer.wrap(packet.getData(), packet.getOffset(), packet.getLength());
            short seq = bb.getShort();
            byte commVersion = bb.get();
            byte status = bb.get();
            byte trace = bb.get();
            double battery;
            {
                byte intc = bb.get();
                byte decc = bb.get();
                battery = (intc & 0xFF) + (decc & 0xFF) / 255.0;
                System.out.println(battery);
            }
            boolean requestTime = bb.get() == 1;
            int bytesLeft = bb.remaining();
            while (bytesLeft > 0 ){
                int len = bb.get();
                byte code = bb.get();
                ByteBuffer tagBuffer = ByteBuffer.wrap(buffer, bb.position(), len);
                bb.position(bb.position() + len);
                bytesLeft -= len;
                switch (code){
                    case 0x01 -> {

                    }
                    default ->  {
                        throw new RuntimeException("Invalid code: " + code);
                    }
                }
            }
            //System.out.println("Recieved Comm Version: " + commVersion);
        }, 0, 20, TimeUnit.MILLISECONDS);
    }

}
