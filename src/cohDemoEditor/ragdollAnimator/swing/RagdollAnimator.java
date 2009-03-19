package cohDemoEditor.ragdollAnimator.swing;

import java.awt.*;
import java.awt.event.ActionEvent;

import javax.media.j3d.*;
import javax.swing.*;
import javax.vecmath.Vector3d;

import cohDemoEditor.ragdollAnimator.*;
import cohDemoEditor.ragdollAnimator.j3d.*;
import cohDemoEditor.ragdollAnimator.swing.action.*;

@SuppressWarnings("serial")
public class RagdollAnimator extends JFrame {

	private Figure figure;
	private KeyFrameGrid grid;
	private FigurePositionInterpolator interpolator;
	private JDialog xFrame, yFrame, zFrame;
	private final KeyFramePanel kfPanel;
	private final KeyFrameGridPanel kfgPanel;

	public static final int DEFAULT_CANVAS_SIZE = 250;

	private static RagdollAnimator ragdollAnimator;

	public static RagdollAnimator getRagdollAnimator() {
		if (ragdollAnimator == null) {
			ragdollAnimator = new RagdollAnimator();
		}
		return ragdollAnimator;
	}

	public KeyFrameGrid getKeyFrameGrid() {
		return grid;
	}

	public void setKeyFrameGrid(KeyFrameGrid grid) {
		this.grid = grid;
		kfgPanel.setKeyFrameGrid(grid);
		kfPanel.setKeyFrameGrid(grid);
		interpolator.setKeyFrameGrid(grid);
	}

	public Figure getFigure() {
		return figure;
	}

	public KeyFramePanel getKeyFramePanel() {
		return kfPanel;
	}

	public KeyFrameGridPanel getKeyFrameGridPanel() {
		return kfgPanel;
	}

	/**
	 * Create a new RagdollAnimator
	 */
	private RagdollAnimator() {
		super("Ragdoll Animator");
		JPopupMenu.setDefaultLightWeightPopupEnabled(false);

		// set up the universe
		this.figure = new Figure();
		this.grid = new KeyFrameGrid();

		// TODO remove this stuff when integration testing with CoH is complete
		// populate with some initial keyframes for testing purposes
		for (int i = 1; i < 12; i++) {
			final KeyFrame kf = new KeyFrame();
			kf.setTime(500L * i);
//			kf.set(i - 1, new javax.vecmath.Vector4d(0, 0, 0, 0));
//			if (i < 11) {
//				kf.set(i, new javax.vecmath.Vector4d(2*Math.PI, 0, 0, 0));
//			}
			grid.add(kf);
		}

		interpolator = figure.getInterpolator();
		interpolator.setKeyFrameGrid(grid);
		FigureUniverse universe = new FigureUniverse();
		universe.addBranchGraph(figure);
		universe.addBehavior(figure.getBehavior(), FigureUniverse.CAMERA_MODE);

		final Container container = getContentPane();
		container.setLayout(new BorderLayout());

		// create the table panels (Center)
		kfPanel = new KeyFramePanel(grid.get(0));
		kfgPanel = new KeyFrameGridPanel(grid);
		kfgPanel.getTable().getColumnModel().getSelectionModel()
				.addListSelectionListener(kfPanel);
		kfPanel.setKeyFrameGridTable(kfgPanel.getTable());
		kfPanel.setKeyFrameGrid(grid);
		final JSplitPane jsp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				true, kfPanel, kfgPanel);
		container.add(jsp, BorderLayout.CENTER);

		// create the three default fixed 3d views
		final Vector3d defaultDistanceVector = new Vector3d(0, 0, 2.41);
		Canvas3D canvas = createCanvas();
		Transform3D transform = new Transform3D();
		transform.transform(defaultDistanceVector);
		universe.createFixedViewPlatform(canvas, transform, figure, kfPanel);
		zFrame = new JDialog(this, "Front View");
		zFrame.add(canvas);
		zFrame.pack();
		zFrame.setSize(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE);
		zFrame.setVisible(true);

