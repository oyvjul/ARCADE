package no.hiof.arcade.util;

import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import java.util.ArrayList;
import java.util.List;
import no.hiof.arcade.exception.IllegalLockedPointFormatException;

/**
 *
 * @author JonAre
 */
public class PointBuilder
{
    public static final int NAME_INDEX = 0;
    public static final int LOCATION_INDEX = 1;
    public static final int DIRECTION_INDEX = 2;
    
    public static final int X_AXIS_INDEX = 0;
    public static final int Y_AXIS_INDEX = 1;
    public static final int Z_AXIS_INDEX = 2;
    
    public static ArrayList<LockedPoint> buildPoints(List<String> stringifiedPoints, String blockDelimiter, String subBlockDelimiter) throws IllegalArgumentException, IllegalLockedPointFormatException
    {
        if(stringifiedPoints == null || blockDelimiter == null || subBlockDelimiter == null)
        {
            throw new IllegalArgumentException("Cannot parse a null object");
        }

        ArrayList<LockedPoint> parsedPoints = new ArrayList<LockedPoint>();

        for(String stringToParse : stringifiedPoints)
        {
            parsedPoints.add(parsePointFromString(stringToParse, blockDelimiter, subBlockDelimiter));
        }

        return parsedPoints;
    }

    public static LockedPoint parsePointFromString(String stringToParse, String blockDelimiter, String subBlockDelimiter) throws IllegalLockedPointFormatException
    {
        if(stringToParse == null || blockDelimiter == null || subBlockDelimiter == null)
        {
            throw new IllegalArgumentException("Cannot parse a null.");
        }

        String[] splitStringToParse = splitStringWithDelimiter(stringToParse,blockDelimiter);

        if(splitStringToParse.length == 3)
        {
            String[] locationVectorAsStrings = splitStringWithDelimiter(splitStringToParse[LOCATION_INDEX], subBlockDelimiter);
            String[] directionVectorAsStrings = splitStringWithDelimiter(splitStringToParse[DIRECTION_INDEX], subBlockDelimiter);
            
            String lockedPointName = splitStringToParse[0];

            Vector3f locationOfLockedPoint = newVectorFromStrings(locationVectorAsStrings);
            Vector3f directionOfLockedPoint = newVectorFromStrings(directionVectorAsStrings);
            
            return newLockedPoint(lockedPointName, locationOfLockedPoint, directionOfLockedPoint);
        }
        else
        {
            throw new IllegalLockedPointFormatException("Format of locked point string"); //Illegal Format?
        }
    }

    private static String[] splitStringWithDelimiter(String inputData, String delimiterForSplit)
    {
        return inputData.split(delimiterForSplit);
    }
    
    private static Vector3f newVectorFromStrings(String[] inputVector)
    {
        if(inputVector.length==3)
        {
            Float xAxis = parseFloatAtIndex(inputVector, X_AXIS_INDEX);
            Float yAxis = parseFloatAtIndex(inputVector, Y_AXIS_INDEX);
            Float zAxis = parseFloatAtIndex(inputVector, Z_AXIS_INDEX);
            
            return new Vector3f(xAxis, yAxis, zAxis);
        }
        else
        {
            return Vector3f.ZERO;
        }
    }
    
    private static Vector3f newQuaternionFromStrings(String[] inputQuaternion)
    {
        if(inputQuaternion.length==3)
        {
            Float xAxis = parseFloatAtIndex(inputQuaternion, X_AXIS_INDEX);
            Float yAxis = parseFloatAtIndex(inputQuaternion, Y_AXIS_INDEX);
            Float zAxis = parseFloatAtIndex(inputQuaternion, Z_AXIS_INDEX);
            return new Vector3f(xAxis, yAxis, zAxis);
        }
        else
        {
            return Vector3f.ZERO;
        }
    }
    
    private static float parseFloatAtIndex(String[] dataToParse, int indexToParse)
    {
        return Float.parseFloat(dataToParse[indexToParse]);
    }
    
    private static LockedPoint newLockedPoint(String nameOfPoint, Vector3f locationOfPoint, Vector3f directionOfPoint)
    {
        return new LockedPoint(nameOfPoint, locationOfPoint, directionOfPoint);
    }
}
