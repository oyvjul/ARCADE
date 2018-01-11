/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hiof.arcade.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author oyvindj
 */
public class TrackAvatarController extends AbstractControl
{
    private float rotationSpeed = 1;
    private Node avatarNode;
    
    public TrackAvatarController()
    {}
    
    public TrackAvatarController(Node avatarNode)
    {
        this.avatarNode = avatarNode;
    }

    @Override
    protected void controlUpdate(float f) 
    {   
        spatial.setLocalRotation(avatarNode.getLocalRotation());
        spatial.setLocalTranslation(avatarNode.getLocalTranslation());
    }
    
    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
        TrackAvatarController control = new TrackAvatarController();
        control.setSpeed(rotationSpeed);
        control.setSpatial(spatial);
        
        return control; 
    }
    
    public float getSpeed() 
    {
        return rotationSpeed;
    }

    public void setSpeed(float speed) 
    {
        this.rotationSpeed = speed;
    }
    
    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {}
}
