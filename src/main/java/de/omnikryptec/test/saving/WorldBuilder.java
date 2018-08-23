package de.omnikryptec.test.saving;

/**
 *
 * @author Panzer1119
 */
public class WorldBuilder implements ObjectBuilder<World> {

	World world = new World();

	@Override
	public ObjectBuilder loadDataMap(DataMap data) {
		world.setName(data.get("name").toString());
		return this;
	}

	@Override
	public World build() {
		return world;
	}

}
