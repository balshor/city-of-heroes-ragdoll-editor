package cohDemoEditor.ragdollAnimator.swing;

import java.awt.*;
import javax.swing.*;
import cohDemoEditor.ragdollAnimator.*;
import cohDemoEditor.ragdollAnimator.swing.action.AddKeyFrameAction;
import cohDemoEditor.ragdollAnimator.swing.action.RemoveKeyFrameAction;

@SuppressWarnings("serial")
public class KeyFrameGridPanel extends JPanel {

	private JTable table;
	private AddKeyFrameAction addAction;
	private RemoveKeyFrameAction removeAction;

	public KeyFrameGridPanel(KeyFrameGrid grid) {
		if (grid == null)
			throw new IllegalArgumentException(
					"Cannot construct a new KeyFrameGridPanel with a null KeyFrameGrid");
		this.setLayout(new BorderLayout());
		table = new JTable();
		table.setModel(grid);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.setColumnSelectionAllowed(true);

		final JScrollPane scrollPane = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		this.add(scrollPane, BorderLayout.CENTER);
		final JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

		addAction = new AddKeyFrameAction(table);
		addAction.setMode(AddKeyFrameAction.JOPTIONPANE_MODE);
		buttonPanel.add(new JButton(addAction));
		removeAction = new RemoveKeyFrameAction(table);
		removeAction.setMode(RemoveKeyFrameAction.JOPTIONPANE_MODE);
		buttonPanel.add(new JButton(removeAction));
		this.add(buttonPanel, BorderLayout.SOUTH);

		table.getColumnModel().getSelectionModel().setSelectionInterval(0, 1);
	}

	public JTable getTable() {
		return table;
	}

	public void setKeyFrameGrid(KeyFrameGrid grid) {
		table.setModel(grid);
		table.getColumnModel().getSelectionModel().setSelectionInterval(0, 1);		
	}
	
}
