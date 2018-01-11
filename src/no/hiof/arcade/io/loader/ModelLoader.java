package no.hiof.arcade.io.loader;

import com.jme3.asset.AssetEventListener;
import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import jme3tools.optimize.GeometryBatchFactory;
import no.hiof.arcade.util.CircularList;
import no.hiof.arcade.util.ModelPointsPair;
import no.hiof.arcade.io.filehandler.FileHandler;
import no.hiof.arcade.util.LockedPoint;
import no.hiof.arcade.util.PointBuilder;

/**
 *
 * @author JonAre and Øyvind
 */
public class ModelLoader implements AssetEventListener
{
    AssetManager assetManager;
    ArrayList<File> modelPaths = new ArrayList<File>();
    ArrayList<ModelPointsPair<Node, CircularList>> availableNodes = new ArrayList<ModelPointsPair<Node, CircularList>>();
    ArrayList<String> potentialLockedPointsPaths = new ArrayList<String>();
    ArrayList<ModelLoadingListener> listeners;

    public ModelLoader(AssetManager assetManager)
    {
        this.assetManager = assetManager;
        initializeAssetManagerListener();
        
        listeners = new ArrayList<ModelLoadingListener>();
    }
    
    private void initializeAssetManagerListener()
    {
        this.assetManager.addAssetEventListener(this);
    }

    public ArrayList<ModelPointsPair<Node, CircularList>> getModelsFromDirectories(String[] directories, boolean batchModels)
    {
        
        
        
        if(directories == null)
        {
            throw new IllegalArgumentException("Cannot load from null array");
        }
        for(String directory : directories)
        {
            File currentFile = new File(directory);
            createModelSubPaths(currentFile);
            assetManager.registerLocator(directory, FileLocator.class);
        }

        notifyListenersModelsToLoad(modelPaths.size());
        for(File modelPath : modelPaths)
        {
            try
            {
                availableNodes.add(loadSingleModel(modelPath, batchModels));
            }
            catch(Exception ex)
            {
                System.out.printf("Loading of model %s failed. Please check files and paths for errors\n", modelPath.getName());
                modelLoadFailed(modelPath.getName());
            }
        }

        return availableNodes;
    }

    private void createModelSubPaths(File directory)
    {
        File[] files = directory.listFiles();

        if(files == null)
        {
            return;
        }

        for(File file : files)
        {
            if(file.isDirectory())
            {
                createModelSubPaths(file);
            }
            else
            {
                if(file.getName().endsWith(".j3o"))
                {
                    modelPaths.add(file);
                }
            }
        }
    }

    private ModelPointsPair<Node, CircularList> loadSingleModel(File file, boolean batchModel)
    {
        
        Node model = (Node) assetManager.loadModel(file.getParentFile().getName() + "/" + file.getName());

        if(batchModel)
        {
            model = (Node) GeometryBatchFactory.optimize(model);
        }

        CircularList<LockedPoint> points = loadLockedPoints(file.getParentFile() + "\\" + file.getName().substring(0, file.getName().indexOf('.')) + ".points");

        return new ModelPointsPair<Node, CircularList>(model, points);
    }

    private CircularList<LockedPoint> loadLockedPoints(String pathName)
    {
        try
        {
            ArrayList<String> linesInFile = FileHandler.getLinesFromFile(pathName);
            CircularList<LockedPoint> availablePoints = new CircularList<LockedPoint>();
            availablePoints.addAllObjectsInList(PointBuilder.buildPoints(linesInFile, "#", ","));
            return availablePoints;
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Could not find point file for path: " + pathName);
            System.out.println("Assuming no file. Setting starting-point to 0,0,0");
        }
        catch(Exception ex)
        {
            System.out.println("An error occured while loading locked points. Please check file containing points: " + pathName);
            System.out.println("Assuming no file. Setting starting-point to 0,0,0");
        }

        CircularList<LockedPoint> fallbackPoints = new CircularList<LockedPoint>();
        fallbackPoints.addObject(new LockedPoint("Start", new Vector3f(0, 0, 0)));

        return fallbackPoints;
    }

    private Node loadSingleModel(String modelParentName, String modelName)
    {
        return (Node) assetManager.loadModel(modelParentName + modelName);
    }

    public void flushPaths()
    {
        modelPaths.clear();
    }

    public static Node loadModelFromZip(AssetManager am, String zipName, String modelName)
    {
        try
        {
            am.registerLocator(zipName, ZipLocator.class);
            return (Node) am.loadModel(modelName);
        }
        catch(Exception ex)
        {
            System.out.printf("Could not find model %s in %s.\n", modelName, zipName);
        }

        return null;
    }

    public void testing()
    {
    }

    public void addModelLoadingListener(ModelLoadingListener listener)
    {
        listeners.add(listener);
    }

    private void notifyListenersModelsToLoad(int modelsToLoad)
    {
        for(ModelLoadingListener listener : listeners)
        {
            listener.modelsToload(modelsToLoad);
        }
    }

    private void modelLoadFailed(String modelName)
    {
        for(ModelLoadingListener listener : listeners)
        {
            listener.modelLoadFailed(modelName);
        }
    }

    private void modelLoadSuccess(String modelName)
    {
        for(ModelLoadingListener listener : listeners)
        {
            listener.modelLoadSuccess(modelName);
        }
    }

    @Override
    public void assetLoaded(AssetKey key)
    {
        if(key.getExtension().equals("j3o") || key.getExtension().equals("scene"))
        {
            modelLoadSuccess(key.getName());
        }
    }

    @Override
    public void assetRequested(AssetKey key)
    {
    }

    @Override
    public void assetDependencyNotFound(AssetKey parentKey, AssetKey dependentAssetKey)
    {
    }
}
