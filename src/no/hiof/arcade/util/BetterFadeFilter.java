package no.hiof.arcade.util;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.post.filters.FadeFilter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import java.util.ArrayList;

/**
 *
 * @author JonAre
 */
public class BetterFadeFilter extends FadeFilter
{
    private float value = 1;
    private boolean isFading = false;
    private float directionOfFade = 1;
    private float durationOfFade = 1;
    ArrayList<AnimatedFilterListener> availableListeners = new ArrayList<AnimatedFilterListener>();

    public BetterFadeFilter()
    {
        super();
    }

    public BetterFadeFilter(float duration)
    {
        this();
        this.durationOfFade = duration;
    }

    @Override
    protected Material getMaterial()
    {
        material.setFloat("Value", value);
        return material;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h)
    {
        material = new Material(manager, "Common/MatDefs/Post/Fade.j3md");
    }

    @Override
    protected void preFrame(float tpf)
    {
        if(isFading)
        {
            value += tpf * directionOfFade / durationOfFade;

            if(directionOfFade > 0 && value > 1)
            {
                value = 1;
                isFading = false;
                notifyListeners((int) directionOfFade);
            }
            if(directionOfFade < 0 && value < 0)
            {
                value = 0;
                isFading = false;
                notifyListeners((int) directionOfFade);
            }
        }
    }

    private void notifyListeners(int directionOfFade)
    {
        for(AnimatedFilterListener listener : availableListeners)
        {
            listener.onAnimationFinished(directionOfFade);
        }
    }

    public void addListener(AnimatedFilterListener listener)
    {
        availableListeners.add(listener);
    }

    public void fadeToBlack()
    {
        fadeOut();
    }

    /**
     * fades the scene out (scene to black)
     */
    @Override
    public void fadeOut()
    {
        setEnabled(true);
        directionOfFade = -1;
        isFading = true;

    }

    public void fadeFromBlack()
    {
        fadeIn();
    }

    /**
     * fades the scene in (black to scene)
     */
    @Override
    public void fadeIn()
    {
        setEnabled(true);
        directionOfFade = 1;
        isFading = true;
    }
}
