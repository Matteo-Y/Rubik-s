package com.matt.livefeed;

import androidx.annotation.NonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import org.opencv.android.CameraActivity;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MainActivity extends CameraActivity {
    final int CAMERA_PERM_CODE = 100, USB_CODE = 101;
    Toast debugMarker, notification;
    private final Scalar[][] colorRanges = {
            {new Scalar(85.0, 180.0, 110.0), new Scalar(130.0, 256.0, 256.0)}, //blue
            {new Scalar(10.0, 120.0, 165.0),  new Scalar(23.0, 256.0, 256.0)}, //orange
            {new Scalar(21.0, 55.0, 117.0),  new Scalar(40.0, 256.0, 256.0)}, //yellow
            {new Scalar(35.0, 80.0, 80.0),  new Scalar(80.0, 256.0, 256.0)}, //green
            {new Scalar(155.0, 100.0, 0.0),  new Scalar(179.0, 256.0, 256.0)}, //red
            {new Scalar(0.0, 0.0, 150.0),    new Scalar(255.0, 120.0, 256.0)}, //white
            {new Scalar(0.0, 100.0, 136.0),  new Scalar(8.0, 256.0, 256.0)} //red IIe
    };
    
    JavaCameraView cameraView;
    ToggleButton flashlight;
    CameraManager cameraManager;
    String cameraID;
    TextView lastSend;
    Button connectButton;
    Button captureButton;
    Button gripButton;

    boolean captureFrame = false;

    String ACTION_USB_PERMISSION = "permission";
    UsbManager usbManager;
    UsbDevice usbDevice = null;
    UsbSerialDevice usbSerial = null;
    UsbDeviceConnection usbConnection = null;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

        debugMarker = Toast.makeText(this, "REACHED", Toast.LENGTH_SHORT);
        notification = Toast.makeText(this, "Notification", Toast.LENGTH_SHORT);

        cameraView = findViewById(R.id.cameraView);
        flashlight = findViewById(R.id.flashlight);
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        lastSend = findViewById(R.id.lastSend);

        connectButton = findViewById(R.id.connect);
        captureButton = findViewById(R.id.capture);
        gripButton = findViewById(R.id.grip_toggle);

        connectButton.setOnClickListener(v -> connectUSB());
        captureButton.setOnClickListener((v) -> captureFrame = true);
        gripButton.setOnClickListener((v) -> writeUSB("T"));

        connectButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) connectButton.setBackgroundColor(Color.rgb(220, 220, 220));
            else if(event.getAction() == MotionEvent.ACTION_UP) connectButton.setBackgroundColor(Color.rgb(255, 255, 255));
            return false;
        });
        captureButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) captureButton.setBackgroundColor(Color.rgb(220, 220, 220));
            else if(event.getAction() == MotionEvent.ACTION_UP) captureButton.setBackgroundColor(Color.rgb(255, 255, 255));
            return false;
        });
        gripButton.setOnTouchListener((v, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) gripButton.setBackgroundColor(Color.rgb(220, 220, 220));
            else if(event.getAction() == MotionEvent.ACTION_UP) gripButton.setBackgroundColor(Color.rgb(255, 255, 255));
            return false;
        });

        cameraView.setCvCameraViewListener(new CameraBridgeViewBase.CvCameraViewListener2() {
            @Override
            public void onCameraViewStarted(int width, int height) {}
            @Override
            public void onCameraViewStopped() {}

            @Override
            public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
                return handleFrame(inputFrame.rgba());
                //return inputFrame.rgba();
            }
        });
        try {
            cameraID = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        flashlight.setOnClickListener(v -> {
            if (flashlight.isChecked()) {
                cameraView.turnOnTheFlash();
            } else {
                cameraView.turnOffTheFlash();
            }
        });


        Toast successfulOpen = Toast.makeText(this, "Connected", Toast.LENGTH_SHORT);
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() != null && intent.getAction() == ACTION_USB_PERMISSION) {
                    boolean permGranted = false;
                    if(intent.getExtras() != null) permGranted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                    if(permGranted) {
                        usbConnection = usbManager.openDevice(usbDevice);
                        usbSerial = UsbSerialDevice.createUsbSerialDevice(usbDevice, usbConnection);
                        if(usbSerial != null) {
                            if(usbSerial.open()) {
                                usbSerial.setBaudRate(9600);
                                usbSerial.setDataBits(UsbSerialInterface.DATA_BITS_8);
                                usbSerial.setStopBits(UsbSerialInterface.STOP_BITS_1);
                                usbSerial.setParity(UsbSerialInterface.PARITY_NONE);
                                usbSerial.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                                successfulOpen.show();
                            }
                        }
                    }
                } else if(intent.getAction() != null && intent.getAction() == UsbManager.ACTION_USB_DEVICE_DETACHED) {
                    disconnectUSB();
                }
            }
        };
        usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        this.registerReceiver(broadcastReceiver, filter, RECEIVER_EXPORTED);

        if(OpenCVLoader.initDebug()) {
            cameraView.enableView();
        }
    }

    public void connectUSB() {
        // Arduino vendorID: 2341
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if(usbDevices.isEmpty()) {
            Toast.makeText(this, "no devices found", Toast.LENGTH_SHORT).show();
            return;
        }
        for(Map.Entry<String, UsbDevice> entry: usbDevices.entrySet()) {
            usbDevice = entry.getValue();
            debugMarker.setText("Attempting connection to: " + usbDevice.getVendorId());
            debugMarker.show();
            PendingIntent intent;
            intent = PendingIntent.getBroadcast(this, USB_CODE, new Intent(ACTION_USB_PERMISSION), PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT | PendingIntent.FLAG_MUTABLE);
            usbManager.requestPermission(usbDevice, intent);
        }
    }
    public boolean writeUSB(String data) {
        if(usbDevice == null) return false;
        usbSerial.write(data.getBytes());
        notification.setText("Sent!");
        notification.show();
        return true;
    }
    public void disconnectUSB() {
        if(usbSerial != null) usbSerial.close();
        usbConnection = null;
        usbDevice = null;
        Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
    }


    int CELL_SAMPLES = 9;
    Point frameCenter;
    int cubeRadius, cubeWidth, cellWidth;
    Rect cubeFrame;
    Rect cellRects[] = new Rect[9];
    public Mat handleFrame(Mat input) {
        Mat output = input.clone();
        frameCenter = new Point(output.cols() / 2, output.rows() / 2);
        cubeWidth = (int)(output.cols() / 2);
        cubeRadius = cubeWidth / 2;
        cellWidth = cubeWidth / 3;

        cubeFrame = new Rect((int)(frameCenter.x - cubeRadius), (int)(frameCenter.y - cubeRadius), 2 * cubeRadius, 2 * cubeRadius);
        cellRects[0] = new Rect(0 ,0, cellWidth, cellWidth); cellRects[1] = new Rect(cellWidth ,0, cellWidth, cellWidth); cellRects[2] = new Rect(2 * cellWidth ,0, cellWidth, cellWidth);
        cellRects[3] = new Rect(0 ,cellWidth, cellWidth, cellWidth); cellRects[4] = new Rect(cellWidth ,cellWidth, cellWidth, cellWidth); cellRects[5] = new Rect(2 * cellWidth ,cellWidth, cellWidth, cellWidth);
        cellRects[6] = new Rect(0 ,2 * cellWidth, cellWidth, cellWidth); cellRects[7] = new Rect(cellWidth ,2 * cellWidth, cellWidth, cellWidth); cellRects[8] = new Rect(2 * cellWidth ,2 * cellWidth, cellWidth, cellWidth);

        Mat copy = new Mat(input, cubeFrame);

        // RGBA to HSV
        Imgproc.cvtColor(copy, copy, Imgproc.COLOR_RGBA2BGR);
        Imgproc.cvtColor(copy, copy, Imgproc.COLOR_BGR2HSV);

        PointColorPair[] cells = new PointColorPair[CELL_SAMPLES];
        int counter = 0;

        // For each color
        for(int i = 0; i < 7; i++) {
            LinkedList<Point> points = getColorLocations(i, copy.clone(), new Point(cubeRadius, cubeRadius), cubeRadius);
            for(Point point : points) {
                if(counter > CELL_SAMPLES - 1) break;

                point.x = point.x + frameCenter.x - cubeRadius;
                point.y = point.y + frameCenter.y - cubeRadius;

                boolean dupe = false;
                for(PointColorPair cell : cells) {
                    if(cell == null) break;
                    if(Math.hypot(Math.abs(point.x - cell.getPoint().x), Math.abs(point.y - cell.getPoint().y)) < 50) {
                        dupe = true;
                        break;
                    }
                }
                if(dupe) continue;

                cells[counter] = new PointColorPair(point, i);
                counter++;
            }
        }

        for(PointColorPair cell : cells) {
            if(cell == null) break;
            Imgproc.circle(output, cell.getPoint(), 80, indexToRGB(cell.color), -1);
        }

        if(captureFrame) {
            captureFrame = false;
            attemptCaptureSend(cells);
        }

        Imgproc.rectangle(output, cubeFrame, new Scalar(255, 255, 255), 3);
        return output;
    }
    public void attemptCaptureSend(PointColorPair[] cells) {
        for(PointColorPair cell : cells) {
            if(cell == null) return;
        }
        PointColorPair[] sortedCells = getSortedCells(cells);
        String outputStr = arrayToString(sortedCells);

        if(writeUSB(outputStr)) lastSend.setText(outputStr);
    }
    public String arrayToString(PointColorPair[] cells) {
        String result = "";
        for(PointColorPair cell : cells) {
            result += indexToColor(cell.getColor());
        }
        return result;
    }
    public LinkedList<Point> getColorLocations(int colorIndex, Mat input, Point center, int threshold) {
        LinkedList<Point> points = new LinkedList<Point>();
        Core.inRange(input, colorRanges[colorIndex][0], colorRanges[colorIndex][1], input);
        for(Rect cellRect : cellRects) {
            Mat cell = new Mat(input, cellRect);
            Scalar mean = Core.mean(cell);
            if(mean.val[0] > 150) points.add(new Point(cellRect.x + cellWidth / 2, cellRect.y + cellWidth / 2));
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

        public double getX() {
            return point.x;
        }

        public double getY() {
            return point.y;
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
            case 0: result = "B";
                    break;
            case 1: result = "O";
                    break;
            case 2: result = "Y";
                    break;
            case 3: result = "G";
                    break;
            case 4: result = "R";
                    break;
            case 5: result = "W";
                    break;
            case 6: result = "R";
                    break;
        }
        return result;
    }
    public Scalar indexToRGB(int index) {
        Scalar result = new Scalar(0, 0, 0);
        switch(index) {
            case 0: result = new Scalar(0, 0, 255);
                break;
            case 1: result = new Scalar(255, 144, 0);
                break;
            case 2: result = new Scalar(255, 230, 0);
                break;
            case 3: result = new Scalar(0, 255, 0);
                break;
            case 4: result = new Scalar(255, 0, 0);
                break;
            case 5: result = new Scalar(255, 255, 255);
                break;
            case 6: result = new Scalar(255, 0, 0);
                break;
        }
        return result;
    }
    public PointColorPair[] getSortedCells(PointColorPair[] cells) {
        PointColorPair[] sorted = cells.clone();

        sortCells(0, 9, 0, sorted);
        for(int i = 0; i < 3; i++) {
            sortCells(i * 3, (i + 1) * 3, 1, sorted);
        }

        return sorted;
    }
    public void sortCells(int start, int end, int axis, PointColorPair[] cells) {
        for (int i = start; i < end; i++) {
            for (int j = i; j < end; j++) {
                if(axis == 0) {
                    if(cells[j].getX() > cells[i].getX()) {
                        PointColorPair temp = cells[i];
                        cells[i] = cells[j];
                        cells[j] = temp;
                    }
                } else if(axis == 1) {
                    if(cells[j].getY() > cells[i].getY()) {
                        PointColorPair temp = cells[i];
                        cells[i] = cells[j];
                        cells[j] = temp;
                    }
                }
            }
        }
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


    public String byteArrayToStringList(byte[] array) {
        String result = "";
        for(byte b : array) {
            result += b + ", ";
        }
        return result;
    }
}