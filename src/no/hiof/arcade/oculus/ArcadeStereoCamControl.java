package no.hiof.arcade.oculus;

import com.jme3.math.Quaternion;
import com.jme3.scene.control.StereoCameraControl;

/**
 *
 * @author JonAre
 */
public class ArcadeStereoCamControl extends StereoCameraControl
{
    public Quaternion getCalculatedWorldRotation()
    {
        return getLookDirection().mult(spatial.getWorldRotation());
    }

    @Override
    public void update(float tpf)
    {
        super.update(tpf);
    }
}