		canvas = createCanvas();
		Transform3D rotation = new Transform3D();
		rotation.rotX(-Math.PI / 2);
		transform.mul(rotation);
		universe.createFixedViewPlatform(canvas, transform, figure, kfPanel);
		yFrame = new JDialog(this, "Top View");
		yFrame.add(canvas);
		yFrame.pack();
		yFrame.setSize(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE);
		yFrame.setLocation(DEFAULT_CANVAS_SIZE, 0);
		yFrame.setVisible(true);

		canvas = createCanvas();
		transform.setIdentity();
		transform.transform(defaultDistanceVector);
		rotation.rotY(Math.PI / 2);
		transform.mul(rotation);
		universe.createFixedViewPlatform(canvas, transform, figure, kfPanel);
		xFrame = new JDialog(this, "Side View");
		xFrame.add(canvas);
		xFrame.pack();
		xFrame.setSize(DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE);
		xFrame.setLocation(2 * DEFAULT_CANVAS_SIZE, 0);
		xFrame.setVisible(true);

		// create the adjustable view dialog
		canvas = createCanvas();
		transform.setIdentity();
		transform.transform(defaultDistanceVector);
		universe.createAdjustableViewPlatform(canvas, transform);
		JDialog aFrame = new JDialog(this, "Adjustable View");
		aFrame.add(canvas);
		aFrame.pack();
		aFrame.setSize(2 * DEFAULT_CANVAS_SIZE, DEFAULT_CANVAS_SIZE + 350);
		aFrame.setLocation(3 * DEFAULT_CANVAS_SIZE, 0);
		aFrame.setVisible(true);

		// Create the play panel (North)
		final JPanel playPanel = new JPanel();
		playPanel.setLayout(new FlowLayout());
		final Open3DViewDialog open3dViewDialog = new Open3DViewDialog(this,
				universe);
		Action action = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				open3dViewDialog.setVisible(true);
			}
		};
		action.putValue(Action.NAME, "Open View");
		playPanel.add(new JButton(action));
		final Action playAction = new PlayAnimationAction(figure.getBehavior(),
				universe);
		playPanel.add(new JButton(playAction));
		final Action stopAction = new StopAnimationAction(figure.getBehavior(),
				universe);
		playPanel.add(new JButton(stopAction));
		final Action saveAction = new SaveAction();
		playPanel.add(new JButton(saveAction));
		final Action loadAction = new LoadAction(kfgPanel, kfPanel);
		playPanel.add(new JButton(loadAction));
		final Action exportAction = new ExportAction(figure.getInterpolator());
		playPanel.add(new JButton(exportAction));
		container.add(playPanel, BorderLayout.NORTH);

		// finish configuring parameters
		figure.getBehavior().setKeyFrameGridPanel(kfgPanel);
		kfgPanel.getTable().getColumnModel().getSelectionModel()
				.addListSelectionListener(universe);

		// Set default location and size.
		// TODO load this stuff from configuration file
		this.setLocation(0, DEFAULT_CANVAS_SIZE);
		this.setSize(750, 350);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates a new RagdollAnimator and makes it visible. Also changes the LaF
	 * to the system LaF.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (InstantiationException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
					System.exit(1);
				} catch (UnsupportedLookAndFeelException e) {
					e.printStackTrace();
					System.exit(1);
				}
				getRagdollAnimator().setVisible(true);
			}
		});
	}

	/**
	 * Convenience method that creates a new Canvas3D based on the best
	 * configuration for the default screen device.
	 * 
	 * @return
	 */
	public static Canvas3D createCanvas() {
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		GraphicsConfigTemplate3D gct = new GraphicsConfigTemplate3D();
		GraphicsConfiguration gc = gd.getBestConfiguration(gct);
		return new Canvas3D(gc);
	}

}
