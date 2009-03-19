package cohDemoEditor.ragdollAnimator.swing.action;

import java.awt.event.ActionEvent;
import java.io.*;

import javax.swing.AbstractAction;
import javax.swing.Action;

import cohDemoEditor.ragdollAnimator.FigurePositionInterpolator;

/**
 * Temporary export action to quickly dump commands into a default file for
 * testing purposes.
 * 
 * @author Darren
 * 
 */
@SuppressWarnings("serial")
public class ExportAction extends AbstractAction {

	private FigurePositionInterpolator fpi;
	public static final String fileName = "export.cohdemo";
	public static final String demoPrefix = "1   0   Version 2\n0   0   Map maps/City_Zones/City_03_01/City_03_01.txt\n0   0   Time 12.000000\n0   CAM POS -200.0 -100.0 -200\n0   CAM PYR -0.0 1.5707963267948966 0\n0   1   Player\n0   1   NEW \"Doctor Leo\"\n0   1   COSTUME 0 9bd2ff -2.015267 -1.000000 0.000000 -0.527472 -1.000000 -1.000000 -1.000000 0.000000 0.000000 0.710000 0.760000 1.000000 -0.350000 1.000000 -1.000000 -1.000000 -1.000000 -1.000000 -1.000000 -0.800000 -1.000000 1.000000 1.000000 1.000000 -1.000000 -1.000000 -1.000000 -0.410000 -0.510000 -0.860000\n0   1   PARTSNAME Tight !Hips_V_Vanguard_01 !Hips_V_Vanguard_01_Mask 660000 ff4d4c\n0   1   PARTSNAME Tight !Chest_V_Vanguard_01 !Chest_V_Vanguard_01_Mask 660000 ff4d4c\n0   1   PARTSNAME V_MALE_HEAD.GEO/GEO_Head_V_Asym_Standard !v_face_skin_head_11 none 000000 000000\n0   1   PARTSNAME Wristband skin_wristband_01a skin_wristband_01b 000000 ff894c\n0   1   PARTSNAME V_MALE_BOOT.GEO/GEO_Lleg*_Rocket_01 !X_male_boot_rocket_01 none 000000 ff894c 000000 000000 AnimatedCharacterParts/RocketBoots.fx\n0   1   PARTSNAME V_MALE_BELT.GEO/GEO_Belt_Vangaurd_02 !X_Vanguard_Belt none 000000 ff894c\n0   1   PARTSNAME Style_03 Style_01a Style_01b 000a1f 000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME Glasses_01 Gradient_01a Gradient_01b 000000 0000ff\n0   1   PARTSNAME V_MALE_EMBLEM.GEO/GEO_Emblem_Vangaurd_02 !X_Vanguard_Belt none 000000 ff894c\n0   1   PARTSNAME V_MALE_SPADR.GEO/GEO_SpadR_Vangaurd_01 !X_Vanguard_Shoulder none 000000 ff894c\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME Chin_01 Tech_01a Tech_01b 0000ff 000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 000000 00000000\n0   1   PARTSNAME none none none 000000 00000000\n0   1   PARTSNAME none none none 000000 00000000\n0   1   PARTSNAME none none none 00000000 000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 000000 00000000\n0   1   PARTSNAME none none none 000000 00000000\n0   1   PARTSNAME none none none 000000 000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   PARTSNAME none none none 00000000 00000000\n0   1   POS -210.0 -100.0 -200.0\n0   1   PYR 0.0 1.5707963267948966 0\n";
	
	public ExportAction(FigurePositionInterpolator fpi) {
		this.fpi = fpi;
		putValue(Action.NAME, "Export");
		putValue(Action.SHORT_DESCRIPTION,
				"Exports to demo file format with default values.");
	}

	public void setFigurePositionInterpolator(FigurePositionInterpolator fpi) {
		this.fpi = fpi;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		File f = new File(fileName);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			final String str = fpi.exportAnimation(33, 1, 500);
			System.out.println(str);
			writer.write(demoPrefix);
			writer.write(str);
			writer.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
