package me.iantapply.driverToRobot;

import me.iantapply.Main;
import me.iantapply.utils.PacketVariables;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class DriverToRobot {

    public static void sendPacketToBuffer() throws IOException {
        // Open datagram sending socket on UDP port 1110
        DatagramSocket socket = new DatagramSocket();
        byte[] IPAddress = Main.robotAddress[0].getAddress();
        //byte[] IPAddress = {127, 0, 0, 1};
        InetAddress address = InetAddress.getByAddress(IPAddress);

        // Schedule the code to run every 20ms
        PacketVariables.executor.scheduleAtFixedRate(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            /**
             * REQUIRED CORE DATA
             */

            // Sequence number (always increases by 1 for every packet sent)
            buffer.putShort(PacketVariables.getSequenceNumber());
            // Comm version (always 1)
            buffer.put(PacketVariables.getCommVersion());
            // Control byte (various mode settings, e-stop, brownout, and enabling)
            byte controlByte = 0b01000000;
            if (PacketVariables.isEstopEnabled()) {
                controlByte |= 0b11000000;
            }
            if (PacketVariables.isBrownoutProtectionEnabled()) {
                controlByte |= 0b01010000;
            }
            if (PacketVariables.isFmsAttached()) {
                controlByte |= 0b01001000;
            }
            if (PacketVariables.isEnabled()) {
                controlByte |= 0b01000100;
            }
            switch (PacketVariables.getMode()) {
                case TELEOP -> controlByte |= 0b01000000;
                case AUTON -> controlByte |= 0b01000010;
                case PRACTICE -> controlByte |= 0b01000000; // TODO
                case TEST -> controlByte |= 0b01000001;
            }
            buffer.put(controlByte);

            // Default request byte (nothing)
            byte requestByte = 0b00000000;
            // Set the reboot bit
            if (PacketVariables.shouldRebootRoboRIO) {
                requestByte |= 0b00001000;
            }
            // Set the restart bit
            if (PacketVariables.shouldRestartCode) {
                requestByte |= 0b00000100;
            }

            // Put the appropriate request byte in the buffer depending on what it should do
            buffer.put(requestByte);

            // Set alliance number byte in buffer
            buffer.put(PacketVariables.allianceByte);

            // For each tag that's included (optional), add it to the buffer
            buffer.put((byte) 5);
            buffer.put((byte) 7);
            buffer.putFloat(System.nanoTime() / 1000000000.0f);

            // tiem data
            buffer.put((byte) 4);
            buffer.put((byte) 0x10);
            buffer.put("EST".getBytes());

            // Construct packet being sent
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), address, 1110);

            // Send packet and update sequence number
            long startTime = System.currentTimeMillis();
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            PacketVariables.sequenceNumber++;
            long endTime = System.currentTimeMillis();
            long difference = endTime - startTime;
            //System.out.println("Packet time to send: " + difference + " milliseconds");
            //System.out.println("beans");
        }, 0, 20, TimeUnit.MILLISECONDS);



//        if (true){
//            return;
//        }


        byte[] receiveData = new byte[1024];

        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);

        while(true)
        {
            DatagramSocket serverSocket = new DatagramSocket(1150);
            serverSocket.receive(receivePacket);
            ByteBuffer bb = ByteBuffer.wrap(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

            short seq = bb.getShort();
//            if (seq > PacketVariables.getSequenceNumber()) {
//                System.out.println("\n \u001B[31m" + "Packet dropped!\n");
//            }
            byte commVersion = bb.get();
            byte status = bb.get();
            byte trace = bb.get();
            //System.out.print(status + "\n");

            double battery;
            {
                byte intc = bb.get();
                byte decc = bb.get();
                battery = (intc & 0xFF) + (decc & 0xFF) / 256.0;
                DecimalFormat decfor = new DecimalFormat("0.00");
                PacketVariables.setBattery(decfor.format(battery));
                System.out.print("\rBattery Voltage: " + decfor.format(battery));
            }
            boolean requestTime = bb.get() == 1;
            int bytesLeft = bb.remaining();
            int startingPosition = bb.position();
            while (bytesLeft > 0) {
                int len = bb.get();
                byte code = bb.get();
                ByteBuffer tagBuffer = ByteBuffer.wrap(receiveData, startingPosition + 2, len - 1);
                bytesLeft -= len + 1;
                startingPosition += len + 2;
                switch (code) {
                    case 0x01 -> {

                    }
                    case 0x04 -> {
                        //System.out.println("Disk Free (MB): " + tagBuffer.getLong());

                    }
                    case 0x05 -> {
                        //System.out.println("CPU Info: " + tagBuffer.getFloat());
                    }
                    case 0x06 -> {
                        //System.out.println("RAM Used: " + tagBuffer.getLong());
                    }
                    default -> {
                        System.out.println("Invalid tag received: " + code);
                    }
                }
            }
            serverSocket.close();
        }
        // should close serverSocket in finally block
    }

}
