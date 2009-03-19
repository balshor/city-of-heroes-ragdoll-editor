package cohDemoEditor.ragdollAnimator.j3d.behavior;

import java.util.Enumeration;
import javax.media.j3d.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.vecmath.*;

import cohDemoEditor.ragdollAnimator.FigurePositionInterpolator;
import cohDemoEditor.ragdollAnimator.swing.KeyFrameGridPanel;

/**
 * This class implements the animation behavior using a
 * FigurePositionInterpolator. The start and end times of the animation are set
 * (with the times relative to the FigurePositionInterpolator's timeline), and
 * this class will automatically set up the appropriate Alpha object.
 * 
 * @author Darren
 * 
 */
public class FigureAnimationBehavior extends Behavior implements
		ListSelectionListener, CanBeDisabled {

	private boolean enabled = true;
	private FigurePositionInterpolator interpolator;
	private long startTime;
	private long endTime;
	private Alpha primaryAlpha = new Alpha();
	private float alphaCorrection = 0f;
	private KeyFrameGridPanel kfgPanel;

	private TransformGroup[] transforms = new TransformGroup[11];

	private final WakeupCriterion wakeupCriterion = (WakeupCriterion) new WakeupOnElapsedFrames(
			0);

	/**
	 * Initializes the Alpha object prior to animation. Note that the startTime
	 * and endTime properties should be set prior to initializing this behavior.
	 */
	@Override
	public void initialize() {
		primaryAlpha.setMode(Alpha.INCREASING_ENABLE);
		primaryAlpha.setIncreasingAlphaDuration(endTime - startTime);
		primaryAlpha.setIncreasingAlphaRampDuration(0);
		wakeupOn(wakeupCriterion);
	}

	/**
	 * Uses the Alpha object to compute how far along we are in the
	 * FigurePositionInterpolator's timeline. Sets the TransformGroups
	 * appropriately.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processStimulus(Enumeration enumeration) {
		if (enabled) {

			float newAlphaValue = (primaryAlpha.value() - alphaCorrection);
			if (newAlphaValue < 0)
				newAlphaValue++;
			final long now = startTime
					+ (long) (newAlphaValue * (endTime - startTime));
			final Transform3D transform = new Transform3D();
			final Vector4d v4d = new Vector4d();
			// final Vector3d v3d = new Vector3d();
			for (int i = 0; i < 11; i++) {
				interpolator.getPosition(i, now, v4d);
				// v3d.set(v4d.getX(), v4d.getY(), v4d.getZ());
				Transform3D xRot = new Transform3D();
				Transform3D yRot = new Transform3D();
				Transform3D zRot = new Transform3D();
				xRot.setEuler(new Vector3d(v4d.getX(),0,0));
				yRot.setEuler(new Vector3d(0,v4d.getY(),0));
				zRot.setEuler(new Vector3d(0,0,v4d.getZ()));
				xRot.mul(zRot);
				xRot.mul(yRot);
				// transform.setEuler(v3d);
				// v3d.scale(1/Math.PI);
				// final double mag = v3d.length();
				// final double angle = Math.abs(v3d.getX()) + Math.abs(v3d.getY()) + Math.abs(v3d.getZ());
				// final double angle = mag;
				transform.set(xRot);
//				if (mag > 1e-12) {
					// System.out.println("Bone " + i + ": (" + v3d.getX() + "," + v3d.getY() + "," + v3d.getZ() +") by " + d);
					// transform.set(new AxisAngle4d(v3d.getX()/mag, v3d.getY()/mag, v3d.getZ()/mag, angle));
					// transform.set(new Quat4d(v3d.getX(), v3d.getY(), v3d.getZ(), 1));
//				} else {
//					transform.setIdentity();
//				}
				transforms[i].setTransform(transform);
			}
		}
		wakeupOn(wakeupCriterion);
	}

	/**
	 * Getter for the FigurePositionInterpolator
	 * 
	 * @return the FigurePositionInterpolator this FigureAnimationBehavior uses
	 *         to calculate animation
	 */
	public FigurePositionInterpolator getFigurePositionInterpolator() {
		return interpolator;
	}

	/**
	 * Setter for the FigurePositionInterpolator
	 * 
	 * @param interpolator
	 *            the FigurePositionInterpolator to set
	 * @return this
	 */
	public FigureAnimationBehavior setFigurePositionInterpolator(
			FigurePositionInterpolator interpolator) {
		this.interpolator = interpolator;
		return this;
	}

	/**
	 * Sets the TransformGroups on which to act.
	 * 
	 * @param boneNumber
	 *            the primary bone number that the TransformGroup acts on
	 * @param tg
	 *            the TransformGroup to set
	 * @return this
	 */
	public FigureAnimationBehavior setTransformGroup(final int boneNumber,
			final TransformGroup tg) {
		this.transforms[boneNumber] = tg;
		return this;
	}

	/**
	 * @return the startTime
	 */
	public final long getStartTime() {
		return startTime;
	}

	/**
	 * @return the endTime
	 */
	public final long getEndTime() {
		return endTime;
	}

	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public final void setStartTime(long startTime) {
		if (endTime < startTime) {
			this.endTime = startTime;
		}
		this.startTime = startTime;
		resetAlpha();
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public final void setEndTime(long endTime) {
		if (startTime > endTime) {
			this.startTime = endTime;
		}
		this.endTime = endTime;
		resetAlpha();
	}

	/**
	 * Sets both the start and the end time.
	 * 
	 * @param startTime
	 * @param endTime
	 */
	public final void setStartAndEndTimes(long startTime, long endTime) {
		if (startTime > endTime)
			throw new IllegalArgumentException(
					"Start time must be less than or equal to end time.");
		this.startTime = startTime;
		this.endTime = endTime;
		resetAlpha();
	}

	/*
	 * This "zeros" the alpha to the time when we call the method by recording
	 * the value of the alpha. We'll use this as an offset elsewhere.
	 */
	private void resetAlpha() {
		primaryAlpha.setIncreasingAlphaDuration(endTime - startTime);
		alphaCorrection = primaryAlpha.value();
	}

	/**
	 * We listen to a KeyFrameGridPanel. When a selection changes, we "pause"
	 * the animation by setting the start and end times to the selected time.
	 * The behavior is still runnning, though.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		final int keyframeIndex = kfgPanel.getTable().getSelectedColumn() - 1;
		if (keyframeIndex < 0)
			return;
		final long time;
		if (keyframeIndex < interpolator.getKeyFrameGrid().size()) {
			time = interpolator.getKeyFrameGrid().get(keyframeIndex).getTime();
		} else {
			time = 0;
		}
		setStartTime(time);
		setEndTime(time);
	}

	/**
	 * Sets the KeyFrameGridPanel.
	 * 
	 * @param kfgPanel
	 */
	public void setKeyFrameGridPanel(KeyFrameGridPanel kfgPanel) {
		if (this.kfgPanel != null) {
			this.kfgPanel.getTable().getColumnModel().getSelectionModel()
					.removeListSelectionListener(this);
		}
		this.kfgPanel = kfgPanel;
		kfgPanel.getTable().getColumnModel().getSelectionModel()
				.addListSelectionListener(this);
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
	}

	/**
	 * Utility method that converts a rotation matrix to an Euler-angle vector.
	 * This is meant to invert the Transform3D.setEuler method. We assume that
	 * the "y-angle" has positive cosine (ie, in [-Pi/2, Pi/2]). The other two
	 * angles will be in [-Pi/2,3Pi/2].
	 * 
	 * @param matrix
	 * @return
	 */
	public static Vector3d matrixToEuler(Matrix3d matrix, Vector3d vector) {
		final double sinb = -matrix.m20;
		final double y = Math.asin(sinb);
		final double cosb = Math.cos(y);
		final double sina = matrix.m21 / cosb;
		final double sinc = matrix.m10 / cosb;
		double x = Math.asin(sina);
		double z = Math.asin(sinc);
		final double cosa = matrix.m22 / cosb;
		final double cosc = matrix.m00 / cosb;
		if (cosa < 0)
			x = Math.PI - x;
		if (cosc < 0)
			z = Math.PI - z;
		if (vector == null)
			vector = new Vector3d(x, y, z);
		else
			vector.set(x, y, z);
		return vector;
	}

	/**
	 * Same as above, except with a Vector4d. We ignore the "W" component. This
	 * is basically a copy/paste of the above method so we don't need to
	 * allocate a Vector3d, grab the coordinates, and insert them into a
	 * Vector4d.
	 * 
	 * @param matrix
	 * @param vector
	 * @return
	 */
	public static Vector4d matrixToEuler(Matrix3d matrix, Vector4d vector) {
		final double sinb = -matrix.m20;
		double y = Math.asin(sinb);
		final double cosb = Math.cos(y);
		final double sina = matrix.m21 / cosb;
		final double sinc = matrix.m10 / cosb;
		double x = Math.asin(sina);
		double z = Math.asin(sinc);
		final double cosa = matrix.m22 / cosb;
		final double cosc = matrix.m00 / cosb;
		if (cosa < 0)
			x = Math.PI - x;
		if (cosc < 0)
			z = Math.PI - z;
		if (vector == null) {
			vector = new Vector4d(x, y, z, 0);
		} else {
			vector.setX(x);
			vector.setY(y);
			vector.setZ(z);
		}
		return vector;
	}

	/**
	 * Test main to verify that the algorithm in matrixToEuler actually works.
	 * Output should be a zero vector (up to rounding error), assuming inputs
	 * are in the correct ranges: x:[-Pi/2,3Pi/2], y:[-Pi/2,Pi/2],
	 * z:[-Pi/2,3Pi/2].
	 * 
	 * @param args
	 */
	public static final void main(String[] args) {
		Vector3d vector = new Vector3d(3 * Math.PI / 4, 3 * Math.PI / 8,
				7 * Math.PI / 5);
		Transform3D transform = new Transform3D();
		transform.setEuler(vector);
		Matrix3d matrix = new Matrix3d();
		transform.get(matrix);
		vector.sub(matrixToEuler(matrix, (Vector3d) null));
		System.out.println(vector);
	}

}
