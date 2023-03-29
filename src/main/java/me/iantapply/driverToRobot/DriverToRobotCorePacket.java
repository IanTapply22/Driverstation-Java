package me.iantapply.driverToRobot;

import lombok.Getter;
import me.iantapply.Main;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The core packet that is sent to the RoboRIO
 */
public class DriverToRobotCorePacket {

    // Timer to send every 20ms
    public ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Number packet sent from the Driver Station
     */
    @Getter
    private short sequenceNumber;

    /**
     * Comm version?
     * Always one as a byte (0b00000001)
     */
    @Getter
    private final byte commVersion = 0b00000001;

    /**
     * Control setting for the RoboRIO, the mask is as follows.
     * [e-stop enabled][ds attached][reserved][brownout protection][fms attached][enabled][mode bit 1][mode bit 2]
     * Example: 0b01100000
     * - 0b represents it's a byte
     * - 0 means e-stop is off
     * - 1 means ds attached but what's the point
     * - 0 is something weird, always needs to be 0
     * - 0 brownout protection off
     * - 0 means fms isn't attached
     * - 1 means it's enabled
     * - the last two zero's represent that it's in teleop (00 is teleop, 01 is test, and 10 is autonomous)
     */
    @Getter
    private byte controlByte;

    /**
     * Request byte values
     * Just made this boolean optional values because there's only two
     */
    @Getter
    private boolean shouldRebootRoboRIO;
    @Getter
    private boolean shouldRestartCode;

    /**
     * Tells the RoboRIO which Driver Station we are at. Either Red1, Red2, Red3, Blue1, Blue2, or Blue3.
     */
    @Getter
    private byte allianceByte;

    /**
     * Tags to send along with the packet
     */
    @Getter
    private byte[] tags;

    private DriverToRobotCorePacket(DriverToRobotCorePacketBuilder packetBuilder) {
        this.sequenceNumber = packetBuilder.sequenceNumber;
        this.controlByte = packetBuilder.controlByte;
        this.shouldRebootRoboRIO = packetBuilder.shouldRebootRoboRIO;
        this.shouldRestartCode = packetBuilder.shouldRestartCode;
        this.allianceByte = packetBuilder.allianceByte;
        this.tags = packetBuilder.tags;
    }

    public void printPacketDetails(byte requestByte) {
        System.out.println("Robot Information:");
        System.out.println("- RoboRIO IP: " + Main.robotAddress[0].toString().replace("/", ""));
        System.out.println("Driver to Robot Core Packet:");
        System.out.println("- Sequence Number: " + sequenceNumber);
        System.out.println("- Comm Version: " + commVersion);
        System.out.println("- Control Byte: " + controlByte);
        System.out.println("- Request Byte: " + requestByte);
        System.out.println("- Alliance Byte: " + allianceByte);
        System.out.println("- Tags: " + Arrays.toString(tags));
    }

    public void sendPacketToBuffer() throws IOException {
//        // Open datagram sending socket on UDP port 1110
//
////        byte[] IPAddress = Main.robotAddress[0].getAddress();
////        byte[] IPAddress = {127, 0, 0, 1};
//        byte[] IPAddress = {10, 11, 14, 2};
//        InetAddress address = InetAddress.getByAddress(IPAddress);
//        DatagramSocket socket = new DatagramSocket();
//
////        socket.connect(new InetSocketAddress(address, 1110));
//
//        // Schedule the code to run every 20ms
//        executor.scheduleAtFixedRate(() -> {
//            ByteBuffer buffer = ByteBuffer.allocate(1024);
//
//            /**
//             * REQUIRED CORE DATA
//             */
//
//            // Sequence number (always increases by 1 for every packet sent)
//            buffer.putShort(this.getSequenceNumber());
//            // Comm version (always 1)
//            buffer.put(this.getCommVersion());
//            // Control byte (various mode settings, e-stop, brownout, and enabling)
//            buffer.put(this.getControlByte());
//
//            buffer.put((byte) 0);
//
//            // Default request byte (nothing)
//            byte requestByte = 0b00000000;
//            // Set the reboot bit
//            if (shouldRebootRoboRIO) {
//                requestByte |= 0b00001000;
//            }
//            // Set the restart bit
//            if (shouldRestartCode) {
//                requestByte |= 0b00000100;
//            }
//
//            // Put the appropriate request byte in the buffer depending on what it should do
//            buffer.put(requestByte);
//
//            // Set alliance number byte in buffer
//            buffer.put(this.allianceByte);
//
//            // For each tag that's included (optional), add it to the buffer
//            if (this.tags != null) {
//                for (int i = 0; i <= this.tags.length; i++) {
//                    buffer.put(this.tags[i]); // Put each tag byte in the buffer
//                }
//            }
//
//            // Construct packet being sent
//            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), address, 1110);
//
//            // Send packet and update sequence number
//            long startTime = System.currentTimeMillis();
//            try {
////                System.out.println(socket.);
//                socket.send(packet);
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            this.sequenceNumber++;
//            long endTime = System.currentTimeMillis();
//            long difference = endTime - startTime;
//            //System.out.println("Packet time to send: " + difference + " milliseconds");
////            this.printPacketDetails(requestByte);
//        }, 0, 20, TimeUnit.MILLISECONDS);
        // Open datagram sending socket on UDP port 1110
        DatagramSocket socket = new DatagramSocket(1110);
        byte[] IPAddress = Main.robotAddress[0].getAddress();
        //byte[] IPAddress = {127, 0, 0, 1};
        InetAddress address = InetAddress.getByAddress(IPAddress);

        // Schedule the code to run every 20ms
        executor.scheduleAtFixedRate(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            /**
             * REQUIRED CORE DATA
             */

            // Sequence number (always increases by 1 for every packet sent)
            buffer.putShort(this.getSequenceNumber());
            // Comm version (always 1)
            buffer.put(this.getCommVersion());
            // Control byte (various mode settings, e-stop, brownout, and enabling)
            buffer.put(this.getControlByte());

            // Default request byte (nothing)
            byte requestByte = 0b00000000;
            // Set the reboot bit
            if (shouldRebootRoboRIO) {
                requestByte |= 0b00001000;
            }
            // Set the restart bit
            if (shouldRestartCode) {
                requestByte |= 0b00000100;
            }

            // Put the appropriate request byte in the buffer depending on what it should do
            buffer.put(requestByte);

            // Set alliance number byte in buffer
            buffer.put(this.allianceByte);

            // For each tag that's included (optional), add it to the buffer
            if (this.tags != null) {
                for (int i = 0; i <= this.tags.length; i++) {
                    buffer.put(this.tags[i]); // Put each tag byte in the buffer
                }
            }

            // Construct packet being sent
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), address, 1110);

