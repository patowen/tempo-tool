package net.patowen.songanalyzer;

import java.awt.Point;

public class SuperMackInput {
	public static final class ActionResize implements InputActionDrag {
		private SuperMack superMack;
		private int initialHeight;
		
		public ActionResize(SuperMack superMack) {
			this.superMack = superMack;
		}
		
		@Override
		public boolean onAction(Point pos, double value) {
			if (!superMack.isDragHandle(pos)) {
				return false;
			}
			initialHeight = superMack.getHeight();
			return true;
		}
		
		@Override
		public void onDrag(Point startRelative) {
			superMack.trySetHeight(initialHeight + startRelative.y);
		}
		
		@Override
		public void onCancel() {
			superMack.trySetHeight(initialHeight);
		}
		
		@Override
		public void onEnd(Point startRelative) {}
	}
}
