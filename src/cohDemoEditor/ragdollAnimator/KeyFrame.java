package cohDemoEditor.ragdollAnimator;

import java.util.*;

import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.vecmath.*;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import cohDemoEditor.ragdollAnimator.bind.BoneXmlAdapter;

/**
 * A KeyFrame sets the location of some or all of the bones at a particular
 * point in time. It also stores the rampRatio for each set bone, which
 * determines how much smoothing to use when transforming from the previous
 * KeyFrame to this KeyFrame.
 * 
 * For convenience, we also store pointers to the previous and next KeyFrames.
 * This allows interpolators to more quickly compute interpolations when there
 * are unset BonePositions.
 * 
 * A KeyFrame is also a TableModel. The table columns are the name of the bone
 * (as defined in the LONG_BONE_NAMES field), Pitch, Yaw, Roll, and Ramp. There
 * are eleven rows, one for each bone.
 * 
 * @author Darren
 * 
 */
@XmlAccessorType(XmlAccessType.NONE)
public class KeyFrame extends AbstractList<Vector4d> implements
		Comparable<KeyFrame>, TableModel {

	/**
	 * The names of each bone.
	 */
	public static final String[] LONG_BONE_NAMES = { "Lower Right Leg",
			"Upper Right Leg", "Lower Left Leg", "Upper Left Leg",
			"Lower Left Arm", "Upper Left Arm", "Lower Right Arm",
			"Upper Right Arm", "Head", "Torso", "Waist" };
	/**
	 * The abbreviations for each bone.
	 */
	public static final String[] SHORT_BONE_NAMES = { "LRL", "URL", "LLL",
			"ULL", "LLA", "ULA", "LRA", "URA", "H", "T", "W" };

	@XmlAttribute(name = "time")
	private long time = 0L;
	@XmlJavaTypeAdapter(BoneXmlAdapter.class)
	private Vector4d[] positions = new Vector4d[11];
	private KeyFrame prevKeyFrame, nextKeyFrame;

	// Listener fields
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	private Set<TableModelListener> listenersToAdd = new HashSet<TableModelListener>();
	private Set<TableModelListener> listenersToRemove = new HashSet<TableModelListener>();
	private boolean firingListeners = false;

	/**
	 * Gets the time of this KeyFrame. The default time is 0L.
	 * 
	 * @return the time of this KeyFrame
	 */
	public long getTime() {
		return time;
	}

	/**
	 * Sets the time of this KeyFrame.
	 * 
	 * @param time
	 *            the time to set
	 * @return this
	 */
	public KeyFrame setTime(long time) {
		this.time = time;
		fireTableModelListeners(new TableModelEvent(this, 0, 0, 1));
		return this;
	}

	/**
	 * Determines if this KeyFrame specifies a position for the given bone.
	 * 
	 * @param boneNumber
	 *            the number of the bone whose position status will be checked
	 * @return true if the specified bone has a position, false otherwise
	 */
	public boolean isPositionSet(int boneNumber) {
		return positions[boneNumber] != null;
	}

	/**
	 * Unsets the specified bone's position.
	 * 
	 * @param boneNumber
	 *            the number of the bone whose position should be unset
	 * @return this
	 */
	public KeyFrame unSetPosition(int boneNumber) {
		positions[boneNumber] = null;
		fireTableModelListeners(new TableModelEvent(this, boneNumber + 1));
		return this;
	}

	/**
	 * Simple getter.
	 * 
	 * @return the prevKeyFrame
	 */
	public final KeyFrame getPrevKeyFrame() {
		return prevKeyFrame;
	}

	/**
	 * Simple getter.
	 * 
	 * @return the nextKeyFrame
	 */
	public final KeyFrame getNextKeyFrame() {
		return nextKeyFrame;
	}

	/**
	 * Simple setter. Returns this for chaining.
	 * 
	 * @param prevKeyFrame
	 *            the prevKeyFrame to set
	 * @return this
	 */
	public final KeyFrame setPrevKeyFrame(KeyFrame prevKeyFrame) {
		this.prevKeyFrame = prevKeyFrame;
		return this;
	}

	/**
	 * Simple setter. Returns this for chaining.
	 * 
	 * @param nextKeyFrame
	 *            the nextKeyFrame to set
	 * @return this
	 */
	public final KeyFrame setNextKeyFrame(KeyFrame nextKeyFrame) {
		this.nextKeyFrame = nextKeyFrame;
		return this;
	}

	/**
	 * KeyFrames are ordered by time.
	 */
	@Override
	public int compareTo(KeyFrame other) {
		if (this.equals(other))
			return 0;
		return (int) (this.time - other.time);
	}

	/**
	 * Two KeyFrames are considered equal if they have the same time.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null)
			return false;
		if (!o.getClass().equals(this.getClass()))
			return false;
		final KeyFrame other = (KeyFrame) o;
		return this.time == other.time;
	}

	/**
	 * Returns a hashCode based on time only.
	 */
	@Override
	public int hashCode() {
		return (int) (time ^ (time >>> 32));
	}

	/**
	 * Gets a copy of the BonePosition of the specified bone. Returns null if
	 * that position has not been set.
	 * 
	 * @param boneNumber
	 *            the number of the bone whose position will be returned
	 * @return the position of the specified bone, or null if that position has
	 *         not been set
	 */
	@Override
	public Vector4d get(int index) {
		if (positions[index] == null)
			return null;
		return new Vector4d(positions[index]);
	}

	/**
	 * Gets a copy of the BonePosition of the specified bone. Returns null if
	 * that position has not been set. If destination is not null, the specified
	 * bone will be copied into destination and returned. This parameter is
	 * ignored if the specified bone is null.
	 * 
	 * @param index
	 *            the number of the bone whose position will be returned
	 * @param destination
	 *            the Vector4d into which the returned value will be copied
	 * @return the position of the specified bone, or null if that position has
	 *         not been set
	 */
	public Vector4d get(int index, Vector4d destination) {
		if (positions[index] == null)
			return null;
		if (destination == null)
			destination = new Vector4d(positions[index]);
		else
			destination.set(positions[index]);
		return destination;
	}

	/**
	 * Returns the number of bones, 11. This is a fixed value.
	 */
	@Override
	public int size() {
		return 11;
	}

	/**
	 * Sets the position of the specified bone. The BonePosition is copied prior
	 * to being stored, so subsequent changes will not affect this KeyFrame.
	 * 
	 * @param boneNumber
	 *            the number of the bone to set
	 * @param bonePosition
	 *            the new position to set the specified bone
	 * @return this
	 */
	@Override
	public Vector4d set(int index, Vector4d position) {
		final Vector4d toReturn = positions[index];
		positions[index] = new Vector4d(position);
		final TableModelEvent evt = new TableModelEvent(this, index + 1);
		fireTableModelListeners(evt);
		return toReturn;
	}

	/**
	 * Fires all listeners using the specified event.
	 * 
	 * @param event
	 *            the TableModelEvent to pass to each registered
	 *            TableModelListener
	 */
	protected void fireTableModelListeners(TableModelEvent event) {
		firingListeners = true;
		for (TableModelListener l : listeners) {
			l.tableChanged(event);
		}
		firingListeners = false;
		listeners.removeAll(listenersToRemove);
		listenersToRemove.clear();
		listeners.addAll(listenersToAdd);
		listenersToAdd.clear();
	}

	/**
	 * Adds a new TableModelListener to this KeyFrame. Note that if this occurs
	 * while the TableModelListeners are firing, this will not occur until after
	 * all listeners have been fired. In particular, the new TableModelListener
	 * will not be fired (unless it has previously been added to this KeyFrame).
	 */
	@Override
	public void addTableModelListener(TableModelListener l) {
		if (!firingListeners) {
			listeners.add(l);
		} else {
			listenersToAdd.add(l);
		}
	}

	/**
	 * Returns the class of each column. The first (index 0) column contains
	 * Strings, while the other four columns contain Doubles.
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return String.class;
		case 4:
			return Double.class;
		default:
			return Integer.class;
		}
	}

	/**
	 * Returns 5, the number of columns. This is a fixed value. The columns are
	 * the bone name, pitch, yaw, roll, and ramp, respectively.
	 */
	@Override
	public int getColumnCount() {
		return 5;
	}

	/**
	 * Returns the names of the five columns.
	 */
	@Override
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return "Bone";
		case 1:
			return "P";
		case 2:
			return "Y";
		case 3:
			return "R";
		case 4:
			return "S";
		}
		return null;
	}

	/**
	 * Returns 12, the number of rows. There is a time row, then one row for
	 * each bone, in order. This is a fixed value.
	 */
	@Override
	public int getRowCount() {
		return 12;
	}

	/**
	 * Returns the value at the specified rowIndex and columnIndex. Returns null
	 * when the bone for that row is not set.
	 * 
	 * This method translates between the 0-1024 CoH format (displayed) and the
	 * radian format (internal).
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex == 0) {
			if (columnIndex == 0)
				return "Time";
			if (columnIndex == 1)
				return time;
			return null;
		}
		if (positions[rowIndex - 1] == null) {
			if (columnIndex == 0)
				return SHORT_BONE_NAMES[rowIndex - 1];
			return null;
		}
		Tuple3i tuple = radiansToCoH(positions[rowIndex - 1]);
		switch (columnIndex) {
		case 0:
			return SHORT_BONE_NAMES[rowIndex-1];
		case 1:
			return tuple.getX();
		case 2:
			return tuple.getY();
		case 3:
			return tuple.getZ();
		case 4:
			return positions[rowIndex - 1].getW();
		}
		return null;
	}

	/**
	 * The cells in the first column (the bone names) are not editable. The
	 * other cells are.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (columnIndex > 0 && rowIndex > 0)
				|| (rowIndex == 0 && columnIndex == 1 && time != 0);
	}

	/**
	 * Removes the specified TableModelListener from this KeyFrame. Note that if
	 * this occurs while the listeners are firing, the removal will not occur
	 * until the firing is complete. In particular, the specified
	 * TableModelListener will still be fired (unless it has already been fired
	 * or was not originally registered with this KeyFrame).
	 */
	@Override
	public void removeTableModelListener(TableModelListener l) {
		if (!firingListeners) {
			listeners.remove(l);
		} else {
			listenersToRemove.add(l);
		}
	}

	/**
	 * Sets the value of the specified cell. If the bone had not previously been
	 * set, the entire bone is initialized with the other row entries set to
	 * their defaults.
	 * 
	 * This method converts between 0-1024 CoH angles (displayed) and 0-2*Pi
	 * radians (internal).
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (value == null)
			return;
		if (rowIndex == 0) { // editing the time
			long time = ((Integer) value).longValue();
			if ((prevKeyFrame != null && time <= prevKeyFrame.getTime())
					|| (nextKeyFrame != null && time >= nextKeyFrame.getTime())) {
				return;
			}
			setTime(time);
			return;
		}
		final TableModelEvent evt;
		if (positions[rowIndex - 1] == null) {
			positions[rowIndex - 1] = new Vector4d();
			evt = new TableModelEvent(this, rowIndex);
		} else {
			evt = new TableModelEvent(this, rowIndex, rowIndex, columnIndex,
					TableModelEvent.UPDATE);
		}
		if (columnIndex < 4) {
			int input = ((Integer) value).intValue();
			Tuple3i tuple = radiansToCoH(positions[rowIndex - 1]);
			switch (columnIndex) {
			case 1:
				tuple.setX(input);
				break;
			case 2:
				tuple.setY(input);
				break;
			case 3:
				tuple.setZ(input);
				break;
			}
			final double w = positions[rowIndex-1].getW();
			positions[rowIndex - 1] = coHToRadians(tuple);
			positions[rowIndex - 1].setW(w);
		} else {
			positions[rowIndex - 1].setW(((Double) value).doubleValue());
		}
		fireTableModelListeners(evt);
	}

	/**
	 * This method translates from the CoH format to the internal radian angle
	 * measure.
	 */
	public static Vector4d coHToRadians(int x, int y, int z) {
		final Vector4d vector = new Vector4d();
		vector.setX(-Math.PI * (((double) x) - 512) / 512);
		vector.setY(Math.PI * (((double) y) - 512) / 512);
		vector.setZ(Math.PI * (((double) z) - 512) / 512);
		return vector;
	}

	private Vector4d coHToRadians(final Tuple3i tuple) {
		return coHToRadians(tuple.getX(), tuple.getY(), tuple.getZ());
	}

	/**
	 * This method translates from the internal radian angle measure to CoH
	 * format.
	 */
	public static Tuple3i radiansToCoH(double x, double y, double z) {
		final Tuple3i tuple = new Point3i();
		tuple.setX((int) (-512 * x / Math.PI) + 512);
		tuple.setY((int) (512 * y / Math.PI) + 512);
		tuple.setZ((int) (512 * z / Math.PI) + 512);
		return tuple;
	}

	public static Tuple3i radiansToCoH(final Vector4d vector) {
		return radiansToCoH(vector.getX(), vector.getY(), vector.getZ());
	}
}