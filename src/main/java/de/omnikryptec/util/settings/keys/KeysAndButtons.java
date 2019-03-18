package de.omnikryptec.util.settings.keys;
//TODO Migrate or Rename
public class KeysAndButtons {
    
    //Straight up stolen from GLFW
    
    /** The key or button was released. */
    public static final int OKE_RELEASE = 0;

    /** The key or button was pressed. */
    public static final int OKE_PRESS = 1;

    /** The key was held down until it repeated. */
    public static final int OKE_REPEAT = 2;
    
    /** The unknown key. */
    public static final int OKE_KEY_UNKNOWN = -1;

    /** Printable keys. */
    public static final int
        OKE_KEY_SPACE         = 32,
        OKE_KEY_APOSTROPHE    = 39,
        OKE_KEY_COMMA         = 44,
        OKE_KEY_MINUS         = 45,
        OKE_KEY_PERIOD        = 46,
        OKE_KEY_SLASH         = 47,
        OKE_KEY_0             = 48,
        OKE_KEY_1             = 49,
        OKE_KEY_2             = 50,
        OKE_KEY_3             = 51,
        OKE_KEY_4             = 52,
        OKE_KEY_5             = 53,
        OKE_KEY_6             = 54,
        OKE_KEY_7             = 55,
        OKE_KEY_8             = 56,
        OKE_KEY_9             = 57,
        OKE_KEY_SEMICOLON     = 59,
        OKE_KEY_EQUAL         = 61,
        OKE_KEY_A             = 65,
        OKE_KEY_B             = 66,
        OKE_KEY_C             = 67,
        OKE_KEY_D             = 68,
        OKE_KEY_E             = 69,
        OKE_KEY_F             = 70,
        OKE_KEY_G             = 71,
        OKE_KEY_H             = 72,
        OKE_KEY_I             = 73,
        OKE_KEY_J             = 74,
        OKE_KEY_K             = 75,
        OKE_KEY_L             = 76,
        OKE_KEY_M             = 77,
        OKE_KEY_N             = 78,
        OKE_KEY_O             = 79,
        OKE_KEY_P             = 80,
        OKE_KEY_Q             = 81,
        OKE_KEY_R             = 82,
        OKE_KEY_S             = 83,
        OKE_KEY_T             = 84,
        OKE_KEY_U             = 85,
        OKE_KEY_V             = 86,
        OKE_KEY_W             = 87,
        OKE_KEY_X             = 88,
        OKE_KEY_Y             = 89,
        OKE_KEY_Z             = 90,
        OKE_KEY_LEFT_BRACKET  = 91,
        OKE_KEY_BACKSLASH     = 92,
        OKE_KEY_RIGHT_BRACKET = 93,
        OKE_KEY_GRAVE_ACCENT  = 96,
        OKE_KEY_WORLD_1       = 161,
        OKE_KEY_WORLD_2       = 162;

