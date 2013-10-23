import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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
import org.opencv.core.Scalar;
import org.opencv.features2d.DMatch;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.KeyPoint;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

class DetectCard {
	FeatureDetector mFeatureDetector;
	DescriptorExtractor mDescriptorExtractor;
	DescriptorMatcher mDescriptorMatcher;

	Mat mSourceDescriptors;
	Mat mSceneDescriptors;
	MatOfKeyPoint mSourceKeyPoints;
	MatOfKeyPoint mSceneKeyPoints;

	public void run() {
		long start = System.currentTimeMillis();
		System.out.println("\nRunning Card Detection");

		Mat sourceRGB = Highgui.imread("data/faeriesource.jpg");
		Mat sceneRGB = Highgui.imread("data/faeriescene2.jpg");

		// Mat sourceRGB = Highgui.imread("data/mysticsource.jpg");
		// Mat sceneRGB = Highgui.imread("data/mysticscene.jpg");

		// Mat sourceRGB = Highgui.imread("data/demonicart.png");
		// Mat sceneRGB = Highgui.imread("data/demonicscene.jpg");

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
		FileOutputStream fout;
		try {
			fout = new FileOutputStream("blobs.blob");
			KeyWriter.write(mSourceKeyPoints, fout);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Highgui.imwrite("Descriptors.png", mSourceDescriptors);

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

		Mat out = new Mat();

		Features2d.drawMatches(source, mSourceKeyPoints, scene,
				mSceneKeyPoints, goodModm, out);

		Core.line(sceneRGB, new Point(scene_corners.get(0, 0)), new Point(
				scene_corners.get(1, 0)), new Scalar(0, 255, 0), 4);
		Core.line(sceneRGB, new Point(scene_corners.get(1, 0)), new Point(
				scene_corners.get(2, 0)), new Scalar(0, 255, 0), 4);
		Core.line(sceneRGB, new Point(scene_corners.get(2, 0)), new Point(
				scene_corners.get(3, 0)), new Scalar(0, 255, 0), 4);
		Core.line(sceneRGB, new Point(scene_corners.get(3, 0)), new Point(
				scene_corners.get(0, 0)), new Scalar(0, 255, 0), 4);
		System.out.println("Computation Time: "+(System.currentTimeMillis() - start)+"ms");
		Highgui.imwrite("Box.png", sceneRGB);
		Highgui.imwrite("Matches.png", out);
	}
}

public class Main {
	public static void main(String[] args) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		new DetectCard().run();
	}
}