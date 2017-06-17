package omnikryptec.terrain;

import org.lwjgl.util.vector.Vector3f;

import de.pcfreak9000.noise.noises.Noise;

public class TerrainGenerator implements Noise {

	private float amplitude = 1;
	private Noise noise;

	/**
	 * flat world
	 */
	public TerrainGenerator() {
		this(new Noise() {

			@Override
			public double valueAt(double arg0, double arg1, double arg2, double arg3) {
				return 0;
			}

			@Override
			public double valueAt(double arg0, double arg1, double arg2) {
				return 0;
			}

			@Override
			public double valueAt(double arg0, double arg1) {
				return 0;
			}
		});
	}

	public TerrainGenerator(Noise noise, float ampli) {
		this(noise);
		setAmplitude(ampli);
	}

	public TerrainGenerator(Noise noise) {
		this.noise = noise;
	}

	public final TerrainGenerator setAmplitude(float a) {
		this.amplitude = a;
		return this;
	}

	public final float getHeight(float x, float y) {
		return (float) (valueAt(x, y) * amplitude);
	}

	public Vector3f generateNormal(float worldX, float worldZ) {
		double hL = valueAt(worldX - 1, worldZ);
		double hR = valueAt(worldX + 1, worldZ);
		double hD = valueAt(worldX, worldZ - 1);
		double hU = valueAt(worldX, worldZ + 1);
		return (Vector3f) new Vector3f((float) (hL - hR), 2f, (float) (hD - hU)).normalise();
	}

	@Override
	public final double valueAt(double arg0, double arg1) {
		return noise.valueAt(arg0, arg1);
	}

	@Override
	public final double valueAt(double arg0, double arg1, double arg2) {
		return noise.valueAt(arg0, arg1, arg2);
	}

	@Override
	public final double valueAt(double arg0, double arg1, double arg2, double arg3) {
		return noise.valueAt(arg0, arg1, arg2, arg3);
	}

}
