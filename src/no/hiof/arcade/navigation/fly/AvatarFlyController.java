package no.hiof.arcade.navigation.fly;

import com.jme3.input.controls.ActionListener;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import no.hiof.arcade.io.input.InputAction;
import no.hiof.arcade.util.NodeName;

/**
 *
 * @author JonAre
 */
public class AvatarFlyController extends AbstractControl implements ActionListener
{
    Vector3f avatarForwardDirection = new Vector3f();
    Vector3f avatarUpDirection = new Vector3f();
    Vector3f avatarLeftDirection = new Vector3f();
    boolean moveForward = false, moveBackward = false, moveUp = false, moveDown = false;
    boolean rotateRight = false, rotateLeft = false, rotateUp = false, rotateDown = false;
    boolean rotateToNormal = false;
    float flySpeed = 6;
    float movementSpeed = 6;
    Camera camera;
    CameraNode camNode = null;

    public AvatarFlyController(Camera camera)
    {
        super();

        this.camera = camera;
    }

    public AvatarFlyController()
    {
        super();
    }

    @Override
    protected void controlUpdate(float tpf)
    {
        if(moveForward)
        {
            spatial.move(camera.getDirection().mult(movementSpeed * tpf));
        }
        else if(moveBackward)
        {
            spatial.move(camera.getDirection().mult(-movementSpeed * tpf));
        }

        if(rotateLeft)
        {
            spatial.rotate(new Quaternion().fromAngleAxis((FastMath.HALF_PI) * tpf, Vector3f.UNIT_Y));
        }
        else if(rotateRight)
        {
            spatial.rotate(new Quaternion().fromAngleAxis(-(FastMath.HALF_PI) * tpf, Vector3f.UNIT_Y));
        }

        if(camNode != null)
        {
            if(rotateUp)
            {
                camNode.rotate(new Quaternion().fromAngleAxis(-(FastMath.HALF_PI) * tpf, Vector3f.UNIT_X));
            }
            else if(rotateDown)
            {
                camNode.rotate(new Quaternion().fromAngleAxis((FastMath.HALF_PI) * tpf, Vector3f.UNIT_X));
            }
        }

        if(moveUp)
        {
            spatial.move(0, flySpeed * tpf, 0);
        }
        else if(moveDown)
        {
            spatial.move(0, -flySpeed * tpf, 0);
        }
        
        if(rotateToNormal)
        {
            rotateToNormal=false;
            
            if(camNode!=null)
            {
                camNode.setLocalRotation(new Quaternion().fromAngleAxis(0, Vector3f.UNIT_X));
            }
        }
    }

    @Override
    public void onAction(String nameOfActionToPerform, boolean isPressed, float timePerFrame)
    {
        if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_MOVE_FORWARD))
        {
            moveForward = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_MOVE_BACKWARD))
        {
            moveBackward = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_ROTATE_UP))
        {
            rotateUp = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_ROTATE_DOWN))
        {
            rotateDown = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_ROTATE_LEFT))
        {
            rotateLeft = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_ROTATE_RIGHT))
        {
            rotateRight = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_FLY_UP))
        {
            moveUp = isPressed;
        }
        else if(nameOfActionToPerform.equals(InputAction.FLY_APPSTATE_FLY_DOWN))
        {
            moveDown = isPressed;
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
    }

    public void registerCamera(Camera camera)
    {
        this.camera = camera;
    }

    @Override
    public void setSpatial(Spatial spatial)
    {
        super.setSpatial(spatial);

        Node tempNode = (Node) spatial;
        camNode = (CameraNode) tempNode.getChild(NodeName.CAMERA_NODE);
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        if(!enabled)
        {
            rotateToNormal=true;
        }
    }
}
