package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

/**
 * This abstract class handles uncaught RuntimeExceptions by either rethrowing
 * them, ignoring them, or displaying the error message in a JOptionPane.
 * Subclasses should implement doAction instead of actionPerformed.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public abstract class ErrorHandlingAction extends AbstractAction {

	public static final int SILENT_MODE = 0;
	public static final int THROWS_MODE = 1;
	public static final int JOPTIONPANE_MODE = 2;

	private int mode = THROWS_MODE;
	private Component parent = null;

	@Override
	public final void actionPerformed(ActionEvent e) {
		try {
			doAction(e);
		} catch (RuntimeException exception) {
			if (mode == SILENT_MODE)
				return;
			if (mode == THROWS_MODE)
				throw exception;
			if (mode == JOPTIONPANE_MODE) {
				JOptionPane.showMessageDialog(parent, exception.getMessage(),
						"Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Sets how we should deal with exceptions. Options are SILENT_MODE,
	 * THROWS_MODE, and JOPTIONPANE_MODE.
	 * 
	 * @param mode
	 */
	public final void setMode(int mode) {
		if (mode != SILENT_MODE && mode != THROWS_MODE
				&& mode != JOPTIONPANE_MODE)
			throw new IllegalArgumentException("Unknown mode");
		this.mode = mode;
	}

	/**
	 * If errors display in a JOptionPane, this Component is set to be the
	 * JOptionPane's parent. This parameter is ignored in silent or throws
	 * modes.
	 * 
	 * @param parent
	 */
	public final void setParent(Component parent) {
		this.parent = parent;
	}

	protected abstract void doAction(ActionEvent e);

}
