package me.iantapply;

import com.studiohartman.jamepad.ControllerManager;
import me.iantapply.utils.RobotDiscovery;
import me.iantapply.utils.gamepad.Gamepad;

import java.net.InetAddress;

public class Main {
    // The robot address (if found)
    public static InetAddress[] robotAddress = {null};


    public static void main(String[] args) throws Exception {
        Gamepad.controllers = new com.studiohartman.jamepad.ControllerManager();
        Gamepad.controllers.initSDLGamepad();

        // Print looking for rio and then start finding it
        System.out.println("Looking for roboRIO...");

        // Create GUI for Driver Station
        new DriverStation();

        // Find the robot and connect to it (send and receive packets)
        RobotDiscovery.findRobotIP(1114);
    }
}