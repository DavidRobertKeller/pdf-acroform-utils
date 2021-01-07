package drkeller.pdf.acroform.utils;

import org.apache.pdfbox.text.TextPosition;

public class PageTextPosition {
	public int pageNumber;
	public TextPosition textPosition;
	
	public PageTextPosition(
			int pagenumber,
			TextPosition textPosition) {
		this.pageNumber = pagenumber;
		this.textPosition = textPosition;
	}
	
}
