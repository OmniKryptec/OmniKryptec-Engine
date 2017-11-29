package omnikryptec.gameobject;

import org.joml.Matrix4f;

import omnikryptec.renderer.RenderChunk3D;
import omnikryptec.settings.GameSettings;
import omnikryptec.util.Maths;
import omnikryptec.util.logger.LogLevel;
import omnikryptec.util.logger.Logger;

public class GameObject3D extends GameObject implements Transformable3D{

    private RenderChunk3D renderChunk;
    private Transform3D transform = new Transform3D();
    private GameObject3D parent = null;
    
    public GameObject3D() {
    	this(null, null);
    }
    
    public GameObject3D(String name) {
    	this(name, null);
    }
    
    public GameObject3D(GameObject3D parent) {
    	this(null, parent);
    }
    
    public GameObject3D(String name, GameObject3D parent) {
    	if(name==null) {
    		name="";
    	}
    	setParent(parent);
    }
    
    
	@Override
	public Transform3D getTransform() {
		return transform;
	}

    public Matrix4f getTransformation() {
        return transform.getTransformation(getUpdateType());
    }

    public GameObject setTransform(Transform3D t) {
        this.transform = t;
        return this;
    }
	
    /**
     * the parent or null if this GameObject has no parent.
     *
     * @return the parent
     */
    public final GameObject3D getParent() {
        return parent;
    }
    
    /**
     * sets the parent for this gameobject or null for no parent
     *
     * @param go the parent
     */
    public final GameObject3D setParent(GameObject3D go) {
        this.parent = go;
        this.transform.setParent(go == null ? null : go.getTransform());
        return this;
    }
    
    /**
    *
    * @return true if a parent is set
    */
   public final boolean hasParent() {
       return parent != null;
   }

   /**
    * the chunkx. used for rendering
    *
    * @see GameSettings#usesRenderChunking()
    * @return chunkx
    */
   public final long getChunkX() {
       return Maths.fastFloor(transform.getPosition(true).x / RenderChunk3D.getWidth());
   }

   /**
    * the chunky. used for rendering
    *
    * @see GameSettings#usesRenderChunking()
    * @return chunky
    */
   public final long getChunkY() {
       return Maths.fastFloor(transform.getPosition(true).y / RenderChunk3D.getHeight());
   }

   /**
    * the chunkz. used for rendering
    *
    * @see GameSettings#usesRenderChunking()
    * @return
    */
   public final long getChunkZ() {
       return Maths.fastFloor(transform.getPosition(true).z / RenderChunk3D.getDepth());
   }
   
   /**
    * checks the chunkpos of this GameObject
    *
    * @param error if true and if the Logger is in debugmode and if the chunk
    * of this gameobject is null a warning will be printed.
    * @return this GameObject
    */
   protected final void checkChunkPos() {
       RenderChunk3D oldchunk = getRenderChunk();
       if (oldchunk != null) {
           if (oldchunk.getChunkX() != getChunkX() || oldchunk.getChunkY() != getChunkY()
                   || oldchunk.getChunkZ() != getChunkZ()) {
               oldchunk.getScene().addGameObject(this);
               oldchunk.removeGameObject(this, false);
           }
       } else if (Logger.isDebugMode()) {
           Logger.log("MyChunk is null: " + toString(), LogLevel.WARNING);
       }
   }
   
   public final GameObject3D setRenderChunk3D(RenderChunk3D myChunk) {
       this.renderChunk = myChunk;
       return this;
   }
   
   /**
    * the {@link RenderChunk3D} this GameObject is in.
    *
    * @return
    */
   public final RenderChunk3D getRenderChunk() {
       return renderChunk;
   }

}
