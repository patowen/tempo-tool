package net.patowen.songanalyzer.undo;

import java.util.ArrayList;
import java.util.List;

public class UserActionList {
	private List<UserAction> actions;
	private int currentAction;
	
	public UserActionList() {
		actions = new ArrayList<>();
		currentAction = 0;
	}
	
	public void applyAction(UserAction action) {
		actions = actions.subList(0, currentAction);
		action.exec();
		actions.add(action);
		currentAction++;
	}
	
	public void undo() {
		if (currentAction > 0) {
			currentAction--;
			actions.get(currentAction).undo();
		}
	}
	
	public void redo() {
		if (currentAction < actions.size()) {
			actions.get(currentAction).exec();
			currentAction++;
		}
	}
	
	public void clear() {
		actions.clear();
		currentAction = 0;
	}
}
