package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.util.TangentBinormalGenerator;

/**
 *
 * @author Moreno
 */
public class Map extends Node{
    
    AssetManager  assetmanager;
    
    Material MapMaterial;
    Spatial MapSpatial;
            
    public Map(AssetManager assetmanager)
    {
         this.assetmanager = assetmanager;
         initObject();
    }
    
    private void initObject()
    {
        MapSpatial = assetmanager.loadModel("Models/Omgeving/Omgeving.j3o");
        TangentBinormalGenerator.generate(MapSpatial);

        this.attachChild(MapSpatial);
    }
}
