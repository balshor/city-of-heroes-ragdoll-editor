package cohDemoEditor.ragdollAnimator.swing;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.Vector4d;

import cohDemoEditor.ragdollAnimator.*;
import cohDemoEditor.ragdollAnimator.swing.action.ErrorHandlingAction;

/**
 * A KeyFramePanel is a Swing component that contains a JTable for displaying
 * the contents of a single KeyFrame. It also contains a few buttons that zero
 * or unset bone positions and a check box that determines whether fixed views
 * use the currently selected bone as the rotate target.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class KeyFramePanel extends JPanel implements ListSelectionListener {

	// current key frame to display
	private KeyFrame keyFrame;

	// the table that displays the key frame
	private JTable keyFrameTable;

	// the table we listen to in order to figure out which key frame to display
	private JTable keyFrameGridTable;

	// the check box that determines the pickmousebehavior target
	private JCheckBox useSelectedButton;

	private KeyFrameGrid grid;
	private int lastKeyFrameIndex = 0;

	/**
	 * Creates a new KeyFramePanel
	 * 
	 * @param keyFrame
	 */
	public KeyFramePanel(KeyFrame keyFrame) {
		if (keyFrame == null)
			throw new IllegalArgumentException(
					"Cannot create a new KeyFramePanel with a null KeyFrame.");
		this.keyFrame = keyFrame;
		setLayout(new BorderLayout());
		keyFrameTable = new JTable(keyFrame);
		keyFrameTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		keyFrameTable.setColumnSelectionAllowed(false);
		keyFrameTable.setRowSelectionAllowed(true);
		add(new JScrollPane(keyFrameTable), BorderLayout.CENTER);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		ErrorHandlingAction unsetAction = new ErrorHandlingAction() {
			@Override
			protected void doAction(ActionEvent e) {
				final int index = keyFrameTable.getSelectedRow() - 1;
				if (index < 0)
					throw new RuntimeException("No bone selected");
				if (KeyFramePanel.this.keyFrame.getTime() == 0L)
					throw new RuntimeException(
							"Cannot unset an initial position.");
				KeyFramePanel.this.keyFrame.unSetPosition(index);
			}
		};
		unsetAction.setMode(ErrorHandlingAction.JOPTIONPANE_MODE);
		unsetAction.putValue(Action.SHORT_DESCRIPTION,
				"Clears the position of the selected bone.");
		unsetAction.putValue(Action.NAME, "Unset");
		buttonPanel.add(new JButton(unsetAction));

		ErrorHandlingAction zeroAction = new ErrorHandlingAction() {
			@Override
			protected void doAction(ActionEvent e) {
				final int index = keyFrameTable.getSelectedRow() - 1;
				if (index < 0)
					throw new RuntimeException("No bone selected");
				KeyFramePanel.this.keyFrame
						.set(index, new Vector4d(0, 0, 0, 0));
			}
		};
		zeroAction.setMode(ErrorHandlingAction.JOPTIONPANE_MODE);
		zeroAction.putValue(Action.SHORT_DESCRIPTION,
				"Sets the PYRS values of the selected bone to zero.");
		zeroAction.putValue(Action.NAME, "Zero");
		buttonPanel.add(new JButton(zeroAction));

		useSelectedButton = new JCheckBox("Drag Lock");
		useSelectedButton
				.setToolTipText("Drag edits will modify the selected bone.");
		buttonPanel.add(useSelectedButton);
		add(buttonPanel, BorderLayout.SOUTH);

		keyFrameTable.getSelectionModel().setSelectionInterval(1, 1);

		setPreferredSize(new java.awt.Dimension(150, 150));
	}

	/**
	 * We listen to the key frame grid table and switch the key frame to display
	 * on ListSelectionEvents.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (e != null && e.getValueIsAdjusting())
			return;
		final int keyFrameIndex = keyFrameGridTable.getSelectedColumn() - 1;
		if (keyFrameIndex < 0) {
			keyFrameGridTable.getColumnModel().getSelectionModel()
					.setSelectionInterval(0, lastKeyFrameIndex + 1);
			return;
		}

		if (keyFrameIndex >= grid.size()) {
			keyFrameGridTable.getColumnModel().getSelectionModel()
					.setSelectionInterval(0, grid.size());
			return;
		}
		lastKeyFrameIndex = keyFrameIndex;
		keyFrame.removeTableModelListener(grid);
		keyFrame = grid.get(keyFrameIndex);
		keyFrame.addTableModelListener(grid);
		keyFrameTable.setModel(keyFrame);

	}

	/**
	 * Sets the key frame grid to listen to.
	 * 
	 * @param grid
	 */
	public void setKeyFrameGrid(KeyFrameGrid grid) {
		keyFrame.removeTableModelListener(this.grid);
		keyFrame.addTableModelListener(grid);
		this.grid = grid;
		lastKeyFrameIndex = 0;
		valueChanged(null);
	}

	/**
	 * Sets the key frame grid table.
	 * 
	 * @param table
	 */
	public void setKeyFrameGridTable(JTable table) {
		keyFrameGridTable = table;
	}

	/**
	 * Gets the current key frame.
	 * 
	 * @return
	 */
	public KeyFrame getKeyFrame() {
		return keyFrame;
	}

	/**
	 * Gets the table that displays the key frame.
	 * 
	 * @return
	 */
	public JTable getTable() {
		return keyFrameTable;
	}

	/**
	 * Gets whether the "use selected bone" check box is selected
	 * 
	 * @return
	 */
	public boolean isUseSelected() {
		return useSelectedButton.isSelected();
	}

	/**
	 * Gets the currently selected bone.
	 * 
	 * @return
	 */
	public int getSelectedBone() {
		return keyFrameTable.getSelectedRow() - 1;
	}
}
