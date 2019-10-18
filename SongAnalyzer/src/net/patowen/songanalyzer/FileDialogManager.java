package net.patowen.songanalyzer;

import java.awt.Component;
import java.nio.file.Path;

import javax.swing.JFileChooser;
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
		
		// TODO: Handle result and return the appropriate path
		return null;
	}
}
