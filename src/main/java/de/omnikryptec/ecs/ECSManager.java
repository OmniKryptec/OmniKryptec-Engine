package de.omnikryptec.ecs;

import java.util.BitSet;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.entity.EntityManager;
import de.omnikryptec.ecs.entity.IEntityManager;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.ecs.system.ISystemManager;
import de.omnikryptec.ecs.system.SystemManager;

public class ECSManager implements IECSManager {

	private IEntityManager entityManager;
	private ISystemManager systemManager;

	private boolean updating = false;
	private Queue<ECSSystemTask> systemTasks;

	private static class ECSSystemTask {

		private static enum ECSSysTaskType {
			REMOVE, ADD;
		}

		public ECSSystemTask(ComponentSystem sys, ECSSysTaskType t) {
			this.system = sys;
			this.type = t;
		}

		ComponentSystem system;
		ECSSysTaskType type;
	}

	public ECSManager() {
		this(new EntityManager(), new SystemManager());
	}

	public ECSManager(IEntityManager entityManager, ISystemManager systemManager) {
		this.systemTasks = new LinkedList<>();
		this.entityManager = entityManager;
		this.systemManager = systemManager;
	}

	@Override
	public void addEntity(Entity entity) {
		entityManager.addEntity(entity);
	}

	@Override
	public void removeEntity(Entity entity) {
		entityManager.removeEntity(entity);
	}

	@Override
	public void addSystem(ComponentSystem system) {
		if (updating) {
			systemTasks.add(new ECSSystemTask(system, ECSSystemTask.ECSSysTaskType.ADD));
		} else {
			addSysInt(system);
		}
	}

	private void addSysInt(ComponentSystem system) {
		systemManager.addSystem(system);
		if(!system.getFamily().isEmpty()) {
			entityManager.addFilter(system.getFamily());
		}
		system.addedToEntityManager(this);
	}

	@Override
	public void removeSystem(ComponentSystem system) {
		if (updating) {
			systemTasks.add(new ECSSystemTask(system, ECSSystemTask.ECSSysTaskType.REMOVE));
		} else {
			remSysInt(system);
		}
	}

	private void remSysInt(ComponentSystem system) {
		systemManager.removeSystem(system);
		if(!system.getFamily().isEmpty()) {
			entityManager.removeFilter(system.getFamily());
		}
		system.removedFromEntityManager(this);
	}

	@Override
	public List<Entity> getEntitesFor(BitSet f) {
		return entityManager.getEntitiesFor(f);
	}

	@Override
	public void update(float deltaTime) {
		updating = true;
		Collection<ComponentSystem> systems = systemManager.getAll();
		for (ComponentSystem system : systems) {
			system.update(this, deltaTime);
		}
		updating = false;
		runTasks();
	}

	private void runTasks() {
		while (!systemTasks.isEmpty()) {
			ECSSystemTask t = systemTasks.poll();
			switch (t.type) {
			case ADD:
				addSysInt(t.system);
				break;
			case REMOVE:
				remSysInt(t.system);
				break;
			default:
				throw new RuntimeException("Weird type");
			}
		}
	}

	public boolean isUpdating() {
		return updating;
	}

	@Override
	public Collection<Entity> getAll() {
		return entityManager.getAll();
	}

}
