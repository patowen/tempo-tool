package net.patowen.songanalyzer;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import net.patowen.songanalyzer.FileDialogManager.DialogKind;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.undo.UserActionList;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputActionStandard;
import net.patowen.songanalyzer.userinput.InputDictionary;
import net.patowen.songanalyzer.userinput.InputMapping;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.InputTypeKeyboard;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;
import net.patowen.songanalyzer.view.DimHeightControlled;
import net.patowen.songanalyzer.view.DimWidthControlled;
import net.patowen.songanalyzer.view.View;

public class Root extends View implements DimWidthControlled, DimHeightControlled {
	private Config config;
	private UserActionList userActionList;
	private FileDialogManager fileDialogManager;
	private Deck deck;
	private InputDictionary inputDictionary;
	
	private Path currentFile;
	
	public Root(Config config, UserActionList userActionList, FileDialogManager fileDialogManager) {
		this.config = config;
		this.userActionList = userActionList;
		this.fileDialogManager = fileDialogManager;
		if (!this.config.loadConfig()) {
			System.err.println("Loading the configuration file failed");
		}
		deck = new Deck(userActionList);
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Undo(), new InputTypeKeyboard(KeyEvent.VK_Z, true, false, false), 1));
		Redo redo = new Redo();
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Z, true, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Y, true, false, false), 1));
		
		inputDictionary.addInputMapping(new InputMapping(new Save(), new InputTypeKeyboard(KeyEvent.VK_S, true, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new SaveWithForcedDialog(), new InputTypeKeyboard(KeyEvent.VK_S, true, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new Open(), new InputTypeKeyboard(KeyEvent.VK_O, true, false, false), 1));
		
		inputDictionary.constructDictionary();
		
		currentFile = config.getConfigEntryPath(Config.Keys.DEFAULT_FILE);
		if (currentFile != null) {
			if (!prechosenLoad(currentFile, true)) {
				reset();
				currentFile = null;
				config.setConfigEntryPath(Config.Keys.DEFAULT_FILE, null);
			}
		} else {
			reset();
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		deck.forwardRender(g);
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction inputAction = inputDictionary.applyInput(inputType, mousePos, value);
		if (inputAction != null) {
			return inputAction;
		}
		
		return deck.forwardInput(inputType, mousePos, value);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return deck.forwardMouseHover(mousePos);
	}

	@Override
	public void setWidth(int width) {
		this.width = width;
		deck.setWidth(width);
	}

	@Override
	public void setHeight(int height) {
		this.height = height;
		deck.setHeight(height);
	}
	
	public void reset() {
		deck.reset();
	}
	
	public void save(DataOutputStream stream) throws IOException {
		stream.writeInt(0);
		deck.save(stream);
	}
	
	public void load(DataInputStream stream) throws IOException, FileFormatException {
		if (stream.readInt() != 0) {
			throw new FileFormatException("Unsupported song analysis file version");
		}
		deck.load(stream);
	}
	
	private void superSave(Path path) throws IOException {
		try (DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
			save(stream);
		}
	}
	
	private void superLoad(Path path) throws IOException, FileFormatException {
		try (DataInputStream stream = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
			load(stream);
		} catch (EOFException e) {
			throw new FileFormatException("File contents ended earlier than expected");
		}
	}
	
	private boolean prechosenSaveOrLoad(Path path, FileDialogManager.DialogKind dialogKind, boolean quiet) {
		try {
			switch (dialogKind) {
			case SAVE:
				superSave(path);
				break;
			case OPEN:
				superLoad(path);
				break;
			default:
				throw new IllegalArgumentException("dialogKind");
			}
			return true;
		} catch (FileFormatException e) {
			fileDialogManager.showFileFormatErrorDialog(path, e.getMessage());
			return false;
		} catch (NoSuchFileException e) {
			if (!quiet || dialogKind != DialogKind.OPEN) {
				fileDialogManager.showErrorDialog(path, dialogKind);
			}
			return false;
		} catch (IOException e) {
			fileDialogManager.showErrorDialog(path, dialogKind);
			return false;
		}
	}
	
	private Path dialogSaveOrLoad(FileDialogManager.DialogKind dialogKind) {
		Path path = fileDialogManager.getUserChosenPath(
				config.getConfigEntryPath(Config.Keys.DEFAULT_FOLDER),
				"Song analyis files",
				new String[] {"songanalysis"},
				dialogKind);
		
		if (path == null) {
			return null;
		}
		
		boolean success = prechosenSaveOrLoad(path, dialogKind, false);
		
		if (!success) {
			return null;
		}

		currentFile = path;
		config.setConfigEntryPath(Config.Keys.DEFAULT_FILE, currentFile);
		config.setConfigEntryPath(Config.Keys.DEFAULT_FOLDER, currentFile.getParent());
		return path;
	}
	
	private boolean prechosenSave(Path path) {
		return prechosenSaveOrLoad(path, FileDialogManager.DialogKind.SAVE, false);
	}
	
	private boolean prechosenLoad(Path path, boolean quiet) {
		return prechosenSaveOrLoad(path, FileDialogManager.DialogKind.OPEN, quiet);
	}
	
	private Path dialogSave() {
		return dialogSaveOrLoad(FileDialogManager.DialogKind.SAVE);
	}
	
	private Path dialogLoad() {
		return dialogSaveOrLoad(FileDialogManager.DialogKind.OPEN);
	}
	
	private class Undo implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			userActionList.undo();
			return true;
		}
	}
	
	private class Redo implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			userActionList.redo();
			return true;
		}
	}
	
	private class Save implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			if (currentFile != null) {
				prechosenSave(currentFile);
			} else {
				dialogSave();
			}
			return true;
		}
	}
	
	private class SaveWithForcedDialog implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			dialogSave();
			return true;
		}
	}
	
	private class Open implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			dialogLoad();
			return true;
		}
	}
}
