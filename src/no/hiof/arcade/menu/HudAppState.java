package no.hiof.arcade.menu;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimationFactory;
import com.jme3.animation.LoopMode;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Transform;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.display.HudDisplay;
import no.hiof.arcade.exception.NoJoystickExistsException;
import no.hiof.arcade.io.input.InputMapper;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.io.input.XboxControllerInput;
import no.hiof.arcade.main.ARCADE;
import no.hiof.arcade.util.NodeName;

/**
 *
 * @author JonAre
 */
public class HudAppState extends AbstractAppState implements ActionListener
{
    private ARCADE app;
    private InputManager inputManager;
    private AssetManager assetManager;
    private AppSettings applicationSettings;
    private RenderManager renderManager;
    private int currentModelIndex = 0;
    private double lastActionDebounce = 0, minimumActionDebounceTime = 300;
    private static HudDisplay hudDisplay;
    public ArrayList<ModelPointsPair<Node, CircularList>> availableModels;
    private Node modelNode = new Node(NodeName.MODELS);
    private Node guiNode;
    private AnimChannel startAnimation;
    private AnimControl animationControl;
    private Transform[] storePosition;
    private AnimationFactory animationFactory;

    public HudAppState()
    {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app)
    {
        super.initialize(stateManager, app);
        this.app = (ARCADE) app;
        this.inputManager = app.getInputManager();
        this.availableModels = this.app.availableModels();
        this.assetManager = this.app.getAssetManager();
        this.guiNode = this.app.getGuiNode();
        this.applicationSettings = this.app.getAppSettings();
        this.renderManager = this.app.getRenderManager();

        createAndDisplayHUD();
        startAndSetAnimation();
        mapInputToModelSelection();
    }

    private void createAndDisplayHUD()
    {
        hudDisplay = new HudDisplay(modelNode, availableModels, assetManager, guiNode, applicationSettings, renderManager);
        hudDisplay.displayModels();
        hudDisplay.setHudDisplay();
        hudDisplay.createHudTextName();
        hudDisplay.createHudTextNumber();
    }

    private void mapInputToModelSelection()
    {
        InputMapper inputForSelection = new InputMapper(inputManager, this);

        try
        {
            inputForSelection.mapJoystickAxis(InputAction.HUD_APPSTATE_VIEW_NEXT_MODEL, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_RIGHT);
            inputForSelection.mapJoystickAxis(InputAction.HUD_APPSTATE_VIEW_PREVIOUS_MODEL, XboxControllerInput.STICK_LEFT_HORIZONTAL_AXIS, XboxControllerInput.STICK_DIRECTION_LEFT);
            inputForSelection.mapJoystickButton(InputAction.HUD_APPSTATE_SELECT_CURRENT_MODEL, XboxControllerInput.BUTTON_A);
            inputForSelection.mapJoystickButton(InputAction.HUD_APPSTATE_EXIT_MODEL_SELECTOR, XboxControllerInput.BUTTON_BUMPER_LEFT);
        }
        catch(NoJoystickExistsException ex)
        {
            System.out.println("Could not find any joysticks. Keyboard must be used");
        }
        finally
        {
            inputForSelection.mapKeyboardKey(InputAction.HUD_APPSTATE_VIEW_NEXT_MODEL, KeyInput.KEY_D);
            inputForSelection.mapKeyboardKey(InputAction.HUD_APPSTATE_VIEW_PREVIOUS_MODEL, KeyInput.KEY_A);
            inputForSelection.mapKeyboardKey(InputAction.HUD_APPSTATE_SELECT_CURRENT_MODEL, KeyInput.KEY_RETURN);
            inputForSelection.mapKeyboardKey(InputAction.HUD_APPSTATE_EXIT_MODEL_SELECTOR, KeyInput.KEY_DELETE);
        }
    }

