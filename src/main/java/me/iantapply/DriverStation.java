package me.iantapply;

import me.iantapply.utils.Modes;
import me.iantapply.utils.PacketVariables;

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

    // Printouts
    private final JLabel batteryVoltage;

    public DriverStation() {
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

        batteryVoltage = new JLabel("Battery Voltage: " + PacketVariables.getBattery() + "v");
        batteryVoltage.setBounds( 10, 80, 320, 20 );

        buttons = new JToggleButton[]{teleopButton, autonomousButton, practiceButton, testButton};

        // Create the panel to hold the button, label, and textfield
        JPanel panel = new JPanel(null);
        panel.add(connectionStatus);
        panel.add( enableButton );
        panel.add(disableButton);

        for (JToggleButton button : buttons) {
            panel.add(button);
        }

        panel.add( batteryVoltage);
        panel.setPreferredSize( new Dimension(800, 600) );

        // Create the frame which is a window
        JFrame frame = new JFrame("Driver Station - Java");
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible( true );

        // Set labels as variables recieved
        Timer timer = new Timer(20, e -> {
            batteryVoltage.setText("Battery Voltage: " + PacketVariables.getBattery() + "v");
            if (Main.robotAddress[0] != null) {
                connectionStatus.setText("Robot Connected with IP: " + Main.robotAddress[0].getHostAddress());
            }
        });
        timer.start();
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
            PacketVariables.setEnabled(true);
        } else if (e.getSource() == disableButton) {
            disableButton.setSelected(true);
            enableButton.setSelected(false);
            PacketVariables.setEnabled(false);

        // Mode selection button toggling
        } else if (e.getSource() == teleopButton) {
            testButton.setSelected(true);
            PacketVariables.setMode(Modes.TELEOP);
        } else if (e.getSource() == autonomousButton) {
            autonomousButton.setSelected(true);
            PacketVariables.setMode(Modes.AUTON);
        } else if (e.getSource() == practiceButton) {
            practiceButton.setSelected(true);
            PacketVariables.setMode(Modes.PRACTICE);
        } else if (e.getSource() == testButton) {
            testButton.setSelected(true);
            PacketVariables.setMode(Modes.TEST);
        }
    }
}
