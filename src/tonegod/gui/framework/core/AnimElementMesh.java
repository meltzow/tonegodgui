/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.framework.core;

import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author t0neg0d
 */
public class AnimElementMesh extends Mesh {
	AnimElement batch;
	private int quadCount = 0;
	private int vCount = 0;
	private int vIndex = 0;
	private int iIndex = 0;
	private boolean init = false;
	private Vector2f pos = new Vector2f(0,0);
	private Vector2f dim = new Vector2f(0,0);
	private Vector2f tempV = new Vector2f(0,0);
	private Vector2f tempV2 = new Vector2f(0,0);
	private boolean buildIndices = true;
	private FloatBuffer vb;
	private ShortBuffer ib;
	private FloatBuffer tcb;
	private FloatBuffer cb;
	
	float[] verts;
	short[] indices;
	
	public AnimElementMesh(AnimElement batch) {
		this.batch = batch;
	}
	
	public void initialize() {
		init = true;
		vb = BufferUtils.createFloatBuffer(batch.getQuads().size()*3*4);
		cb = BufferUtils.createFloatBuffer(batch.getQuads().size()*4*4);
		tcb = BufferUtils.createFloatBuffer(batch.getQuads().size()*2*4);
		ib = BufferUtils.createShortBuffer(batch.getQuads().size()*6);
	}
	
	public void update(float tpf) {
		if (init) {
			vb.rewind();
			cb.rewind();
			if (batch.getQuads().size() != quadCount) {
				buildIndices = true;
				tcb.rewind();
				ib.rewind();
				quadCount = batch.getQuads().size();
			}
			updateMeshData(tpf);
		}
	}
	
	private void updateMeshData(float tpf) {
		vIndex = 0;
		iIndex = 0;
		vCount = 0;
		
		for (QuadData qd : batch.getQuads().values()) {
			qd.update(tpf);
			addQuad(vb, qd);
		}
		
		setBuffers();
	}
	
	private void addQuad(FloatBuffer fb, QuadData qd) {
		qd.index = vIndex;
		
		pos.set(0, 0);
		dim.set(qd.width, qd.height);
		
		/** VERT 1 **/
		applyTransforms(qd, pos.x, pos.y);
		vb	.put(tempV.x)
			.put(tempV.y)
			.put(0);
		
		/** VERT 2 **/
		applyTransforms(qd, pos.x+dim.x, pos.y);
		vb	.put(tempV.x)
			.put(tempV.y)
			.put(0);
		
		/** VERT 3 **/
		applyTransforms(qd, pos.x, pos.y+dim.y);
		vb	.put(tempV.x)
			.put(tempV.y)
			.put(0);
		
		/** VERT 4 **/
		applyTransforms(qd, pos.x+dim.x, pos.y+dim.y);
		vb	.put(tempV.x)
			.put(tempV.y)
			.put(0);
		
		for (int i = 0; i < 4; i++) {
			cb	.put(qd.color.r)
				.put(qd.color.g)
				.put(qd.color.b)
				.put(qd.color.a);
		}
		
		if (buildIndices) {
			
			tcb	.put(qd.region.getU())
				.put(qd.region.getV());
			tcb	.put(qd.region.getU2())
				.put(qd.region.getV());
			tcb	.put(qd.region.getU())
				.put(qd.region.getV2());
			tcb	.put(qd.region.getU2())
				.put(qd.region.getV2());
			
			ib.put((short)(vCount+2));	iIndex++;
			ib.put((short)(vCount));	iIndex++;
			ib.put((short)(vCount+1));	iIndex++;
			ib.put((short)(vCount+1));	iIndex++;
			ib.put((short)(vCount+3));	iIndex++;
			ib.put((short)(vCount+2));	iIndex++;
		}
		
		vCount += 4;
	}
	
	private void applyTransforms(QuadData qd, float x,  float y) {
		AnimElement a = batch;
		QuadData p = qd.parent;
		tempV.set(x,y);
		tempV.subtractLocal(qd.origin);
		tempV.set(rot(tempV, qd.rotation));
		tempV.multLocal(tempV2.set(qd.scaleX, qd.scaleY));
		tempV.addLocal(qd.origin);
		tempV.addLocal(qd.x,qd.y);
		while (p != null) {
			tempV.subtractLocal(p.origin.x, p.origin.y);
			tempV.set(rot(tempV, p.rotation));
			tempV.multLocal(tempV2.set(p.scaleX, p.scaleY));
			tempV.addLocal(p.origin.x, p.origin.y);
			tempV.addLocal(p.x,p.y);
			p = p.parent;
		}
		while (a != null) {
			tempV.subtractLocal(a.getOriginX(), a.getOriginY());
			tempV.set(rot(tempV, a.getRotation()));
			tempV.multLocal(tempV2.set(a.getScaleX(), a.getScaleY()));
			tempV.addLocal(a.getOriginX(), a.getOriginY());
			tempV.addLocal(a.getPositionX(),a.getPositionY());
			if (a.getParent() instanceof AnimElement)
				a = (AnimElement)a.getParent();
			else
				a = null;
		}
	}
	
	float cos, sin, x, y;
	private Vector2f rot(Vector2f p, float angle) {
		cos = FastMath.cos(angle*FastMath.DEG_TO_RAD);
		sin = FastMath.sin(angle*FastMath.DEG_TO_RAD);
		x = p.x*cos-p.y*sin;
		y = p.x*sin+p.y*cos;
		return p.set(x,y);
	}
	
	private void setBuffers() {
	//	vb.flip();
		this.clearBuffer(VertexBuffer.Type.Position);
		this.setBuffer(VertexBuffer.Type.Position, 3, vb);
		this.clearBuffer(VertexBuffer.Type.Color);
		this.setBuffer(VertexBuffer.Type.Color, 4, cb);
		if (buildIndices) {
	//		tcb.flip();
			this.clearBuffer(VertexBuffer.Type.TexCoord);
			this.setBuffer(VertexBuffer.Type.TexCoord, 2, tcb);
	//		ib.flip();
			this.clearBuffer(VertexBuffer.Type.Index);
			this.setBuffer(VertexBuffer.Type.Index, 3, ib);
	//		cb.flip();
			buildIndices = false;
		}
		createCollisionData();
		updateBound();
	}
}
