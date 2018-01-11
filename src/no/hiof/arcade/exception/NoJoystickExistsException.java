package no.hiof.arcade.exception;

/**
 *
 * @author JonAre
 */
public class NoJoystickExistsException extends Exception
{
    public NoJoystickExistsException()
    {
        super();
    }
    
    public NoJoystickExistsException(String message)
    {
        super(message);
    }
}
