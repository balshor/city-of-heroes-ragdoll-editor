package cohDemoEditor.ragdollAnimator.bind;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import cohDemoEditor.ragdollAnimator.KeyFrame;
import cohDemoEditor.ragdollAnimator.KeyFrameGrid;

public class KeyFrameGridXmlAdapter extends
		XmlAdapter<KeyFrameGridWrapper, KeyFrameGrid> {

	@Override
	public KeyFrameGridWrapper marshal(KeyFrameGrid v) throws Exception {
		KeyFrameGridWrapper wrapper = new KeyFrameGridWrapper();
		for (KeyFrame kf : v) {
			wrapper.indexList.add(kf);
		}
		return wrapper;
	}

	@Override
	public KeyFrameGrid unmarshal(KeyFrameGridWrapper v) throws Exception {
		KeyFrameGrid grid = new KeyFrameGrid();
		for(KeyFrame kf : v.indexList) {
			if (kf.getTime() == 0L) {
				for(int i = 0; i < 11; i++) {
					grid.get(0).set(i, kf.get(i));
				}
			} else {
				grid.add(kf);
			}
		}
		return grid;
	}
}
