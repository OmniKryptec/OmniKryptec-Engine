package omnikryptec.gameobject.particlesV2;

public class GarbageCollectorThread {

	private static class CollectorRunnable implements Runnable{
		
		private ParticleSimulationBuffers buffers;
		private boolean allowed;
		
		
		private CollectorRunnable(ParticleSimulationBuffers buffers) {
			this.buffers = buffers;
		}
		
		@Override
		public void run() {
			while(allowed) {
				
			}
			//Clean as much dead particles as possible before the next modifications by logic of the particles
		}
		
	}
	
	private Thread thread;
	
	public GarbageCollectorThread(ParticleSimulationBuffers buffers) {
		this.thread = new Thread(new CollectorRunnable(buffers));
	}
	
	public Thread getThread() {
		return thread;
	}
}
