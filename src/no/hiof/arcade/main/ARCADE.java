package no.hiof.arcade.main;

import no.hiof.arcade.models.TrackAvatarController;
import no.hiof.arcade.util.ModelChangedListener;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.StereoCamAppState;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.control.CameraControl;
import no.hiof.arcade.navigation.fly.FlyAppState;
import no.hiof.arcade.menu.HudAppState;
import no.hiof.arcade.navigation.lockedpoints.LockedPointAppState;
import no.hiof.arcade.navigation.regularwalk.WalkAppState;
import no.hiof.arcade.util.AvailableAppState;
import no.hiof.arcade.exception.NoJoystickExistsException;
import no.hiof.arcade.io.input.InputMapper;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.io.input.XboxControllerInput;
import no.hiof.arcade.util.NodeName;
import com.jme3.input.KeyInput;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.system.AppSettings;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;
import no.hiof.arcade.navigation.oculuswalk.WalkOculusAppState;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.display.TabletDisplay;
import no.hiof.arcade.util.BetterFadeFilter;
import no.hiof.arcade.util.AnimatedFilterListener;
import no.hiof.arcade.io.loader.ModelLoader;
import no.hiof.arcade.io.loader.ModelLoadingListener;
import no.hiof.arcade.util.LockedPoint;
import no.hiof.arcade.models.BoxCreator;
import no.hiof.arcade.models.RotateModelController;
import no.hiof.arcade.oculus.ArcadeStereoCamAppState;
import no.hiof.arcade.oculus.ArcadeStereoCamControl;
import no.hiof.arcade.settings.SettingParser;
import no.hiof.arcade.settings.SettingsLoader;
import no.hiof.arcade.settings.SettingKey;
import no.hiof.arcade.settings.SettingsType;
import no.hiof.arcade.splash.SplashLoaderScreen;

/**
 *
 * @author JonAre
 */
public class ARCADE extends SimpleApplication implements ActionListener, AnimatedFilterListener, ModelLoadingListener
{
    //Models
    ArrayList<ModelPointsPair<Node, CircularList>> availbableLoadedModels = new ArrayList<ModelPointsPair<Node, CircularList>>();
    ModelPointsPair<Node, CircularList> currentModel;
    //AppStates
    HudAppState hudAppState;
    WalkAppState walkAppState;
    WalkOculusAppState walkOculusAppState;
    FlyAppState flyAppState;
    LockedPointAppState lockedPointAppState;
    StereoCamAppState stereoCamAppState;
    BulletAppState bulletAppState;
    //AppState holder
    AvailableAppState currentNavigationState;
    //Scene
    Node sceneNode;
    RigidBodyControl scenePhysicsController;
    Node tabletModels = new Node(NodeName.TABLET);
    Node tabletAndArmNode = new Node(NodeName.TABLET_BOX);
    Node boxNode = new Node(NodeName.BOX_NODE);
    Node avatarBoxNode = new Node(NodeName.AVATAR_BOX);
    Node lockedPointBoxNode = new Node(NodeName.LOCKED_POINT_BOX);
    
    //Endre navn
    Node offViewNode = new Node(NodeName.OFF_VIEW);
    Node sceneViewNode = new Node(NodeName.SCENE_VIEW_NODE);
    
    //Avatar
    Node avatarNode;
    Node avatarHeadNode;
    BetterCharacterControl avatarPhysicsController;
    //Properties
    public Properties applicationProperties;
    public Properties modelPaths;
    //Fade filter
    BetterFadeFilter fadeFilter;
    FilterPostProcessor filterPostProcessor;
    //Listeners
    ArrayList<ModelChangedListener> modelChangedListeners = new ArrayList<ModelChangedListener>();
    //Enabled's
    boolean debugModeIsEnabled = false;
    boolean useOculusRift = true;
    //Model loading
    int modelsToLoad = 0;
    int failedModelLoads = 0;
    int successfullModelLoads;
    //Splash screen loader
    SplashLoaderScreen splashLoaderScreen;
    final long MINIMUM_DISPLAY_TIME = 5000;
    long startTime = 0;
    //Setup of environment/physics/appsates
    boolean environmentIsSetUp = false;

    public ARCADE()
    {
        super();
    }

    @Override
    public void simpleInitApp()
    {
        super.setDisplayFps(false);
        super.setDisplayStatView(false);
        viewPort.clearScenes();
        
        cam.setFrustumPerspective(60f, (float) cam.getWidth() / cam.getHeight(), 0.1f, 1000f);
        inputManager.setAxisDeadZone(0.5f);
        splashLoaderScreen = new SplashLoaderScreen(assetManager, guiNode, settings.getWidth(), settings.getHeight());

        parseConfigFile();

        parseModelFile();

        initializeAvatarNodes();

        initializeFadeFilter();

        initializeStereoCamAppState();

        initializeCamera();
        
    }

