package cohDemoEditor.ragdollAnimator.j3d;

import static java.lang.Math.PI;
import javax.media.j3d.*;
import javax.vecmath.*;

import cohDemoEditor.ragdollAnimator.*;
import cohDemoEditor.ragdollAnimator.j3d.behavior.FigureAnimationBehavior;

import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.universe.SimpleUniverse;

/**
 * A Figure is a branch graph containing the Shape3D, TransformGroup, and
 * Behavior objects necessary to display and manipulate the 11-bone figure we
 * want to animate. It exposes sufficient methods to position and animate the
 * figure.
 * 
 * We use a simple collection of boxes and cylinders to represent the figure.
 * The rigid pieces of the figure are as follows:
 * 
 * ( 1) Lower Right Leg ( 2) Upper Right Leg ( 3) Lower Left Leg ( 4) Upper Left
 * Leg ( 5) Lower Left Arm ( 6) Upper Left Arm ( 7) Lower Right Arm ( 8) Upper
 * Right Arm ( 9) Head (10) Torso (11) Figure
 * 
 * @author Darren
 * 
 */
public class Figure extends BranchGroup {

	public static final int LOWER_RIGHT_LEG = 0;
	public static final int UPPER_RIGHT_LEG = 1;
	public static final int LOWER_LEFT_LEG = 2;
	public static final int UPPER_LEFT_LEG = 3;
	public static final int LOWER_LEFT_ARM = 4;
	public static final int UPPER_LEFT_ARM = 5;
	public static final int LOWER_RIGHT_ARM = 6;
	public static final int UPPER_RIGHT_ARM = 7;
	public static final int HEAD = 8;
	public static final int TORSO = 9;
	public static final int WAIST = 10;
	public static final String[] BONE_NAMES = { "Lower Right Leg",
			"Upper Right Leg", "Lower Left Leg", "Upper Left Leg",
			"Lower Left Arm", "Upper Left Arm", "Lower Right Arm",
			"Upper Right Arm", "Head", "Torso", "Waist" };

	private Bone[] bones;
	private FigurePositionInterpolator interpolator;
	private FigureAnimationBehavior behavior;

	private static final ColoringAttributes RED = new ColoringAttributes(1f,
			0f, 0f, ColoringAttributes.SHADE_GOURAUD);
	private static final ColoringAttributes BLUE = new ColoringAttributes(0f,
			0f, 1f, ColoringAttributes.SHADE_GOURAUD);
	private static final ColoringAttributes GREEN = new ColoringAttributes(0f,
			1f, 0f, ColoringAttributes.SHADE_GOURAUD);
	private static final ColoringAttributes YELLOW = new ColoringAttributes(1f,
			1f, 0f, ColoringAttributes.SHADE_GOURAUD);
	private static final ColoringAttributes PURPLE = new ColoringAttributes(1f,
			0f, 1f, ColoringAttributes.SHADE_GOURAUD);

	private float waistWidth = .19f;
	private float waistHeight = .1f;
	private float waistDepth = .075f;

	private float torsoWidth = .175f;
	private float torsoHeight = .15f;
	private float torsoDepth = .075f;
	private float shoulderHeight = .025f;
	private float shoulderWidth = .2f;

	private float limbLength = .125f;
	private float limbWidth = .05f;
	private float limbDepth = .08f;

	private float headHeight = .09f;
	private float headWidth = .075f;
	private float headDepth = .075f;

	public Figure() {
		setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		setCapability(BranchGroup.ALLOW_DETACH);
		this.bones = new Bone[11];
		this.interpolator = new FigurePositionInterpolator();
		this.behavior = new FigureAnimationBehavior();
		behavior.setFigurePositionInterpolator(interpolator);
		behavior.setSchedulingBounds(new BoundingBox());

		for (int i = 0; i < 11; i++) {
			bones[i] = new Bone();
			behavior.setTransformGroup(i, bones[i]
					.getTransformGroup(Bone.ROTATION_TG));
		}
		createTorso();
		createHead();
		createWaist();
		for (int i = 0; i < 8; i++) {
			createLimb(i);
		}
		connectBones();
		addChild(behavior);
	}

	public void setInterpolator(FigurePositionInterpolator interpolator) {
		this.interpolator = interpolator;
		this.behavior.setFigurePositionInterpolator(interpolator);
		for (int i = 0; i < 11; i++) {
			behavior.setTransformGroup(i, bones[i]
					.getTransformGroup(Bone.ROTATION_TG));
		}
	}

	public FigurePositionInterpolator getInterpolator() {
		return interpolator;
	}

