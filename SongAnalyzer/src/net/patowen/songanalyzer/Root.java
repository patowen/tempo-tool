package net.patowen.songanalyzer;

import java.awt.Color;
import java.awt.Component;
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
import java.nio.file.Paths;
import java.util.Collections;

import net.patowen.songanalyzer.DialogManager.DialogKind;
import net.patowen.songanalyzer.bundle.RootBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.data.Obj;
import net.patowen.songanalyzer.deck.Deck;
import net.patowen.songanalyzer.grid.Grid;
import net.patowen.songanalyzer.grid.GridColumn;
import net.patowen.songanalyzer.grid.GridRow;
import net.patowen.songanalyzer.header.Header;
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
	private final RootBundle bundle;
	
	private final Config config;
	private final UserActionList userActionList;
	private final DialogManager dialogManager;
	private final AnimationController animationController;
	private final AudioPlayer audioPlayer;

	private Header header;
	private Deck deck;
	
	private final Grid grid = new Grid();
	private final GridRow headerRow = new GridRow();
	private final GridRow deckRow = new GridRow();
	private final GridColumn gridColumn = new GridColumn();
	
	private Path currentFile;
	
	private InputDictionary inputDictionary;
	
	public Root(Component component) {
		bundle = new RootBundle(component);
		
		this.config = bundle.config;
		this.userActionList = bundle.userActionList;
		this.dialogManager = bundle.dialogManager;
		this.animationController = bundle.animationController;
		this.audioPlayer = bundle.audioPlayer;
		
		if (!this.config.loadConfig()) {
			System.err.println("Loading the configuration file failed");
		}
		
		deck = new Deck(bundle);
		header = new Header(bundle);
		
		grid.setAsOuterGrid();
		grid.setStartRows(Collections.singletonList(headerRow));
		grid.setCenterRow(deckRow);
		grid.setCenterColumn(gridColumn);
		headerRow.setSize(32);
		
		inputDictionary = new InputDictionary();
		inputDictionary.addInputMapping(new InputMapping(new Undo(), new InputTypeKeyboard(KeyEvent.VK_Z, true, false, false), 1));
		Redo redo = new Redo();
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Z, true, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(redo, new InputTypeKeyboard(KeyEvent.VK_Y, true, false, false), 1));
		
		inputDictionary.addInputMapping(new InputMapping(new Save(), new InputTypeKeyboard(KeyEvent.VK_S, true, false, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new SaveWithForcedDialog(), new InputTypeKeyboard(KeyEvent.VK_S, true, true, false), 1));
		inputDictionary.addInputMapping(new InputMapping(new Open(), new InputTypeKeyboard(KeyEvent.VK_O, true, false, false), 1));
		
		inputDictionary.addInputMapping(new InputMapping(new TogglePlay(), new InputTypeKeyboard(KeyEvent.VK_SPACE, false, false, false), 1));
		
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
	
	public void destroy() {
		config.saveConfig();
		animationController.destroy();
		audioPlayer.destroy();
	}
	
	@Override
	public void render(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, width, height);
		
		grid.setWidth(width);
		grid.setHeight(height);
		g.setColor(Color.WHITE);
		grid.renderGridlines(g);
		
		header.setWidth(gridColumn.getSize());
		header.setHeight(headerRow.getSize());
		header.setXPos(gridColumn.getPos());
		header.setYPos(headerRow.getPos());
		
		deck.setWidth(gridColumn.getSize());
		deck.setHeight(deckRow.getSize());
		deck.setXPos(gridColumn.getPos());
		deck.setYPos(deckRow.getPos());
		
		header.forwardRender(g);
		deck.forwardRender(g);
		
		audioPlayer.pollBufferingStatus();
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		InputAction inputAction = inputDictionary.applyInput(inputType, mousePos, value);
		if (inputAction != null) {
			return inputAction;
		}
		
		inputAction = header.forwardInput(inputType, mousePos, value);
		if (inputAction != null) {
			return inputAction;
		}
		
		return deck.forwardInput(inputType, mousePos, value);
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return deck.forwardMouseHover(mousePos);
	}
	
	private void reset() {
		deck.reset();
	}
	
	private interface Keys {
		int deck = 0;
		int hasAudioFile = 1;
		int audioFile = 2;
	}
	
	private Dict save() {
		Dict dict = new Dict();
		dict.set(Keys.deck, deck.save());
		
		Path audioFile = audioPlayer.getAudioFile();
		dict.set(Keys.hasAudioFile, audioFile != null);
		if (audioFile != null) {
			dict.set(Keys.audioFile, audioFile.toString());
		}
		
		return dict;
	}
	
	private void load(Dict dict) throws FileFormatException {
		deck.load(dict.get(Keys.deck).asDict());
		
		if (dict.get(Keys.hasAudioFile).asBool()) {
			Path path = Paths.get(dict.get(Keys.audioFile).asString());
			audioPlayer.loadAudioFileFromSave(path, dialogManager);
		}
	}
	
	private void superSave(Path path) throws IOException {
		Dict dict = save();
		try (DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(Files.newOutputStream(path)))) {
			dict.saveObj(stream);
		}
	}
	
	private void superLoad(Path path) throws IOException, FileFormatException {
		try (DataInputStream stream = new DataInputStream(new BufferedInputStream(Files.newInputStream(path)))) {
			Dict dict = Obj.loadObj(stream).asDict();
			load(dict);
		} catch (EOFException e) {
			throw new FileFormatException("File contents ended earlier than expected");
		}
	}
	
	private boolean prechosenSaveOrLoad(Path path, DialogManager.DialogKind dialogKind, boolean quiet) {
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
			dialogManager.showFileFormatErrorDialog(path, e.getMessage());
			return false;
		} catch (NoSuchFileException e) {
			if (!quiet || dialogKind != DialogKind.OPEN) {
				dialogManager.showErrorDialog(path, dialogKind);
			}
			return false;
		} catch (IOException e) {
			dialogManager.showErrorDialog(path, dialogKind);
			return false;
		}
	}
	
	private Path dialogSaveOrLoad(DialogManager.DialogKind dialogKind) {
		Path path = dialogManager.getUserChosenPath(
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
		return prechosenSaveOrLoad(path, DialogManager.DialogKind.SAVE, false);
	}
	
	private boolean prechosenLoad(Path path, boolean quiet) {
		return prechosenSaveOrLoad(path, DialogManager.DialogKind.OPEN, quiet);
	}
	
	private Path dialogSave() {
		return dialogSaveOrLoad(DialogManager.DialogKind.SAVE);
	}
	
	private Path dialogLoad() {
		return dialogSaveOrLoad(DialogManager.DialogKind.OPEN);
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
	
	private class TogglePlay implements InputActionStandard {
		@Override
		public boolean onAction(Point pos, double value) {
			audioPlayer.setPlaying(!audioPlayer.isPlaying());
			return true;
		}
	}
}
