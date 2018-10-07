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
	private Queue<ECSEntityTask> entityTasks;
	
	private static enum ECSTaskType {
		REMOVE, ADD;
	}
	
	private static class ECSSystemTask {
		
		ComponentSystem system;
		ECSTaskType type;
		
		private ECSSystemTask(ComponentSystem sys, ECSTaskType t) {
			this.system = sys;
			this.type = t;
		}
	}

	private static class ECSEntityTask {
		
		Entity entity;
		ECSTaskType type;
		
		private ECSEntityTask(Entity e, ECSTaskType t) {
			this.entity = e;
			this.type = t;
		}
	}
	
	public ECSManager() {
		this(new EntityManager(), new SystemManager());
	}

	public ECSManager(IEntityManager entityManager, ISystemManager systemManager) {
		this.systemTasks = new LinkedList<>();
		this.entityTasks = new LinkedList<>();
		this.entityManager = entityManager;
		this.systemManager = systemManager;
	}

	@Override
	public void addEntity(Entity entity) {
		if (updating) {
			entityTasks.add(new ECSEntityTask(entity, ECSTaskType.ADD));
		} else {
			addEntityInt(entity);
		}
	}

	private void addEntityInt(Entity e) {
		entityManager.addEntity(e);
	}
	
	@Override
	public void removeEntity(Entity entity) {
		if (updating) {
			entityTasks.add(new ECSEntityTask(entity, ECSTaskType.REMOVE));
		} else {
			remEntityInt(entity);
		}
	}

	private void remEntityInt(Entity e) {
		entityManager.removeEntity(e);
	}
	
	@Override
	public void addSystem(ComponentSystem system) {
		if (updating) {
			systemTasks.add(new ECSSystemTask(system, ECSTaskType.ADD));
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
			systemTasks.add(new ECSSystemTask(system, ECSTaskType.REMOVE));
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
		while (!entityTasks.isEmpty()) {
			ECSEntityTask t = entityTasks.poll();
			switch (t.type) {
			case ADD:
				addEntityInt(t.entity);
				break;
			case REMOVE:
				remEntityInt(t.entity);
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
