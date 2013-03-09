/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.util.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 *
 * @author t0neg0d
 */
public class ElementQuadGrid extends Mesh {
	private FloatBuffer verts = BufferUtils.createFloatBuffer(16*3);
	private FloatBuffer coords = BufferUtils.createFloatBuffer(16*2);
	private IntBuffer indexes = BufferUtils.createIntBuffer(3*3*6);
	private FloatBuffer normals = BufferUtils.createFloatBuffer(16*3);
	private FloatBuffer colors = null;
	
	private float[] templateNormals = new float[] { 0f,0f,1f };
	private Vector2f dimensions;
	private Vector4f borders;
	
	private float[] templateX, templateY, templateCoordX, templateCoordY;
	private float templateZ = 0;
	
	private int[] templateIndexes = new int[] {
		0,1,5,5,4,0
	};
	
	public ElementQuadGrid(Vector2f dimensions, Vector4f borders, float imgWidth, float imgHeight, float pixelWidth, float pixelHeight, float atlasX, float atlasY, float atlasW, float atlasH) {
		this.dimensions = dimensions;
		this.borders = borders;
		
		// Place verts according to resize borders
		templateX = new float[] {
			0f, borders.y, dimensions.x-borders.z, dimensions.x
		};
		templateY = new float[] {
			0f, borders.x, dimensions.y-borders.w, dimensions.y
		};
		
		// Place verts according to resize borders
		float fX = pixelWidth*atlasX;
		float fW = pixelWidth*(atlasX+atlasW);
		float fY = pixelHeight*atlasY;
		float fH = pixelHeight*(atlasY+atlasH);
		
	//	System.out.println(fX + " : " + fY + " : " + fW + " : " + fH);
		
		templateCoordX = new float[] {
			fX, fX+(pixelWidth*borders.y), fW-(pixelWidth*borders.z), fW
		};
		templateCoordY = new float[] {
			fY, fY+(pixelHeight*borders.x), fH-(pixelHeight*borders.w), fH
		};
		
		updateMesh();
		
		// determine tex corrds based on resize border
		int index = 0;
		int indexX = 0;
		int indexY = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				coords.put(index, templateCoordX[indexX]);
				index++;
				coords.put(index, templateCoordY[indexY]);
				index++;
				indexX++;
				if (indexX == 4) { indexX = 0; }
			}
			indexY++;
			if (indexY == 4) { indexY = 0; }
		}
		
		// Populate indexes from temple
		indexX = 0;
		for (int x = 0; x < indexes.capacity(); x += 18) {
			indexes.put(x, templateIndexes[0]+indexX);
			indexes.put(x+1, templateIndexes[1]+indexX);
			indexes.put(x+2, templateIndexes[2]+indexX);
			indexes.put(x+3, templateIndexes[3]+indexX);
			indexes.put(x+4, templateIndexes[4]+indexX);
			indexes.put(x+5, templateIndexes[5]+indexX);
			indexes.put(x+6, templateIndexes[0]+indexX+1);
			indexes.put(x+7, templateIndexes[1]+indexX+1);
			indexes.put(x+8, templateIndexes[2]+indexX+1);
			indexes.put(x+9, templateIndexes[3]+indexX+1);
			indexes.put(x+10, templateIndexes[4]+indexX+1);
			indexes.put(x+11, templateIndexes[5]+indexX+1);
			indexes.put(x+12, templateIndexes[0]+indexX+2);
			indexes.put(x+13, templateIndexes[1]+indexX+2);
			indexes.put(x+14, templateIndexes[2]+indexX+2);
			indexes.put(x+15, templateIndexes[3]+indexX+2);
			indexes.put(x+16, templateIndexes[4]+indexX+2);
			indexes.put(x+17, templateIndexes[5]+indexX+2);
			indexX += 4;
		}
		
		// Populate normals from template
		for (int x = 0; x < normals.capacity(); x += 3) {
			normals.put(x, templateNormals[0]);
			normals.put(x+1, templateNormals[1]);
			normals.put(x+2, templateNormals[2]);
		}
		
		setBuffers(true);
	}
	
	public void updateDimensions(float w, float h) {
		dimensions.setX(w);
		templateX[2] = dimensions.x-borders.z;
		templateX[3] = dimensions.x;
		dimensions.setY(h);
		templateY[2] = dimensions.y-borders.w;
		templateY[3] = dimensions.y;
		updateMesh();
		setBuffers(false);
	}
	public void updateWidth(float w) {
		dimensions.setX(w);
		templateX[2] = dimensions.x-borders.z;
		templateX[3] = dimensions.x;
		updateMesh();
		setBuffers(false);
	}
	public void updateHeight(float h) {
		dimensions.setY(h);
		templateY[2] = dimensions.y-borders.w;
		templateY[3] = dimensions.y;
		updateMesh();
		setBuffers(false);
	}
	public void setColorBuffer(FloatBuffer colors) {
		this.colors = colors;
		setBuffers(true);
	}
	public void setGradientFillVertical(ColorRGBA start, ColorRGBA end) {
		colors = BufferUtils.createFloatBuffer(16*4);
		for (int i = 0; i < 4; i++) {
			colors.put(start.r);
			colors.put(start.g);
			colors.put(start.b);
			colors.put(start.a);
			colors.put(start.r);
			colors.put(start.g);
			colors.put(start.b);
			colors.put(start.a);
			colors.put(end.r);
			colors.put(end.g);
			colors.put(end.b);
			colors.put(end.a);
			colors.put(end.r);
			colors.put(end.g);
			colors.put(end.b);
			colors.put(end.a);
		}
		setBuffers(true);
	}
	public void setGradientFillHorizontal(ColorRGBA start, ColorRGBA end) {
		colors = BufferUtils.createFloatBuffer(16*4);
		for (int i = 0; i < 8; i++) {
			colors.put(start.r);
			colors.put(start.g);
			colors.put(start.b);
			colors.put(start.a);
		}
		for (int i = 0; i < 8; i++) {
			colors.put(end.r);
			colors.put(end.g);
			colors.put(end.b);
			colors.put(end.a);
		}
		setBuffers(true);
	}
	
	public void resetColorBuffer() {
		colors = null;
		setBuffers(true);
	}
	
	private void updateMesh() {
		int index = 0, indexX = 0, indexY = 0;
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				verts.put(index, templateX[indexX]);
				index++;
				verts.put(index, templateY[indexY]);
				index++;
				verts.put(index, templateZ);
				index++;
				indexX++;
				if (indexX == 4) { indexX = 0; }
			}
			indexY++;
		}
	}
	private void setBuffers(boolean updateAll) {
		this.clearBuffer(Type.Position);
		this.setBuffer(Type.Position, 3, verts);
		if (updateAll) {
			this.clearBuffer(Type.TexCoord);
			this.setBuffer(Type.TexCoord, 2, coords);
			this.clearBuffer(Type.Index);
			this.setBuffer(Type.Index, 3, indexes);
			this.clearBuffer(Type.Color);
			if (colors != null) {
				this.setBuffer(Type.Color, 4, colors);
			}
		}
		createCollisionData();
		updateBound();
	}
}
