package no.hiof.arcade.models;

import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author BassE
 */
public class RotateModelController extends AbstractControl
{
    private float rotationSpeed = 1;

    @Override
    protected void controlUpdate(float f)
    {
        if(isEnabled())
        {
            if(spatial != null)
            {
                spatial.rotate(0, rotationSpeed * f, 0);
            }
        }
    }

    @Override
    public Control cloneForSpatial(Spatial spatial)
    {
        RotateModelController control = new RotateModelController();
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
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
    }
}
