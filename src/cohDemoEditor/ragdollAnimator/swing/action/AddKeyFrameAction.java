package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import cohDemoEditor.ragdollAnimator.*;
import cohDemoEditor.ragdollAnimator.swing.RagdollAnimator;

/**
 * This action adds a new key frame.
 * 
 * @author Darrn
 * 
 */
@SuppressWarnings("serial")
public class AddKeyFrameAction extends ErrorHandlingAction {

	public static final long DEFAULT_KEYFRAME_SPACING = 1000L;

	private JTable table;

	/**
	 * Creates a new AddKeyFrameAction.
	 * 
	 * @param table
	 *            the table that determines where to add the new key frame
	 * @param grid
	 *            the grid to add the key frame to
	 */
	public AddKeyFrameAction(JTable table) {
		if (table == null)
			throw new IllegalArgumentException(
					"Cannot construct a new AddKeyFrameAction with null table or grid");
		this.table = table;
		setParent(table);
		putValue(SHORT_DESCRIPTION,
				"Adds a new key frame after the selected key frame");
		putValue(NAME, "Add Key Frame");
	}

	@Override
	public void doAction(ActionEvent e) {
		final int index = table.getSelectedColumn();
		if (index == -1)
			throw new RuntimeException("No key frame is selected.");
		if (index == 0)
			throw new RuntimeException(
					"Key frames cannot be added before the first key frame.");
		final KeyFrameGrid grid = RagdollAnimator.getRagdollAnimator().getKeyFrameGrid();
		final KeyFrame kf = grid.get(index - 1);
		final KeyFrame nextkf = kf.getNextKeyFrame();
		final long time;
		if (nextkf == null) {
			time = kf.getTime() + DEFAULT_KEYFRAME_SPACING;
		} else {
			final long timespacing = nextkf.getTime() - kf.getTime();
			if (timespacing == 1)
				throw new RuntimeException(
						"Cannot insert a new key frame between times "
								+ kf.getTime() + " and " + nextkf.getTime());
			time = (kf.getTime() + nextkf.getTime()) / 2;
		}
		final KeyFrame newkf = new KeyFrame();
		newkf.setTime(time);
		grid.add(newkf);
	}

	public void setTable(JTable table) {
		if (table == null)
			throw new IllegalArgumentException("Cannot set the table to null.");
		this.table = table;
	}

}
