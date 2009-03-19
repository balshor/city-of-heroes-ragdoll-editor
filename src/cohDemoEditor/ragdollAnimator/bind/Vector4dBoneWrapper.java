package cohDemoEditor.ragdollAnimator.bind;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class Vector4dBoneWrapper {

	@XmlAttribute
	public boolean isSet; 
	@XmlAttribute
	public int boneNumber;
	@XmlAttribute
	public double x;
	@XmlAttribute
	public double y;
	@XmlAttribute
	public double z;
	@XmlAttribute
	public double w;

	public Vector4dBoneWrapper() {
		isSet = true;
	}
	
	public Vector4dBoneWrapper(int boneNumber, double x, double y, double z, double w) {
		this.boneNumber = boneNumber;
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	
}
