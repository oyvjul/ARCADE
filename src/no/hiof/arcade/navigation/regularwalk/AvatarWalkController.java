package no.hiof.arcade.navigation.regularwalk;

import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.StereoCameraControl;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.util.NodeName;
import no.hiof.arcade.oculus.ArcadeStereoCamControl;

/**
 *
 * @author JonAre
 */
public class AvatarWalkController extends AbstractControl implements ActionListener
{
    protected BetterCharacterControl avatarPhysicsController;
    protected Vector3f directionToView = new Vector3f(0, 0, 1);
    protected Vector3f directionToWalk = new Vector3f(0, 0, 0);
    protected Vector3f avatarForwardDirection = new Vector3f();
    protected Vector3f avatarLeftDirection = new Vector3f();
    protected boolean moveForward = false, moveBackward = false, moveRight = false, moveLeft = false;
    protected boolean rotateRight = false, rotateLeft = false, rotateUp = false, rotateDown = false;
    protected float walkSpeed = 2.0f;
    private CameraNode cameraNode;
    private boolean fromSetEnabled = false;
    private Vector3f temporaryDirectionToView;

    public AvatarWalkController(BetterCharacterControl avatarPhysicsController)
    {
        super();

        this.avatarPhysicsController = avatarPhysicsController;
    }

    public AvatarWalkController()
    {
        super();
    }

    @Override
    protected void controlUpdate(float timePerFrame)
    {
        if(fromSetEnabled)
        {
            resetBodyToHeadRotation();

            spatial.lookAt(temporaryDirectionToView, new Vector3f(0, 1, 0));

            fromSetEnabled = false;
        }

//        avatarForwardDirection.set(spatial.getWorldRotation().mult(Vector3f.UNIT_Z));
//        avatarLeftDirection.set(spatial.getWorldRotation().mult(Vector3f.UNIT_X));
        directionToWalk.set(0, 0, 0);

        if(moveForward || moveBackward)
        {
            avatarForwardDirection.set(spatial.getWorldRotation().mult(Vector3f.UNIT_Z));
        }

        if(moveForward)
        {
            directionToWalk.addLocal(avatarForwardDirection.mult(walkSpeed));
        }
        else if(moveBackward)
        {
            directionToWalk.addLocal(avatarForwardDirection.mult(-walkSpeed));
        }

        if(moveLeft || moveRight)
        {
            avatarLeftDirection.set(spatial.getWorldRotation().mult(Vector3f.UNIT_X));
        }

        if(moveLeft)
        {
            directionToWalk.addLocal(avatarLeftDirection.mult(walkSpeed));
        }
        else if(moveRight)
        {
            directionToWalk.addLocal(avatarLeftDirection.mult(-walkSpeed));
        }

        if(rotateLeft)
        {
            Quaternion rotationToLeft = new Quaternion().fromAngleAxis(FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_Y);
            rotationToLeft.multLocal(directionToView);
        }
        else if(rotateRight)
        {
            Quaternion rotationToRight = new Quaternion().fromAngleAxis(-FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_Y);
            rotationToRight.multLocal(directionToView);
        }

        if(cameraNode != null)
        {
            if(rotateUp && cameraNode.getLocalRotation().getX() > -0.6030258f)
            {
                Quaternion rotationUp = new Quaternion().fromAngleAxis(-FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_X);
                cameraNode.rotate(rotationUp);
            }
            else if(rotateDown && cameraNode.getLocalRotation().getX() < 0.3684704)
            {
                Quaternion rotationDown = new Quaternion().fromAngleAxis(FastMath.PI / 4 * timePerFrame, Vector3f.UNIT_X);
                cameraNode.rotate(rotationDown);
            }

        }

        avatarPhysicsController.setViewDirection(directionToView);
        avatarPhysicsController.setWalkDirection(directionToWalk);

    }

    @Override
    public void onAction(String nameOfActionToPerform, boolean isPressed, float timePerFrame)
    {
        if(isEnabled())
        {
            if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_ROTATE_LEFT))
            {
                rotateLeft = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_ROTATE_RIGHT))
            {
                rotateRight = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_ROTATE_UP))
            {
                rotateUp = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_ROTATE_DOWN))
            {
                rotateDown = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_MOVE_FORWARD))
            {
                moveForward = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_MOVE_BACKWARD))
            {
                moveBackward = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_MOVE_LEFT))
            {
                moveLeft = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_MOVE_RIGHT))
            {
                moveRight = isPressed;
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_JUMP))
            {
                avatarPhysicsController.jump();
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_PRINT_POSITION))
            {
                printPositionAndRotation();
            }
            else if(nameOfActionToPerform.equals(InputAction.WALK_APPSTATE_RESET_AVATAR))
            {
                System.out.println("RESETTING");
                resetBodyToHeadRotation();
            }
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
            directionToView = avatarPhysicsController.getViewDirection();
            temporaryDirectionToView = avatarPhysicsController.getViewDirection().clone();
            avatarPhysicsController.setEnabled(enabled);

            fromSetEnabled = enabled;
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

    protected void printPositionAndRotation()
    {
        Vector3f spatialLocation = spatial.getLocalTranslation();
        Vector3f spatialRotation = directionToView;

        System.out.printf("%s,%s,%s", spatialLocation.x, spatialLocation.y, spatialLocation.z);
        System.out.print("#");
        System.out.printf("%s,%s,%s", spatialRotation.getX(), spatialRotation.getY(), spatialRotation.getZ());
        System.out.print("\n");
    }

    private void resetBodyToHeadRotation()
    {
        Node tempNode = (Node) spatial;
        Node headNode = (Node) tempNode.getChild(NodeName.AVATAR_HEAD);
        Node tabletNode = (Node) tempNode.getChild(NodeName.TABLET_BOX);

        spatial.removeControl(avatarPhysicsController);
        AbstractControl headControl = headNode.getControl(ArcadeStereoCamControl.class);
        
        if(headControl != null)
        {
            setWorldOrientation(spatial, headNode.getControl(StereoCameraControl.class).getCalculatedWorldRotation());
        }

        headNode.setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
        tabletNode.setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_Y));
        spatial.addControl(avatarPhysicsController);
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