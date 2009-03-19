package cohDemoEditor.ragdollAnimator;

import javax.media.j3d.Alpha;
import javax.vecmath.Tuple3i;
import javax.vecmath.Vector4d;

/**
 * A FigurePositionInterpolator calculates the position of bones at arbitrary
 * times based on the data stored in a KeyFrameGrid. It assumes that the
 * KeyFrameGrid has correctly set the previous and next KeyFrame properties of
 * each Keyframe.
 * 
 * @author Darren
 * 
 */
public class FigurePositionInterpolator {

	/*
	 * The grid to interpolate
	 */
	private KeyFrameGrid keyFrameGrid;

	/*
	 * This keyframe is used by the getPosition method so we don't need to
	 * allocate a new one every time that it's called.
	 */
	private transient KeyFrame keyFrame;

	/*
	 * This alpha is used for interpolating between key frames. We use it for
	 * ramp calculations only, not for timing.
	 */
	private Alpha alpha;

	/**
	 * Creates a new FigurePositionInterpolator.
	 */
	public FigurePositionInterpolator() {
		keyFrame = new KeyFrame();
		alpha = new Alpha();
		alpha.setMode(Alpha.INCREASING_ENABLE);
		alpha.setStartTime(0L);
		alpha.setPhaseDelayDuration(0L);
	}

	/**
	 * Returns the position of the given bone at the given time. If {@code
	 * destination} is not null, the position data will be copied into {@code
	 * destination}. Otherwise, a new copy of the position data will be
	 * returned.
	 * 
	 * @param boneNumber
	 *            the number of the bone whose position should be retrieved
	 * @param time
	 *            the time to retrieve the position
	 * @param destination
	 *            a {@code Vector4d} in which to copy the position. If this
	 *            parameter is {@code null}, a new {@code Vector4d} will be
	 *            allocated and returned
	 * @return the position of the given bone at the given time
	 */
	public Vector4d getPosition(int boneNumber, long time, Vector4d destination) {
		keyFrame.setTime(time);
		KeyFrame next = keyFrameGrid.ceiling(keyFrame);
		KeyFrame prev = keyFrameGrid.floor(keyFrame);
		while (next != null && !next.isPositionSet(boneNumber)) {
			next = next.getNextKeyFrame();
		}
		while (prev != null && !prev.isPositionSet(boneNumber)) {
			prev = prev.getPrevKeyFrame();
		}
		if (next == null) {
			if (prev == null) { // no key frame present
				return null;
			}
			// after the last key frame
			return prev.get(boneNumber, destination);
		}
		if (prev == null) {
			// before the first key frame
			return next.get(boneNumber, destination);
		}
		destination = prev.get(boneNumber, destination);
		final Vector4d nextBP = next.get(boneNumber, null);
		final double rampRatio = nextBP.getW();
		final long zeroTime = prev.getTime();
		final long duration = next.getTime() - zeroTime;
		alpha.setIncreasingAlphaDuration(duration);
		alpha.setIncreasingAlphaRampDuration((long) (duration * rampRatio));
		final double value = alpha.value(time - zeroTime);
		destination.interpolate(nextBP, value);
		return destination;
	}

	/**
	 * Simple getter.
	 * 
	 * @return the keyFrameGrid
	 */
	public final KeyFrameGrid getKeyFrameGrid() {
		return keyFrameGrid;
	}

	/**
	 * Simple setter. This property must be non-null.
	 * 
	 * @param keyFrameGrid
	 *            the keyFrameGrid to set
	 */
	public final void setKeyFrameGrid(KeyFrameGrid keyFrameGrid) {
		if (keyFrameGrid == null)
			throw new IllegalArgumentException(
					"A FigurePositionInterpolator cannot have a null KeyFrameGrid.");
		this.keyFrameGrid = keyFrameGrid;
	}

	/**
	 * Generates the EntRagdoll commands corresponding to the set animation.
	 * 
	 * @param timePerStep
	 * @param ref
	 * @param argTime
	 * @return
	 */
	public String exportAnimation(final int timePerStep, final int ref,
			final long argTime) {
		final StringBuilder sb = new StringBuilder();
		final long endTime = keyFrameGrid
				.get(keyFrameGrid.getColumnCount() - 2).getTime();
		Vector4d vector = new Vector4d();
		for (long time = 1; time < endTime + timePerStep; time += timePerStep) {
			if (time == 1) {
				sb.append("1");
			} else {
				sb.append(timePerStep);
			}
			sb.append(" ").append(ref).append(" EntRagdoll 11 ");
			sb.append(argTime + 3 * (time + timePerStep)).append(" ").append(
					argTime + 3 * (time)).append(" ");
			for (int i = 0; i < 11; i++) {
				// TODO export keyframes explicitly
				vector = getPosition(i, time, vector);
				Tuple3i tuple = KeyFrame.radiansToCoH(vector);
				appendHexString(tuple.getX(), sb);
				appendHexString(tuple.getY(), sb);
				appendHexString(tuple.getZ(), sb);
			}
			sb.append("\n");
			sb.append("0 ").append(ref).append(" Chat 10 0 \"").append(time).append("\"\n");
		}
		return sb.toString();
	}

	/**
	 * Formats an angle as a 8-char hex string. Used for exporting to .cohdemo
	 * format.
	 * 
	 * @param angle
	 * @return
	 */
	private static void appendHexString(int cohAngle,
			final StringBuilder destination) {
		while (cohAngle < 0) {
			cohAngle += 1024;
		}
		while (cohAngle > 1024) {
			cohAngle -= 1024;
		}
		final String str = Integer.toHexString(cohAngle).toUpperCase();
		for (int i = 0; i < 8 - str.length(); i++) {
			destination.append("0");
		}
		destination.append(str);
	}

}
