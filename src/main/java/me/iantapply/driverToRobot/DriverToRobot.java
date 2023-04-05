package me.iantapply.driverToRobot;

import me.iantapply.Main;
import me.iantapply.utils.constants.PacketConstants;
import me.iantapply.utils.gamepad.Gamepad;
import me.iantapply.utils.gamepad.enums.AxisType;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

public class DriverToRobot {

    static int amountOfButtons = 10;
    static byte[] buttonBytes = new byte[(int) Math.ceil((amountOfButtons+7)/8)];

    /**
     * Bruh
     * @param butt bruh??
     * @param pressed BrUh!?!!
     */
    public static void bruh(int butt,  boolean pressed){
        butt -=1;
        if (pressed) {
            buttonBytes[butt / 8] |= (1 << (butt % 8));
            //buttonBytes[butt >> 3] |= (1 << (butt & 0b111));
        } else {
            buttonBytes[butt / 8] &= ~(1 << (butt % 8));
            //buttonBytes[butt >> 3] &= ~(1 << (butt & 0b111));
        }
    }

    /**
     * Starts sending the packets non-stop with the information. Packet
     * and byte information can be changed through the lombok setter method
     * for that variable.
     * @throws IOException IO exception that's caught through sending the packet.
     */
    public static void sendPacketToBuffer() throws IOException {
        // Open datagram sending socket on UDP port 1110
        DatagramSocket socket = new DatagramSocket(); // DONT SPECIFY PORT BECAUSE THIS WILL BREAK
        byte[] IPAddress = Main.robotAddress[0].getAddress();
        InetAddress address = InetAddress.getByAddress(IPAddress);

        // Schedule the code to run every 20ms
        PacketConstants.executor.scheduleAtFixedRate(() -> {
            ByteBuffer buffer = ByteBuffer.allocate(1024);

            bruh(10, Gamepad.controllers.getState(0).a);
            bruh(9, Gamepad.controllers.getState(0).b);
            bruh(8, Gamepad.controllers.getState(0).y);
            bruh(7, Gamepad.controllers.getState(0).x);
            bruh(5, Gamepad.controllers.getState(0).start);
            bruh(5, Gamepad.controllers.getState(0).start);
            bruh(5, Gamepad.controllers.getState(0).start);
            bruh(5, Gamepad.controllers.getState(0).start);
            bruh(5, Gamepad.controllers.getState(0).start);
            bruh(5, Gamepad.controllers.getState(0).start);

            /**
             * REQUIRED CORE DATA
             */

            /**
             * CONTROL BYTE
             */

            // Sequence number (always increases by 1 for every packet sent)
            buffer.putShort(PacketConstants.getSequenceNumber());
            // Comm version (always 1)
            buffer.put(PacketConstants.getCommVersion());
            // Control byte (various mode settings, e-stop, brownout, and enabling)
            byte controlByte = 0b00000000;
            if (PacketConstants.isEstopEnabled()) {
                controlByte |= 0b10000000;
            }
            if (PacketConstants.isBrownoutProtectionEnabled()) {
                controlByte |= 0b00010000;
            }
            if (PacketConstants.isFmsAttached()) {
                controlByte |= 0b00001000;
            }
            if (PacketConstants.isEnabled()) {
                controlByte |= 0b00000100;
            }
            switch (PacketConstants.getMode()) {
                case TELEOP -> controlByte |= 0b00000000;
                case TEST -> controlByte |= 0b00000001;
                case AUTON -> controlByte |= 0b00000010;
            }
            buffer.put(controlByte);

            /**
             * REQUEST BYTE
             */

            // Default request byte (nothing)
            byte requestByte = 0b00000000;
            // Set the reboot bit
            if (PacketConstants.shouldRebootRoboRIO) {
                requestByte |= 0b00001000;
            }
            // Set the restart bit
            if (PacketConstants.shouldRestartCode) {
                requestByte |= 0b00000100;
            }

            // Put the appropriate request byte in the buffer depending on what it should do
            buffer.put(requestByte);

            /**
             * ALLIANCE BYTE
             */
            // Set alliance number byte in buffer
            buffer.put(PacketConstants.allianceByte);

            /**
             * CONTROLLER/GAMEPAD/AXIS DATA
             */

            // Tag data
            buffer.put((byte) 14); // Size of data all together
            buffer.put((byte) 0x0c); // Tag ID

            // AXIS
            buffer.put((byte) 6); // axis count (2 triggers and 2 joysticks)
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.LeftJoystickX)); // axes (-128 to 127 for each axis) (N [0]
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.LeftJoystickY));
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.LeftTrigger));
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.RightTrigger));
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.RightJoystickX));
            buffer.put((byte) Gamepad.getModifiedAxis(AxisType.RightJoystickY));

            // BUTTOS
            buffer.put((byte) amountOfButtons); // button count (10 for 10 buttons)
            for(int i = 0; i < buttonBytes.length; i++) {
                buffer.put(buttonBytes[i]);
                System.out.println("Button byte: " + buttonBytes[i]);
            }

            // POVS
            buffer.put((byte) 1); // number of POV's
            buffer.putShort((short) -1);
            // N is defined as no specified length, just keep adding the same type of byte one after the other
            // until you've added your sufficient data.

            // Construct packet being sent
            DatagramPacket packet = new DatagramPacket(buffer.array(), buffer.position(), address, 1110);

            // Packet data being sent