            // Send packet and update sequence number
            long startTime = System.currentTimeMillis();
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            this.sequenceNumber++;
            long endTime = System.currentTimeMillis();
            long difference = endTime - startTime;
            //System.out.println("Packet time to send: " + difference + " milliseconds");
            //this.printPacketDetails(requestByte);
        }, 0, 20, TimeUnit.MILLISECONDS);



//        if (true){
//            return;
//        }


            DatagramSocket serverSocket = new DatagramSocket(1150, InetAddress.getByName("0.0.0.0"));
            byte[] receiveData = new byte[1024];

            DatagramPacket receivePacket = new DatagramPacket(receiveData,
                    receiveData.length);

            while(true)
            {
                serverSocket.receive(receivePacket);
//                System.out.println("asldkjasdlkjasdkjlasdlkjasdlkjaf");
//                {
//                    var data = receivePacket.getData();
//                    for (Byte b : data){
//                        System.out.println(b);
//                    }
//                }
                ByteBuffer bb = ByteBuffer.wrap(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

                short seq = bb.getShort();
                if (seq > this.getSequenceNumber()) {
                    System.out.println("\u001B[31m" + "Packet dropped!");
                }
                byte commVersion = bb.get();
                byte status = bb.get();
                byte trace = bb.get();
                System.out.print(status + "\n");

                double battery;
                {
                    byte intc = bb.get();
                    byte decc = bb.get();
                    battery = (intc & 0xFF) + (decc & 0xFF) / 256.0;
                    DecimalFormat decfor = new DecimalFormat("0.00");
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
            }
        // should close serverSocket in finally block
    }



    public static class DriverToRobotCorePacketBuilder {
        private final short sequenceNumber; // Sequence number. (updates +1 every 20ms)
        private final byte controlByte; // Byte to control e-stop, ds attached, brownout, fms, enabling, and the driving mode.
        private boolean shouldRebootRoboRIO; // Request bit to reboot RoboRIO.
        private boolean shouldRestartCode; // Request bit to restart the code.
        private final byte allianceByte; // Alliance byte. (Driver Station it's at)
        private byte[] tags; // Different optional tags. (timezone, date, joystick data)

        public DriverToRobotCorePacketBuilder(short sequenceNumber, byte controlByte, byte allianceByte) {
            this.sequenceNumber = sequenceNumber;
            this.controlByte = controlByte;
            this.allianceByte = allianceByte;
        }

        /**
         * Sets if the request byte should contain the request to reboot the RoboRIO.
         * @return The current Driver Station to RoboRIO packet.
         */
        public DriverToRobotCorePacketBuilder setShouldRebootRoboRIO() {
            this.shouldRebootRoboRIO = true;
            return this;
        }

        /**
         * Sets if the request byte should contain the request to restart the code.
         * @return The current Driver Station to RoboRIO packet.
         */
        public DriverToRobotCorePacketBuilder setShouldRestartCode() {
            this.shouldRestartCode = true;
            return this;
        }

        /**
         * Sets the tags that should be included in the packet (date, timezone, etc).
         * @param tags Array of tag bytes to add.
         * @return The current packet.
         */
        public DriverToRobotCorePacketBuilder setTags(byte[] tags) {
            this.tags = tags;
            return this;
        }

        /**
         * Build the current packet to its current form.
         * @return The final packet.
         */
        public DriverToRobotCorePacket build() {
            return new DriverToRobotCorePacket(this);
        }
    }

}
