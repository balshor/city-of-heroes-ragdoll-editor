package cohDemoEditor.ragdollAnimator.j3d;

import javax.media.j3d.*;
import javax.vecmath.*;

/**
 * This class encapsulates the transformations that comprise a bone's position.
 * 
 * @author Darren
 * 
 */
public class Bone {

	private Vector3f transVector = new Vector3f();
	private Vector3f offsetVector = new Vector3f();
	private Vector3d pyrVector = new Vector3d();

	private TransformGroup[] tgs;

	private static final int NUM_TGS = 4;
	public static final int OFFSET_TG = 1;
	public static final int ROTATION_TG = 2;
	public static final int OFFSET_INV_TG = 3;
	public static final int TRANSLATION_TG = 0;

	public Bone() {
		tgs = new TransformGroup[NUM_TGS];
		for (int i = 0; i < NUM_TGS; i++) {
			tgs[i] = new TransformGroup();
			if (i != 0) {
				tgs[i - 1].addChild(tgs[i]);
			}
			tgs[i].setUserData(this);
		}

		tgs[ROTATION_TG].setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		tgs[ROTATION_TG].setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		tgs[ROTATION_TG].setCapability(Node.ENABLE_PICK_REPORTING);
		computeOffset();
		computeTranslation();
		computePYR();
	}

	public TransformGroup getTopTransformGroup() {
		return tgs[0];
	}

	public TransformGroup getBottomTransformGroup() {
		return tgs[NUM_TGS - 1];
	}

	public TransformGroup getTransformGroup(int transformGroupNumber) {
		return tgs[transformGroupNumber];
	}

	/**
	 * @return the offsetX
	 */
	public final float getOffsetX() {
		return offsetVector.getX();
	}

	/**
	 * @return the offsetY
	 */
	public final float getOffsetY() {
		return offsetVector.getY();
	}

	/**
	 * @return the offsetZ
	 */
	public final float getOffsetZ() {
		return offsetVector.getZ();
	}

	/**
	 * @return the pitch
	 */
	public final double getPitch() {
		return pyrVector.getX();
	}

	/**
	 * @return the yaw
	 */
	public final double getYaw() {
		return pyrVector.getY();
	}

	/**
	 * @return the roll
	 */
	public final double getRoll() {
		return pyrVector.getZ();
	}

	/**
	 * @return the transX
	 */
	public final float getTransX() {
		return transVector.getX();
	}

	/**
	 * @return the transY
	 */
	public final float getTransY() {
		return transVector.getY();
	}

	/**
	 * @return the transZ
	 */
	public final float getTransZ() {
		return transVector.getZ();
	}

	/**
	 * @param transX
	 *            the transX to set
	 */
	public final void setTransX(float transX) {
		transVector.setX(transX);
		computeTranslation();
	}

	/**
	 * @param transY
	 *            the transY to set
	 */
	public final void setTransY(float transY) {
		transVector.setY(transY);
		computeTranslation();
	}

	/**
	 * @param transZ
	 *            the transZ to set
	 */
	public final void setTransZ(float transZ) {
		transVector.setZ(transZ);
		computeTranslation();
	}

	/**
	 * @param offsetX
	 *            the offsetX to set
	 */
	public final void setOffsetX(float offsetX) {
		offsetVector.setX(offsetX);
		computeOffset();
	}

	/**
	 * @param offsetY
	 *            the offsetY to set
	 */
	public final void setOffsetY(float offsetY) {
		offsetVector.setY(offsetY);
		computeOffset();
	}

	/**
	 * @param offsetZ
	 *            the offsetZ to set
	 */
	public final void setOffsetZ(float offsetZ) {
		offsetVector.setZ(offsetZ);
		computeOffset();
	}

	/**
	 * @param pitch
	 *            the pitch to set
	 */
	public final void setPitch(double pitch) {
		pyrVector.setX(pitch);
		computePYR();
	}

	/**
	 * @param yaw
	 *            the yaw to set
	 */
	public final void setYaw(double yaw) {
		pyrVector.setY(yaw);
		computePYR();
	}

	/**
	 * @param roll
	 *            the roll to set
	 */
	public final void setRoll(double roll) {
		pyrVector.setZ(roll);
		computePYR();
	}

	/**
	 * Convenience method to set all three offsets in one method call.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public final void setOffset(float x, float y, float z) {
		offsetVector.set(x, y, z);
		computeOffset();
	}

	/**
	 * Convenience method to set all three PYR values in one method call.
	 * 
	 * @param pitch
	 * @param yaw
	 * @param roll
	 */
	public final void setPYR(double pitch, double yaw, double roll) {
		pyrVector.set(pitch, yaw, roll);
		computePYR();
	}

	public final void setPYR(Tuple4d pyrr) {
		pyrVector.set(pyrr.getX(), pyrr.getY(), pyrr.getZ());
		computePYR();
	}

	public final void setTranslation(float x, float y, float z) {
		transVector.set(x, y, z);
		computeTranslation();
	}

	private void computeOffset() {
		final Transform3D offsetTranslation = new Transform3D();
		offsetTranslation.setTranslation(offsetVector);
		tgs[OFFSET_TG].setTransform(offsetTranslation);
		offsetTranslation.invert();
		tgs[OFFSET_INV_TG].setTransform(offsetTranslation);
	}

	private void computePYR() {
		final Transform3D pyrRotation = new Transform3D();
		final double mag = pyrVector.length();
		if (mag < 1.0e-12) {
			pyrRotation.setIdentity();
		} else {
			final AxisAngle4d aa4d = new AxisAngle4d(pyrVector.getX() / mag,
					pyrVector.getY() / mag, pyrVector.getZ() / mag, mag);
			System.out.println(aa4d);
			pyrRotation.set(aa4d);
		}
		tgs[ROTATION_TG].setTransform(pyrRotation);
	}

	private void computeTranslation() {
		final Transform3D translation = new Transform3D();
		translation.setTranslation(transVector);
		tgs[TRANSLATION_TG].setTransform(translation);
	}

}
