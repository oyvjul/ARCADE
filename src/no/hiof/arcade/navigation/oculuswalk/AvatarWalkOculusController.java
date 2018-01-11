package no.hiof.arcade.navigation.oculuswalk;

import no.hiof.arcade.navigation.regularwalk.AvatarWalkController;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.StereoCameraControl;
import no.hiof.arcade.util.NodeName;

/**
 *
 * @author JonAre
 */
public class AvatarWalkOculusController extends AvatarWalkController
{
    protected Node headNode;
    protected Node secondaryRotationNode;
    protected Node thirdNode;
    protected StereoCameraControl cameraControl;

    public AvatarWalkOculusController()
    {
        super();
    }

    public AvatarWalkOculusController(BetterCharacterControl avatarPhysicsControl)
    {
        super(avatarPhysicsControl);
    }

    public AvatarWalkOculusController(BetterCharacterControl avatarPhysicsControl, Node headNode, StereoCameraControl cameraControl)
    {
        super(avatarPhysicsControl);

        this.headNode = headNode;
    }

    @Override
    protected void controlUpdate(float timePerFrame)
    {
        Quaternion headRotation = cameraControl.getCalculatedWorldRotation(); //Kommentert ut frem til testing. Må endres til å bruke wrapper.
        Quaternion cameraRotation = cameraControl.getCamera().getRotation();
        Vector3f headDirection = headRotation.mult(Vector3f.UNIT_Z.negate());
        headDirection.setY(0);
        Vector3f headLeft = headRotation.mult(Vector3f.UNIT_X.negate());
        headLeft.setY(0);
        directionToWalk = new Vector3f();

        if(rotateRight)
        {
            headNode.rotate(new Quaternion().fromAngleAxis(-(FastMath.HALF_PI) * timePerFrame, Vector3f.UNIT_Y));
        }
        if(rotateLeft)
        {
            headNode.rotate(new Quaternion().fromAngleAxis((FastMath.HALF_PI) * timePerFrame, Vector3f.UNIT_Y));
        }
        if(moveBackward)
        {
            directionToWalk.addLocal(headDirection.mult(2));
        }
        if(moveForward)
        {
            directionToWalk.addLocal(headDirection.mult(2).negate());
        }
        if(moveRight)
        {
            directionToWalk.addLocal(headLeft);
        }
        if(moveLeft)
        {
            directionToWalk.addLocal(headLeft.negate());
        }

        setWorldOrientation(secondaryRotationNode, cameraRotation);
        Quaternion restrictedRotation = restrictRotationOfSecondaryNode();

        secondaryRotationNode.setLocalRotation(restrictedRotation);
        avatarPhysicsController.setWalkDirection(directionToWalk);
        //thirdNode.setLocalTranslation(directionToWalk);
    }

    public Quaternion restrictRotationOfSecondaryNode()
    {
        Quaternion tempRotation = secondaryRotationNode.getLocalRotation().clone();

        Vector3f upVector = new Vector3f(0, 1, 0);
        Vector3f forwardVector = new Vector3f(0, 0, 1);

        tempRotation.multLocal(forwardVector);

        forwardVector.y = 0;

        Quaternion restirctedRotation = new Quaternion();
        restirctedRotation.lookAt(forwardVector, upVector);

        return restirctedRotation;
    }

    //Copied from http://hub.jmonkeyengine.org/forum/topic/setting-world-rotation/ @ 06.05.2014
    public static void setWorldOrientation(Spatial spatialToRotate, Quaternion worldRotationToRotateTo)
    {
        if(spatialToRotate==null)
            System.out.println("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        
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

    public void registerHeadNode(Node headNode)
    {
        this.headNode = headNode;
    }

    public void registerCameraControl(StereoCameraControl cameraControl)
    {
        this.cameraControl = cameraControl;
    }

    @Override
    public void setEnabled(boolean enabled)
    {
        super.setEnabled(enabled);

        if(avatarPhysicsController != null)
        {
            avatarPhysicsController.setEnabled(enabled);
        }
    }

    @Override
    public void setSpatial(Spatial node)
    {
        super.setSpatial(node);
        if(spatial != null)
        {
            System.out.println("SPATIAL IS NOT NULL!");
            Node tempNode = (Node) spatial;
            headNode = (Node) tempNode.getChild(NodeName.AVATAR_HEAD);
            secondaryRotationNode = (Node) tempNode.getChild(NodeName.TABLET_BOX);
            cameraControl = (StereoCameraControl) headNode.getControl(0);
        }
        else
        {
            System.out.println("SPATIAL IS NULL");
        }
    }
}