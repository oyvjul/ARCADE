package no.hiof.arcade.display;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture2D;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.models.TabletSurface;
import no.hiof.arcade.util.NodeName;
/**
 *
 * @author oyvindj
 */
public class TabletDisplay extends Display
{ 
    private Node parentNode;
    private ModelPointsPair<Node, CircularList> models;
            
    public TabletDisplay(Node tabletNode, ModelPointsPair<Node, CircularList> models, AssetManager assetManager, Node guiNode, AppSettings settings, RenderManager renderManager)
    {
        this.parentNode = tabletNode;
        this.models = models;
        this.assetManager = assetManager;
        this.guiNode = guiNode;
        this.settings = settings;
        this.renderManager = renderManager;
    }
    
    @Override
    public void setTabletDisplay(Node offView)
    {
        Node tempNode = new Node();
        Node screenMesh = new Node(NodeName.SCREEN_MESH);
        TabletSurface tabletBox = new TabletSurface(screenMesh, assetManager);
        Texture2D setupOffscreenView = setupOffscreenView(offView);

        tabletBox.addBox(0.34f, 0.249f, 0.1f, setupOffscreenView);
        screenMesh.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*115, Vector3f.UNIT_X));
        screenMesh.setLocalTranslation(-0.168f, 0.1701f, 0.04f); //-0.168f, 0.1701f, 0.04f
        tempNode.attachChild(screenMesh);
        
        Spatial armsAndTablet = assetManager.loadModel("model/Arms_HiRes/ogre/out.scene");
        tempNode.attachChild(armsAndTablet);
        tempNode.setLocalTranslation(0, 0.4f, 0.6f);//0.7
        tempNode.rotate(-FastMath.DEG_TO_RAD*30,0,0);
        parentNode.attachChild(tempNode);
    }
    
    public void scaleRoof(Node noRoofModel)
    {
        noRoofModel.depthFirstTraversal(sceneGraphVisitor);
    }
    
    SceneGraphVisitor sceneGraphVisitor = new SceneGraphVisitor() 
    {
        @Override
        public void visit(Spatial spatial) 
        {
            if (spatial instanceof Geometry && spatial.getName().startsWith("roof")) 
            {
                Geometry toGeometry = (Geometry) spatial;
                toGeometry.setLocalScale(0.0001f);
            }
        }
    };
    
    @Override
    public void setCameraPosition()
    {
        float tempX = ((BoundingBox)models.getModel().getWorldBound()).getXExtent();
        float tempZ = ((BoundingBox)models.getModel().getWorldBound()).getZExtent();
        float circumference = tempX + tempX + tempZ + tempZ;
        
        offViewCamera.setLocation(new Vector3f(0f, circumference*15, 0));
        offViewCamera.setRotation(new Quaternion().fromAngleNormalAxis(FastMath.HALF_PI, Vector3f.UNIT_X));
    }
}
