package no.hiof.arcade.io.input;

import com.jme3.input.InputManager;
import com.jme3.input.Joystick;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.JoyAxisTrigger;
import com.jme3.input.controls.JoyButtonTrigger;
import com.jme3.input.controls.KeyTrigger;
import java.util.ArrayList;
import java.util.List;
import no.hiof.arcade.exception.NoJoystickExistsException;

/**
 *
 * @author JonAre
 */
public class InputMapper
{
    InputManager inputManager;
    ActionListener listenerToNotifyOnAction;
    Joystick joystickToMapTo;
    int idOfJoystick;
    List<String> registeredNames;

    public InputMapper(InputManager inputManager, ActionListener listener) throws IllegalArgumentException
    {
        this.inputManager = inputManager;
        this.listenerToNotifyOnAction = listener;
        this.registeredNames = new ArrayList<String>();

        if(this.inputManager == null || listenerToNotifyOnAction == null)
        {
            throw new IllegalArgumentException("Cannot use null as argument");
        }
        
        System.out.println(inputManager);
        autoregisterJoystick();
    }

    public void mapInputToName(String nameOfAction, InputType inputType, int inputCode, boolean inputDirection) throws NoJoystickExistsException
    {
        switch(inputType)
        {
            case JOYSTICK_BUTTON:
                mapJoystickButton(nameOfAction, inputCode);
                break;
            case JOYSTICK_AXIS:
                mapJoystickAxis(nameOfAction, inputCode, inputDirection);
                break;
            case KEYBOARD:
                mapKeyboardKey(nameOfAction, inputCode);
                break;
            case MOUSE_BUTTON:
                break;
            case MOUSE_AXIS:
                break;
        }
    }

    public void mapJoystickButton(String nameOfAction, int buttonCode) throws NoJoystickExistsException
    {
        if(joystickToMapTo == null)
        {
            throw new NoJoystickExistsException("Cannot map a button when no joystick is registered");
        }
        

        registeredNames.add(nameOfAction);
        inputManager.addMapping(nameOfAction, new JoyButtonTrigger(joystickToMapTo.getJoyId(), buttonCode));
        inputManager.addListener(listenerToNotifyOnAction, nameOfAction);
    }

    public void mapJoystickAxis(String nameOfAction, int inputCode, boolean inputDirection) throws NoJoystickExistsException
    {
        if(joystickToMapTo == null)
        {
            throw new NoJoystickExistsException("Cannot map a button when no joystick is registered");
        }

        registeredNames.add(nameOfAction);
        inputManager.addMapping(nameOfAction, new JoyAxisTrigger(joystickToMapTo.getJoyId(), inputCode, inputDirection));
        inputManager.addListener(listenerToNotifyOnAction, nameOfAction);
    }

    public void mapKeyboardKey(String nameOfAction, int keyCode)
    {
        if(nameOfAction == null)
        {
            throw new IllegalArgumentException();
        }

        registeredNames.add(nameOfAction);
        inputManager.addMapping(nameOfAction, new KeyTrigger(keyCode));
        inputManager.addListener(listenerToNotifyOnAction, nameOfAction);
    }

    public void mapMouseButton(String nameOfAction, int inputCode, int inputDirection)
    {
        throw new UnsupportedOperationException();
    }

    public void mapMouseAxis(String nameOfAction, int inputCode, int inputDirection)
    {
        throw new UnsupportedOperationException();
    }

    public void registerJoystick(Joystick joystickToMap)
    {
        if(joystickToMap == null)
        {
            throw new IllegalArgumentException("Cannot register a null joystick");
        }

        this.joystickToMapTo = joystickToMap;
        this.idOfJoystick = this.joystickToMapTo.getJoyId();
    }
    
    private void autoregisterJoystick()
    {
        Joystick[] joysticksInSystem = inputManager.getJoysticks();
        
        for(Joystick currentJoystick: joysticksInSystem)
        {
            if(currentJoystick.getName().contains("XBOX") || currentJoystick.getName().contains("Xbox"))
            {
                registerJoystick(currentJoystick);
            }
        }
    }

    public void setJoystickSensitivity(float thresholdToReach) throws IllegalArgumentException
    {
        if(Float.isInfinite(thresholdToReach) || Float.isNaN(thresholdToReach))
        {
            throw new IllegalArgumentException();
        }
        
        inputManager.setAxisDeadZone(thresholdToReach);
    }
}