    @Override
    public void onAction(String nameOfAction, boolean isPressed, float timePerFrame)
    {
        if(nameOfAction.equals(InputAction.HUD_APPSTATE_SELECT_CURRENT_MODEL))
        {
            if(checkActionDebounce())
            {
                if(isEnabled() == true)
                {
                    hudDisplay.detachHudFromGuiNode();
                    setCurrentIndexAsSceneModel();
                    hudDisplay.stopAllAvailableModelsRotation();
                }
            }
        }
        else if(nameOfAction.equals(InputAction.HUD_APPSTATE_EXIT_MODEL_SELECTOR))
        {
            if(checkActionDebounce())
            {
                if(isEnabled() == false)
                {
                    app.detachAndSwitchToHud();
                    hudDisplay.startAllAvailableModelsRotation();
                    hudDisplay.attachHudToGuiNode();
                }
            }
        }
        else if(nameOfAction.equals(InputAction.HUD_APPSTATE_VIEW_NEXT_MODEL))
        {
            if(checkActionDebounce() && (currentModelIndex < availableModels.size() - 1))
            {
                if(isEnabled() == true)
                {
                    currentModelIndex++;
                    switchRightAnimation();
                }
            }
        }
        else if(nameOfAction.equals(InputAction.HUD_APPSTATE_VIEW_PREVIOUS_MODEL))
        {
            if(checkActionDebounce() && currentModelIndex > 0)
            {
                if(isEnabled() == true)
                {
                    currentModelIndex--;
                    switchLeftAnimation();
                }
            }
        }
    }

    private boolean checkActionDebounce()
    {
        if(((System.currentTimeMillis() - lastActionDebounce) > minimumActionDebounceTime))
        {
            lastActionDebounce = System.currentTimeMillis();
            return true;
        }

        return false;
    }

    private void setCurrentIndexAsSceneModel()
    {
        app.setSceneNode(availableModels.get(currentModelIndex));
        availableModels.get(currentModelIndex).getModel().updateGeometricState();
    }

    private void startAndSetAnimation()
    {
        animationFactory = new AnimationFactory(1, "anim", 5);
        storePosition = new Transform[2];
        storePosition[0] = Transform.IDENTITY;
        storePosition[1] = new Transform(new Vector3f(0, 0, 0));

        animationFactory.addKeyFrameTransform(0, storePosition[0]);
        animationFactory.addKeyFrameTransform(1, storePosition[1]);

        animationControl = new AnimControl();
        animationControl.addAnim(animationFactory.buildAnimation());

        modelNode.addControl(animationControl);

        startAnimation = animationControl.createChannel();
        startAnimation.setLoopMode(LoopMode.DontLoop);
    }

    private void switchRightAnimation()
    {
        storePosition[0] = storePosition[1];
        storePosition[1] = new Transform(storePosition[0].getTranslation().add(new Vector3f(5f, 0, 0)));

        animationFactory.addKeyFrameTransform(0, storePosition[0]);
        animationFactory.addKeyFrameTransform(1, storePosition[1]);
        animationControl.addAnim(animationFactory.buildAnimation());

        startAnimation = animationControl.createChannel();

        startAnimation.setAnim("anim", 0.0f);
        startAnimation.setLoopMode(LoopMode.DontLoop);
    }

    private void switchLeftAnimation()
    {
        storePosition[0] = storePosition[1];
        storePosition[1] = new Transform(storePosition[0].getTranslation().add(new Vector3f(-5f, 0, 0)));

        animationFactory.addKeyFrameTransform(0, storePosition[0]);
        animationFactory.addKeyFrameTransform(1, storePosition[1]);
        animationControl.addAnim(animationFactory.buildAnimation());

        startAnimation = animationControl.createChannel();

        startAnimation.setAnim("anim", 0.0f);
        startAnimation.setLoopMode(LoopMode.DontLoop);
    }

    @Override
    public void update(float timePerFrame)
    {
        modelNode.updateLogicalState(timePerFrame);
        modelNode.updateGeometricState();
        if(availableModels != null && !availableModels.isEmpty())
        {
            hudDisplay.setGuiNumberText(currentModelIndex + 1, availableModels.size());
            hudDisplay.setGuiNameText(availableModels.get(currentModelIndex).getModel().getName());
        }
        else
        {
            hudDisplay.setGuiErrorText("Cannot find any models");
        }
    }
}
