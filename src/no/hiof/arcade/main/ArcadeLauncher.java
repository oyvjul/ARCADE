package no.hiof.arcade.main;

import com.jme3.system.AppSettings;
import java.util.Properties;
import no.hiof.arcade.settings.SettingsLoader;
import no.hiof.arcade.settings.SettingKey;
import no.hiof.arcade.settings.SettingsType;
import oculusvr.input.OculusRiftReader;

/**
 *
 * @author JonAre
 */
public class ArcadeLauncher
{
    private static AppSettings displaySettings;
    private static Properties appProperties;
    private static final String APP_NAME = "ARCADE";
    private static final String APP_VERSION = "0.1";

    public static void main(String[] args)
    {
        getSettingsFromFile();
        setUpSettings();
        
        ARCADE arcadeApplication = new ARCADE();
        arcadeApplication.setSettings(displaySettings);
//        arcadeApplication.setShowSettings(false);
        arcadeApplication.start();
        OculusRiftReader.initialize();
    }

    private static void setUpSettings()
    {
        displaySettings = new AppSettings(true);
        displaySettings.setTitle(String.format("%s %s", APP_NAME, APP_VERSION));
//        displaySettings.setFrameRate(60);
        displaySettings.setFrequency(Integer.parseInt(appProperties.getProperty(SettingKey.DISPLAY_FREQUENCY)));
        displaySettings.setResolution(Integer.parseInt(appProperties.getProperty(SettingKey.DISPLAY_RESOLUTION_WIDTH)), Integer.parseInt(appProperties.getProperty(SettingKey.DISPLAY_RESOLUTION_HEIGHT)));
        displaySettings.setBitsPerPixel(32);
        displaySettings.setFullscreen(Boolean.parseBoolean(appProperties.getProperty(SettingKey.DISPLAY_FULL_SCREEN)));
        displaySettings.putBoolean("DisableJoysticks", false);
        displaySettings.setSettingsDialogImage("Textures/arcade-splash.png");
    }

    private static void getSettingsFromFile()
    {
        appProperties = SettingsLoader.getSettingsFromFile(SettingsType.DISPLAY_SETTINGS, "display.preferences");
    }
}
