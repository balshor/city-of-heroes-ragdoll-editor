package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cohDemoEditor.ragdollAnimator.j3d.FigureUniverse;
import cohDemoEditor.ragdollAnimator.j3d.behavior.FigureAnimationBehavior;

/**
 * Stops playback by setting the start and end times of the
 * FigureAnimationBehavior both to zero. Also sets the universe mode to allow
 * both camera and edit behaviors.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class StopAnimationAction extends AbstractAction {

	private FigureAnimationBehavior behavior;
	private FigureUniverse universe;

	public StopAnimationAction(FigureAnimationBehavior behavior,
			FigureUniverse universe) {
		if (behavior == null || universe == null) {
			throw new IllegalArgumentException(
					"StopAnimationAction constructor cannot have null arguments.");
		}
		this.behavior = behavior;
		this.universe = universe;
		this.putValue(Action.NAME, "Stop");
		this.putValue(Action.SHORT_DESCRIPTION, "Stops the current animation.");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		behavior.setStartAndEndTimes(0L, 0L);
		universe.setMode(FigureUniverse.CAMERA_MODE | FigureUniverse.EDIT_MODE);
	}

	/**
	 * @return the behavior
	 */
	public final FigureAnimationBehavior getBehavior() {
		return behavior;
	}

	/**
	 * @return the universe
	 */
	public final FigureUniverse getUniverse() {
		return universe;
	}

	/**
	 * @param behavior
	 *            the behavior to set
	 */
	public final void setBehavior(FigureAnimationBehavior behavior) {
		if (behavior == null)
			throw new IllegalArgumentException(
					"Cannot set StopAnimationAction behavior property to null");
		this.behavior = behavior;
	}

	/**
	 * @param universe
	 *            the universe to set
	 */
	public final void setUniverse(FigureUniverse universe) {
		if (universe == null)
			throw new IllegalArgumentException(
					"Cannot set StopAnimationAction universe property to null.");
		this.universe = universe;
	}

}
