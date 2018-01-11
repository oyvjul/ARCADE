package no.hiof.arcade.io.input;

import com.jme3.app.SimpleApplication;

/**
 *
 * @author JonAre
 */
public class InputAction
{
    //Walk appstate
    public static final String WALK_APPSTATE_MOVE_FORWARD = "was_forward";
    public static final String WALK_APPSTATE_MOVE_BACKWARD = "was_backward";
    public static final String WALK_APPSTATE_MOVE_RIGHT = "was_move_right";
    public static final String WALK_APPSTATE_MOVE_LEFT = "was_move_left";
    public static final String WALK_APPSTATE_ROTATE_RIGHT = "was_rotate_right";
    public static final String WALK_APPSTATE_ROTATE_LEFT = "was_rotate_left";
    public static final String WALK_APPSTATE_ROTATE_UP = "was_rotate_up";
    public static final String WALK_APPSTATE_ROTATE_DOWN = "was_rotate_down";
    public static final String WALK_APPSTATE_JUMP = "was_jump";
    public static final String WALK_APPSTATE_RUN = "was_run";
    public static final String WALK_APPSTATE_PRINT_POSITION = "was_print_position";
    public static final String WALK_APPSTATE_RESET_AVATAR = "was_reset_avatar";
    //Fly appstate
    public static final String FLY_APPSTATE_MOVE_FORWARD = "fas_forward";
    public static final String FLY_APPSTATE_MOVE_BACKWARD = "fas_backward";
    public static final String FLY_APPSTATE_MOVE_RIGHT = "fas_move_right";
    public static final String FLY_APPSTATE_MOVE_LEFT = "fas_move_left";
    public static final String FLY_APPSTATE_ROTATE_RIGHT = "fas_rotate_right";
    public static final String FLY_APPSTATE_ROTATE_LEFT = "fas_rotate_left";
    public static final String FLY_APPSTATE_ROTATE_UP = "fas_rotate_up";
    public static final String FLY_APPSTATE_ROTATE_DOWN = "fas_rotate_down";
    public static final String FLY_APPSTATE_FLY_UP = "fas_fly_up";
    public static final String FLY_APPSTATE_FLY_DOWN = "fas_fly_down";
    public static final String FLY_APPSTATE_PRINT_LOCATION = "fas_print_location";
    //Locked point appstate
    public static final String LOCKEDPOINT_APPSTATE_MOVE_TO_NEXT_POINT = "lpas_next_point";
    public static final String LOCKEDPOINT_APPSTATE_MOVE_TO_PREVIOUS_POINT = "lpas_previous_point";
    public static final String LOCKEDPOINT_APPSTATE_ROTATE_RIGHT = "lpas_rotate_right";
    public static final String LOCKEDPOINT_APPSTATE_ROTATE_LEFT = "lpas_rotate_left";
    public static final String LOCKEDPOINT_APPSTATE_ROTATE_UP = "lpas_rotate_up";
    public static final String LOCKEDPOINT_APPSTATE_ROTATE_DOWN = "lpas_rotate_down";
    //Main
    public static final String APP_TOGGLE_APP_STATE = "main_toggle_app_state";
    public static final String APP_SELECT_WALK_APP_STATE = "main_select_walk_app_state";
    public static final String APP_SELECT_WALK_OCULUS_APP_STATE = "main_select_walk_oculus_app_state";
    public static final String APP_SELECT_FLY_APP_STATE = "main_select_fly_app_state";
    public static final String APP_SELECT_LOCKED_POINT_APP_STATE = "main_select_locked_point_app_state";
    public static final String APP_SELECT_HUD_APP_STATE = "main_select_hud_app_state";
    //HUD appstate
    public static final String HUD_APPSTATE_VIEW_NEXT_MODEL = "has_view_next_model";
    public static final String HUD_APPSTATE_VIEW_PREVIOUS_MODEL = "has_view_previous_model";
    public static final String HUD_APPSTATE_SELECT_CURRENT_MODEL = "has_select_current_model";
    public static final String HUD_APPSTATE_EXIT_MODEL_SELECTOR = "has_exit_model_selector";
    //Global
    public static final String EXIT_ARCADE = SimpleApplication.INPUT_MAPPING_EXIT;
}
