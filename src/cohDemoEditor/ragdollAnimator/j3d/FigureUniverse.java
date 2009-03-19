package cohDemoEditor.ragdollAnimator.j3d;

import java.util.HashSet;
import java.util.Set;
import javax.media.j3d.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix3d;
import javax.vecmath.Vector4d;

import cohDemoEditor.ragdollAnimator.KeyFrame;
import cohDemoEditor.ragdollAnimator.j3d.behavior.*;
import cohDemoEditor.ragdollAnimator.swing.KeyFramePanel;

import com.sun.j3d.utils.behaviors.mouse.MouseBehavior;
import com.sun.j3d.utils.pickfast.behaviors.*;

/**
 * A FigureUniverse is Universe that allows for a single Locale and multiple
 * Views. It also provides methods for automatically creating new fixed or
 * adjustable views. Fixed views are created with behaviors that allow the user
 * to rotate bones with the mouse. Adjustable views are created with behaviors
 * that allow the user to move the view with the mouse.
 * 
 * @author Darren
 * 
 */
public class FigureUniverse extends VirtualUniverse implements
		ListSelectionListener {

	protected Locale locale;
	private BranchGroup viewBranchGroup;
	private Set<CanBeDisabled> cameraModeBehaviors = new HashSet<CanBeDisabled>();
	private Set<CanBeDisabled> editModeBehaviors = new HashSet<CanBeDisabled>();
	private int mode = CAMERA_MODE | EDIT_MODE;

	public static final int CAMERA_MODE = 1;
	public static final int EDIT_MODE = 2;

	/**
	 * Create a new FigureUniverse.
	 */
	public FigureUniverse() {
		locale = new Locale(this);
		viewBranchGroup = new BranchGroup();
		viewBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
		viewBranchGroup.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
		locale.addBranchGraph(viewBranchGroup);
	}

	/**
	 * Adds a new BranchGraph to the locale.
	 * 
	 * @param bg
	 */
	public void addBranchGraph(BranchGroup bg) {
		locale.addBranchGraph(bg);
	}

	/**
	 * Helper to create and add a new view platform to this universe.
	 * 
	 * @param canvas
	 * @param projectionPolicy
	 * @return
	 */
	private ViewPlatform createViewPlatform(Canvas3D canvas,
			int projectionPolicy) {
		View v = new View();
		ViewPlatform vp = new ViewPlatform();
		vp.setViewAttachPolicy(View.NOMINAL_SCREEN);
		PhysicalBody pb = new PhysicalBody();
		PhysicalEnvironment pe = new PhysicalEnvironment();
		v.addCanvas3D(canvas);
		v.setPhysicalBody(pb);
		v.setPhysicalEnvironment(pe);
		v.attachViewPlatform(vp);
		v.setProjectionPolicy(projectionPolicy);
		return vp;
	}

	/**
	 * Create a fixed view platform.
	 * 
	 * @param canvas3d
	 *            the canvas that will display the view
	 * @param offset
	 *            a Transform3D that defines where the view platform will be
	 *            located
	 * @param figure
	 *            the target figure that will be the target of the behaviors
	 * @param panel
	 *            the KeyFramePanel that determines whether we are in pick or
	 *            selected mode
	 */
	public void createFixedViewPlatform(Canvas3D canvas3d, Transform3D offset,
			final Figure figure, final KeyFramePanel panel) {
		ViewPlatform vp = createViewPlatform(canvas3d, View.PARALLEL_PROJECTION);
		BranchGroup bp = new BranchGroup();
		if (offset != null) {
			final TransformGroup tg = new TransformGroup(offset);
			tg.addChild(vp);
			bp.addChild(tg);
		} else {
			bp.addChild(vp);
		}
		viewBranchGroup.addChild(bp);
		locale.removeBranchGraph(figure);
		final CustomPickRotateBehavior pfb = new CustomPickRotateBehavior(
				figure, canvas3d, new BoundingBox(), this);
		pfb.setKeyFramePanel(panel);
		addBehavior(pfb, EDIT_MODE);
		pfb.setupCallback(new PickingCallback() {

			/**
			 * This method will be called on one of the behavior threads.
			 */
			@Override
			public void transformChanged(int type, TransformGroup tg) {
				if (tg == null) {
					return;
				}
				final Object userData = tg.getUserData();
				if (!Bone.class.isAssignableFrom(userData.getClass())) {
					return;
				}
				final Bone bone = (Bone) userData;
				final int index = figure.indexOf(bone);
				final Matrix3d matrix = new Matrix3d();
				final Transform3D transform = new Transform3D();
				tg.getTransform(transform);
				transform.get(matrix);
				final AxisAngle4d aa4d = new AxisAngle4d();
				aa4d.set(matrix);
				
				final KeyFrame kf = panel.getKeyFrame();

								
				final Vector4d values = new Vector4d();
				// FigureAnimationBehavior.matrixToEuler(matrix, values);
				values.setX(aa4d.getX()*aa4d.getAngle());
				values.setY(aa4d.getY()*aa4d.getAngle());
				values.setZ(aa4d.getZ()*aa4d.getAngle());
				final Vector4d destination = kf.get(index);
				if (destination != null) {
					values.setW(destination.getW());
				} else {
					values.setW(0);
				}

				kf.set(index, values);
			}
		});
		figure.addChild(pfb);
		locale.addBranchGraph(figure);
	}

	public void createAdjustableViewPlatform(Canvas3D canvas3d,
			Transform3D offset) {
		final TransformGroup translationGroup = new TransformGroup();
		translationGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		final TransformGroup rotateGroup = new TransformGroup();
		rotateGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		final TransformGroup zoomGroup;
		if (offset != null) {
			zoomGroup = new TransformGroup(offset);
		} else {
			zoomGroup = new TransformGroup();
		}
		zoomGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		final BoundingLeaf boundingLeaf = new BoundingLeaf(new BoundingBox());
		zoomGroup.addChild(boundingLeaf);

		MouseBehavior behavior = new CBDMouseTranslate(canvas3d);
		behavior.setTransformGroup(translationGroup);
		behavior.setSchedulingBoundingLeaf(boundingLeaf);
		zoomGroup.addChild(behavior);
		// addBehavior((CanBeDisabled) behavior, CAMERA_MODE);

		behavior = new CBDMouseRotate(canvas3d);
		behavior.setTransformGroup(rotateGroup);
		behavior.setSchedulingBoundingLeaf(boundingLeaf);
		translationGroup.addChild(behavior);
		// addBehavior((CanBeDisabled) behavior, CAMERA_MODE);

		behavior = new CBDMouseZoom(canvas3d);
		behavior.setTransformGroup(zoomGroup);
		behavior.setSchedulingBoundingLeaf(boundingLeaf);
		zoomGroup.addChild(behavior);
		// addBehavior((CanBeDisabled) behavior, CAMERA_MODE);

		behavior = new CBDMouseWheelZoom(canvas3d);
		behavior.setTransformGroup(zoomGroup);
		behavior.setSchedulingBoundingLeaf(boundingLeaf);
		zoomGroup.addChild(behavior);
		// addBehavior((CanBeDisabled) behavior, CAMERA_MODE);

		ViewPlatform vp = createViewPlatform(canvas3d,
				View.PERSPECTIVE_PROJECTION);
		zoomGroup.addChild(vp);

		BranchGroup bp = new BranchGroup();
		bp.addChild(translationGroup);
		translationGroup.addChild(rotateGroup);
		rotateGroup.addChild(zoomGroup);
		viewBranchGroup.addChild(bp);
	}

	/**
	 * The mode of the FigureUniverse determines which behaviors are active. The
	 * two types of behaviors are edit mode, where mouse movements correspond to
	 * changes in bone positions, and camera mode, where mouse movements
	 * correspond to changes in camera positions. These can be independently
	 * turned on or off with this method.
	 * 
	 * @param mode
	 *            the mode to set: either CAMERA_MODE, EDIT_MODE, or a bitwise
	 *            combination of the two
	 */
	public void setMode(int mode) {
		if (this.mode == mode)
			return;
		if ((mode & CAMERA_MODE) != (this.mode & CAMERA_MODE)) {
			for (CanBeDisabled cbd : cameraModeBehaviors) {
				cbd.setEnabled((mode & CAMERA_MODE) != 0);
			}
		}
		if ((mode & EDIT_MODE) != (this.mode & EDIT_MODE)) {
			for (CanBeDisabled cbd : editModeBehaviors) {
				cbd.setEnabled((mode & EDIT_MODE) != 0);
			}
		}
		this.mode = mode;
	}

	/**
	 * Returns the current mode.
	 * 
	 * @return
	 */
	public int getMode() {
		return mode;
	}

	/**
	 * Removes the given behavior from this FigureUniverse.
	 * 
	 * @param cbd
	 *            the behavior to remove
	 */
	public void removeBehavior(Object cbd) {
		editModeBehaviors.remove(cbd);
		cameraModeBehaviors.remove(cbd);
	}

	/**
	 * Adds the given behavior with the given mode to be enabled or disabled
	 * appropriately
	 * 
	 * @param behavior
	 *            the behavior to add
	 * @param mode
	 *            the mode(s) to add the behavior to
	 */
	public void addBehavior(CanBeDisabled behavior, int mode) {
		if (mode == CAMERA_MODE) {
			cameraModeBehaviors.add(behavior);
		} else if (mode == EDIT_MODE) {
			editModeBehaviors.add(behavior);
		} else {
			throw new IllegalArgumentException("Unknown mode.");
		}
		behavior.setEnabled((this.mode & mode) != 0);
	}

	/**
	 * A FigureUniverse is a ListSelectionListener that listens to a JTable
	 * displaying all of the key frames. When the user selects a key frame, any
	 * playback should pause and we should therefore enable both camera and edit
	 * modes. This method performs that last step. (Playback pausing is
	 * accomplished through the FigureAnimationBehavior.)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		setMode(CAMERA_MODE | EDIT_MODE);
	}

}