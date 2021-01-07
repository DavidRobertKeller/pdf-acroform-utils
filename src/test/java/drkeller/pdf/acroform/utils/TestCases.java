package drkeller.pdf.acroform.utils;

import java.util.List;

public class TestCases {

	public static void main(String[] args) throws Exception {
		System.out.println("TestCases starts...");
//		test1();
//		test2();
//		test3();
		test4();
	}
	
	
	public static void test1() throws Exception {
		String acroFormPathfile = "C:\\my\\test\\path\\pdf-with-acroform.pdf";
		String inPathfile = "C:\\my\\test\\path\\simple-document.pdf";
		String outPathfile = inPathfile + ".merged.PDF";
		PdfFormUtils.copyAcroForm(acroFormPathfile, inPathfile, outPathfile);
	}

	
	public static void test2() throws Exception {
		String inPathfile = "C:\\my\\test\\path\\simple-document.pdf";
		String outPathfile = inPathfile + ".build.pdf";
		PdfFormUtils.addAcroForm(inPathfile, outPathfile);
	}
	
	public static void test3() throws Exception {
		String inPathfile = "C:\\my\\test\\path\\simple-document.pdf";
		String text = PdfFormUtils.getText(inPathfile);
		System.out.println("text : " + text);
	}
	
	public static void test4() throws Exception {
		String inPathfile = "C:\\my\\test\\path\\simple-document.pdf";
		String text = "myWordToFind";
		List<FoundTextPosition> list = PdfFormUtils.findTextPositionList(inPathfile, text);
		for (FoundTextPosition foundTextPosition : list) {
			System.out.println("foundTextPosition : " + foundTextPosition.toJSONOject().toString(2));
		}
	}

}
