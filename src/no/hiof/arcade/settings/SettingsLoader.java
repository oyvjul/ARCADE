package no.hiof.arcade.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author JonAre
 */
public class SettingsLoader
{
    public static Properties getSettingsFromFile(SettingsType settingsType, String pathToFile)
    {
        File settingsFile = new File(pathToFile);
        Properties availableProperties = new Properties();
        InputStream inputStream;
        
        try
        {
            inputStream = new FileInputStream(settingsFile);
            availableProperties.load(inputStream);
        }
        catch(FileNotFoundException ex)
        {
            System.out.println("Could not find settings file. Trying to create settings file.");
            createSettingsFile(settingsType, pathToFile);
            return getSettingsFromFile(settingsType, pathToFile);
        }
        catch(IOException ex)
        {
            System.out.println("Could not load file. Retrying");
            return getSettingsFromFile(settingsType, pathToFile);
        }
        
        return availableProperties;
    }
    
    public static void createSettingsFile(SettingsType settingsType, String pathToFile)
    {
        File settingsFile = new File(pathToFile);
        Properties newProperties = new Properties();
        OutputStream outputStream = null;
        try
        {
            settingsFile.createNewFile();
            
            outputStream = new FileOutputStream(pathToFile);
            
            
            buildPropertiesForFileCreation(newProperties, settingsType);
        
            newProperties.store(outputStream, null);
        }
        catch(IOException ex)
        {
            System.out.println("Could not create settings file.");
        }
    }
    
    private static void buildPropertiesForFileCreation(Properties properties, SettingsType settingsType)
    {
        if(settingsType==SettingsType.APPLICATION_SETTINGS)
        {
            properties.setProperty(SettingKey.DEBUG_MODE_ENABLED, LegalSettingValue.FALSE);
            properties.setProperty(SettingKey.WALK_WITHOUT_HEAD, LegalSettingValue.FALSE);
            properties.setProperty(SettingKey.BATCH_MODELS_WHEN_LOADING, LegalSettingValue.FALSE);
            properties.setProperty(SettingKey.USE_OCULUS_RIFT, LegalSettingValue.TRUE);
            
        }
        else if(settingsType==SettingsType.MODEL_PATH_SETTINGS)
        {
            properties.setProperty("DUMMYMODELPATH", "C:/temp/models/remove_line_before_use");
        }
        else if(settingsType == SettingsType.DISPLAY_SETTINGS)
        {
            properties.setProperty(SettingKey.DISPLAY_RESOLUTION_WIDTH, "1280");
            properties.setProperty(SettingKey.DISPLAY_RESOLUTION_HEIGHT, "800");
            properties.setProperty(SettingKey.DISPLAY_FREQUENCY, "60");
            properties.setProperty(SettingKey.DISPLAY_FULL_SCREEN, LegalSettingValue.TRUE);
            properties.setProperty(SettingKey.DISPLAY_ANTI_ALIASING_FACTOR, "0");
        }
    }
}
