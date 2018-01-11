package no.hiof.arcade.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

/**
 *
 * @author JonAre
 */
public class LockedPoint
{
    private String nameOfPoint;
    private Vector3f positionOfPoint;
    private Vector3f directionOfPoint;

    public LockedPoint()
    {
    }

    public LockedPoint(String nameOfPoint, Vector3f positionOfoint)
    {
        this.nameOfPoint = nameOfPoint;
        this.positionOfPoint = positionOfoint;
        this.directionOfPoint = Vector3f.ZERO;
    }

    public LockedPoint(String nameOfPoint, Vector3f positionOfPoint, Vector3f rotationOfPoint)
    {
        this.nameOfPoint = nameOfPoint;
        this.positionOfPoint = positionOfPoint;
        this.directionOfPoint = rotationOfPoint;
    }

    public String nameOfPoint()
    {
        return this.nameOfPoint;
    }

    public void setNameOfPoint(String name)
    {
        this.nameOfPoint = name;
    }

    public Vector3f locationOfPoint()
    {
        return this.positionOfPoint;
    }

    public void setPosition(Vector3f pos)
    {
        this.positionOfPoint = pos;
    }

    public Vector3f directionOfPoint()
    {
        return this.directionOfPoint;
    }

    public void setDirectionOfPoint(Vector3f dir)
    {
        this.directionOfPoint = dir;
    }

    @Override
    public String toString()
    {
        return String.format("%s#%s#%s", nameOfPoint, positionOfPoint, directionOfPoint);
    }
}