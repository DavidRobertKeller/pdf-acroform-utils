package drkeller.pdf.acroform.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceCharacteristicsDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDTextField;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

public class PdfFormUtils {

	public static void copyAcroForm(
			String acroFormPathfile,
			String inPathfile,
			String outPathfile) 
		throws IOException {

		try (
			PDDocument acroFormDocument = PDDocument.load(new File(acroFormPathfile));
			PDDocument outDocument = PDDocument.load(new File(inPathfile));) 
		{
			PDAcroForm templateAcroForm = acroFormDocument.getDocumentCatalog().getAcroForm();
			PDAcroForm outAcroForm = new PDAcroForm(outDocument);
			
			outAcroForm.setCacheFields(true);
			outAcroForm.setFields(templateAcroForm.getFields());
		    outDocument.getDocumentCatalog().setAcroForm(outAcroForm);
	        
	        int pageIndex = 0;
	        for (PDPage page: acroFormDocument.getPages()) {
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
		try (PDDocument outDocument = PDDocument.load(new File(inPathfile));) 
		{
//			PDFTextStripper stripper = new PDFTextStripper();
			TextLocation stripper = new TextLocation();
			return stripper.getText(outDocument);
		}
	}

	public static List<FoundTextPosition> findTextPositionList(
			String inPathfile,
			String text) throws IOException 
	{
		try (PDDocument outDocument = PDDocument.load(new File(inPathfile));) 
		{
			TextLocation stripper = new TextLocation();
			int maxPage = -1 ;
			boolean isRegularExpression = false;
			stripper.locateText(outDocument, text, maxPage, isRegularExpression);
			return stripper.foundTextPositionList;
		}
	}

	public static void addAcroForm(
			String inPathfile,
			String outPathfile) 
		throws IOException {

		try (
			PDDocument outDocument = PDDocument.load(new File(inPathfile));) 
		{
			//PDAcroForm templateAcroForm = acroFormDocument.getDocumentCatalog().getAcroForm();
			PDAcroForm acroForm = outDocument.getDocumentCatalog().getAcroForm();
			if (acroForm == null) {
				acroForm = new PDAcroForm(outDocument);
			}

			// get first page
			PDPage page = outDocument.getPage(0);
			
            // Adobe Acrobat uses Helvetica as a default font and
            // stores that under the name '/Helv' in the resources dictionary
            PDFont font = PDType1Font.HELVETICA;
            PDResources resources = new PDResources();
            resources.put(COSName.getPDFName("Helv"), font);
            
//            // Add a new AcroForm and add that to the document
//            PDAcroForm acroForm = new PDAcroForm(outDocument);
            outDocument.getDocumentCatalog().setAcroForm(acroForm);
            
            // Add and set the resources and default appearance at the form level
            acroForm.setDefaultResources(resources);
            
            // Acrobat sets the font size on the form level to be
            // auto sized as default. This is done by setting the font size to '0'
            String defaultAppearanceString = "/Helv 0 Tf 0 g";
            acroForm.setDefaultAppearance(defaultAppearanceString);
            
            // Add a form field to the form.
            PDTextField textBox = new PDTextField(acroForm);
            textBox.setPartialName("SampleField");
            // Acrobat sets the font size to 12 as default
            // This is done by setting the font size to '12' on the
            // field level.
            // The text color is set to blue in this example.
            // To use black, replace "0 0 1 rg" with "0 0 0 rg" or "0 g".
            defaultAppearanceString = "/Helv 12 Tf 0 0 1 rg";
            textBox.setDefaultAppearance(defaultAppearanceString);
            
            // add the field to the acroform
            acroForm.getFields().add(textBox);
            // Specify the widget annotation associated with the field
            PDAnnotationWidget widget = textBox.getWidgets().get(0);
            PDRectangle rect = new PDRectangle(50, 750, 200, 50);
            widget.setRectangle(rect);
            widget.setPage(page);

            // if you prefer defaults, delete this code block
            PDAppearanceCharacteristicsDictionary fieldAppearance
                    = new PDAppearanceCharacteristicsDictionary(new COSDictionary());
            fieldAppearance.setBorderColour(new PDColor(new float[]{0,1,0}, PDDeviceRGB.INSTANCE));
            fieldAppearance.setBackground(new PDColor(new float[]{1,1,0}, PDDeviceRGB.INSTANCE));
            widget.setAppearanceCharacteristics(fieldAppearance);
            // make sure the widget annotation is visible on screen and paper
            widget.setPrinted(true);
            
            // Add the widget annotation to the page
            page.getAnnotations().add(widget);
            // set the alignment ("quadding")
            textBox.setQ(PDVariableText.QUADDING_CENTERED);
            // set the field value
            textBox.setValue("Sample field content");
            
			outDocument.save(outPathfile);
		}
	}
	
}
