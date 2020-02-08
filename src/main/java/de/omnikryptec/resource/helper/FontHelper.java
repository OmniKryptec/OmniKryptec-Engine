package de.omnikryptec.resource.helper;

import java.util.HashMap;
import java.util.Map;

import de.omnikryptec.libapi.exposed.render.Texture;
import de.omnikryptec.resource.Font;
import de.omnikryptec.resource.FontFile;
import de.omnikryptec.resource.loadervpc.ResourceProvider;

public class FontHelper {
    
    private static final String DEFAULT_FNT_END = ".fnt";
    private static final String DEFAULT_TEX_END = ".png";
    
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
        Font f = fonts.get(name);
        if (f == null) {
            f = sdfFonts.get(name);
        }
        return f;
    }
    
    public Font getFont(String name) {
        return getFont(name, null, null, false);
    }
    
    public Font getFontSDF(String name) {
        return getFont(name, null, null, true);
    }
    
    //TODO pcfreak9000 HMM... the order in which the different "branches" are tried might be oof?
    
    public Font getFont(String name, String fname, String tname, boolean sdf) {
        Map<String, Font> fonts = sdf ? this.sdfFonts : this.fonts;
        Font f = fonts.get(name);
        if (f == null) {
            FontFile ff = resProvider.get(FontFile.class, fname == null ? name + DEFAULT_FNT_END : fname);
            if (ff == null) {
                ff = FontFile.getFontFile(name);
                if (ff == null) {
                    throw new NullPointerException(
                            "Could not find font with name=" + name + ", fname=" + fname + ", tname=" + tname);
                }
            }
            Texture t = tHelper.get(tname == null ? name + DEFAULT_TEX_END : tname);
            if (tHelper.isMissingTexture(t)) {
                t = tHelper.get(ff.getTexFileName());
            }
            f = new Font(ff, t, sdf);
            fonts.put(name, f);
        }
        return f;
    }
    
}
