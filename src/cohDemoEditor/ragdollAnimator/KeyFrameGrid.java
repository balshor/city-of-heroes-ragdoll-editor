package cohDemoEditor.ragdollAnimator;

import java.util.*;

import javax.swing.event.*;
import javax.swing.table.TableModel;
import javax.vecmath.Vector4d;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import cohDemoEditor.ragdollAnimator.bind.KeyFrameGridXmlAdapter;

import static cohDemoEditor.ragdollAnimator.KeyFrame.LONG_BONE_NAMES;

/**
 * A KeyFrameGrid is essentially a sorted set of KeyFrames.
 * 
 * The KeyFrameGrid also defines a TableModel. The first column is a list of
 * bone names. Each subsequent column is a single keyframe. The 12 rows consist
 * of the key frame time, then symbols to indicate whether the key frame is set
 * or not.
 * 
 * @author Darren
 * 
 */
@XmlRootElement
@XmlJavaTypeAdapter(KeyFrameGridXmlAdapter.class)
@XmlAccessorType(XmlAccessType.NONE) // persist only marked fields
@SuppressWarnings("serial")
public class KeyFrameGrid extends TreeSet<KeyFrame> implements TableModel,
		TableModelListener {

	@XmlElementWrapper(name="keyframes")
	@XmlElements(@XmlElement(name="keyframe", type=KeyFrame.class))
	private ArrayList<KeyFrame> indexList = new ArrayList<KeyFrame>();
	private final static String FIRST_COLUMN_NAME = "Key Frame";

	// Listener fields
	private Set<TableModelListener> listeners = new HashSet<TableModelListener>();
	private Set<TableModelListener> listenersToAdd = new HashSet<TableModelListener>();
	private Set<TableModelListener> listenersToRemove = new HashSet<TableModelListener>();
	private boolean firingListeners = false;

	/**
	 * Creates a new KeyFrameGrid. The KeyFrameGrid will contain one KeyFrame at
	 * time zero with all bone angles set to zero.
	 */
	public KeyFrameGrid() {
		final KeyFrame kf = new KeyFrame();
		kf.setTime(0L);
		final Vector4d defaultVector = new Vector4d(0, 0, 0, 0);
		for (int i = 0; i < 11; i++) {
			kf.set(i, defaultVector);
		}
		add(kf);
	}

	/**
	 * Adds a KeyFrame to this KeyFrameGrid
	 * 
	 * @param kf
	 *            the KeyFrame to add
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	@Override
	public boolean add(final KeyFrame kf) {
		final boolean changed = addHelper(kf);
		if (changed) {
			fireTableModelListeners(new TableModelEvent(this,
					TableModelEvent.HEADER_ROW));
		}
		return changed;
	}

	/**
	 * Helper method that adds the given KeyFrame and correctly sets all
	 * previous and next key frames.
	 * 
	 * @param kf
	 *            the KeyFrame to add
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	private boolean addHelper(final KeyFrame kf) {
		final boolean changed = super.add(kf);
		if (changed) {
			final KeyFrame previous = this.lower(kf);
			final KeyFrame next = this.higher(kf);
			int index = indexList.size();
			if (previous != null) {
				kf.setPrevKeyFrame(previous);
				previous.setNextKeyFrame(kf);
			}
			if (next != null) {
				kf.setNextKeyFrame(next);
				next.setPrevKeyFrame(kf);
				index = indexList.indexOf(next);
			}
			indexList.add(index, kf);
		}
		return changed;
	}

	/**
	 * Adds all KeyFrames in the given collection to this KeyFrameGrid
	 * 
	 * @param c
	 *            a Collection of KeyFrames to add to this KeyFrameGrid
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	@Override
	public boolean addAll(Collection<? extends KeyFrame> c) {
		boolean changed = false;
		for (KeyFrame kf : c) {
			changed = addHelper(kf);
		}
		if (changed) {
			fireTableModelListeners(new TableModelEvent(this,
					TableModelEvent.HEADER_ROW));
		}
		return changed;
	}

	/**
	 * Removes the given Object from this KeyFrameGrid
	 * 
	 * @param o
	 *            the object to remove
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	@Override
	public boolean remove(Object o) {
		final boolean changed = removeHelper(o);
		if (changed) {
			fireTableModelListeners(new TableModelEvent(this,
					TableModelEvent.HEADER_ROW));
		}
		return changed;
	}

	/**
	 * Helper method that removes the given object and correctly sets the next
	 * and previous properties of the remaining key frames. The previous/next
	 * properties are unchanged in the removed key frame.
	 * 
	 * @param o
	 *            the object to remove
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	private boolean removeHelper(Object o) {
		final boolean changed = super.remove(o);
		if (changed) {
			final KeyFrame prev = this.lower((KeyFrame) o);
			final KeyFrame next = this.higher((KeyFrame) o);
			if (prev != null) {
				prev.setNextKeyFrame(next);
			}
			if (next != null) {
				next.setPrevKeyFrame(prev);
			}
			indexList.remove(o);
		}
		return changed;
	}

	/**
	 * Removes all objects in the given collection from this KeyFrameGrid.
	 * 
	 * @param c
	 *            the Collection of Objects to remove
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : c) {
			changed = removeHelper(o);
		}
		if (changed)
			fireTableModelListeners(new TableModelEvent(this,
					TableModelEvent.HEADER_ROW));
		return changed;
	}

	/**
	 * Removes all except the given collection of objects from this KeyFrameGrid
	 * 
	 * @param c
	 *            the Collection of Objects that should not be removed
	 * @return true if the KeyFrameGrid was modified as a result of this method
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		for (Object o : this) {
			if (!c.contains(o)) {
				changed = removeHelper(o);
			}
		}
		if (changed)
			fireTableModelListeners(new TableModelEvent(this,
					TableModelEvent.HEADER_ROW));
		return changed;
	}

	/**
	 * Calls tableChanged(event) on all registered listeners. Any changes to the
	 * stored listeners are postponed until all registered listeners have fired.
	 * In particular, new listeners added as a result of the fired event will
	 * not receive notification of that event, and listeners removed as a result
	 * of the fired event will still receive notification of the event if they
	 * have not already been notified.
	 * 
	 * @param event
	 *            the TableModelEvent to pass to the listeners
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
	 * Registers a new TableModelListener to receive TableModelEvents from this
	 * KeyFrameGrid
	 * 
	 * @param l
	 *            the TableModelListener to register
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
	 * Returns String.class. Used by a JTable to determine the class to use for
	 * rendering and editing.
	 * 
	 * @return String.class
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	/**
	 * Returns the number of columns in the KeyFrameGrid. There is one leading
	 * label column, then one column for each KeyFrame.
	 * 
	 * @return the number of KeyFrames in this KeyFrameGrid, plus one
	 */
	@Override
	public int getColumnCount() {
		return indexList.size() + 1;
	}

	/**
	 * Returns the name of each column. The first column is named "Key Frame"
	 * while subsequent columns are numbered with their column index.
	 * 
	 * @param columnIndex
	 *            the index of the column whose name should be retrieved
	 * @return "Key Frame" if columnIndex == 0 or the columnIndex for all other
	 *         columns
	 */
	@Override
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0)
			return FIRST_COLUMN_NAME;
		return Integer.toString(columnIndex);
	}

	/**
	 * Returns the number of rows in this table. There are 12 rows in a
	 * KeyFrameGrid: one time row and eleven bone rows
	 * 
	 * @return 12
	 */
	@Override
	public int getRowCount() {
		return 12;
	}

	/**
	 * Returns the String that should be rendered in the cell at (rowIndex,
	 * columnIndex)
	 * 
	 * @param rowIndex
	 *            the row index of the cell to render
	 * @param columnIndex
	 *            the column index of the cell to render
	 * @return the String to render in that cell
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// first column is all labels
		if (columnIndex == 0) {
			if (rowIndex == 0)
				return "Time";
			return LONG_BONE_NAMES[rowIndex - 1];
		}
		// first row is times
		if (rowIndex == 0 && columnIndex < indexList.size()+1) {
			return indexList.get(columnIndex - 1).getTime();
		}
		// second column = first key frame = all bones set
		if (columnIndex == 1) {
			return " X-";
		}
		// all other columns: are bones set?
		if (rowIndex > 0) {
			final int boneIndex = rowIndex - 1;
			KeyFrame kf = indexList.get(columnIndex - 1);
			if (kf != null && kf.isPositionSet(boneIndex)) {
				if (kf.get(boneIndex).getW() > 0) {
					return "->X";
				}
				return "--X";
			}
			while (kf != null && !kf.isPositionSet(boneIndex)) {
				kf = kf.getNextKeyFrame();
			}
			if (kf != null)
				return "---";
		}
		return null;
	}

	/**
	 * Returns whether a cell is editable. The only editable cells are in row 0
	 * with columnIndex>1. These cells contain the times of all KeyFrames except
	 * the first.
	 * 
	 * @param rowIndex
	 *            the index of the row of the cell
	 * @param columnIndex
	 *            the index of the column of the cell
	 * @return true if the given cell is editable, false otherwise
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return (rowIndex == 0) && (columnIndex > 1);
	}

	/**
	 * Unregisters the given TableModelListener from receiving TableModelEvents
	 * from this KeyFrameGrid.
	 * 
	 * @param l
	 *            the TableModelListener to unregister
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
	 * Sets the value of the given cell. The parameter value should be a String
	 * which can be parsed into a long. If the given time is not between the
	 * previous and next KeyFrame times, the input is ignored.
	 * 
	 * @param value
	 *            the new time to set
	 * @param rowIndex
	 *            the index of the row of the cell to edit
	 * @param columnIndex
	 *            the index of the column of the cell to edit
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (rowIndex != 0 || columnIndex < 2)
			throw new IllegalArgumentException(
					"Only times are editable through the table model.");
		try {
			long newTime = Long.parseLong((String) value);
			final KeyFrame kf = get(columnIndex - 1);
			final KeyFrame prev = kf.getPrevKeyFrame();
			if (newTime <= prev.getTime()) {
				return;
			}
			final KeyFrame next = kf.getNextKeyFrame();
			if (next != null && newTime >= next.getTime()) {
				return;
			}
			kf.setTime(newTime);
			final TableModelEvent evt = new TableModelEvent(this, 0, 0,
					columnIndex, TableModelEvent.UPDATE);
			fireTableModelListeners(evt);
		} catch (NumberFormatException nfe) {
			nfe.printStackTrace();
		}
	}

	/**
	 * Returns the KeyFrame at the specified index. Throws
	 * IndexOutOfBoundsException if the index is invalid.
	 * 
	 * @param n
	 *            the index of the KeyFrame to retrieve
	 * @return the requested KeyFrame
	 */
	public KeyFrame get(int n) {
		return indexList.get(n);
	}

	/**
	 * A KeyFrameGrid listens to a KeyFrame to determine when edits have been
	 * made. The rows of a KeyFrameGrid are the same as the rows of a KeyFrame
	 * (time+11bones), so a change in rows of a KeyFrame cause a corresponding
	 * update of the rows in a KeyFrameGrid.
	 * 
	 * @param e
	 *            the TableModelEvent describing which KeyFrame has been edited
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
		final Object kf = e.getSource();
		final int index = indexList.indexOf(kf);
		if (index < 0)
			return;
		fireTableModelListeners(new TableModelEvent(this, e.getFirstRow(), e
				.getLastRow()));
	}

}
