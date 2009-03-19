package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cohDemoEditor.ragdollAnimator.KeyFrameGrid;
import cohDemoEditor.ragdollAnimator.j3d.FigureUniverse;
import cohDemoEditor.ragdollAnimator.j3d.behavior.FigureAnimationBehavior;
import cohDemoEditor.ragdollAnimator.swing.RagdollAnimator;

/**
 * This Action sets the FigureAnimationBehavior to play its animation from start
 * to finish.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class PlayAnimationAction extends AbstractAction {

	private boolean loop = true;
	private FigureAnimationBehavior behavior;
	private FigureUniverse universe;
	
	public PlayAnimationAction(FigureAnimationBehavior behavior, FigureUniverse universe) {
		if (behavior == null || universe == null)
			throw new IllegalArgumentException(
					"PlayAnimationAction constructor requires non-null arguments.");
		this.behavior = behavior;
		this.universe = universe;
		this.putValue(Action.NAME, "Play");
		this.putValue(Action.SHORT_DESCRIPTION,
				"Plays the animation from start to finish");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		universe.setMode(FigureUniverse.CAMERA_MODE);
		final KeyFrameGrid grid = RagdollAnimator.getRagdollAnimator().getKeyFrameGrid();
		behavior.setStartAndEndTimes(grid.get(0).getTime(), grid.get(grid.size()-1).getTime());
	}

	public final void setLoop(boolean loop) {
		this.loop = loop;
	}

	public final boolean isLoop() {
		return loop;
	}

	/**
	 * @return the behavior
	 */
	public final FigureAnimationBehavior getBehavior() {
		return behavior;
	}

	/**
	 * @param behavior
	 *            the behavior to set
	 */
	public final void setBehavior(FigureAnimationBehavior behavior) {
		if (behavior == null)
			throw new IllegalArgumentException(
					"Cannot set a PlayAnimationAction grid property to null");
		this.behavior = behavior;
	}

	/**
	 * @return the universe
	 */
	public final FigureUniverse getUniverse() {
		return universe;
	}

	/**
	 * @param universe the universe to set
	 */
	public final void setUniverse(FigureUniverse universe) {
		if (universe == null) throw new IllegalArgumentException("Cannot set a PlayAnimationAction universe property to null");
		this.universe = universe;
	}

}
