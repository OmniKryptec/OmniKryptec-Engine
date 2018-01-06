package omnikryptec.postprocessing.main;

import de.codemakers.serialization.XMLable;
import org.jdom2.Element;
import org.lwjgl.opengl.GL11;

public class RenderTarget implements XMLable {

    public final int target;
    public final int extended;

    public RenderTarget(int target) {
        this(target, GL11.GL_RGBA8);
    }

    public RenderTarget(int target, int extended) {
        this.target = target;
        this.extended = extended;
    }

    @Override
    public final Element toXML() {
        return new Element(getClass().getSimpleName()).setAttribute("target", "" + target).setAttribute("extended", "" + extended);
    }

}
