/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.hiof.arcade.settings;

import no.hiof.arcade.util.AvailableAppState;

/**
 *
 * @author JonAre
 */
public class SettingParser
{
    public static AvailableAppState parseStartingAppState(String settingValue)
    {
        if(settingValue.equals(LegalSettingValue.WALK_APP_STATE))
        {
            return AvailableAppState.WALK_APP_STATE;
        }
        else if(settingValue.equals(LegalSettingValue.WALK_OCULUS_APP_STATE))
        {
            return AvailableAppState.WALK_OCULUS_APP_STATE;
        }
        else if(settingValue.equals(LegalSettingValue.FLY_APP_STATE))
        {
            return AvailableAppState.FLY_APP_STATE;
        }
        else if(settingValue.equals(LegalSettingValue.LOCKED_POINTS_APP_STATE))
        {
            return AvailableAppState.LOCKED_POINT_APP_STATE;
        }
        else
        {
            return AvailableAppState.FLY_APP_STATE;
        }
    }
}