    private void initializeEnvironment()
    {

        loadModels();

        initializeAppStates();
        initializeAppStateToggleInput();
        initializeAvatarControllers();
        initializeLights();

        AvailableAppState startingAppState = SettingParser.parseStartingAppState(applicationProperties.getProperty(SettingKey.START_NAVIGATION_APP_STATE));

        if(startingAppState.equals(AvailableAppState.WALK_OCULUS_APP_STATE) && !useOculusRift)
        {
            startingAppState = AvailableAppState.WALK_APP_STATE;
        }

        setStartAppState(startingAppState);
        toggleAppStates(AvailableAppState.HUD_APP_STATE);
    }

    private void parseConfigFile()
    {
        applicationProperties = SettingsLoader.getSettingsFromFile(SettingsType.APPLICATION_SETTINGS, "config.properties");
        debugModeIsEnabled = Boolean.parseBoolean(applicationProperties.getProperty(SettingKey.DEBUG_MODE_ENABLED));
        useOculusRift = Boolean.parseBoolean(applicationProperties.getProperty(SettingKey.USE_OCULUS_RIFT));
    }

    private void parseModelFile()
    {
        modelPaths = SettingsLoader.getSettingsFromFile(SettingsType.MODEL_PATH_SETTINGS, "modelPathConfig.properties");
    }

    private void loadModels()
    {
        ModelLoader modelLoader = new ModelLoader(assetManager);
        modelLoader.addModelLoadingListener(this);

        String[] paths = getPathsAsStrings();

        availbableLoadedModels = modelLoader.getModelsFromDirectories(paths, false);
        System.out.println("MODELS LOADED: " + availbableLoadedModels.size());
    }

    private String[] getPathsAsStrings()
    {
        Enumeration<?> enumeratedProperties = modelPaths.propertyNames();
        ArrayList<String> paths = new ArrayList<String>();

        while(enumeratedProperties.hasMoreElements())
        {
            String propertyKey = (String) enumeratedProperties.nextElement();
            paths.add(modelPaths.getProperty(propertyKey));
        }

        return paths.toArray(new String[paths.size()]);
    }

    public void initializeAppStates()
    {
        hudAppState = new HudAppState();
        stateManager.attach(hudAppState);

        walkAppState = new WalkAppState();
        stateManager.attach(walkAppState);

        walkOculusAppState = new WalkOculusAppState();
        stateManager.attach(walkOculusAppState);

        if(debugModeIsEnabled)
        {
            flyAppState = new FlyAppState();
            stateManager.attach(flyAppState);
        }

        lockedPointAppState = new LockedPointAppState();
        modelChangedListeners.add(lockedPointAppState);
        stateManager.attach(lockedPointAppState);

        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        disableAllAppStates();
    }

    public void attachSceneNode(Node sceneNodeToAttach)
    {
        sceneNode = sceneNodeToAttach;
        sceneNode.setName(NodeName.SCENE);
        sceneNode.setLocalTranslation(0, 0, 0);
        
        rootNode.attachChild(sceneNode);
    }

    public void initializeAvatarNodes()
    {
        avatarNode = new Node(NodeName.AVATAR);
        avatarNode.setLocalTranslation(0, 10f, 0);

        avatarHeadNode = new Node(NodeName.AVATAR_HEAD);
        avatarHeadNode.setLocalTranslation(0f, 1.7f, 0); 
        
        avatarNode.attachChild(tabletAndArmNode);
        
        avatarNode.attachChild(avatarHeadNode);
        sceneViewNode.attachChild(avatarNode);
    }

    public void initializeScenePhysics()
    {
        scenePhysicsController = new RigidBodyControl(0f);
        sceneNode.addControl(scenePhysicsController);

        bulletAppState.getPhysicsSpace().add(sceneNode);
    }

    private void initializeStereoCamAppState()
    {
        if(useOculusRift)
        {
            stereoCamAppState = new ArcadeStereoCamAppState();
            stateManager.attach(stereoCamAppState);
        }
    }