	public FigureAnimationBehavior getBehavior() {
		return behavior;
	}

	private void connectBones() {
		// connect head to torso
		bones[TORSO].getBottomTransformGroup().addChild(
				bones[HEAD].getTopTransformGroup());
		// connect lower limbs to upper limbs
		bones[UPPER_LEFT_LEG].getBottomTransformGroup().addChild(
				bones[LOWER_LEFT_LEG].getTopTransformGroup());
		bones[UPPER_RIGHT_LEG].getBottomTransformGroup().addChild(
				bones[LOWER_RIGHT_LEG].getTopTransformGroup());
		bones[UPPER_LEFT_ARM].getBottomTransformGroup().addChild(
				bones[LOWER_LEFT_ARM].getTopTransformGroup());
		bones[UPPER_RIGHT_ARM].getBottomTransformGroup().addChild(
				bones[LOWER_RIGHT_ARM].getTopTransformGroup());
		// connect arms to torso
		bones[TORSO].getBottomTransformGroup().addChild(
				bones[UPPER_LEFT_ARM].getTopTransformGroup());
		bones[TORSO].getBottomTransformGroup().addChild(
				bones[UPPER_RIGHT_ARM].getTopTransformGroup());
		// connect torso to waist
		bones[WAIST].getBottomTransformGroup().addChild(
				bones[TORSO].getTopTransformGroup());
		// connect legs to waist
		bones[WAIST].getBottomTransformGroup().addChild(
				bones[UPPER_LEFT_LEG].getTopTransformGroup());
		bones[WAIST].getBottomTransformGroup().addChild(
				bones[UPPER_RIGHT_LEG].getTopTransformGroup());
		// connect waist to this BranchGroup
		addChild(bones[WAIST].getTopTransformGroup());
	}

	private void createHead() {
		final Appearance app = new Appearance();
		app.setColoringAttributes(PURPLE);
		final Box box = new Box(headWidth, headHeight, headDepth, app);
		box.setUserData("Head");
		bones[HEAD].setTranslation(0, headHeight + torsoHeight, 0);
		bones[HEAD].setOffset(0, -headHeight, 0);
		bones[HEAD].getBottomTransformGroup().addChild(box);
	}

	private void createLimb(int limbNumber) {
		Box box = createLegBox(BLUE);
		box.setUserData(BONE_NAMES[limbNumber]);
		if (limbNumber < 0 || limbNumber > 7)
			throw new IllegalArgumentException(
					"limbNumber must be between 0 and 7 inclusive.  argument = "
							+ limbNumber);
		switch (limbNumber) {
		case 0: // lower right leg
		case 2: // lower left leg
			bones[limbNumber].setTranslation(0, -2 * limbLength, 0);
			box = createLegBox(RED);
			bones[limbNumber].setOffset(0, limbLength, 0);
			break;
		case 4: // lower left arm
			bones[limbNumber].setTranslation(2*limbLength, 0, 0);
			box = createArmBox(RED);
			bones[limbNumber].setOffset(-limbLength, 0, 0);
			break;
		case 6: // lower right arm
			bones[limbNumber].setTranslation(-2*limbLength, 0, 0);
			box = createArmBox(RED);
			bones[limbNumber].setOffset(limbLength, 0, 0);
			break;
		case 1: // upper right leg
			bones[UPPER_RIGHT_LEG].setTranslation(-waistWidth / 2, -waistHeight
					- limbLength, 0);
			box = createLegBox(YELLOW);
			bones[limbNumber].setOffset(0, limbLength, 0);
			break;
		case 3: // upper left leg
			bones[UPPER_LEFT_LEG].setTranslation(waistWidth / 2, -waistHeight
					- limbLength, 0);
			box = createLegBox(YELLOW);
			bones[limbNumber].setOffset(0, limbLength, 0);
			break;
		case 5: // upper left arm
			bones[UPPER_LEFT_ARM].setTranslation(limbLength+shoulderWidth,torsoHeight+shoulderHeight-3*limbWidth/2, 0);
			box = createArmBox(YELLOW);
			bones[limbNumber].setOffset(-limbLength, limbWidth, 0);
			break;
		case 7: // upper right arm
			bones[UPPER_RIGHT_ARM].setTranslation(-limbLength-shoulderWidth,torsoHeight+shoulderHeight-3*limbWidth/2, 0);
			box = createArmBox(YELLOW);
			bones[limbNumber].setOffset(limbLength, limbWidth, 0);
		}
		bones[limbNumber].getBottomTransformGroup().addChild(box);
	}

