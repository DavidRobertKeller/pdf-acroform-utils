package drkeller.pdf.acroform.utils;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class CopyAcroFormSwing {
	
    static JTextField pdfWithAcroFormJTextField;
    static JTextField inputPdfJTextField;
    
    
	public static void addComponentsToPane(Container pane) {

		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		JLabel pdfWithAcroFormLabel = new JLabel("PDF with AcroForm: ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		c.gridy = 0;
		c.gridx = 0;
		pane.add(pdfWithAcroFormLabel, c);

		
		pdfWithAcroFormJTextField = new JTextField("");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 40.0;
		c.gridwidth = 1;
		c.gridx = 1;
		pane.add(pdfWithAcroFormJTextField, c);

		
		JButton pdfWithAcroFormButton = new JButton("Select");
		pdfWithAcroFormButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		        int returnValue = jfc.showOpenDialog(null);
		        // int returnValue = jfc.showSaveDialog(null);

		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = jfc.getSelectedFile();
		            System.out.println(selectedFile.getAbsolutePath());
		            
		            pdfWithAcroFormJTextField.setText(selectedFile.getAbsolutePath());
		        }
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		c.gridx = 2;
		pane.add(pdfWithAcroFormButton, c);

		// next row
		c.gridy++;
		
		JLabel inputPdfLabel = new JLabel("Input PDF: ");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		c.gridx = 0;
		pane.add(inputPdfLabel, c);

		
		inputPdfJTextField = new JTextField("");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 40.0;
		c.gridwidth = 1;
		c.gridx = 1;
		pane.add(inputPdfJTextField, c);

		JButton inputPdfButton = new JButton("Select");
		inputPdfButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				JFileChooser jfc = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());

		        int returnValue = jfc.showOpenDialog(null);
		        // int returnValue = jfc.showSaveDialog(null);

		        if (returnValue == JFileChooser.APPROVE_OPTION) {
		            File selectedFile = jfc.getSelectedFile();
		            System.out.println(selectedFile.getAbsolutePath());
		            
		            inputPdfJTextField.setText(selectedFile.getAbsolutePath());
		        }
			}
		});
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1;
		c.gridwidth = 1;
		c.gridx = 2;
		pane.add(inputPdfButton, c);
	
		// next row
		c.gridy++;

		JButton mergeButton = new JButton("Copy AcroForm");
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 0; // reset to default
		c.weighty = 1.0; // request any extra vertical space
		c.anchor = GridBagConstraints.PAGE_END; // bottom of space
		c.insets = new Insets(10, 0, 0, 0); // top padding
		c.gridwidth = 2; // 2 columns wide
		c.gridy = 2; // third row
		pane.add(mergeButton, c);
		
		mergeButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				String acroFormPathfile = pdfWithAcroFormJTextField.getText();
				String inPathfile = inputPdfJTextField.getText();
				String outPathfile = inPathfile + ".merged.pdf";
				try {
					System.out.println("acroFormPathfile:  " + acroFormPathfile);
					System.out.println("inPathfile:  " + inPathfile);
					System.out.println("outPathfile:  " + outPathfile);
					PdfFormUtils.copyAcroForm(acroFormPathfile, inPathfile, outPathfile);
				} catch (IOException e1) {
					 JOptionPane.showMessageDialog(null, e1.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
					 return;
				}
				JOptionPane.showMessageDialog(null, "AcroForm is copied in file:\n" + outPathfile);
			}
		});
		
	}

	private static void createAndShowGUI() {
		// Create and set up the window.
		JFrame frame = new JFrame("Copy PDF AcroForm");
		frame.setSize(600, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Set up the content pane.
		addComponentsToPane(frame.getContentPane());

		// Display the window.
//		frame.pack();
		frame.setVisible(true);
	}

	public static void main(String[] args) {
		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}
}
