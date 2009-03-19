package cohDemoEditor.ragdollAnimator.swing;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Vector3d;

import cohDemoEditor.ragdollAnimator.j3d.FigureUniverse;

/**
 * This is a quick-and-dirty class defining a dialog that allows the user to
 * open new views in the universe.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class Open3DViewDialog extends JDialog {

	public Open3DViewDialog(final Frame owner, final FigureUniverse universe) {
		super(owner);
		setModal(false);
		setLayout(new BorderLayout());

		JPanel panel;
		Action action;
		JLabel label;

		panel = new JPanel();
		panel.setLayout(new GridLayout(3, 2));
		label = new JLabel("Distance");
		panel.add(label);
		final JTextField distTextField = new JTextField("2.41");
		panel.add(distTextField);
		label = new JLabel("Horizontal Rotation");
		panel.add(label);
		final JTextField hRotTextField = new JTextField("0");
		panel.add(hRotTextField);
		label = new JLabel("Vertical Rotation");
		panel.add(label);
		final JTextField vRotTextField = new JTextField("0");
		panel.add(vRotTextField);
		add(panel, BorderLayout.CENTER);

		panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		ButtonGroup bg = new ButtonGroup();
		final JRadioButton fixedButton = new JRadioButton("Fixed Position");
		fixedButton.setSelected(true);
		bg.add(fixedButton);
		final JRadioButton adjustableButton = new JRadioButton(
				"Adjustable Position");
		adjustableButton.setSelected(false);
		bg.add(adjustableButton);
		panel.add(fixedButton);
		panel.add(adjustableButton);
		add(panel, BorderLayout.EAST);

		label = new JLabel("Open New 3D View");
		add(label, BorderLayout.NORTH);

		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		action = new AbstractAction() {

			// Does the actual adding of the new view.
			@Override
			public void actionPerformed(ActionEvent e) {
				double dist, hRot, vRot;
				try {
					dist = Double.parseDouble(distTextField.getText());
					hRot = Double.parseDouble(hRotTextField.getText());
					vRot = Double.parseDouble(vRotTextField.getText());
				} catch (NumberFormatException nfe) {
					// TODO provide user feedback if there is a parsing
					// exception
					nfe.printStackTrace();
					return;
				}

				Transform3D offset = new Transform3D();
				offset.transform(new Vector3d(0, 0, dist));
				Transform3D transform = new Transform3D();
				transform.rotX(vRot);
				offset.mul(transform);
				transform.rotY(hRot);
				offset.mul(transform);

				final GraphicsEnvironment ge = GraphicsEnvironment
						.getLocalGraphicsEnvironment();
				final GraphicsDevice gd = ge.getDefaultScreenDevice();
				final GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
				final GraphicsConfiguration gc = gd.getBestConfiguration(gct);
				final Canvas3D canvas = new Canvas3D(gc);
				final JDialog dialog;
				if (fixedButton.isSelected()) {
					universe.createFixedViewPlatform(canvas, offset,
							RagdollAnimator.getRagdollAnimator().getFigure(),
							RagdollAnimator.getRagdollAnimator()
									.getKeyFramePanel());
					dialog = new JDialog(owner, "Fixed View");
				} else {
					universe.createAdjustableViewPlatform(canvas, offset);
					dialog = new JDialog(owner, "Adjustable View");
				}
				dialog.setLayout(new BorderLayout());
				dialog.add(canvas, BorderLayout.CENTER);
				dialog.pack();
				dialog.setSize(RagdollAnimator.DEFAULT_CANVAS_SIZE,
						RagdollAnimator.DEFAULT_CANVAS_SIZE);
				dialog.setVisible(true);
				Open3DViewDialog.this.setVisible(false);
			}
		};
		action.putValue(Action.NAME, "Open");
		action.putValue(Action.SHORT_DESCRIPTION, "Open a new 3D view.");
		panel.add(new JButton(action));
		action = new AbstractAction() {

			// Cancel, so we hide this dialog and do nothing else.
			@Override
			public void actionPerformed(ActionEvent e) {
				Open3DViewDialog.this.setVisible(false);
			}
		};
		action.putValue(Action.NAME, "Cancel");
		panel.add(new JButton(action));
		add(panel, BorderLayout.SOUTH);
		pack();
	}

}
