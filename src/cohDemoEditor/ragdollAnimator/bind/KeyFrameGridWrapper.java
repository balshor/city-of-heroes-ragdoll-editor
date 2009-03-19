package cohDemoEditor.ragdollAnimator.bind;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import cohDemoEditor.ragdollAnimator.KeyFrame;

@XmlRootElement
public class KeyFrameGridWrapper {

	@XmlElementWrapper(name="keyframes")
	@XmlElements(@XmlElement(name="keyframe", type=KeyFrame.class))
	public ArrayList<KeyFrame> indexList = new ArrayList<KeyFrame>();
	
}
