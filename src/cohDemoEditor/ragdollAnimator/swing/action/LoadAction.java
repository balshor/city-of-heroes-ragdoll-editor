package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.Action;
import javax.xml.bind.*;

import cohDemoEditor.ragdollAnimator.KeyFrameGrid;
import cohDemoEditor.ragdollAnimator.bind.KeyFrameGridWrapper;
import cohDemoEditor.ragdollAnimator.bind.KeyFrameGridXmlAdapter;
import cohDemoEditor.ragdollAnimator.swing.*;

/**
 * This is a temporary load action.
 * @author Darren
 *
 */
@SuppressWarnings("serial")
public class LoadAction extends javax.swing.AbstractAction {

	private KeyFrameGridPanel kfgPanel;
	private KeyFramePanel kfPanel;
	
	public LoadAction(KeyFrameGridPanel kfgPanel, KeyFramePanel kfPanel) {
		this.kfgPanel = kfgPanel;
		this.kfPanel = kfPanel;
		putValue(Action.NAME, "Load");
		putValue(Action.SHORT_DESCRIPTION, "Loads from the default XML file");
	}
	
	// TODO: fix this
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (kfgPanel == null || kfPanel == null) return;
		try {
			FileInputStream fis = new FileInputStream(SaveAction.DEFAULT_FILE_NAME);
			JAXBContext context = JAXBContext.newInstance(KeyFrameGridWrapper.class);
			Unmarshaller u = context.createUnmarshaller();
			Object obj = u.unmarshal(fis);
			if (obj instanceof KeyFrameGridWrapper) {
				final KeyFrameGrid kfg = (new KeyFrameGridXmlAdapter()).unmarshal(((KeyFrameGridWrapper) obj));
				RagdollAnimator.getRagdollAnimator().setKeyFrameGrid(kfg);
			} else {
				throw new RuntimeException("Unmarshalled non-keyframegrid");
			}
			fis.close();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