//            System.out.print("[");
//            for(int i = 0; i < buffer.position(); i ++){
//                System.out.print(buffer.array()[i] + ", ");
//            }
//            System.out.println("]");

            // Send packet and update sequence number
            PacketConstants.sequenceNumber++;
            try {
                socket.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 0, 20, TimeUnit.MILLISECONDS); // Send every 20ms


        byte[] receiveData = new byte[1024];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
                receiveData.length);
        DatagramSocket serverSocket = new DatagramSocket(1150);

        while(true)
        {
            serverSocket.receive(receivePacket);
            ByteBuffer bb = ByteBuffer.wrap(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());

            short seq = bb.getShort();
            // Check and send packet dropped
            if (seq > PacketConstants.getSequenceNumber()) {
                System.out.println("\n \u001B[31m" + "Packet dropped!\n");
            }
            byte commVersion = bb.get();
            byte status = bb.get();
//            System.out.println("Status Byte: " + PacketUtils.printByteBits(status));
            byte trace = bb.get();
//            System.out.println("Trace Byte: " + PacketUtils.printByteBits(trace));

            double battery;
            {
                byte intc = bb.get();
                byte decc = bb.get();
                battery = (intc & 0xFF) + (decc & 0xFF) / 256.0;
                DecimalFormat decfor = new DecimalFormat("0.00");
                PacketConstants.setBattery(decfor.format(battery));
            }
            byte request = bb.get();
//            System.out.println("Request Byte: " + PacketUtils.printByteBits(request));
            try{
                int bytesLeft = bb.remaining();
                int startingPosition = bb.position();
                while (bytesLeft > 0) {
                    int len = bb.get();
                    byte code = bb.get();
                    ByteBuffer tagBuffer;
                    try {
                        tagBuffer = ByteBuffer.wrap(receiveData, startingPosition + 2, len);
                    }catch (Exception e){
                        e.printStackTrace();
                        continue;
                    }
                    bytesLeft -= len + 1;
                    startingPosition += len + 1;
                    switch (code) {
                        case 0x01 -> {

                        }
                        case 0x04 -> {
                            //System.out.println("Disk Free (MB): " + tagBuffer.getLong());
                            PacketConstants.setDiskFree(tagBuffer.getLong());

                        }
                        case 0x05 -> {
                            //System.out.println("CPU Info: " + tagBuffer.getFloat());
                        }
                        case 0x06 -> {
                            //System.out.println("RAM Used: " + tagBuffer.getLong());
                            PacketConstants.setRamFree(tagBuffer.getLong());
                        }
                        default -> {
                            System.out.println("Invalid tag received: " + code);
                        }
                    }
                }
            }catch (Exception e){
                //e.printStackTrace();
            }
        }
        // should close serverSocket in finally block
    }

}
