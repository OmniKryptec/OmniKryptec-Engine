package omnikryptec.main;

public class ChunkCoord2D {

	public final long x, y;

	public ChunkCoord2D(long x, long y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof ChunkCoord2D) {
			ChunkCoord2D t = (ChunkCoord2D) obj;
			if (t.x == x && t.y == y) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return (int) ((((0)^x)*397)^y);
	}

}
