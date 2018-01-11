package no.hiof.arcade.navigation.fly;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import no.hiof.arcade.exception.NoJoystickExistsException;
import no.hiof.arcade.io.input.InputMapper;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.io.input.XboxControllerInput;
import no.hiof.arcade.main.ARCADE;

/**
 *
 * @author JonAre
 */
public class FlyAppState extends AbstractAppState
{
    private SimpleApplication app;
    private InputManager inputManager;
    AvatarFlyController avatarFlyController;
    
    public FlyAppState()
    {
        avatarFlyController = new AvatarFlyController();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        this.app = (ARCADE) app;
        this.inputManager = app.getInputManager();
        avatarFlyController.registerCamera(app.getCamera());
        
        mapInputToFlyController();
    }

    private void mapInputToFlyController()
    {
        InputMapper inputForFlying = new InputMapper(inputManager, avatarFlyController);

        try
        {
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_MOVE_FORWARD, XboxControllerInput.STICK_LEFT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_UP);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_MOVE_BACKWARD, XboxControllerInput.STICK_LEFT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_DOWN);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_ROTATE_LEFT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_ROTATE_RIGHT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_ROTATE_UP, XboxControllerInput.STICK_RIGHT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_UP);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_ROTATE_DOWN, XboxControllerInput.STICK_RIGHT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_DOWN);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_FLY_UP, XboxControllerInput.TRIGGER_AXIS, XboxControllerInput.TRIGGER_RIGHT);
            inputForFlying.mapJoystickAxis(InputAction.FLY_APPSTATE_FLY_DOWN, XboxControllerInput.TRIGGER_AXIS, XboxControllerInput.TRIGGER_LEFT);
        }
        catch(NoJoystickExistsException ex)
        {
            System.out.println("Could not find any joysticks. Keyboard must be used");
        }
        finally
        {
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_MOVE_FORWARD, KeyInput.KEY_W);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_MOVE_BACKWARD, KeyInput.KEY_S);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_ROTATE_LEFT, KeyInput.KEY_LEFT);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_ROTATE_RIGHT, KeyInput.KEY_RIGHT);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_ROTATE_UP, KeyInput.KEY_UP);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_ROTATE_DOWN, KeyInput.KEY_DOWN);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_FLY_UP, KeyInput.KEY_LSHIFT);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_FLY_DOWN, KeyInput.KEY_LCONTROL);
            inputForFlying.mapKeyboardKey(InputAction.FLY_APPSTATE_PRINT_LOCATION, KeyInput.KEY_P);
        }
    }

    public AvatarFlyController getAvatarFlyController()
    {
        return avatarFlyController;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        
        avatarFlyController.setEnabled(enabled);
    }
}
