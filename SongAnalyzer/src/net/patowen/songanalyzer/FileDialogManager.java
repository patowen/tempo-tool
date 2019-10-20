package net.patowen.songanalyzer;

import java.awt.Component;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class FileDialogManager {
	public enum DialogKind {
		SAVE,
		OPEN
	}
	
	private Component parent;
	
	public FileDialogManager(Component parent) {
		this.parent = parent;
	}
	
	public Path getUserChosenPath(Path startDirectory, String filterDescription, String[] filterExtensions, DialogKind dialogKind) {
		JFileChooser fileChooser = new JFileChooser(startDirectory.toFile());
		FileNameExtensionFilter filter = new FileNameExtensionFilter(filterDescription, filterExtensions);
		fileChooser.setFileFilter(filter);
		
		int chooserResult;
		switch (dialogKind) {
		case SAVE:
			chooserResult = fileChooser.showSaveDialog(parent);
			break;
		case OPEN:
			chooserResult = fileChooser.showOpenDialog(parent);
			break;
		default:
			throw new IllegalArgumentException("dialogKind");
		}
		
		if (chooserResult != JFileChooser.APPROVE_OPTION) {
			return null;
		}
		
		Path path = fileChooser.getSelectedFile().toPath();
		
		if (dialogKind == DialogKind.SAVE) {
			// The following logic for appending the extension only works because there is
			// only one allowed extension.
			if (!Files.exists(path) && filterExtensions.length > 0) {
				String desiredEnd = "." + filterExtensions[0];
				if (!path.getFileName().toString().endsWith(desiredEnd)) {
					path = Paths.get(path.toString() + desiredEnd);
				}
			}
			
			if (Files.exists(path)) {
				int chosenConfirmOption = JOptionPane.showConfirmDialog(parent,
						"Are you sure you want to overwrite " + path.getFileName() + "?",
						"Overwrite file",
						JOptionPane.YES_NO_OPTION);
				
				if (chosenConfirmOption != JOptionPane.YES_OPTION) {
					return null;
				}
			}
		}
		
		return path;
	}
	
	public void showErrorDialog(Path path, DialogKind dialogKind) {
		switch (dialogKind) {
		case SAVE:
			JOptionPane.showMessageDialog(parent,
					"Failed to save " + path.getFileName() + ". Please check to make sure you have the appropriate permissions.",
					"Save failed",
					JOptionPane.ERROR_MESSAGE);
			break;
		case OPEN:
			JOptionPane.showMessageDialog(parent,
					"Failed to load " + path.getFileName() + ". Please check to make sure you have the appropriate permissions.",
					"Load failed",
					JOptionPane.ERROR_MESSAGE);
			break;
		default:
			throw new IllegalArgumentException("dialogKind");
		}
	}
	
	public void showFileFormatErrorDialog(Path path, String message) {
		JOptionPane.showMessageDialog(parent,
				"Unexpected content in " + path.getFileName() + ": " + message,
				"Unknown file format",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void showCustomErrorDialog(Path path, String message) {
		JOptionPane.showMessageDialog(parent,
				message,
				"Error",
				JOptionPane.ERROR_MESSAGE);
	}
}