    public void initializeAvatarControllers()
    {
        avatarPhysicsController = new BetterCharacterControl(0.20f, 1.7f, 30f);
        avatarPhysicsController.setEnabled(false);

        avatarNode.addControl(avatarPhysicsController);
        avatarNode.addControl(walkAppState.getAvatarWalkController());
        avatarNode.addControl(lockedPointAppState.getAvatarLockedPointsController());

        if(debugModeIsEnabled)
        {
            avatarNode.addControl(flyAppState.getAvatarFlyController());
        }

        if(useOculusRift)
        {
            avatarNode.addControl(walkOculusAppState.getAvatarWalkController());
        }

        bulletAppState.getPhysicsSpace().add(avatarNode);
    }

    public void initializeLights()
    {
        AmbientLight ambient = new AmbientLight();
        ambient.setColor(new ColorRGBA(1.1f, 1.1f, 1.1f, 1.0f));
        DirectionalLight sun = new DirectionalLight();
        sun.setDirection(new Vector3f(1.4f, -1.4f, -1.4f));

        rootNode.addLight(ambient);
        rootNode.addLight(sun);
    }

    public void initializeFadeFilter()
    {
        filterPostProcessor = new FilterPostProcessor(assetManager);

        fadeFilter = new BetterFadeFilter(0.5f);
        fadeFilter.addListener(this);

        filterPostProcessor.addFilter(fadeFilter);
        viewPort.addProcessor(filterPostProcessor);
    }

    private void setStartAppState(AvailableAppState appState)
    {
        setCurrentAppState(appState);
    }

    private void setCurrentAppState(AvailableAppState appState)
    {
        currentNavigationState = appState;
    }

    private void initializeCamera()
    {
        if(useOculusRift)
        {
            avatarHeadNode.addControl(stereoCamAppState.getCameraControl());
        }
        else
        {
            initializeOrdinaryCamera();
        }
    }

    public void initializeOrdinaryCamera()
    {
        CameraNode camNode = new CameraNode(NodeName.CAMERA_NODE, cam);

        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(new Vector3f(0, 1.7f, 0));

        Quaternion quat = new Quaternion();
        quat.lookAt(Vector3f.UNIT_Z, Vector3f.UNIT_Y);
        camNode.setLocalRotation(quat);

        avatarNode.attachChild(camNode);

        camNode.setEnabled(true);
        flyCam.setEnabled(false);
    }

    @Override
    public void onAction(String nameOfAction, boolean inputIsActive, float timePerFrame)
    {
        if(!isLegalAction(nameOfAction))
        {
            return;
        }

        if(nameOfAction.equals(InputAction.APP_SELECT_HUD_APP_STATE))
        {
            setCurrentAppState(AvailableAppState.HUD_APP_STATE);
        }
        else if(nameOfAction.equals(InputAction.APP_SELECT_WALK_APP_STATE))
        {
            setCurrentAppState(AvailableAppState.WALK_APP_STATE);
        }
        else if(nameOfAction.equals(InputAction.APP_SELECT_WALK_OCULUS_APP_STATE))
        {
            setCurrentAppState(AvailableAppState.WALK_OCULUS_APP_STATE);
        }
        else if(nameOfAction.equals(InputAction.APP_SELECT_FLY_APP_STATE))
        {
            setCurrentAppState(AvailableAppState.FLY_APP_STATE);
        }
        else if(nameOfAction.equals(InputAction.APP_SELECT_LOCKED_POINT_APP_STATE))
        {
            setCurrentAppState(AvailableAppState.LOCKED_POINT_APP_STATE);
        }

        fadeFilter.fadeToBlack();
    }

    private boolean isLegalAction(String nameOfAction)
    {
        if(!debugModeIsEnabled && nameOfAction.equals(InputAction.APP_SELECT_FLY_APP_STATE))
        {
            return false;
        }

        if(!useOculusRift && nameOfAction.equals(InputAction.APP_SELECT_WALK_OCULUS_APP_STATE))
        {
            return false;
        }

        return true;
    }

    public void toggleAppStates(AvailableAppState appStateToUse)
    {
        disableAllAppStates();

        if(appStateToUse == AvailableAppState.WALK_APP_STATE)
        {
            walkAppState.setEnabled(true);
        }
        else if(appStateToUse == AvailableAppState.WALK_OCULUS_APP_STATE)
        {
            walkOculusAppState.setEnabled(true);
        }
        else if(appStateToUse == AvailableAppState.FLY_APP_STATE && debugModeIsEnabled)
        {
            flyAppState.setEnabled(true);
        }
        else if(appStateToUse == AvailableAppState.LOCKED_POINT_APP_STATE)
        {
            lockedPointAppState.setEnabled(true);
        }
        else if(appStateToUse == AvailableAppState.HUD_APP_STATE)
        {
            hudAppState.setEnabled(true);
        }
    }

