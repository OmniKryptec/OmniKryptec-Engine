package de.omnikryptec.ecs;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import de.omnikryptec.ecs.entity.Entity;
import de.omnikryptec.ecs.entity.EntityManager;
import de.omnikryptec.ecs.entity.IEntityManager;
import de.omnikryptec.ecs.family.Family;
import de.omnikryptec.ecs.family.FamilyManager;
import de.omnikryptec.ecs.family.IFamilyManager;
import de.omnikryptec.ecs.system.ComponentSystem;
import de.omnikryptec.ecs.system.ISystemManager;
import de.omnikryptec.ecs.system.SystemManager;

public class ECSManager implements IECSManager {

	private IEntityManager entityManager;
	private IFamilyManager familyManager;
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
		this(new EntityManager(), new FamilyManager(), new SystemManager());
	}

	public ECSManager(IEntityManager entityManager, IFamilyManager familyManager, ISystemManager systemManager) {
		this.systemTasks = new LinkedList<>();
		this.entityManager = entityManager;
		this.familyManager = familyManager;
		this.systemManager = systemManager;
	}

	@Override
	public void addEntity(Entity entity) {
		entityManager.addEntity(entity);
		familyManager.addFilteredEntity(entity);
	}

	@Override
	public void removeEntity(Entity entity) {
		entityManager.removeEntity(entity);
		familyManager.removeFilteredEntity(entity);
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
		familyManager.addFilter(system.getFamily());
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
		familyManager.removeFilter(system.getFamily());
		system.removedFromEntityManager(this);
	}

	@Override
	public List<Entity> getEntitesFor(Family f) {
		return familyManager.getEntitiesFor(f);
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
