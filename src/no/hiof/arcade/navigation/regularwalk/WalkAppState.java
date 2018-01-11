package no.hiof.arcade.navigation.regularwalk;

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
public class WalkAppState extends AbstractAppState
{
    private SimpleApplication app;
    private InputManager inputManager;
    AvatarWalkController avatarWalkController;

    public WalkAppState()
    {
        avatarWalkController = new AvatarWalkController();
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        this.app = (ARCADE) app;
        this.inputManager = app.getInputManager();

        mapInputToWalkController();
    }

    private void mapInputToWalkController()
    {
        InputMapper inputForWalking = new InputMapper(inputManager, avatarWalkController);

        try
        {
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_MOVE_FORWARD, XboxControllerInput.STICK_LEFT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_UP);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_MOVE_BACKWARD, XboxControllerInput.STICK_LEFT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_DOWN);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_MOVE_LEFT, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_MOVE_RIGHT, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_ROTATE_LEFT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_ROTATE_RIGHT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_ROTATE_UP, XboxControllerInput.STICK_RIGHT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_UP);
            inputForWalking.mapJoystickAxis(InputAction.WALK_APPSTATE_ROTATE_DOWN, XboxControllerInput.STICK_RIGHT_VERTICAL_AXIS, XboxControllerInput.STICK_DIRECTION_DOWN);
            inputForWalking.mapJoystickButton(InputAction.WALK_APPSTATE_RESET_AVATAR, XboxControllerInput.BUTTON_BUMPER_RIGHT);
        }
        catch(NoJoystickExistsException ex)
        {
            System.out.println("Could not find any joysticks. Keyboard must be used");
        }
        finally
        {
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_MOVE_FORWARD, KeyInput.KEY_W);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_MOVE_BACKWARD, KeyInput.KEY_S);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_MOVE_LEFT, KeyInput.KEY_A);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_MOVE_RIGHT, KeyInput.KEY_D);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_ROTATE_LEFT, KeyInput.KEY_LEFT);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_ROTATE_RIGHT, KeyInput.KEY_RIGHT);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_ROTATE_UP, KeyInput.KEY_UP);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_ROTATE_DOWN, KeyInput.KEY_DOWN);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_PRINT_POSITION, KeyInput.KEY_P);
            inputForWalking.mapKeyboardKey(InputAction.WALK_APPSTATE_RESET_AVATAR, KeyInput.KEY_R);
        }
    }

    public AvatarWalkController getAvatarWalkController()
    {
        return avatarWalkController;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        avatarWalkController.setEnabled(enabled);
    }
}
