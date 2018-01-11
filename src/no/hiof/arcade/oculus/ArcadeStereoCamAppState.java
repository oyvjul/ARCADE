package no.hiof.arcade.oculus;

import com.jme3.app.state.StereoCamAppState;
import com.jme3.scene.control.StereoCameraControl;
/**
 *
 * @author JonAre
 */
public class ArcadeStereoCamAppState extends StereoCamAppState
{
    public ArcadeStereoCamAppState()
    {
        this.setCameraControl(new ArcadeStereoCamControl());
    }

    @Override
    public void update(float tpf)
    {
        super.update(tpf);
    }
    
    @Override
    public StereoCameraControl getCameraControl()
    {
        return super.getCameraControl();
    }
}
