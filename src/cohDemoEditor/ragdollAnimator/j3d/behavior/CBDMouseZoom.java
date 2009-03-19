package cohDemoEditor.ragdollAnimator.j3d.behavior;

import java.awt.Component;
import java.awt.event.MouseEvent;

import com.sun.j3d.utils.behaviors.mouse.MouseZoom;

/**
 * We extend MouseZoom so it only processes mouse events when enabled.
 * 
 * @author Darren
 * 
 */
public class CBDMouseZoom extends MouseZoom implements CanBeDisabled {

	private Component mouseEventSource;
	private boolean enabled = true;

	public CBDMouseZoom(Component mouseEventSource) {
		this.mouseEventSource = mouseEventSource;
	}

	@Override
	public void processMouseEvent(MouseEvent e) {
		if (mouseEventSource.isFocusOwner())
			super.processMouseEvent(e);
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

}
