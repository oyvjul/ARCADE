package no.hiof.arcade.navigation.lockedpoints;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import no.hiof.arcade.util.BetterFadeFilter;
import no.hiof.arcade.util.AnimatedFilterListener;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.LockedPoint;
import no.hiof.arcade.util.NodeName;

/**
 *
 * @author JonAre
 */
public class AvatarLockedPointsController extends AbstractControl implements ActionListener, AnimatedFilterListener
{
    AssetManager assetManager = null;
    ViewPort viewPort = null;
    boolean rotateRight = false, rotateLeft = false, rotateUp = false, rotateDown = false;
    boolean moveToNextPoint = false, moveToPreviousPoint = false, finishedWithFadingToBlack = false;
    boolean moveToNext = false, moveToPrevious = false;
    BetterCharacterControl avatarPhysicsController;
    BetterFadeFilter fadeFilter;
    CircularList<LockedPoint> availableLockedPoints;
    Vector3f viewDirection = new Vector3f(0, 0, 1);
    CameraNode cameraNode;
    long timeSinceLastMove = 0, minimumDebounceTime = 800;

    public AvatarLockedPointsController()
    {
        super();
    }

    public AvatarLockedPointsController(ViewPort viewPort, AssetManager assetmanager)
    {
        super();

        this.viewPort = viewPort;
        this.assetManager = assetmanager;
    }

    @Override
    protected void controlUpdate(float timePerFrame)
    {
        if(rotateLeft)
        {
            Quaternion rotationToLeft = new Quaternion().fromAngleAxis(FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_Y);
            rotationToLeft.multLocal(viewDirection);
        }
        else if(rotateRight)
        {
            Quaternion rotationToRight = new Quaternion().fromAngleAxis(-FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_Y);
            rotationToRight.multLocal(viewDirection);
        }

        if(availableLockedPoints != null && availableLockedPoints.sizeOfCollection() > 1)
        {
            if(moveToNextPoint)
            {
                if(System.currentTimeMillis() - timeSinceLastMove > minimumDebounceTime && !finishedWithFadingToBlack)
                {
                    moveToNext = true;
                    fadeFilter.fadeToBlack();
                    timeSinceLastMove = System.currentTimeMillis();
                }
            }
            else if(moveToPreviousPoint)
            {
                if(System.currentTimeMillis() - timeSinceLastMove > minimumDebounceTime && !finishedWithFadingToBlack)
                {
                    moveToPrevious = true;
                    fadeFilter.fadeToBlack();
                    timeSinceLastMove = System.currentTimeMillis();
                }
            }
        }

        if(cameraNode != null)
        {
            if(rotateUp && cameraNode.getLocalRotation().getX() > -0.6030258f)
            {
                System.out.println(cameraNode.getLocalRotation());
                Quaternion rotationUp = new Quaternion().fromAngleAxis(-FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_X);
                cameraNode.rotate(rotationUp);
            }
            else if(rotateDown && cameraNode.getLocalRotation().getX() < 0.3684704)
            {
                System.out.println(cameraNode.getLocalRotation());
                Quaternion rotationDown = new Quaternion().fromAngleAxis(FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_X);
                cameraNode.rotate(rotationDown);
            }

        }

        avatarPhysicsController.setViewDirection(viewDirection);

        if(finishedWithFadingToBlack)
        {
            LockedPoint currentLockedPoint = null;

            if(moveToNext)
            {
                moveToNext = false;
                currentLockedPoint = availableLockedPoints.nextObject();
            }
            else if(moveToPrevious)
            {
                moveToPrevious = false;
                currentLockedPoint = availableLockedPoints.previousObject();
            }

            if(currentLockedPoint != null)
            {
                moveAvatarToPoint(currentLockedPoint);
                finishedWithFadingToBlack = false;
            }

            fadeFilter.fadeFromBlack();
        }
    }

    private void moveAvatarToPoint(LockedPoint point)
    {
        spatial.removeControl(avatarPhysicsController);
        spatial.setLocalTranslation(point.locationOfPoint());
        viewDirection = point.directionOfPoint();
        spatial.addControl(avatarPhysicsController);

    }

    @Override
    public void onAction(String nameOfActionToPerform, boolean isPressed, float timePerFrame)
    {
        if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_RIGHT))
        {
            rotateRight = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_LEFT))
        {
            rotateLeft = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_UP))
        {
            rotateUp = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_ROTATE_DOWN))
        {
            rotateDown = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_NEXT_POINT))
        {
            moveToNextPoint = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.LOCKEDPOINT_APPSTATE_MOVE_TO_PREVIOUS_POINT))
        {
            moveToPreviousPoint = isPressed;
        }
    }

    public void registerFadeFilter(BetterFadeFilter fadeFilter)
    {
        this.fadeFilter = fadeFilter;
    }

    @Override
    public void onAnimationFinished(int direction)
    {
        if(direction == -1)
        {
            finishedWithFadingToBlack = true;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        if(avatarPhysicsController != null)
        {
            viewDirection = avatarPhysicsController.getViewDirection();
            avatarPhysicsController.setEnabled(enabled);
        }
    }

    @Override
    public void setSpatial(Spatial node)
    {
        super.setSpatial(node);

        if(spatial != null)
        {
            avatarPhysicsController = spatial.getControl(BetterCharacterControl.class);

            Node tempNode = (Node) spatial;
            cameraNode = (CameraNode) tempNode.getChild(NodeName.CAMERA_NODE);
        }
    }

    public void registerLockedPoints(CircularList<LockedPoint> lockedPoints)
    {
        availableLockedPoints = lockedPoints;
    }

    public void registerViewPort(ViewPort viewPort)
    {
        this.viewPort = viewPort;
    }

    public void registerAssetmanager(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }

    public void initializeFadeFilter()
    {
        if(fadeFilter == null)
        {
            FilterPostProcessor filterPostProcessor = new FilterPostProcessor(assetManager);

            fadeFilter = new BetterFadeFilter(0.5f);
            fadeFilter.addListener(this);

            filterPostProcessor.addFilter(fadeFilter);
            viewPort.addProcessor(filterPostProcessor);
        }
    }

    //Copied from http://hub.jmonkeyengine.org/forum/topic/setting-world-rotation/ @ 06.05.2014
    public static void setWorldOrientation(Spatial spatialToRotate, Quaternion worldRotationToRotateTo)
    {
        Spatial spatialParent = spatialToRotate.getParent();
        Quaternion newLocalRotation;

        if(spatialParent != null)
        {
            newLocalRotation = spatialParent.getWorldRotation().inverse();
            newLocalRotation.multLocal(worldRotationToRotateTo);
            newLocalRotation.normalizeLocal();
        }
        else
        {
            newLocalRotation = worldRotationToRotateTo.clone();
        }

        spatialToRotate.setLocalRotation(newLocalRotation);

        RigidBodyControl rigidBodyControl = spatialToRotate.getControl(RigidBodyControl.class);
        if(rigidBodyControl != null)
        {
            rigidBodyControl.setPhysicsRotation(worldRotationToRotateTo.clone());
        }
    }
}
