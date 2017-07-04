package omnikryptec.gameobject.gameobject;

import omnikryptec.settings.GameSettings;

/**
 * 
 * @author pcfreak9000
 *
 */
public interface Rangeable extends Positionable{

	/**
     * sets the {@link RenderType} of this instance.
     * if set to {@link RenderType#ALWAYS} this will always rendered if possible, only restricted by {@link GameSettings#usesRenderChunking()}
     * @see {@link GameSettings#getRadiusBig()},{@link GameSettings#getRadiusMedium()},{@link GameSettings#getRadiusFoliage()}
     * @param type
     * @return
     */
	RenderType getType();
}
