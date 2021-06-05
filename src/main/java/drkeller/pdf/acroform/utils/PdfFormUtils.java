package drkeller.pdf.acroform.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDSignatureField;
import org.apache.pdfbox.text.TextPosition;

public class PdfFormUtils {

	public static void copyAcroForm(
			String acroFormPathfile,
			String inPathfile,
			String outPathfile) 
		throws IOException {

		try (
			PDDocument acroFormDocument = Loader.loadPDF(new File(acroFormPathfile));
			PDDocument outDocument = Loader.loadPDF(new File(inPathfile));) 
		{
			PDAcroForm templateAcroForm = acroFormDocument.getDocumentCatalog().getAcroForm();
			PDAcroForm outAcroForm = new PDAcroForm(outDocument);
			
			outAcroForm.setCacheFields(true);
			outAcroForm.setFields(templateAcroForm.getFields());
		    outDocument.getDocumentCatalog().setAcroForm(outAcroForm);
	        
	        int pageIndex = 0;
	        for(PDPage page: acroFormDocument.getPages()){
	        	outDocument.getPage(pageIndex).setAnnotations(page.getAnnotations());
	            outDocument.getPage(pageIndex).setResources(page.getResources());
	            pageIndex++;
	        }

			outDocument.save(outPathfile);
		}
	}
	
	public static String getText(
			String inPathfile) throws IOException 
	{
		try (PDDocument outDocument = Loader.loadPDF(new File(inPathfile));) 
		{
			TextLocation stripper = new TextLocation();
			return stripper.getText(outDocument);
		}
	}

	public static List<FoundTextPosition> findTextPositionList(
			String inPathfile,
			String text) throws IOException 
	{
		try (PDDocument outDocument = Loader.loadPDF(new File(inPathfile));) 
		{
			TextLocation stripper = new TextLocation();
			stripper.caseSensitive = true;
			int maxPage = -1 ;
			boolean isRegularExpression = false;
			stripper.locateText(outDocument, text, maxPage, isRegularExpression);
			return stripper.foundTextPositionList;
		}
	}

	public static void buildAcroform(
			File inFile,
			File outFile,
			int signatureBoxOffsetX,
			int signatureBoxOffsetY,
			int signatureBoxWidth,
			int signatureBoxHeight,
			List<String> tags)
	throws IOException {
		
		findTextPositionList(inFile, tags);
	}

	public static List<FoundTextPosition> findTextPositionList(
			File inFile,
			List<String> tags) 
	throws IOException 
	{
		List<FoundTextPosition> positions = new ArrayList<FoundTextPosition>();
		for (String tag : tags) {
			positions.addAll(PdfFormUtils.findTextPositionList(inFile.getAbsolutePath(), tag));
		}
		
		return positions;
	}	
	
	public static void addSignatureAcroForm(
			File inFile,
			File outFile,
    		int signatureBoxOffsetX,
			int signatureBoxOffsetY,
			int signatureBoxWidth,
			int signatureBoxHeight,
			List<FoundTextPosition> positions) 
	throws IOException {

		try (PDDocument document = Loader.loadPDF(inFile); ) {

            PDAcroForm acroForm = document.getDocumentCatalog().getAcroForm();
            if (acroForm == null) {
				acroForm = new PDAcroForm(document);
				document.getDocumentCatalog().setAcroForm(acroForm);
            }
            
            acroForm.setSignaturesExist(true);
            acroForm.setAppendOnly(true);
            acroForm.getCOSObject().setDirect(true);
            
            for (FoundTextPosition foundTextPosition : positions) {
				 // Create empty signature field, it will get the name "signature-xxx"
            	PDPage page = document.getPage(foundTextPosition.pageNumber - 1);
				TextPosition textStart = foundTextPosition.textStart;
	            PDSignatureField signatureField = new PDSignatureField(acroForm);
	            signatureField.setPartialName(foundTextPosition.keyWord.replace("#", ""));
	            PDAnnotationWidget signatureWidget = signatureField.getWidgets().get(0);

	            signatureWidget.setRectangle(
	            		new PDRectangle(
	            				signatureBoxOffsetX + textStart.getX(), 
	            				signatureBoxOffsetY + page.getMediaBox().getHeight() - textStart.getY() - signatureBoxHeight, 
	            				signatureBoxWidth,
	            				signatureBoxHeight));
	            
	            signatureWidget.getCOSObject().setNeedToBeUpdated(true);
	            signatureWidget.setPage(page);
	            page.getAnnotations().add(signatureWidget);
	            page.getCOSObject().setNeedToBeUpdated(true);
	            acroForm.getFields().add(signatureField);
			}
            
            // general updates
            document.getDocumentCatalog().getCOSObject().setNeedToBeUpdated(true);

            try (OutputStream os = new FileOutputStream(outFile);) {
                document.saveIncremental(os);
            }
		}		
	}
	
	
}
