package omnikryptec.input;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import org.lwjgl.glfw.GLFW;

/**
 * JoystickHandler
 *
 * @author Panzer1119
 */
public class JoystickHandler {

    private static final ArrayList<JoystickHandler> joystickHandlers = new ArrayList<>();

    private final JoystickHandler ME = this;
    private final int joystick;
    private FloatBuffer dataAxes = null;
    private ByteBuffer dataButtons = null;
    private ByteBuffer dataHats = null;

    public JoystickHandler(int joystick) {
        this.joystick = joystick;
    }

    public final JoystickHandler update() {
        if (!isConnected()) {
            return this;
        }
        dataAxes = GLFW.glfwGetJoystickAxes(joystick);
        dataButtons = GLFW.glfwGetJoystickButtons(joystick);
        dataHats = GLFW.glfwGetJoystickHats(joystick);
        return this;
    }

    public final JoystickHandler init() {
        if (!joystickHandlers.contains(this)) {
            joystickHandlers.add(this);
        }
        return this;
    }

    public final JoystickHandler close() {
        joystickHandlers.remove(this);
        return this;
    }

    public final int getJoystick() {
        return joystick;
    }

    public final FloatBuffer getDataAxes() {
        return dataAxes;
    }

    public final ByteBuffer getDataButtons() {
        return dataButtons;
    }

    public final ByteBuffer getDataHats() {
        return dataHats;
    }

    public final String getName() {
        return getName(joystick);
    }

    public final boolean isConnected() {
        return isConnected(joystick);
    }

    public static final String getName(int joystick) {
        return GLFW.glfwGetJoystickName(joystick);
    }

    public static final boolean isConnected(int joystick) {
        return getName(joystick) != null;
    }

    public static final ArrayList<Integer> getJoysticks() {
        final ArrayList<Integer> joysticks = new ArrayList<>();
        for (int i = GLFW.GLFW_JOYSTICK_1; i <= GLFW.GLFW_JOYSTICK_LAST; i++) {
            joysticks.add(i);
        }
        return joysticks;
    }

    public static final HashMap<Integer, String> getConnectedJoysticks() {
        final HashMap<Integer, String> joysticks = new HashMap<>();
        getJoysticks().stream().forEach((joystick) -> {
            final String name = getName(joystick);
            if (name != null) {
                joysticks.put(joystick, name);
            }
        });
        return joysticks;
    }

    protected static final void updateAll() {
        if(joystickHandlers.isEmpty()) {
            return;
        }
        joystickHandlers.stream().forEach((joystickHandler) -> {
            joystickHandler.update();
        });
    }

}
