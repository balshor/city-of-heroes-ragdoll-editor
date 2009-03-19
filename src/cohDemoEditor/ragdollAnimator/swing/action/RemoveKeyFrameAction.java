package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;

import javax.swing.JTable;

import cohDemoEditor.ragdollAnimator.KeyFrameGrid;
import cohDemoEditor.ragdollAnimator.swing.RagdollAnimator;

/**
 * Removes a key frame.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class RemoveKeyFrameAction extends ErrorHandlingAction {

	private JTable table;

	public RemoveKeyFrameAction(JTable table) {
		if (table == null)
			throw new IllegalArgumentException(
					"Cannot construct a new AddKeyFrameAction with null table or grid");
		this.table = table;
		setParent(table);
		putValue(SHORT_DESCRIPTION, "Removes the selected key frame");
		putValue(NAME, "Remove Key Frame");
	}

	@Override
	public void doAction(ActionEvent e) {
		final int index = table.getSelectedColumn();
		if (index == 1)
			throw new RuntimeException("The first key frame is not removable.");
		final KeyFrameGrid grid = RagdollAnimator.getRagdollAnimator().getKeyFrameGrid();
		grid.remove(grid.get(index - 1));
	}

	public void setTable(JTable table) {
		if (table == null)
			throw new IllegalArgumentException("Cannot set the table to null.");
		this.table = table;
	}

}
