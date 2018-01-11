package no.hiof.arcade.splash;

import com.jme3.math.FastMath;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 *
 * @author JonAre
 */
public class SpinnerControl extends AbstractControl
{
    @Override
    protected void controlUpdate(float tpf)
    {
        spatial.rotate(0, 0, FastMath.HALF_PI*tpf);
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp)
    {
    }

}
