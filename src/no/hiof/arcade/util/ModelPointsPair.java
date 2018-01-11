package no.hiof.arcade.util;

/**
 *
 * @author JonAre
 */
public class ModelPointsPair<T, E> implements AbstractPair<T, E>
{
    private T model;
    private E points;

    public ModelPointsPair()
    {
        
    }
    
    public ModelPointsPair(T model, E points)
    {
        this.model = model;
        this.points = points;
    }
    
    @Override
    public T getFirst()
    {
        return model;
    }

    @Override
    public E getSecond()
    {
        return points;
    }

    @Override
    public void setFirst(T firstObject)
    {
        model = firstObject;
    }

    @Override
    public void setSecond(E secondObject)
    {
        points = secondObject;
    }
   
    public T getModel()
    {
        return model;
    }
    
    public E getPoints()
    {
        return points;
    }
    
    public void setModel(T model)
    {
        this.model = model;
    }
    
    public void setPoints(E points)
    {
        this.points = points;
    }
}
