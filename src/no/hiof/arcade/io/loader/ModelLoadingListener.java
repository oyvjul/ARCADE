package no.hiof.arcade.io.loader;

/**
 *
 * @author JonAre
 */
public interface ModelLoadingListener
{
    public void modelsToload(int modelsToLoad);
    public void modelLoadFailed(String modelName);
    public void modelLoadSuccess(String modelName);
}
