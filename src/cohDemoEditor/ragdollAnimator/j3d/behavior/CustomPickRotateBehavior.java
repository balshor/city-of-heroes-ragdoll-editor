package cohDemoEditor.ragdollAnimator.j3d.behavior;

import java.util.Enumeration;

import javax.media.j3d.Bounds;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.PickInfo;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import cohDemoEditor.ragdollAnimator.j3d.Bone;
import cohDemoEditor.ragdollAnimator.j3d.Figure;
import cohDemoEditor.ragdollAnimator.j3d.FigureUniverse;
import cohDemoEditor.ragdollAnimator.swing.KeyFramePanel;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.pickfast.behaviors.PickRotateBehavior;
import com.sun.j3d.utils.pickfast.behaviors.PickingCallback;

/**
 * This behavior allows the user to drag the various bones around to reposition
 * them. We modify the basic PickRotateBehavior so it can be completely
 * enabled/disabled and so we can force the target to be either the picked bone
 * or a specific bone selected in a key frame panel. To accomplish the latter,
 * we use the technique from PickRotateBehavior of storing a separate
 * MouseRotate behavior that we will activate whenever we are rotating a fixed
 * bone.
 * 
 * @author Darren
 */
public class CustomPickRotateBehavior extends PickRotateBehavior implements
		CanBeDisabled {

	private final Canvas3D canvas;
	private boolean enabled = true;
	private boolean oldButtonPress = false;
	private final FigureUniverse universe;
	private final Figure figure;
	private MouseRotate mouseRotate;
	private KeyFramePanel keyFramePanel;
	private PickingCallback pickingCallback;

	public CustomPickRotateBehavior(Figure figure, Canvas3D canvas,
			Bounds bounds, FigureUniverse universe) {
		super(figure, canvas, bounds, PickInfo.PICK_GEOMETRY);
		if (universe == null)
			throw new IllegalArgumentException(
					"Cannot have a null universe for a CustomPickRotateBehavior");
		this.canvas = canvas;
		this.universe = universe;
		this.figure = figure;
		mouseRotate = new MouseRotate(MouseRotate.MANUAL_WAKEUP);
		mouseRotate.setTransformGroup(currGrp);
		mouseRotate.setSchedulingBounds(new javax.media.j3d.BoundingSphere(
				new javax.vecmath.Point3d(0, 0, 0), 10));
		figure.addChild(mouseRotate);
	}

	public void setKeyFramePanel(KeyFramePanel keyFramePanel) {
		this.keyFramePanel = keyFramePanel;
	}

	/*
	 * We need to disable camera mode when draging around bones or else weird
	 * things happen when we drag the mouse into a camera window.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processStimulus(Enumeration criteria) {
		super.processStimulus(criteria);
		if (buttonPress != oldButtonPress) {
			if (buttonPress) {
				universe.setMode(universe.getMode()
						& ~FigureUniverse.CAMERA_MODE);
			} else {
				universe.setMode(universe.getMode()
						| FigureUniverse.CAMERA_MODE);
			}
			oldButtonPress = buttonPress;
		}
	}

	/*
	 * This is where we decide how to change the figure. Either we pass to
	 * super.updateScene to modify the picked bone or we activate mouseRotate to
	 * modify the selected bone.
	 */
	@Override
	public void updateScene(int x, int y) {
		if (canvas != mevent.getComponent() || !enabled)
			return;
		if (keyFramePanel == null || !keyFramePanel.isUseSelected()) {
			super.updateScene(x, y);
			return;
		}
		final int boneIndex = keyFramePanel.getSelectedBone();
		if (boneIndex >= 0) {
			final TransformGroup tg = figure.get(boneIndex).getTransformGroup(
					Bone.ROTATION_TG);
			mouseRotate.setTransformGroup(tg);
			mouseRotate.wakeup();
		}
	}

	/**
	 * @return the enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled
	 *            the enabled to set
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
		this.oldButtonPress = !this.buttonPress;
	}

	/*
	 * Ensures that the callback is properly set with the mouseRotate behavior.
	 */
	@Override
	public void setupCallback(PickingCallback callback) {
		super.setupCallback(callback);
		if (callback == null) {
			mouseRotate.setupCallback(null);
		} else {
			mouseRotate.setupCallback(this);
		}
		pickingCallback = callback;
	}

	@Override
	public void transformChanged(int type, Transform3D transform) {
		if (keyFramePanel.isUseSelected()) {
			if (mouseRotate.getTransformGroup() != null) {
				pickingCallback.transformChanged(type, mouseRotate
						.getTransformGroup());
			}
		} else {
			super.transformChanged(type, transform);
		}
	}

}
