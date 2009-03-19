package cohDemoEditor.ragdollAnimator.j3d.behavior;

import java.awt.Component;
import java.awt.event.MouseEvent;


import com.sun.j3d.utils.behaviors.mouse.MouseRotate;

/**
 * We extend MouseRotate so it only processes mouse events when it is enabled.
 * 
 * @author Darren
 *
 */
public class CBDMouseRotate extends MouseRotate implements CanBeDisabled {

	private Component mouseEventSource;
	private boolean enabled = true;
	
	public CBDMouseRotate(Component mouseEventSource) {
		this.mouseEventSource = mouseEventSource;
	}

	@Override
	public void processMouseEvent(MouseEvent e) {
		if (enabled && mouseEventSource.isFocusOwner())
			super.processMouseEvent(e);
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
}
