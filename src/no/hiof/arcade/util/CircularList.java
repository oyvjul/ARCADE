package no.hiof.arcade.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JonAre
 */
public class CircularList<T>
{
    private List collectionOfObjects;
    private T currentlySelectedObject;
    private int currentlySelectedPosition;

    public CircularList()
    {
        collectionOfObjects = new ArrayList<T>();
        currentlySelectedPosition = 0;
    }

    public void addObject(T objectToAdd)
    {
        if(objectToAdd == null)
        {
            throw new IllegalArgumentException("Cannot add a null");
        }

        collectionOfObjects.add(objectToAdd);
        if(collectionOfObjects.size() == 1)
        {
            resetToFirstPosition();
        }
    }

    private void resetToFirstPosition()
    {
        currentlySelectedPosition = 0;
        currentlySelectedObject = (T) collectionOfObjects.get(currentlySelectedPosition);
    }
    
    private void setToLastPosition()
    {
        currentlySelectedPosition = collectionOfObjects.size()-1;
        currentlySelectedObject = (T) collectionOfObjects.get(currentlySelectedPosition);
    }

    public void addAllObjectsInList(List<T> objectsToAdd)
    {
        if(objectsToAdd == null)
        {
            throw new IllegalArgumentException("Cannot add a null.");
        }

        collectionOfObjects.addAll(objectsToAdd);
        if(collectionOfObjects.size() == 1)
        {
            resetToFirstPosition();
        }
    }

    public T getAtIndex(int index)
    {
        return currentlySelectedObject = (T) collectionOfObjects.get(index);
    }
    
    public T nextObject()
    {
        if(collectionOfObjects.indexOf(currentlySelectedObject) < collectionOfObjects.size() - 1)
        {
            currentlySelectedPosition++;
            currentlySelectedObject = (T) collectionOfObjects.get(currentlySelectedPosition);
        }
        else if(collectionOfObjects.indexOf(currentlySelectedObject) == collectionOfObjects.size() - 1)
        {
            resetToFirstPosition();
        }

        return currentlySelectedObject;
    }
    
    public T previousObject()
    {
        if(collectionOfObjects.indexOf(currentlySelectedObject) >0)
        {
            currentlySelectedPosition--;
            currentlySelectedObject = (T) collectionOfObjects.get(currentlySelectedPosition);
        }
        else if(collectionOfObjects.indexOf(currentlySelectedObject) == 0)
        {
            setToLastPosition();
        }

        return currentlySelectedObject;
    }

    public T currentObject()
    {
        return currentlySelectedObject;
    }

    public int currentPosition()
    {
        return currentlySelectedPosition;
    }

    public int positionOfObject(T objectToGetPositionOf)
    {
        if(objectToGetPositionOf == null)
        {
            throw new IllegalArgumentException("Cannot check collection for null");
        }
        
        return collectionOfObjects.indexOf(objectToGetPositionOf);
    }

    public int sizeOfCollection()
    {
        return collectionOfObjects.size();
    }
}