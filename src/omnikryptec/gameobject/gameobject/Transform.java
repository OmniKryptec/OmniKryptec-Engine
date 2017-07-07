package omnikryptec.gameobject.gameobject;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import omnikryptec.display.DisplayManager;
import omnikryptec.util.Maths;
import omnikryptec.util.RenderUtil;

public class Transform implements Positionable{
	
	private Transform parent;
	public Vector3f position;
	public Quaternionf rotation;
	public Vector3f scale;
	
	private Matrix4f transformation;
	
	private boolean disableRecalculation = false;
	
	public Transform(){
		this(new Vector3f(0));
	}
	
	public Transform(Vector3f pos){
		this(pos, new Quaternionf(0, 0, 0));
	}
	
	public Transform(Vector3f pos, Quaternionf rot){
		this(pos, rot, new Vector3f(1));
	}
	
	public Transform(Vector3f pos, Quaternionf rot, Vector3f scale){
		this(null, pos, rot, scale);
	}
	
	public Transform(Transform parent, Vector3f pos, Quaternionf rot, Vector3f scale){
		this.parent = parent;
		this.position = pos;
		this.rotation = rot;
		this.scale = scale;
		transformation = new Matrix4f();
		recalculateTransformation();
	}
	
	public Transform(Transform parent){
		this();
		setParent(parent);
	}
	
	public Transform getNewCopy(){
		return new Transform(parent, getPositionNew(), getRotationNew(), getScaleNew());
	}
	
	public Transform setParent(Transform transform){
		this.parent = transform;
		return this;
	}
	
	public Transform getParent(){
		return parent;
	}
	
	public Transform setDisableRecalculation(boolean b){
		this.disableRecalculation = b;
		return this;
	}
	
	public boolean isRecalculationDisabled(){
		return disableRecalculation;
	}
	
	public Transform increasePosition(float x, float y, float z){
		this.position.x += x;
		this.position.y += y;
		this.position.z += z;
		return this;
	}
	
	public Transform increaseRotation(float x, float y, float z, float w){
		this.rotation.x += x;
		this.rotation.y += y;
		this.rotation.z += z;
		this.rotation.w += w;
		return this;
	}
	
	public Transform increaseScale(float x, float y, float z){
		this.scale.x += x;
		this.scale.y += y;
		this.scale.z += z;
		return this;
	}
	
	public Transform setPosition(float x, float y, float z){
		this.position.set(x, y, z);
		return this;
	}
	
	public Transform setRotation(float x, float y, float z, float w){
		this.rotation.set(x, y, z, w);
		return this;
	}
	
	public Transform setScale(float x, float y, float z){
		this.scale.set(x, y, z);
		return this;
	}
	
	public Transform setScale(float d){
		return setScale(d, d, d);
	}
	
	public Transform setPosition(Vector3f pos){
		this.position = pos;
		return this;
	}
	
	public Transform setRotation(Quaternionf q){
		this.rotation = q;
		return this;
	}
	
	public Transform setScale(Vector3f scale){
		this.scale = scale;
		return this;
	}
	
	public Vector3f getPositionNew(){
		return new Vector3f(position);
	}
	
	public Quaternionf getRotationNew(){
		return new Quaternionf(rotation);
	}
	
	public Vector3f getScaleNew(){
		return new Vector3f(scale);
	}
	
	public Vector3f getPositionSimple(){
		return position;
	}
	
	public Quaternionf getRotationSimple(){
		return rotation;
	}
	
	public Vector3f getScaleSimple(){
		return scale;
	}
	
	@Override
	public Vector3f getPosition(){
		return getPosition(false);
	}
	
	public Vector3f getPosition(boolean simple){
		if(parent==null){
			return simple?position:new Vector3f(position);
		}
		return parent.getPosition(false).add(position);
	}
	
	public Quaternionf getRotation(){
		return getRotation(false);
	}
	
	public Quaternionf getRotation(boolean simple){
		if(parent == null){
			return simple?rotation:new Quaternionf(rotation);
		}
		return parent.getRotation(false).add(rotation);
	}
	
	public Vector3f getScale(){
		return getScale(false);
	}
	
	public Vector3f getScale(boolean simple){
		if(parent == null){
			return simple?scale:new Vector3f(scale); 
		}
		return parent.getScale(false).add(scale);
	}
	
	
	public Matrix4f getTransformation(){
		return this.getTransformation(true);
	}
	
	public Matrix4f getTransformation(boolean checkupdate){
		return this.getTransformation(UpdateType.DYNAMIC, 1, checkupdate);
	}
	
	public Matrix4f getTransformation(UpdateType type){
		return this.getTransformation(type, 1);
	}    
	
	public Matrix4f getTransformation(UpdateType type, int freq){
		return this.getTransformation(type, freq, true);
	}

    public Matrix4f getTransformation(UpdateType updatetype, int freq, boolean checkupdate){
    	if(checkupdate&&!disableRecalculation&&RenderUtil.needsUpdate(lastframe, freq, updatetype)&&(!Maths.fastEquals3f(lastpos, getPosition())||!Maths.fastEquals4f(lastrot, getRotation())||!Maths.fastEquals3f(lastscale, getScale()))){	
    		return recalculateTransformation();
    	}
    	return transformation;
    }
    
    public Matrix4f recalculateTransformation(){
		if(transformation == null){
			transformation = new Matrix4f();
		}
		if(disableRecalculation){
			return transformation;
		}
    	lastframe = DisplayManager.instance().getFramecount();
		transformation.identity();
		transformation.translate(lastpos.set(getPosition()));
		transformation.rotate(lastrot.set(getRotation()));
		transformation.scale(lastscale.set(getScale()));
		return transformation;
    }
    
    //TMP-Vars
	private Vector3f lastpos = new Vector3f(), lastscale = new Vector3f();
    private Quaternionf lastrot = new Quaternionf();
    private long lastframe=0;
}
