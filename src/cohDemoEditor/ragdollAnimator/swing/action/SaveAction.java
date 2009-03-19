package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.xml.bind.*;

import cohDemoEditor.ragdollAnimator.bind.KeyFrameGridWrapper;
import cohDemoEditor.ragdollAnimator.bind.KeyFrameGridXmlAdapter;
import cohDemoEditor.ragdollAnimator.swing.RagdollAnimator;

/**
 * This is a temporary save action.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class SaveAction extends AbstractAction {

	public static final String DEFAULT_FILE_NAME = "animate.xml";

	public SaveAction() {
		putValue(Action.NAME, "Save");
		putValue(Action.SHORT_DESCRIPTION, "Save the animation to XML");
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		FileOutputStream fos;
		try {
			final KeyFrameGridWrapper grid = (new KeyFrameGridXmlAdapter()).marshal(RagdollAnimator.getRagdollAnimator().getKeyFrameGrid());
			fos = new FileOutputStream(DEFAULT_FILE_NAME);
			JAXBContext context = JAXBContext.newInstance(KeyFrameGridWrapper.class);
			Marshaller m = context.createMarshaller();
			m.marshal(grid, fos);
			fos.close();
		} catch (JAXBException e1) {
			e1.printStackTrace();
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		} catch (IOException e3) {
			e3.printStackTrace();
		} catch (Exception e4) {
			// TODO Auto-generated catch block
			e4.printStackTrace();
		}
	}

}