    /** Function keys. */
    public static final int
        OKE_KEY_ESCAPE        = 256,
        OKE_KEY_ENTER         = 257,
        OKE_KEY_TAB           = 258,
        OKE_KEY_BACKSPACE     = 259,
        OKE_KEY_INSERT        = 260,
        OKE_KEY_DELETE        = 261,
        OKE_KEY_RIGHT         = 262,
        OKE_KEY_LEFT          = 263,
        OKE_KEY_DOWN          = 264,
        OKE_KEY_UP            = 265,
        OKE_KEY_PAGE_UP       = 266,
        OKE_KEY_PAGE_DOWN     = 267,
        OKE_KEY_HOME          = 268,
        OKE_KEY_END           = 269,
        OKE_KEY_CAPS_LOCK     = 280,
        OKE_KEY_SCROLL_LOCK   = 281,
        OKE_KEY_NUM_LOCK      = 282,
        OKE_KEY_PRINT_SCREEN  = 283,
        OKE_KEY_PAUSE         = 284,
        OKE_KEY_F1            = 290,
        OKE_KEY_F2            = 291,
        OKE_KEY_F3            = 292,
        OKE_KEY_F4            = 293,
        OKE_KEY_F5            = 294,
        OKE_KEY_F6            = 295,
        OKE_KEY_F7            = 296,
        OKE_KEY_F8            = 297,
        OKE_KEY_F9            = 298,
        OKE_KEY_F10           = 299,
        OKE_KEY_F11           = 300,
        OKE_KEY_F12           = 301,
        OKE_KEY_F13           = 302,
        OKE_KEY_F14           = 303,
        OKE_KEY_F15           = 304,
        OKE_KEY_F16           = 305,
        OKE_KEY_F17           = 306,
        OKE_KEY_F18           = 307,
        OKE_KEY_F19           = 308,
        OKE_KEY_F20           = 309,
        OKE_KEY_F21           = 310,
        OKE_KEY_F22           = 311,
        OKE_KEY_F23           = 312,
        OKE_KEY_F24           = 313,
        OKE_KEY_F25           = 314,
        OKE_KEY_KP_0          = 320,
        OKE_KEY_KP_1          = 321,
        OKE_KEY_KP_2          = 322,
        OKE_KEY_KP_3          = 323,
        OKE_KEY_KP_4          = 324,
        OKE_KEY_KP_5          = 325,
        OKE_KEY_KP_6          = 326,
        OKE_KEY_KP_7          = 327,
        OKE_KEY_KP_8          = 328,
        OKE_KEY_KP_9          = 329,
        OKE_KEY_KP_DECIMAL    = 330,
        OKE_KEY_KP_DIVIDE     = 331,
        OKE_KEY_KP_MULTIPLY   = 332,
        OKE_KEY_KP_SUBTRACT   = 333,
        OKE_KEY_KP_ADD        = 334,
        OKE_KEY_KP_ENTER      = 335,
        OKE_KEY_KP_EQUAL      = 336,
        OKE_KEY_LEFT_SHIFT    = 340,
        OKE_KEY_LEFT_CONTROL  = 341,
        OKE_KEY_LEFT_ALT      = 342,
        OKE_KEY_LEFT_SUPER    = 343,
        OKE_KEY_RIGHT_SHIFT   = 344,
        OKE_KEY_RIGHT_CONTROL = 345,
        OKE_KEY_RIGHT_ALT     = 346,
        OKE_KEY_RIGHT_SUPER   = 347,
        OKE_KEY_MENU          = 348,
        OKE_KEY_LAST          = OKE_KEY_MENU;

    /** If this bit is set one or more Shift keys were held down. */
    public static final int OKE_MOD_SHIFT = 0x1;

    /** If this bit is set one or more Control keys were held down. */
    public static final int OKE_MOD_CONTROL = 0x2;

    /** If this bit is set one or more Alt keys were held down. */
    public static final int OKE_MOD_ALT = 0x4;

    /** If this bit is set one or more Super keys were held down. */
    public static final int OKE_MOD_SUPER = 0x8;

    /** If this bit is set the Caps Lock key is enabled and the {@link #OKE_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int OKE_MOD_CAPS_LOCK = 0x10;

    /** If this bit is set the Num Lock key is enabled and the {@link #OKE_LOCK_KEY_MODS LOCK_KEY_MODS} input mode is set. */
    public static final int OKE_MOD_NUM_LOCK = 0x20;

    /** Mouse buttons. See <a target="_blank" href="http://www.glfw.org/docs/latest/input.html#input_mouse_button">mouse button input</a> for how these are used. */
    public static final int
        OKE_MOUSE_BUTTON_1      = 0,
        OKE_MOUSE_BUTTON_2      = 1,
        OKE_MOUSE_BUTTON_3      = 2,
        OKE_MOUSE_BUTTON_4      = 3,
        OKE_MOUSE_BUTTON_5      = 4,
        OKE_MOUSE_BUTTON_6      = 5,
        OKE_MOUSE_BUTTON_7      = 6,
        OKE_MOUSE_BUTTON_8      = 7,
        OKE_MOUSE_BUTTON_LAST   = OKE_MOUSE_BUTTON_8,
        OKE_MOUSE_BUTTON_LEFT   = OKE_MOUSE_BUTTON_1,
        OKE_MOUSE_BUTTON_RIGHT  = OKE_MOUSE_BUTTON_2,
        OKE_MOUSE_BUTTON_MIDDLE = OKE_MOUSE_BUTTON_3;
}
