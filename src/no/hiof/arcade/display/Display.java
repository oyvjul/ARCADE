/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hiof.arcade.display;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import no.hiof.arcade.util.NodeName;
/**
 *
 * @author oyvindj
 */
abstract class Display
{
    protected ViewPort offView;
    protected RenderManager renderManager;
    protected AssetManager assetManager;
    protected Node guiNode;
    protected AppSettings settings;
    protected BitmapFont guiFont;
    protected Picture picture = new Picture(NodeName.HUD_PICTURE);
    protected AmbientLight ambientLight = new AmbientLight();
    protected DirectionalLight directionalLight = new DirectionalLight();
    protected Camera offViewCamera = new Camera(512, 512);
    private Texture2D offViewTexture;

    public void setHudDisplay(){}
    public void setTabletDisplay(Node offView){}
    public void addLights(Node modelNode){}
    public void setCameraPosition(){}
    
    protected Texture2D setupOffscreenView(Node modelNode) 
    { 
        removeLight(modelNode);
        setupOffviewCamera();
        setupOffViewTexture();
        setCameraPosition();
        setupOffViewBuffer();
        addLights(modelNode);
        attachAndUpdateModels(modelNode);
        return modelTexture();
    }
    
    private void attachAndUpdateModels(Node modelNode)
    {
        offView.detachScene(modelNode);
        offView.attachScene(modelNode);
    }
    
    private void setupOffviewCamera()
    {
        offView = renderManager.createPreView("Offscreen View", offViewCamera);
        
        offView.setClearFlags(true, true, true);
        offViewCamera.setFrustumPerspective(30f, 1f, 1f, 1000f);
        offViewCamera.setLocation(new Vector3f(0f, 1f, -2f)); //0f, 1f, -2f
    }
    
    private void setupOffViewBuffer()
    {
        FrameBuffer offViewBuffer = new FrameBuffer(512, 512, 1);
        offViewBuffer.setDepthBuffer(Image.Format.Depth);
        offViewBuffer.setColorTexture(offViewTexture);
        offView.setOutputFrameBuffer(offViewBuffer);
    }
    
    private void setupOffViewTexture()
    {
        offViewTexture = new Texture2D(512, 512, Image.Format.RGBA8);
        offViewTexture.setMinFilter(Texture.MinFilter.Trilinear);
        offViewTexture.setMagFilter(Texture.MagFilter.Bilinear);
    }
    
    private Texture2D modelTexture()
    {
        return offViewTexture;
    }
    
    private void removeLight(Node modelNode)
    {
        modelNode.removeLight(ambientLight);
        modelNode.removeLight(directionalLight);
    }
}
