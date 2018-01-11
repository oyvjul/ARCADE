package no.hiof.arcade.display;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import java.util.ArrayList;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.models.RotateModelController;
import no.hiof.arcade.util.NodeName;
/**
 *
 * @author oyvindj
 */
public class HudDisplay extends Display
{
    private BitmapText modelNumberText, modelNameText;
    private int modelIndex;
    private Node parentNode;
    private ArrayList<ModelPointsPair<Node, CircularList>> availableModels;
    
    public HudDisplay(Node modelNode, ArrayList<ModelPointsPair<Node, CircularList>> availableModels, AssetManager assetManager, 
            Node guiNode, AppSettings settings, RenderManager renderManager)
    {
        this.parentNode = modelNode;
        this.availableModels = availableModels;
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.settings = settings;
        this.renderManager = renderManager;
    }

    @Override
    public void setHudDisplay() 
    {
        Texture2D setupOffscreenView = setupOffscreenView(parentNode);

        picture.setTexture(assetManager, setupOffscreenView, true);
        picture.setWidth(settings.getWidth() / 2);
        picture.setHeight(settings.getHeight() / 3);
        picture.setPosition(settings.getWidth() / 4, settings.getHeight() / 4);
        guiNode.attachChild(picture);
    }
    
    private void setGuiFont()
    {
        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
    }
    
    public void createHudTextName()
    {
        setGuiFont();
        modelNameText = new BitmapText(guiFont, false);
        modelNameText.setSize(30f);
        modelNameText.setLocalTranslation(settings.getWidth() / 2.1f, settings.getHeight() / 1.8f, 0);
        modelNameText.setName("model_name");
        guiNode.attachChild(modelNameText);
    }
    
    public void createHudTextNumber()
    {
        setGuiFont();
        modelNumberText = new BitmapText(guiFont, false);
        modelNumberText.setSize(30f);
        modelNumberText.setLocalTranslation(settings.getWidth() /2.1f, settings.getHeight() / 3, 0);
        modelNumberText.setName("model_number");
        guiNode.attachChild(modelNumberText);
    }
    
    public void setGuiNumberText(int index, int size)
    {
        modelNumberText.setText(index + " / " + size);
    }
    
    public void setGuiNameText(String name)
    {
        modelNameText.setText(name.substring(0, name.indexOf("/")));
    }
    
    public void setGuiErrorText(String errorText)
    {
        modelNameText.setText(errorText);
    }
    
    public void displayModels()
    {
        for(modelIndex = 0; modelIndex < availableModels.size(); modelIndex++) 
        {
            placeAndScaleModels();
            rotateModels();
            parentNode.attachChild(getModels());
        }
    }
    
    public void detachHudFromGuiNode()
    {
        guiNode.detachChildNamed(NodeName.HUD_PICTURE);
        guiNode.detachChildNamed("model_name");
        guiNode.detachChildNamed("model_number");
    }
    
    public void attachHudToGuiNode()
    {
        guiNode.attachChild(picture);
        guiNode.attachChild(modelNameText);
        guiNode.attachChild(modelNumberText);
    }
    
    private void placeAndScaleModels()
    {    
        BoundingVolume worldBound = getModels().getWorldBound();
        Vector3f center = worldBound.getCenter();
        
        BoundingBox boundingBox = new BoundingBox(center, 0, 0, 0);
        boundingBox.mergeLocal(worldBound);
        
        Vector3f extent = boundingBox.getExtent(null);
        float maxExtent = Math.max(Math.max(extent.x, extent.y), extent.z);

        getModels().setLocalScale(1f / maxExtent, 0.9f / maxExtent, 1f / maxExtent);//1f / maxExtent, 0.9f / maxExtent, 1f / maxExtent
        getModels().setLocalTranslation(-5*modelIndex, 1, 4);
        getModels().rotate(-0.5f, 0, 0);
    }
    
    private void rotateModels()
    {
        RotateModelController rotateYAxis = new RotateModelController();
        rotateYAxis.setSpeed(0.5f);
        getModels().addControl(rotateYAxis);
    }
    
    private Node getModels()
    { 
        return availableModels.get(modelIndex).getModel();
    }

    @Override
    public void addLights(Node modelNode) 
    {
        ambientLight.setColor(new ColorRGBA(1.1f, 1.1f, 1.1f, 1.1f));
        directionalLight.setDirection(new Vector3f(-1.4f, -1.4f, -1.4f));//1.4f, -1.4f, -1.4f
        modelNode.addLight(ambientLight);
        modelNode.addLight(directionalLight);
    }
    
    public void startAllAvailableModelsRotation()
    {
        for(ModelPointsPair modelPair: availableModels)
        {
            Node currentModel = (Node)modelPair.getModel();
            currentModel.getControl(RotateModelController.class).setEnabled(true);
        }
    }
    
    public void stopAllAvailableModelsRotation()
    {
        for(ModelPointsPair modelPair: availableModels)
        {
            Node currentModel = (Node)modelPair.getModel();
            currentModel.getControl(RotateModelController.class).setEnabled(false);
        }
    }
}