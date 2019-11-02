package net.patowen.songanalyzer.deck.beatmack;

import java.awt.Graphics2D;
import java.awt.Point;

import net.patowen.songanalyzer.bundle.DeckBundle;
import net.patowen.songanalyzer.data.Dict;
import net.patowen.songanalyzer.data.FileFormatException;
import net.patowen.songanalyzer.deck.Mack;
import net.patowen.songanalyzer.userinput.InputAction;
import net.patowen.songanalyzer.userinput.InputType;
import net.patowen.songanalyzer.userinput.MouseHoverFeedback;

public class BeatMack extends Mack {
	public static final int type = 2;
	
	public BeatMack(DeckBundle bundle) {
		
	}

	@Override
	public int getType() {
		return type;
	}

	@Override
	public void render(Graphics2D g) {
		
	}

	@Override
	public InputAction applyInputAction(InputType inputType, Point mousePos, double value) {
		return null;
	}

	@Override
	public MouseHoverFeedback applyMouseHover(Point mousePos) {
		return null;
	}

	@Override
	public void save(Dict dict) {
	}

	@Override
	public void load(Dict dict) throws FileFormatException {
	}
}
