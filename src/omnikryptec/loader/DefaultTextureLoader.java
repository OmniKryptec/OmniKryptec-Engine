package omnikryptec.loader;

import omnikryptec.texture.SimpleTexture;
import omnikryptec.util.AdvancedFile;

public class DefaultTextureLoader implements Loader {

	@Override
	public RessourceObject load(AdvancedFile advancedFile) {
		return SimpleTexture.newTexture(advancedFile);
	}

	@Override
	public String[] getExtensions() {
		return new String[]{"png"};
	}

	@Override
	public String[] getBlacklist() {
		return null;
	}

}
