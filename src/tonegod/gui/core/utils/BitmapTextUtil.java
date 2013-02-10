/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tonegod.gui.core.utils;

import com.jme3.font.BitmapText;
import com.jme3.font.LineWrapMode;
import com.jme3.font.Rectangle;
import tonegod.gui.core.Element;

/**
 *
 * @author t0neg0d
 */
public class BitmapTextUtil {
	
	public static float getTextWidth(Element ref, String text) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);
		
		return eval.getLineWidth();
	}
	
	public static float getTextWidth(Element ref, String text, float maxWidth) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setText("Xg");
		eval.setText(text);
		
		return (eval.getLineWidth() < maxWidth) ? eval.getLineWidth() : maxWidth;
	}
	
	public static float getTextLineHeight(Element ref, String text) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setLineWrapMode(LineWrapMode.NoWrap);
		eval.setBox(null);
		eval.setText(text);
		
		return eval.getLineHeight();
	}
	
	public static float getTextTotalHeight(Element ref, String text, float maxWidth) {
		BitmapText eval = new BitmapText(ref.getFont());
		eval.setSize(ref.getFontSize());
		eval.setText("Xg");
		eval.setBox(new Rectangle(0,0,maxWidth, eval.getLineHeight()));
		eval.setText(text);
		
		return eval.getLineWidth()*eval.getLineCount();
	}
}
