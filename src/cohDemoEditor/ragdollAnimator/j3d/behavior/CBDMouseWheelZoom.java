package cohDemoEditor.ragdollAnimator.j3d.behavior;

import java.awt.Component;
import java.awt.event.MouseEvent;


import com.sun.j3d.utils.behaviors.mouse.MouseWheelZoom;

/**
 * We extend MouseWheelZoom so it only processes mouse events when enabled.
 * 
 * @author Darren
 *
 */
public class CBDMouseWheelZoom extends MouseWheelZoom implements CanBeDisabled {

	private Component mouseEventSource;
	private boolean enabled = true;
	
	public CBDMouseWheelZoom(Component mouseEventSource) {
		this.mouseEventSource = mouseEventSource;
	}

	@Override
	public void processMouseEvent(MouseEvent e) {
		// TODO mouse wheel events are somehow always being processed
		if (enabled && mouseEventSource.isFocusOwner())
			super.processMouseEvent(e);
	}

	/**
	 * @return the enabled
	 */
	public final boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public final void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

}
