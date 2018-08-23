package de.omnikryptec.main;

public class ChunkCoord3D {
	
	public final long x,y,z;
	
	public ChunkCoord3D(long x, long y, long z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ChunkCoord3D) {
			ChunkCoord3D t = (ChunkCoord3D) obj;
			if (t.x == x && t.y == y && t.z == z) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int)((((((0)^x)*397)^y)*397)^z);
	}
	
	
}
