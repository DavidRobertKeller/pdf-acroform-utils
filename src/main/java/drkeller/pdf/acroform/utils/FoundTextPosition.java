package drkeller.pdf.acroform.utils;

import org.apache.pdfbox.text.TextPosition;
import org.json.JSONException;
import org.json.JSONObject;

public class FoundTextPosition {
	public String keyWord;
	public int pageNumber;
	public TextPosition textStart;
	public TextPosition textEnd;
	// used if textStart or textEnd are null
	public float x;
	public float y;

	public FoundTextPosition(
			String keyWord,
			int pageNumber,
			float x,
			float y) {
		this.keyWord = keyWord;
		this.pageNumber = pageNumber;
		this.x = x;
		this.y = y;
	}

	
	public FoundTextPosition(
			String keyWord,
			int pageNumber,
			TextPosition textStart,
			TextPosition textEnd) {
		this.keyWord = keyWord;
		this.pageNumber = pageNumber;
		this.textStart = textStart;
		this.textEnd = textEnd;
	}
	
	public JSONObject toJSONOject() throws JSONException {
		JSONObject item = new JSONObject();
		
		item.put("keyWord", keyWord);
		item.put("pageNumber", pageNumber);
		
		if (textStart != null) {
			item.put("textPositionStart", toJSONOject(textStart));
		}
		else {
			item.put("textPositionStart", toJSONOject(x,y));
		}
		
		if (textEnd != null) {
			item.put("textPositionEnd", toJSONOject(textEnd));
		}
		else {
			item.put("textPositionEnd", toJSONOject(x,y));
		}
		
		return item;
	}
	


	public static JSONObject toJSONOject(
			float x, 
			float y)
	throws JSONException {
		JSONObject item = new JSONObject();
		
		item.put("x" , x);
		item.put("y" , y);
		
		return item;
	}
	
	public static JSONObject toJSONOject(TextPosition textPosition)
			throws JSONException {
		JSONObject item = new JSONObject();
		
		item.put("x", textPosition.getX());
		item.put("y", textPosition.getY());
		item.put("fontType", textPosition.getFont().getType());
		item.put("fontSize", textPosition.getFontSize());
		item.put("height", textPosition.getHeight());
		item.put("width", textPosition.getWidth());
		
		return item;
	}
}