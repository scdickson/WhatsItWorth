import java.util.LinkedList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class DetectCard {
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	};
	FeatureDetector mFeatureDetector;
	DescriptorExtractor mDescriptorExtractor;
	DescriptorMatcher mDescriptorMatcher;

	Mat mSourceDescriptors;
	Mat mSceneDescriptors;
	MatOfKeyPoint mSourceKeyPoints;
	MatOfKeyPoint mSceneKeyPoints;

	public boolean run(String sourceName, String sceneName) {
		long start = System.currentTimeMillis();
		System.out.println("\nRunning Card Detection");

		Mat sourceRGB = Highgui.imread(sourceName);
		Mat sceneRGB = Highgui.imread(sceneName);

		Mat source = new Mat();
		Mat scene = new Mat();
		Imgproc.cvtColor(sourceRGB, source, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.cvtColor(sceneRGB, scene, Imgproc.COLOR_BGRA2GRAY);

		mFeatureDetector = FeatureDetector.create(FeatureDetector.SURF);
		mDescriptorExtractor = DescriptorExtractor
				.create(DescriptorExtractor.SURF);
		mDescriptorMatcher = DescriptorMatcher
				.create(DescriptorMatcher.FLANNBASED);
		mSourceDescriptors = new Mat();
		mSceneDescriptors = new Mat();

		mSourceKeyPoints = new MatOfKeyPoint();
		mSceneKeyPoints = new MatOfKeyPoint();

		mFeatureDetector.detect(source, mSourceKeyPoints);
		mFeatureDetector.detect(scene, mSceneKeyPoints);

		mDescriptorExtractor.compute(source, mSourceKeyPoints,
				mSourceDescriptors);
		mDescriptorExtractor.compute(scene, mSceneKeyPoints, mSceneDescriptors);

		/*
		 * FileOutputStream fout; try { fout = new
		 * FileOutputStream("blobs.blob"); KeyWriter.write(mSourceKeyPoints,
		 * fout); } catch (FileNotFoundException e) { e.printStackTrace(); }
		 */

		// Highgui.imwrite("Descriptors.png", mSourceDescriptors);

		MatOfDMatch modm = new MatOfDMatch();
		mDescriptorMatcher.match(mSourceDescriptors, mSceneDescriptors, modm);
		//
		float minDist = Float.MAX_VALUE;

		// -- Quick calculation of max and min distances between keypoints
		List<DMatch> matchList = modm.toList();

		for (DMatch d : matchList) {
			float dist = d.distance;
			if (dist < minDist)
				minDist = dist;
		}

		minDist *= 3f;

		LinkedList<DMatch> goodMatches = new LinkedList<DMatch>();
		for (DMatch d : matchList) {
			if (d.distance < minDist) {
				goodMatches.addLast(d);
			}
		}

		MatOfDMatch goodModm = new MatOfDMatch();
		goodModm.fromList(goodMatches);

		LinkedList<Point> sourceList = new LinkedList<Point>();
		LinkedList<Point> sceneList = new LinkedList<Point>();

		List<KeyPoint> keypointsSourceList = mSourceKeyPoints.toList();
		List<KeyPoint> keypointsSceneList = mSceneKeyPoints.toList();

		for (int i = 0; i < goodMatches.size(); i++) {
			sourceList
					.addLast(keypointsSourceList.get(goodMatches.get(i).queryIdx).pt);
			sceneList
					.addLast(keypointsSceneList.get(goodMatches.get(i).trainIdx).pt);
		}

		MatOfPoint2f obj = new MatOfPoint2f();
		obj.fromList(sourceList);

		MatOfPoint2f scenePoints = new MatOfPoint2f();
		scenePoints.fromList(sceneList);

		Mat H = Calib3d.findHomography(obj, scenePoints, 8, 10);

		Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
		Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

		obj_corners.put(0, 0, new double[] { 0, 0 });
		obj_corners.put(1, 0, new double[] { source.cols(), 0 });
		obj_corners.put(2, 0, new double[] { source.cols(), source.rows() });
		obj_corners.put(3, 0, new double[] { 0, source.rows() });

		Core.perspectiveTransform(obj_corners, scene_corners, H);

		double p0x = scene_corners.get(0, 0)[0];
		double p0y = scene_corners.get(0, 0)[1];

		double p1x = scene_corners.get(1, 0)[0];
		double p1y = scene_corners.get(1, 0)[1];

		double p2x = scene_corners.get(2, 0)[0];
		double p2y = scene_corners.get(2, 0)[1];

		double p3x = scene_corners.get(3, 0)[0];
		double p3y = scene_corners.get(3, 0)[1];

		double deltax = p0x - p1x;
		double deltay = p0y - p1y;
		// Bottom Line
		double line0 = Math.sqrt(deltax * deltax + deltay * deltay);

		deltax = p1x - p2x;
		deltay = p1y - p2y;
		// Left Line
		double line1 = Math.sqrt(deltax * deltax + deltay * deltay);

		deltax = p2x - p3x;
		deltay = p2y - p3y;
		// Top Line
		double line2 = Math.sqrt(deltax * deltax + deltay * deltay);

		deltax = p3x - p0x;
		deltay = p3y - p0y;
		// Right Line
		double line3 = Math.sqrt(deltax * deltax + deltay * deltay);

		double topleft = line2 / line1 - .701;
		double bottomright = line0 / line3 - .701;

		double finalerror = topleft + bottomright;

		double errorthreshold = .1;

		if (finalerror < errorthreshold) {
			System.out.println("Match found!");
		} else {
			System.out.println("No match found");
		}
		System.out.println("Computation Time: "
				+ (System.currentTimeMillis() - start) + "ms");

		return finalerror < errorthreshold;
		/*
		 * Mat out = new Mat();
		 * 
		 * Features2d.drawMatches(source, mSourceKeyPoints, scene,
		 * mSceneKeyPoints, goodModm, out);
		 * 
		 * Core.line(sceneRGB, new Point(scene_corners.get(0, 0)), new Point(
		 * scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
		 * Core.line(sceneRGB, new Point(scene_corners.get(1, 0)), new Point(
		 * scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
		 * Core.line(sceneRGB, new Point(scene_corners.get(2, 0)), new Point(
		 * scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
		 * Core.line(sceneRGB, new Point(scene_corners.get(3, 0)), new Point(
		 * scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
		 * System.out.println("Computation Time: " + (System.currentTimeMillis()
		 * - start) + "ms");
		 */
		// Highgui.imwrite("Box.png", sceneRGB);
		// Highgui.imwrite("Matches.png", out);
	}

	public static void main(String[] args) {
		new DetectCard().run("data/faeriesource.jpg", "data/faeriescene2.jpg");
	}
}