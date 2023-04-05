package me.iantapply.utils;

import javax.swing.*;

public class UIUtils {

    public static JProgressBar initializeProgressBar(Integer yPosition, String text) {
        JProgressBar newProgressBar =  new JProgressBar();
        newProgressBar.setStringPainted(true);
        newProgressBar.setString(text);
        newProgressBar.setMinimum(-127);
        newProgressBar.setMaximum(128);
        //newProgressBar.setIndeterminate(true);
        newProgressBar.setBounds(10, yPosition, 150, 20);

        return newProgressBar;
    }
}
