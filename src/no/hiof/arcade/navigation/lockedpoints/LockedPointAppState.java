package no.hiof.arcade.navigation.lockedpoints;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import no.hiof.arcade.util.ModelChangedListener;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.exception.NoJoystickExistsException;
import no.hiof.arcade.io.input.InputMapper;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.io.input.XboxControllerInput;
import no.hiof.arcade.util.LockedPoint;
import no.hiof.arcade.main.ARCADE;

/**
 *
 * @author JonAre
 */
public class LockedPointAppState extends AbstractAppState implements ModelChangedListener
{
    private ARCADE app;
    private InputManager inputManager;
    private AssetManager assetManager;
    private ViewPort viewPort;
    
    AvatarLockedPointsController avatarLockedPointsController;
    ModelPointsPair<Node, CircularList> currentModel;

    public LockedPointAppState()
    {
        avatarLockedPointsController = new AvatarLockedPointsController();
    }
    
    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        
        this.app = (ARCADE) app;
        this.inputManager = app.getInputManager();
        this.assetManager = app.getAssetManager();
        this.viewPort = app.getViewPort();
        this.currentModel = this.app.currentModel();
        
        avatarLockedPointsController.registerAssetmanager(assetManager);
        avatarLockedPointsController.registerViewPort(viewPort);
        avatarLockedPointsController.initializeFadeFilter();
        
        mapInputToLockedPointController();
    }

    private void mapInputToLockedPointController()
    {
        InputMapper inputForWalking = new InputMapper(inputManager, avatarLockedPointsController);
        try
        {
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_NEXT_POINT, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_PREVIOUS_POINT, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_LEFT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_RIGHT, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_UP, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_UP);
            inputForWalking.mapJoystickAxis(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_DOWN, XboxControllerInput.STICK_RIGHT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_DOWN);
        }
        catch(NoJoystickExistsException ex)
        {
            System.out.println("Could not find any joysticks. Keyboard must be used");
        }
        finally
        {
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_NEXT_POINT, KeyInput.KEY_D);
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_PREVIOUS_POINT, KeyInput.KEY_A);
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_LEFT, KeyInput.KEY_LEFT);
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_RIGHT, KeyInput.KEY_RIGHT);
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_UP, KeyInput.KEY_UP);
            inputForWalking.mapKeyboardKey(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_DOWN, KeyInput.KEY_DOWN);
        }
    }

    public AvatarLockedPointsController getAvatarLockedPointsController()
    {
        return avatarLockedPointsController;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);
        avatarLockedPointsController.setEnabled(enabled);
    }

    @Override
    public void onModelChanged(ModelPointsPair modelPair)
    {
        this.currentModel = modelPair;
        avatarLockedPointsController.registerLockedPoints((CircularList<LockedPoint>) modelPair.getPoints());
    }
}
