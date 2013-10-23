import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.util.zip.DeflaterOutputStream;

import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.KeyPoint;

public class KeyWriter {
	public static void write(MatOfKeyPoint key, FileOutputStream fout) {
		try {
			DataOutputStream dos = new DataOutputStream(new DeflaterOutputStream(fout));
			KeyPoint[] kp = key.toArray();
			int length = kp.length;
			dos.writeInt(length);
			KeyPoint k;
			for (int i = 0; i < length; i++) {
				k = kp[i];
				dos.writeFloat(k.angle);
				dos.writeFloat(k.class_id);
				dos.writeFloat(k.octave);
				dos.writeFloat(k.response);
				dos.writeFloat(k.size);
				dos.writeDouble(k.pt.x);
				dos.writeDouble(k.pt.y);
			}		
			dos.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
