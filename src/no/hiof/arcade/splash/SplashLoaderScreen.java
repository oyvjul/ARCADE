package no.hiof.arcade.splash;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;

/**
 *
 * @author JonAre
 */
public class SplashLoaderScreen
{
    private AssetManager assetManager;
    private Node guiNode;
    private int screenHeight;
    private int screenWidth;
    SpinnerControl spinnerControl;
    private Node arcadeLogo;
    private Node loadingSpinner;

    public SplashLoaderScreen(AssetManager assetManager, Node guiNode, int screenWidth, int screenHeight)
    {
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.screenHeight = screenHeight;
        this.screenWidth = screenWidth;

        initialize();
    }

    private void initialize()
    {
        spinnerControl = new SpinnerControl();

        arcadeLogo = loadSpatialFromTexture("arcade-small-logo");
        arcadeLogo.setLocalTranslation(screenWidth / 2, (screenHeight / 2), 0);

        loadingSpinner = loadSpatialFromTexture("arcade-loading-spinner");
        loadingSpinner.setLocalTranslation(screenWidth / 2, arcadeLogo.getLocalTranslation().getY() - 140f, 0);
        loadingSpinner.addControl(spinnerControl);

        guiNode.attachChild(arcadeLogo);
        guiNode.attachChild(loadingSpinner);

        spinnerControl.setEnabled(true);
    }

    public Node loadSpatialFromTexture(String name)
    {
        Node imageNode = new Node(name);
        Picture picture = new Picture(name);
        Texture2D textureToDisplay = (Texture2D) assetManager.loadTexture("Textures/" + name + ".png");
        picture.setTexture(assetManager, textureToDisplay, true);

        float width = textureToDisplay.getImage().getWidth();
        float height = textureToDisplay.getImage().getHeight();

        picture.setHeight(height);
        picture.setWidth(width);
        picture.move(-width / 2f, -height / 2f, 0);

        Material pictureMaterial = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        pictureMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.AlphaAdditive);

        imageNode.setMaterial(pictureMaterial);
        imageNode.attachChild(picture);

        return imageNode;
    }

    public void setEnabled(boolean isEnabled)
    {
        spinnerControl.setEnabled(isEnabled);
        if(isEnabled)
        {
            if(!arcadeLogo.hasAncestor(guiNode))//Ensures that nodes dont get attached if they already are.
            {
                guiNode.attachChild(arcadeLogo);
            }
            if(!loadingSpinner.hasAncestor(guiNode))
            {
                guiNode.attachChild(loadingSpinner);
            }
        }
        else
        {
            arcadeLogo.removeFromParent();
            loadingSpinner.removeFromParent();
        }
    }
}
