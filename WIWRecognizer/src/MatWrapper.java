import java.io.Serializable;
import java.util.List;

import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.KeyPoint;

public class MatWrapper implements Serializable {

	float[] angle;
	float[] class_id;
	float[] octave;
	double[] ptx;
	double[] pty;
	float[] response;
	float[] size;

	public MatWrapper(MatOfKeyPoint key) {
		KeyPoint[] kp = key.toArray();
		int length = kp.length;
		angle = new float[length];
		class_id = new float[length];
		octave = new float[length];
		ptx = new double[length];
		pty = new double[length];
		response = new float[length];
		size = new float[length];
		KeyPoint k;
		for (int i = 0; i < length; i++) {
			k = kp[i];
			angle[i] = k.angle;
			class_id[i] = k.class_id;
			octave[i] = k.octave;
			ptx[i] = k.pt.x;
			pty[i] = k.pt.y;
			response[i] = k.response;
			size[i] = k.size;
		}
		k = kp[0];
		System.out.println(k.toString());
	}
}
