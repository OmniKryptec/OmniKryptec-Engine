package omnikryptec.component;

import omnikryptec.entity.GameObject;

public interface Component {

	void execute(GameObject instance);
	void onDelete(GameObject instance);
	float getLvl();
}
