package drdkeller.pdf.acroform.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

public class TextLocation extends PDFTextStripper {
	
	private static Logger logger = Logger.getLogger(TextLocation.class.getName());
	public static boolean debug = false;

    boolean startOfLine = true;

	public String keyWord ;
	public boolean isRegularExpression;
	public boolean caseSensitive;
	public boolean replaceOnlyFirstOccurrence;
	public StringBuffer capturedTextStringBuffer;
	public List<PageTextPosition> pageTextPositionList;
	public List<FoundTextPosition> foundTextPositionList;

    public TextLocation() throws IOException {
		super();
    	pageTextPositionList = new ArrayList<PageTextPosition>();
    	foundTextPositionList = new ArrayList<FoundTextPosition>();
    	capturedTextStringBuffer = new StringBuffer();
	}

//	@Override
//    protected void startPage(PDPage page) throws IOException
//    {
//        startOfLine = true;
//        super.startPage(page);
//    }
//
//    @Override
//    protected void writeLineSeparator() throws IOException
//    {
//        startOfLine = true;
//        super.writeLineSeparator();
//    }
//
//    @Override
//    protected void writeString(String text, List<TextPosition> textPositions) throws IOException
//    {
//        if (startOfLine)
//        {
//            TextPosition firstProsition = textPositions.get(0);
//            writeString(String.format("[%s]", firstProsition.getXDirAdj()));
//            startOfLine = false;
//        }
//        super.writeString(text, textPositions);
//    }
    
    public void locateText(
    		PDDocument document,
			String keyword, 
			int maxPage, 
			boolean isRegularExpression) 
	throws IOException
	{
    	this.keyWord = keyword;
    	this.isRegularExpression = isRegularExpression;
		getText(document);
    	foundText();
	}
    
	protected void foundText() throws IOException
	{
		String capturedText = this.capturedTextStringBuffer.toString();
		String pattern = this.keyWord;

		if (debug) {
			logger.info("capturedText : " + capturedText);
		}
		
		if (!this.caseSensitive) {
			capturedText = capturedText.toLowerCase();
			pattern = pattern.toLowerCase();
		}
		
		if ((capturedText != null) && (pattern != null) && (pattern.length() > 0) ) { 
			int i = 0;  
			int startPatternIndex = capturedText.indexOf(pattern, i); 
			int patternLength = pattern.length(); 
			int textLength = capturedText.length(); 
			String textFound = pattern;
			
			Matcher matcher = null;
			Pattern textPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
			
			if (this.isRegularExpression) {
				matcher = textPattern.matcher(capturedText);
				while (matcher.find()) {
					startPatternIndex = matcher.start();
					patternLength = matcher.end() - startPatternIndex;
					textFound = matcher.group();
					break;
				}
			}
			
			if (debug) {
				logger.info("startPatternIndex : " + startPatternIndex);
			}
			
			
			if (startPatternIndex > -1) { 
				while (startPatternIndex > -1) {      
					/*
					 * we found an occurrence, we need to found TextPosition start and end 
					 * 
					 *  1           2             3             4  
					 *  this is the message where is the #word# to find
					 * 
					 */
					PageTextPosition textStart = getTextPositionFromCharacterIndex(startPatternIndex);
					PageTextPosition textEnd = getTextPositionFromCharacterIndex(startPatternIndex + (textFound.length()-1));
					
					if (textEnd != null && textStart != null)
					{
						/*
						 * Set the current page from the text position
						 */
//						this.pageCurrent = getPDPageFromCharacterIndex(startPatternIndex);
//						this.occurrenceFound = true;
						onFoundText(textStart.textPosition, textEnd.textPosition, textFound, textStart.pageNumber);
					}

					/*
					 * Update indexes
					 */
					i = startPatternIndex + patternLength; 
					startPatternIndex = (i > textLength) ? -1 : capturedText.indexOf(textFound, i); 
				
					if (this.isRegularExpression) {
						matcher = textPattern.matcher(capturedText);
						while (matcher.find(i)) {
							startPatternIndex = matcher.start();
							patternLength = matcher.end() - startPatternIndex;
							textFound = matcher.group();
							break;
						}
					}
					
					
					/*
					 * First occurrence found we stop the search
					 */
					if (this.replaceOnlyFirstOccurrence)
					{
						break;
					}
				} 
			} 
		} 

	}
	
	/**
	 * Callback to override
	 * 
	 * @param startTextPosition
	 * @param endTextPosition
	 * @throws IOException 
	 */
	protected void onFoundText( 
			TextPosition startTextPosition,
			TextPosition endTextPosition,
			String textFound,
			int pageNumber) 
	throws IOException
	{
		FoundTextPosition foundTextPosition 
			= new FoundTextPosition(
					textFound, 
					pageNumber,
					startTextPosition, 
					endTextPosition);

		foundTextPositionList.add(foundTextPosition);
	}
    
    @Override
    protected void processTextPosition(TextPosition text) {
    	
    	this.capturedTextStringBuffer.append(text.getUnicode());
		this.pageTextPositionList.add(new PageTextPosition(getCurrentPageNo(), text));
    	
    	int pageNumber = getCurrentPageNo();

    	if (debug) {
            logger.info("Page[" + pageNumber + "] String[" + text.getXDirAdj() + ","
                    + text.getYDirAdj() + " fs=" + text.getFontSize() + " xscale="
                    + text.getXScale() + " height=" + text.getHeightDir() + " space="
                    + text.getWidthOfSpace() + " width="
                    + text.getWidthDirAdj() + "]" + text.getUnicode());
    	}
        
        super.processTextPosition(text);
    }
    
    
    
	public PageTextPosition getTextPositionFromCharacterIndex(int index)
	{
		return getTextPositionFromCharacterIndex(index, this.pageTextPositionList) ;
	}

	public static PageTextPosition getTextPositionFromCharacterIndex(
			int index,
			List<PageTextPosition> pageTextPositionList)
	{
		int currentIndex = 0;

		for (PageTextPosition ptp : pageTextPositionList) {
			currentIndex += ptp.textPosition.getUnicode().length();

			if (currentIndex > index)
			{
				return ptp;
			}
		}
		return null;
	}
}
