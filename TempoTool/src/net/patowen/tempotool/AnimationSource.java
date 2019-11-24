package net.patowen.tempotool;

public class AnimationSource {
	private final AnimationController animationController;
	
	public AnimationSource(AnimationController animationController) {
		this.animationController = animationController;
	}
	
	public void start() {
		animationController.enableAnimationSource(this);
	}
	
	public void stop() {
		animationController.disableAnimationSource(this);
	}
}
