/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hiof.arcade.models;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;

/**
 *
 * @author oyvju_000
 */
public class BoxCreator 
{
    AssetManager assetManager;
    
    public BoxCreator(AssetManager assetManager)
    {
        this.assetManager = assetManager;
    }
    
    public Geometry getAvatarTrackBox()
    {
        Sphere sphereMesh = new Sphere(32, 32, 0.5f);
        Geometry shinyGeo = new Geometry("Shiny rock", sphereMesh);
        Material shinyMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        shinyMat.setBoolean("UseMaterialColors", true); 
        shinyMat.setColor("Ambient", ColorRGBA.Green);
        shinyGeo.setMaterial(shinyMat);
        return shinyGeo;
    }
    
    public Geometry getLockedPointBoxes(Vector3f position)
    {
        Box avatarBox = new Box(1.5f,.9f,1.5f); 
        Geometry avatarGeometry = new Geometry("Colored Box", avatarBox); 
        Material boxMaterial = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
        
        boxMaterial.setBoolean("UseMaterialColors", true); 
        boxMaterial.setColor("Ambient", ColorRGBA.Red); 
        avatarGeometry.setMaterial(boxMaterial);
        avatarGeometry.setLocalScale(0.2f);
        avatarGeometry.setLocalTranslation(position);
        return avatarGeometry;
    }
}
