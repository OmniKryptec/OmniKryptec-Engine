package de.omnikryptec.resource.helper;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.Font;
import de.omnikryptec.resource.FontFile;
import de.omnikryptec.resource.loadervpc.ResourceProvider;
import de.omnikryptec.util.Logger;
import de.omnikryptec.util.Util;

public class FontHelper {
    
    private static final String DEFAULT_FNT_END = ".fnt";
    private static final String DEFAULT_TEX_END = ".png";

    private static final Logger LOGGER = Logger.getLogger(FontHelper.class);

    private final Map<String, Font> fonts;
    private final Map<String, Font> sdfFonts;
    private final ResourceProvider resProvider;
    private final TextureHelper tHelper;

    public FontHelper(ResourceProvider provider, TextureHelper tHelper) {
        this.resProvider = provider;
        this.tHelper = tHelper;
        this.fonts = new HashMap<>();
        this.sdfFonts = new HashMap<>();
    }

    //Unsafe but maybe useful for plugins who know the font is loaded but not if it's sdf.... hmmm
    public Font getFontAuto(String name) {
        Font f = this.fonts.get(name);
        if (f == null) {
            f = this.sdfFonts.get(name);
        }
        return f;
    }

    public Font getFont(String name) {
        return getFont(name, null, null, false);
    }

    public Font getFontSDF(String name) {
        return getFont(name, null, null, true);
    }
    
    public Font getFont(String name, String fname, String tname, boolean sdf) {
        Map<String, Font> fonts = sdf ? this.sdfFonts : this.fonts;
        Font f = fonts.get(name);
        if (f == null) {
            //TODO pcfreak9000 HMM... the order in which the different "branches" are tried might be oof?
            FontFile ff = this.resProvider.get(FontFile.class, Util.defaultIfNull(name + DEFAULT_FNT_END, fname));//fname == null ? name + DEFAULT_FNT_END : fname
            if (ff == null) {
                ff = FontFile.getFontFile(name);
                if (ff == null) {
                    LOGGER.error(
                            String.format("Could not find font with name=%s, fname=%s, tname=%s", name, fname, tname));
                    return null;
                }
            }
            Texture t = null;
            if (tname != null) {
                t = this.tHelper.get(tname);
            }
            if (this.tHelper.isMissingTexture(t) || t == null) {
                t = this.tHelper.get(ff.getTexFileName());
            }
            if (this.tHelper.isMissingTexture(t) || t == null) {
                t = this.tHelper.get(name + DEFAULT_TEX_END);
            }
            f = new Font(ff, t, sdf);
            fonts.put(name, f);
        }
        return f;
    }

    public void clearFonts() {
        this.fonts.clear();
    }

    public void clearSDFFonts() {
        this.sdfFonts.clear();
    }

}
