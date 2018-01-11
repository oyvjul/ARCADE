/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hiof.arcade.models;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.util.BufferUtils;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
/**
 *
 * @author BassE
 */
public class TabletSurface 
{
    private final Node parentNode;
    private final AssetManager assetManager;
    private Geometry frontGeometry;
    private Material frontMaterial;
    private float x,y,z;
    
    public TabletSurface(Node parentNode, AssetManager assetManager)
    {
        this.parentNode = parentNode;
        this.assetManager = assetManager;
    }

    public void addBox(float x, float y, float z, Texture tex)
    {   
        this.x = x;
        this.y = y;
        this.z = z;
        setGeometry(this.x, this.y, this.z);
        setMaterialToGeometry(tex);
        addGeometryToParentNode();
    }
    
    private void addGeometryToParentNode()
    {
        parentNode.attachChild(frontGeometry);
    }
    
    private void setGeometry(float x, float y, float z)
    {
        frontGeometry = setFrontGeometry(x,y,z);
    }
    
    private void setMaterialToGeometry(Texture tex)
    {
        frontGeometry.setMaterial(createFrontMaterial(tex));
    }
    
    private Material createFrontMaterial(Texture tex)
    {
        frontMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        frontMaterial.setTexture("ColorMap", tex);
        frontMaterial.setColor("Color", new ColorRGBA(1, 1, 1, 1));
        return frontMaterial;
    }
    
    private Geometry setFrontGeometry(float x, float y, float z)
    {
        Geometry back = new Geometry("BackMesh", setFrontMesh(x,y,z));
        return back;
    }
    
    private Mesh setFrontMesh(float x, float y, float z)
    {
        Mesh frontSide = new Mesh();
        
        Vector3f [] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(x,0,-z);
        vertices[1] = new Vector3f(0,0,-z);
        vertices[2] = new Vector3f(x,y,-z);
        vertices[3] = new Vector3f(0,y,-z);
        
        setBuffer(frontSide,vertices);
        
        return frontSide; 
    }
    
    private void setBuffer(Mesh mesh, Vector3f[] vertices)
    {
        mesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
        mesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(setTextCoord()));
        mesh.setBuffer(Type.Index,    3, BufferUtils.createIntBuffer(index()));
        mesh.updateBound();
    }
    
    private Vector2f[] setTextCoord()
    {
        Vector2f[] texCoord = new Vector2f[4];
        texCoord[0] = new Vector2f(0,0);
        texCoord[1] = new Vector2f(1,0);
        texCoord[2] = new Vector2f(0,1);
        texCoord[3] = new Vector2f(1,1);
        
        return texCoord;
    }
    
    private int[] index()
    {
        int[] indexes = {2,0,1, 1,3,2}; 
        return indexes;
    }
}

