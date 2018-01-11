package no.hiof.arcade.util;

/**
 *
 * @author JonAre
 */
public interface AbstractPair<T,E>
{
    public T getFirst();
    public E getSecond();
    public void setFirst(T firstObject);
    public void setSecond(E secondObject);
}