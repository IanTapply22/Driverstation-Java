package me.iantapply.utils.gamepad;

import com.studiohartman.jamepad.ControllerManager;
import me.iantapply.utils.MathUtils;
import me.iantapply.utils.gamepad.enums.AxisType;

public class Gamepad {

    public static ControllerManager controllers;

    /**
     * Gets the raw, unmodified value of the specified axis.
     * @param axisType The type of axis to get the raw value of.
     * @return The unmodified raw value of the axis. (-1 to 1)
     */
    public static double getRawAxis(AxisType axisType) {
        switch(axisType) {
            case LeftJoystickX -> {
                return controllers.getState(0).leftStickX;
            }
            case LeftJoystickY -> {
                return controllers.getState(0).leftStickY;
            }
            case LeftTrigger -> {
                return controllers.getState(0).leftTrigger;
            }
            case RightTrigger -> {
                return controllers.getState(0).rightTrigger;
            }
            case RightJoystickX -> {
                return controllers.getState(0).rightStickX;
            }
            case RightJoystickY -> {
                return controllers.getState(0).rightStickY;
            }
            default -> {
                return 0;
            }
        }
    }

    /**
     * Gets the axis number/identifier according to the order in which they need to
     * be sent and from what should be displayed on driver station UI.
     * @param axisType The type of axis to get the identifier/number of.
     * @return The axis identifier/number.
     */
    public static int getAxisIdentifier(AxisType axisType) {
        switch(axisType) {
            case LeftJoystickX -> {
                return 0;
            }
            case LeftJoystickY -> {
                return 1;
            }
            case LeftTrigger -> {
                return 2;
            }
            case RightTrigger -> {
                return 3;
            }
            case RightJoystickX -> {
                return 4;
            }
            case RightJoystickY -> {
                return 5;
            }
            default -> {
                return 69420;
            }
        }
    }

    /**
     * Gets the modified value of the axis within the range of -127
     * to 128.
     * @param axisType Type of axis to parse.
     * @return The new modified value.
     */
    public static int getModifiedAxis(AxisType axisType) {
        switch(axisType) {
            case LeftJoystickX -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.LeftJoystickX));
            }
            case LeftJoystickY -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.LeftJoystickY));
            }
            case LeftTrigger -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.LeftTrigger));
            }
            case RightTrigger -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.RightTrigger));
            }
            case RightJoystickX -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.RightJoystickX));
            }
            case RightJoystickY -> {
                return MathUtils.calculateAxisValue(getRawAxis(AxisType.RightJoystickY));
            }
            default -> {
                return 69420;
            }
        }
    }
}
