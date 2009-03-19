package cohDemoEditor.ragdollAnimator.bind;

import javax.vecmath.Vector4d;
import javax.xml.bind.annotation.adapters.XmlAdapter;


public class BoneXmlAdapter extends
		XmlAdapter<Vector4dBoneWrapper[], Vector4d[]> {

	@Override
	public Vector4dBoneWrapper[] marshal(Vector4d[] v) throws Exception {
		if (v == null)
			return null;
		if (v.length != 11)
			throw new IllegalArgumentException(
					"Can only marshal vector arrays of length 11.");
		final Vector4dBoneWrapper[] array = new Vector4dBoneWrapper[11];
		for(int i = 0; i < 11; i++) {
			if (v[i] == null) {
				array[i] = new Vector4dBoneWrapper(i,0,0,0,0);
				array[i].isSet = false;
			} else {
				array[i] = new Vector4dBoneWrapper(i, v[i].x, v[i].y, v[i].z, v[i].w);
				array[i].isSet = true;
			}
		}
		return array;
	}

	@Override
	public Vector4d[] unmarshal(Vector4dBoneWrapper[] v) throws Exception {
		if (v == null)
			return null;
		if (v.length != 11)
			throw new IllegalArgumentException(
					"Can only unmarshal lists of length 11.");
		final Vector4d[] array = new Vector4d[11];
		for(int i = 0; i < 11; i++) {
			final Vector4dBoneWrapper wrapper = v[i];
			if (!wrapper.isSet) {
				array[i] = null;
			} else {
				array[i] = new Vector4d(wrapper.x, wrapper.y, wrapper.z, wrapper.w);
			}
		}		
		return array;
	}

}
