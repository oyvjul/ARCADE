package no.hiof.arcade.io.input;

import com.jme3.input.JoyInput;

public class XboxControllerInput
{
    //Analog axis
    public static final int STICK_LEFT_VERTICAL_AXIS = 0;
    public static final int STICK_LEFT_HORIZONTAL_AXIS = 1;
    public static final int STICK_RIGHT_VERTICAL_AXIS = 2;
    public static final int STICK_RIGHT_HORIZONTAL_AXIS = 3;
    public static final int TRIGGER_AXIS = 4;
    //D-pad
    public static final int DIRECTIONAL_PAD_UP = JoyInput.AXIS_POV_X;
    public static final int DIRECTIONAL_PAD_DOWN = JoyInput.AXIS_POV_X;
    public static final int DIRECTIONAL_PAD_LEFT = JoyInput.AXIS_POV_Y;
    public static final int DIRECTIONAL_PAD_RIGHT = JoyInput.AXIS_POV_Y;
    //Digital buttons
    public static final int BUTTON_A = 0;
    public static final int BUTTON_B = 1;
    public static final int BUTTON_X = 2;
    public static final int BUTTON_Y = 3;
    public static final int BUTTON_START = 7;
    public static final int BUTTON_BACK = 6;
    public static final int BUTTON_BUMPER_LEFT = 4;
    public static final int BUTTON_BUMPER_RIGHT = 5;
    public static final int BUTTON_LEFT_STICK = 8;
    public static final int BUTTON_RIGHT_STICK = 9;
    //Analog axis directions
    public static final boolean TRIGGER_RIGHT = true;
    public static final boolean TRIGGER_LEFT = false;
    public static final boolean STICK_DIRECTION_LEFT = true;
    public static final boolean STICK_DIRECTION_RIGHT = false;
    public static final boolean STICK_DIRECTION_UP = true;
    public static final boolean STICK_DIRECTION_DOWN = false;
}
