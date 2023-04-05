package me.iantapply.utils.constants;

import lombok.Getter;
import lombok.Setter;
import me.iantapply.utils.Modes;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class PacketConstants {

    // Timer to send every 20ms
    public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    /**
     * Number packet sent from the Driver Station
     */
    @Getter
    public static short sequenceNumber = 1;

    /**
     * Comm version?
     * Always one as a byte (0b00000001)
     */
    @Getter
    public static final byte commVersion = 0b00000001;

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
     * - the last two zero's represent that it's in teleop (00 is teleop, 01 is test, and 11 is autonomous)
     */
    @Getter
    @Setter
    public static boolean estopEnabled = false;
    @Getter
    @Setter
    public static boolean brownoutProtectionEnabled = false;
    @Getter
    @Setter
    public static boolean fmsAttached = false;
    @Getter
    @Setter
    public static boolean isEnabled = false;
    @Getter
    @Setter
    public static Modes mode = Modes.TELEOP;
    /**
     * Request byte values
     * Just made this boolean optional values because there's only two
     */
    @Getter
    @Setter
    public static boolean shouldRebootRoboRIO = false;
    @Getter
    public static boolean shouldRestartCode = false;

    /**
     * Tells the RoboRIO which Driver Station we are at. Either Red1, Red2, Red3, Blue1, Blue2, or Blue3.
     */
    @Getter
    public static byte allianceByte = 0x00000000;

    /**
     * Tags to send along with the packet
     */

    @Getter
    @Setter
    public static String battery = "0.00";
    @Getter
    @Setter
    public static Long diskFree = 0L;
    @Getter
    @Setter
    public static Long ramFree = 0L;
    @Getter
    @Setter
    public static boolean brownoutProtectionOn = false;


    /**
     * Gamepad stuff
     */


}