	private Box createArmBox(ColoringAttributes color) {
		final Appearance appearance = new Appearance();
		appearance.setColoringAttributes(color);
		return new Box(limbLength, limbWidth, limbDepth, appearance);
	}
	
	private Box createLegBox(ColoringAttributes color) {
		final Appearance appearance = new Appearance();
		appearance.setColoringAttributes(color);
		return new Box(limbWidth, limbLength, limbDepth, appearance);
	}

	private void createWaist() {
		Appearance appearance = new Appearance();
		appearance.setColoringAttributes(GREEN);
		Box box = new Box(waistWidth, waistHeight, waistDepth, appearance);
		box.setUserData("Waist");
		bones[WAIST].getBottomTransformGroup().addChild(box);
	}

	private void createTorso() {
		Appearance appearance = new Appearance();
		appearance.setColoringAttributes(BLUE);
		Box box1 = new Box(shoulderWidth, shoulderHeight, torsoDepth,
				appearance);
		box1.setUserData("Torso1");
		Box box2 = new Box(torsoWidth, torsoHeight - shoulderHeight,
				torsoDepth, appearance);
		box2.setUserData("Torso2");
		Transform3D transform3d = new Transform3D();
		transform3d.setTranslation(new Vector3d(0,
				torsoHeight - shoulderHeight, 0));
		TransformGroup tg = new TransformGroup(transform3d);
		tg.addChild(box1);
		bones[TORSO].getBottomTransformGroup().addChild(tg);
		transform3d = new Transform3D();
		transform3d.setTranslation(new Vector3d(0, -(shoulderHeight), 0));
		tg = new TransformGroup(transform3d);
		tg.addChild(box2);
		bones[TORSO].setTranslation(0, waistHeight + torsoHeight, 0);
		bones[TORSO].setOffset(0, -torsoHeight, 0);
		bones[TORSO].getBottomTransformGroup().addChild(tg);
	}

	public void setPYR(int pieceNumber, double pitch, double yaw, double roll) {
		bones[pieceNumber].setPYR(pitch, yaw, roll);
	}

	public void setPitch(int pieceNumber, double pitch) {
		bones[pieceNumber].setPitch(pitch);
	}

	public void setYaw(int pieceNumber, double yaw) {
		bones[pieceNumber].setYaw(yaw);
	}

	public void setRoll(int pieceNumber, double roll) {
		bones[pieceNumber].setRoll(roll);
	}

	/**
	 * test main
	 * 
	 * @param args
	 *            command-line arguments (ignored)
	 */
	/**
	 * @param args
	 */
	public static final void main(String[] args) {
		SimpleUniverse universe = new SimpleUniverse(null);
		Canvas3D canvas = new Canvas3D(SimpleUniverse
				.getPreferredConfiguration());
		universe.getViewingPlatform().setNominalViewingTransform();
		Figure figure = new Figure();

		// Create test animation here
		final KeyFrameGrid grid = new KeyFrameGrid();
		figure.getInterpolator().setKeyFrameGrid(grid);
		figure.getBehavior().setStartTime(0L);
		figure.getBehavior().setEndTime(2000L);

		// kf 0: default position
		KeyFrame kf = new KeyFrame();
		kf.setTime(0L);
		for (int i = 0; i < 11; i++) {
			kf.set(i, new Vector4d(0, 0, 0, 0));
		}
		grid.add(kf);

		// kf 1: rotate arms
		kf = new KeyFrame();
		kf.setTime(1000L);
		for (int i = 0; i < 8; i++) {
			kf.set(i, new Vector4d(0, 0, PI, 0));
		}
		grid.add(kf);

		figure.compile();
		universe.addBranchGraph(figure);

		java.applet.Applet applet = new java.applet.Applet();
		applet.setLayout(new java.awt.BorderLayout());
		applet.add(canvas, java.awt.BorderLayout.CENTER);
		java.awt.Frame frame = new com.sun.j3d.utils.applet.MainFrame(applet,
				256, 256);
		frame.setVisible(false);
	}

	/**
	 * Returns the index of the given bone, or -1 if the bone is not part of
	 * this figure
	 * 
	 * @param bone
	 *            the bone to get the index of
	 * @return the index of the given bone, or -1 if the bone is not part of
	 *         this figure
	 */
	public int indexOf(Bone bone) {
		for (int i = 0; i < bones.length; i++) {
			if (bones[i].equals(bone))
				return i;
		}
		return -1;
	}

	/**
	 * Retrieves the specified bone.
	 * 
	 * @param index
	 * @return
	 */
	public Bone get(int index) {
		return bones[index];
	}
}
