package omnikryptec.util;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.collision.shapes.ConvexHullShape;
import com.bulletphysics.collision.shapes.SphereShape;
import com.bulletphysics.util.ObjectArrayList;
import javax.vecmath.Vector3f;
import omnikryptec.logger.LogEntry.LogLevel;
import omnikryptec.logger.Logger;
import omnikryptec.model.Model;
import omnikryptec.objConverter.ModelData;

/**
 *
 * @author Panzer1119
 */
public class PhysicsUtil {
    
    public static final CollisionShape createConvexHullShape(Model model) {
        if(model == null) {
            if(Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null Model! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return createConvexHullShape(model.getModelData());
    }
    
    public static final CollisionShape createConvexHullShape(ModelData modelData) {
        if(modelData == null) {
            if(Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null ModelData! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return createConvexHullShape(modelData.getVertices());
    }
    
    public static final CollisionShape createConvexHullShape(float[] vertices) {
        return createConvexHullShape(ConverterUtil.convertToObjectArrayListVector3f(vertices));
    }
    
    public static final CollisionShape createConvexHullShape(ObjectArrayList<Vector3f> vertices) {
        if(vertices == null) {
            if(Logger.isDebugMode()) {
                Logger.log("Cannot create ConvexHullShape of a null ObjectArrayList! Returning default CollisionShape!", LogLevel.WARNING);
            }
            return createStandardCollisionShape(1.0F);
        }
        return new ConvexHullShape(vertices);
    }
    
    public static final CollisionShape createStandardCollisionShape(float radius) {
        return new SphereShape(radius);
    }
    
}
