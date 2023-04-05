package me.iantapply;

import me.iantapply.utils.Modes;
import me.iantapply.utils.constants.PacketConstants;
import me.iantapply.utils.UIUtils;
import me.iantapply.utils.gamepad.Gamepad;
import me.iantapply.utils.gamepad.enums.AxisType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DriverStation extends JFrame implements ActionListener {
    private final JToggleButton[] buttons;

    public JLabel connectionStatus;

    // Control
    private final JToggleButton enableButton;
    private final JToggleButton disableButton;

    // Mode setting
    private final JToggleButton teleopButton;
    private final JToggleButton autonomousButton;
    private final JToggleButton practiceButton;
    private final JToggleButton testButton;

    // Gamepad stoof
    private final JProgressBar joystickLeftX = UIUtils.initializeProgressBar(140, "Left Joystick X");
    private final JProgressBar joystickLeftY = UIUtils.initializeProgressBar(170, "Left Joystick Y");

    private final JProgressBar joystickRightX = UIUtils.initializeProgressBar(200, "Right Joystick X");
    private final JProgressBar joystickRightY = UIUtils.initializeProgressBar(230, "Right Joystick Y");

    // Printouts
    private final JLabel batteryVoltage;
    private final JLabel diskFree;
    private final JLabel ramFree;

    public DriverStation() throws InterruptedException {
        super("Driver Station - Java");

        // Create the button to submit the value
        connectionStatus = new JLabel("No Robot Communication");
        connectionStatus.setBounds(10, 10, 320, 20);

        enableButton = new JToggleButton("Enable");
        disableButton = new JToggleButton("Disable");
        disableButton.setSelected(true);

        enableButton.addActionListener(this);
        disableButton.addActionListener(this);
        enableButton.setBounds( 10, 30, 100, 20 );
        disableButton.setBounds(120, 30, 100, 20);

        // Create the label to display the result
        teleopButton = new JToggleButton("Teleop");
        autonomousButton = new JToggleButton("Auton");
        practiceButton = new JToggleButton("Practice");
        testButton = new JToggleButton("Test");
        teleopButton.setSelected(true);

        teleopButton.addActionListener(this);
        autonomousButton.addActionListener(this);
        practiceButton.addActionListener(this);
        testButton.addActionListener(this);

        teleopButton.setBounds(10, 60, 85, 20);
        autonomousButton.setBounds(105, 60, 85, 20);
        practiceButton.setBounds(200, 60, 100, 20);
        testButton.setBounds(310, 60, 85, 20);

        batteryVoltage = new JLabel("Battery Voltage: " + PacketConstants.getBattery() + "v");
        batteryVoltage.setBounds( 10, 80, 320, 20 );
        diskFree = new JLabel("Disk Free (MB): " + PacketConstants.getDiskFree());
        diskFree.setBounds( 10, 100, 320, 20 );
        ramFree = new JLabel("RAM Free (MB): " + PacketConstants.getRamFree());
        ramFree.setBounds( 10, 120, 320, 20 );

        buttons = new JToggleButton[]{teleopButton, autonomousButton, practiceButton, testButton};

        // Create the panel to hold the button, label, and textfield
        JPanel panel = new JPanel(null);

        // Control buttons
        panel.add(connectionStatus);
        panel.add( enableButton );
        panel.add(disableButton);

        // Add mode selection buttons
        for (JToggleButton button : buttons) {
            panel.add(button);
        }

        // Add robot printouts
        panel.add( batteryVoltage);
        panel.add(diskFree);
        panel.add(ramFree);

        // Add gamepad values
        panel.add(joystickLeftX);
        panel.add(joystickLeftY);

        panel.add(joystickRightX);
        panel.add(joystickRightY);

        panel.setPreferredSize( new Dimension(800, 600) );

        // Create the frame which is a window
        JFrame frame = new JFrame("Driver Station - Java");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible( true );

        // Set labels as variables received
        Timer timer = new Timer(1, e -> {
            batteryVoltage.setText("Battery Voltage: " + PacketConstants.getBattery() + "v");
            diskFree.setText("Disk Free (MB): " + PacketConstants.getDiskFree().intValue()/1000/1000);
            ramFree.setText("RAM Free (MB): " + PacketConstants.getRamFree());

            // Gamepad values
            joystickLeftX.setValue(Gamepad.getModifiedAxis(AxisType.LeftJoystickX));
            joystickLeftY.setValue(Gamepad.getModifiedAxis(AxisType.LeftJoystickY));

            joystickRightX.setValue(Gamepad.getModifiedAxis(AxisType.RightJoystickX));
            joystickRightY.setValue(Gamepad.getModifiedAxis(AxisType.RightJoystickY));

            // Robot connection status
            if (Main.robotAddress[0] != null) {
                connectionStatus.setText("Robot Connected with IP: " + Main.robotAddress[0].getHostAddress());
            } else {
                connectionStatus.setText("No Robot Communication");
            }
        });
        timer.start();

//        while(true) {
//            // Gamepad values
//            joystickLeftX.setValue((int) ((int) (Main.controllers.getState(0).leftStickX*127.5)-0.5));
//            joystickLeftY.setValue((int) ((int) (Main.controllers.getState(0).leftStickY*127.5)-0.5));
//
//            joystickRightX.setValue((int) ((int) (Main.controllers.getState(0).rightStickX*127.5)-0.5));
//            joystickRightY.setValue((int) ((int) (Main.controllers.getState(0).rightStickY*127.5)-0.5));
//        }
    }

    public void actionPerformed(ActionEvent e) {
        JToggleButton clickedButton = (JToggleButton) e.getSource();
        for (JToggleButton button : buttons) {
            if (button != clickedButton && clickedButton != enableButton && clickedButton != disableButton) {
                button.setSelected(false);
            }
        }
        // Control button toggling
        if (e.getSource() == enableButton) {
            disableButton.setSelected(false);
            enableButton.setSelected(true);
            PacketConstants.setEnabled(true);
        } else if (e.getSource() == disableButton) {
            disableButton.setSelected(true);
            enableButton.setSelected(false);
            PacketConstants.setEnabled(false);

        // Mode selection button toggling
        } else if (e.getSource() == teleopButton) {
            teleopButton.setSelected(true);
            PacketConstants.setMode(Modes.TELEOP);
        } else if (e.getSource() == autonomousButton) {
            autonomousButton.setSelected(true);
            PacketConstants.setMode(Modes.AUTON);
        } else if (e.getSource() == practiceButton) {
            practiceButton.setSelected(true);
            PacketConstants.setMode(Modes.PRACTICE);
        } else if (e.getSource() == testButton) {
            testButton.setSelected(true);
            PacketConstants.setMode(Modes.TEST);
        }
    }
}