    public void disableAllAppStates()
    {
        if(debugModeIsEnabled)
        {
            flyAppState.setEnabled(false);
        }

        walkAppState.setEnabled(false);
        lockedPointAppState.setEnabled(false);
        hudAppState.setEnabled(false);
        walkOculusAppState.setEnabled(false);
    }

    public void setSceneNode(ModelPointsPair<Node, CircularList> newSceneNode)
    {
        if(sceneNode != null)
        {
            sceneViewNode.detachChild(sceneNode);
            deleteScenePhysics();
        }

        currentModel = newSceneNode;
        sceneNode = prepareNodeForAttachement((Node) newSceneNode.getModel().clone());
        setTabletNode(newSceneNode);

        LockedPoint startPoint = (LockedPoint) newSceneNode.getPoints().currentObject();
        avatarNode.setLocalTranslation(startPoint.locationOfPoint());

        Vector3f upVector = new Vector3f(0, 1, 0);
        Vector3f tempViewDirection = startPoint.directionOfPoint().clone();

        if(!tempViewDirection.equals(Vector3f.ZERO))
        {
            tempViewDirection.y = 0;

            Quaternion lookDirection = new Quaternion();
            lookDirection.lookAt(tempViewDirection, upVector);

            if(currentNavigationState == AvailableAppState.WALK_OCULUS_APP_STATE)
            {
                avatarHeadNode.setLocalRotation(lookDirection);
            }
            else if(currentNavigationState == AvailableAppState.FLY_APP_STATE)
            {
                avatarNode.setLocalRotation(lookDirection);
            }
            else if(currentNavigationState == AvailableAppState.WALK_APP_STATE || currentNavigationState == AvailableAppState.LOCKED_POINT_APP_STATE)
            {
                avatarNode.removeControl(avatarPhysicsController);
                avatarNode.setLocalTranslation(startPoint.locationOfPoint());
                avatarPhysicsController.setViewDirection(startPoint.directionOfPoint().clone());
                avatarNode.addControl(avatarPhysicsController);
            }
        }

        initializeScenePhysics();
        sceneViewNode.attachChild(sceneNode);
        sceneViewNode.attachChild(avatarNode);
        viewPort.attachScene(sceneViewNode);
        
        if(stereoCamAppState!=null)
        {
            stereoCamAppState.getRightViewPort().clearScenes();
            stereoCamAppState.getRightViewPort().attachScene(sceneViewNode);
        }
        
        offViewNode.attachChild(sceneViewNode);
        rootNode.attachChild(offViewNode);
        notifyModelChangedListeners();
        toggleAppStates(currentNavigationState);
    }
    
    public void setTabletNode(ModelPointsPair<Node, CircularList> newSceneNode)
    {
        TabletDisplay tabletDisplay = new TabletDisplay(tabletAndArmNode,newSceneNode, assetManager, guiNode, settings, renderManager);

        lockedPointBoxNode.detachAllChildren();
        createAvatarAndTrackBox(newSceneNode);
        
        boxNode.attachChild(avatarBoxNode);
        boxNode.attachChild(lockedPointBoxNode);
        offViewNode.attachChild(boxNode);
        tabletDisplay.setTabletDisplay(offViewNode);
    }

    private void createAvatarAndTrackBox(ModelPointsPair<Node, CircularList> newSceneNode)
    {
        CircularList<LockedPoint> lockedPointList = newSceneNode.getPoints();
        BoxCreator boxCreator = new BoxCreator(assetManager);
        TrackAvatarController trackAvatarController = new TrackAvatarController(avatarNode);

        if(lockedPointList != null)
        {
            for(int x = 0; x < lockedPointList.sizeOfCollection(); x++)
            {
                lockedPointBoxNode.attachChild(boxCreator.getLockedPointBoxes(lockedPointList.getAtIndex(x).locationOfPoint()));
            }
        }

        avatarBoxNode.attachChild(boxCreator.getAvatarTrackBox());
        avatarBoxNode.addControl(trackAvatarController);
    }

    public void detachAndSwitchToHud()
    {
        sceneViewNode.detachChild(sceneNode);
        sceneViewNode.detachChild(avatarNode);
        sceneViewNode.detachAllChildren();
        toggleAppStates(AvailableAppState.HUD_APP_STATE);
    }

    private void deleteScenePhysics()
    {
        if(sceneNode != null && scenePhysicsController != null)
        {
            bulletAppState.getPhysicsSpace().remove(sceneNode);
            sceneNode.removeControl(scenePhysicsController);
        }
    }

