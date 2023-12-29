package com.matt.livefeed;

import static org.opencv.imgproc.Imgproc.cvtColor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends CameraActivity {
    int CAMERA_PERM_CODE = 100;
    private Scalar[][] colorRanges = {
            {new Scalar(96.0, 180.0, 110.0), new Scalar(130.0, 256.0, 256.0)}, //blue
            {new Scalar(5.0, 120.0, 165.0),  new Scalar(17.0, 256.0, 256.0)}, //orange
            {new Scalar(21.0, 55.0, 117.0),  new Scalar(40.0, 256.0, 256.0)}, //yellow
            {new Scalar(45.0, 80.0, 100.0),  new Scalar(80.0, 256.0, 256.0)}, //green
            {new Scalar(165.0, 114.0, 0.0),  new Scalar(179.0, 256.0, 256.0)}, //red
            {new Scalar(0.0, 0.0, 150.0),    new Scalar(255.0, 120.0, 256.0)}, //white
            {new Scalar(0.0, 114.0, 136.0),  new Scalar(4.0, 256.0, 256.0)} //red II
    };
    
    CameraBridgeViewBase cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        cameraView = findViewById(R.id.cameraView);

        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {}
            @Override
            public void onCameraViewStopped() {}

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                return handleFrame(inputFrame.rgba());
            }
        });

        if(OpenCVLoader.initDebug()) {
            cameraView.enableView();
            Log.d("LOADED", "Success!");
        }
        else {
            Log.d("LOADED", "Error.");
        }
    }

    public Mat handleFrame(Mat input) {
        Mat output = input.clone();
        Point frameCenter = new Point(output.cols() / 2, output.rows() / 2);
        int cubeRadius = 300;

        PointColorPair[] cells = new PointColorPair[128];
        int counter = 0;
        Mat copy = input.clone();
        Imgproc.cvtColor(copy, copy, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(copy, copy, Imgproc.COLOR_BGR2HSV);
        for(int i = 0; i < 7; i++) {
            LinkedList<Point> points = getColorLocations(i, copy.clone(), frameCenter, cubeRadius);
            for(Point point : points) {
                if(counter > 127) break;
                cells[counter] = new PointColorPair(point, i);
                counter++;
            }
        }

        PointColorPair centerCell = null;
        double closest = Double.POSITIVE_INFINITY;
        for(PointColorPair cell : cells) {
            if(cell == null) break;
            double dist = Math.hypot(Math.abs(frameCenter.x - cell.getPoint().x), Math.abs(frameCenter.y - cell.getPoint().y));
            if(dist < closest) {
                closest = dist;
                centerCell = cell;
            }
        }
        for(PointColorPair cell : cells) {
            if(cell == null) break;
            Scalar color = new Scalar(0, 255, 0);
            if(cell == centerCell) color = new Scalar(255, 0, 0);
            Imgproc.circle(output, cell.getPoint(), 80, color, 5);
            Imgproc.putText(output, indexToColor(cell.getColor()), cell.point, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(0, 255, 0), 3);
        }

        Imgproc.circle(output, frameCenter, 10, new Scalar(255, 255, 255), 3);
        Imgproc.circle(output, frameCenter, cubeRadius, new Scalar(255, 255, 255), 3);
        return output;
    }

    public LinkedList<Point> getColorLocations(int colorIndex, Mat input, Point center, int threshold) {
        // Highlight selected color
        Core.inRange(input, colorRanges[colorIndex][0], colorRanges[colorIndex][1], input);

        Mat binary = new Mat();
        Imgproc.threshold(input, binary, 0, 100, Imgproc.THRESH_BINARY_INV);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchey = new Mat();
        Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        LinkedList<Point> points = new LinkedList<Point>();
        for(int i = 0; i < contours.size(); i++) {
            if(Imgproc.contourArea(contours.get(i)) < 1000) continue;
            Rect rect = Imgproc.boundingRect(contours.get(i));
            if(rect.height < 100 || rect.width < 100) continue; // Too small
            if(rect.height > 250 || rect.width > 250) continue; // Too big
            if(Math.abs(rect.height - rect.width) > 100) continue; // Too non-square
            int x = rect.x + rect.width / 2;
            int y = rect.y + rect.height / 2;
            double dist = Math.hypot(Math.abs(center.x - x), Math.abs(center.y - y));
            if(dist > threshold) continue;
            points.add(new Point(x, y));
        }
        return points;
    }

    class PointColorPair {
        protected Point point;
        protected int color;
        public PointColorPair(Point point, int color) {
            this.point = point;
            this.color = color;
        }

        public Point getPoint() {
            return point;
        }

        public int getColor() {
            return color;
        }
    }

    public String indexToColor(int index) {
        String result = "";
        switch(index) {
            case 0: result = "blue";
                    break;
            case 1: result = "orange";
                    break;
            case 2: result = "yellow";
                    break;
            case 3: result = "green";
                    break;
            case 4: result = "red";
                    break;
            case 5: result = "white";
                    break;
            case 6: result = "red";
                    break;
        }
        return result;
    }

    @Override
    protected List<? extends CameraBridgeViewBase> getCameraViewList() {
        return Collections.singletonList(cameraView);
    }

    public void getPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCoe, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCoe, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            getPermission();
        }
    }
}