    private Node prepareNodeForAttachement(Node nodeToPrepare)
    {
        nodeToPrepare = (Node) currentModel.getModel().clone();

        nodeToPrepare.removeControl(RotateModelController.class);
        nodeToPrepare.setLocalScale(1f, 1f, 1f);
        nodeToPrepare.setLocalRotation(new Quaternion(0, 0, 0, 0));
        nodeToPrepare.rotate(0.6f, 0, 0);
        nodeToPrepare.setName(NodeName.SCENE);
        nodeToPrepare.setLocalTranslation(0, 0, 0);

        return nodeToPrepare;
    }

    private void notifyModelChangedListeners()
    {
        for(ModelChangedListener modelChangedListener : modelChangedListeners)
        {
            modelChangedListener.onModelChanged(currentModel);
        }
    }

    public ModelPointsPair<Node, CircularList> currentModel()
    {
        return currentModel;
    }

    public ArrayList<ModelPointsPair<Node, CircularList>> availableModels()
    {
        return this.availbableLoadedModels;
    }

    public AppSettings getAppSettings()
    {
        return this.settings;
    }

    @Override
    public Node getGuiNode()
    {
        return this.guiNode;
    }

    private void initializeAppStateToggleInput()
    {
        InputMapper toggleMapper = new InputMapper(inputManager, this);
        try
        {
            if(useOculusRift)
            {
                toggleMapper.mapJoystickButton(InputAction.APP_SELECT_WALK_OCULUS_APP_STATE, XboxControllerInput.BUTTON_X);
            }

            toggleMapper.mapJoystickButton(InputAction.APP_SELECT_WALK_APP_STATE, XboxControllerInput.BUTTON_A);
            toggleMapper.mapJoystickButton(InputAction.APP_SELECT_FLY_APP_STATE, XboxControllerInput.BUTTON_Y);
            toggleMapper.mapJoystickButton(InputAction.APP_SELECT_LOCKED_POINT_APP_STATE, XboxControllerInput.BUTTON_B);
//            toggleMapper.mapJoystickButton(InputAction.APP_SELECT_HUD_APP_STATE, XboxControllerInput.BUTTON_START);
            toggleMapper.mapJoystickButton(InputAction.EXIT_ARCADE, XboxControllerInput.BUTTON_BACK);
        }
        catch(NoJoystickExistsException ex)
        {
            System.out.println("Could not find any joysticks. Keyboard must be used");
        }
        finally //Always map keyboard for fall-back input
        {
            if(useOculusRift)
            {
                toggleMapper.mapKeyboardKey(InputAction.APP_SELECT_WALK_OCULUS_APP_STATE, KeyInput.KEY_H);
            }

            toggleMapper.mapKeyboardKey(InputAction.APP_SELECT_WALK_APP_STATE, KeyInput.KEY_G);
            toggleMapper.mapKeyboardKey(InputAction.APP_SELECT_FLY_APP_STATE, KeyInput.KEY_J);
            toggleMapper.mapKeyboardKey(InputAction.APP_SELECT_LOCKED_POINT_APP_STATE, KeyInput.KEY_K);
//            toggleMapper.mapKeyboardKey(InputAction.APP_SELECT_HUD_APP_STATE, KeyInput.KEY_SPACE);
            toggleMapper.mapKeyboardKey(InputAction.EXIT_ARCADE, KeyInput.KEY_ESCAPE);
        }
    }

    @Override
    public void onAnimationFinished(int direction)
    {
        if(direction == 1)
        {
            System.out.println("Fade In");
        }
        else if(direction == -1)
        {
            System.out.println("Fade Out");
            toggleAppStates(currentNavigationState);

            fadeFilter.fadeFromBlack();
        }
    }
    

    @Override
    public void simpleUpdate(float tpf)
    {
        if(startTime == 0 && !environmentIsSetUp)
        {
            startTime = System.currentTimeMillis();
        }

        super.simpleUpdate(tpf);
        

        if(modelLoadingIsFinished() && !environmentIsSetUp && System.currentTimeMillis() - startTime > MINIMUM_DISPLAY_TIME)
        {
            //Start up environment
            initializeEnvironment();
            splashLoaderScreen.setEnabled(false);
            environmentIsSetUp = true;
        }
    }

    public boolean modelLoadingIsFinished()
    {
        return (failedModelLoads + successfullModelLoads == modelsToLoad);
    }

    @Override
    public void modelsToload(int modelsToLoad)
    {
        System.out.println("Trying to load " + modelsToLoad + " models");
    }

    @Override
    public void modelLoadFailed(String modelName)
    {
        System.out.println("Failed to load model " + modelName);
    }

    @Override
    public void modelLoadSuccess(String modelName)
    {
        System.out.println("Loaded model " + modelName + " successfully");
    }
}