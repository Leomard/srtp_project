package com.ns.srtp_project;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.bluetooth.BluetoothAdapter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import utils.CameraHelper;
import utils.CameraHelper.CameraInfo2;
import utils.GPUImageFilterTools;
import utils.GPUImageFilterTools.FilterAdjuster;
import utils.GPUImageFilterTools.OnGpuImageFilterChosenListener;


/**
 * Created by 28537 on 2017/10/29.
 */

public class ActivityCamera extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private GPUImage mGPUImage;
    private CameraHelper mCameraHelper;
    private CameraLoader mCamera;
    private GPUImageFilter mFilter;
    private FilterAdjuster mFilterAdjuster;
    private Switch aSwitch;
    private TextView textView;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice device;
    private BroadcastReceiver receiver;
    private String TAG="";
    private String BLUETOOTH_NAME="BT04-A";
    final String SPP_UUID="00001101-0000-1000-8000-00805F9B34FB";
    private UUID uuid;
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private BluetoothSocket mSocket;
    Pattern pattern=Pattern.compile("[0-9]{1,}.[0-9]{1,}");
    private Handler mHandler=new Handler(){
        public void handleMessage(android.os.Message msg) {
            Bundle bundle=msg.getData();
            byte b[]=bundle.getByteArray("1");
            String n=new String(b);
            Matcher matcher = pattern.matcher(n);
            String toPrint=new String("");
            if(matcher.find()) {
                toPrint = "距离：" + matcher.group() + "m   " + "速度：" ;
            }
            if(matcher.find()) {
                toPrint=toPrint+matcher.group() + "m/s";
            }
            textView.setText(toPrint);
        };

    };
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        ((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);
        findViewById(R.id.button_choose_filter).setOnClickListener(this);
        findViewById(R.id.button_capture).setOnClickListener(this);
        textView=findViewById(R.id.textView);
        mGPUImage = new GPUImage(this);
        mGPUImage.setGLSurfaceView((GLSurfaceView) findViewById(R.id.surfaceView));
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mCameraHelper = new CameraHelper(this);
        mCamera = new CameraLoader();
        uuid=UUID.fromString(SPP_UUID);
        View cameraSwitchView = findViewById(R.id.img_switch_camera);
        cameraSwitchView.setOnClickListener(this);
        if (!mCameraHelper.hasFrontCamera() || !mCameraHelper.hasBackCamera()) {
            cameraSwitchView.setVisibility(View.GONE);
        }
        aSwitch=(Switch) findViewById(R.id.switch_quwu);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true)
                {
                    mGPUImage.setFlag(1);
                }
                else
                {
                    mGPUImage.setFlag(0);
                }
            }
        });
        IntentFilter filter=new IntentFilter(BluetoothDevice.ACTION_FOUND);
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                Log.d(TAG,"mBluetoothReceiver action ="+action);
                if(BluetoothDevice.ACTION_FOUND.equals(action)){//每扫描到一个设备，系统都会发送此广播。
                    //获取蓝牙设备
                    device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(device == null || device.getName() == null) return;
                    Log.d(TAG, "name="+device.getName()+"address="+device.getAddress());
                    //蓝牙设备名称
                    String name = device.getName();
                    Log.i("ii",name);
                    if(name != null && name.equals(BLUETOOTH_NAME)){
                        mBluetoothAdapter.cancelDiscovery();
                        //取消扫描
                        connectThread=new ConnectThread(device);
                        connectThread.start();
                        Log.i("ii","connectThread create!");

                    }
                }
            }
        };
        registerReceiver(receiver,filter);
        mBluetoothAdapter.enable();
    }
    public void setToText(String n){
        textView.setText(n);
    }
    @Override
    protected void onStart() {
        super.onStart();
        reserch();
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCamera.onResume();
    }

    @Override
    protected void onPause() {
        mCamera.onPause();
        super.onPause();
        if(connectThread!=null) {
            connectThread.cancel();
        }
    }
    public void reserch(){
        new Thread(){
            public void run(){
                mBluetoothAdapter.startDiscovery();
            }
        }.start();
    }
    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.button_choose_filter:
                GPUImageFilterTools.showDialog(this, new OnGpuImageFilterChosenListener() {

                    @Override
                    public void onGpuImageFilterChosenListener(final GPUImageFilter filter) {
                        switchFilterTo(filter);
                    }
                });
                break;

            case R.id.button_capture:
                if (mCamera.mCameraInstance.getParameters().getFocusMode().equals(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    takePicture();
                } else {
                    mCamera.mCameraInstance.autoFocus(new Camera.AutoFocusCallback() {

                        @Override
                        public void onAutoFocus(final boolean success, final Camera camera) {
                            takePicture();
                        }
                    });
                }
                break;

            case R.id.img_switch_camera:
                mCamera.switchCamera();
                break;
        }
    }

    private void takePicture() {
        // TODO get a size that is about the size of the screen
        Camera.Parameters params = mCamera.mCameraInstance.getParameters();
        params.setRotation(90);
        mCamera.mCameraInstance.setParameters(params);
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            Log.i("ASDF", "Supported: " + size.width + "x" + size.height);
        }
        mCamera.mCameraInstance.takePicture(null, null,
                new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, final Camera camera) {

                        final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d("ASDF",
                                    "Error creating media file, check storage permissions");
                            return;
                        }

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(data);
                            fos.close();
                        } catch (FileNotFoundException e) {
                            Log.d("ASDF", "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d("ASDF", "Error accessing file: " + e.getMessage());
                        }

                        data = null;
                        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());
                        // mGPUImage.setImage(bitmap);
                        final GLSurfaceView view = (GLSurfaceView) findViewById(R.id.surfaceView);
                        view.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
                        mGPUImage.saveToPictures(bitmap, "GPUImage",
                                System.currentTimeMillis() + ".jpg",
                                new GPUImage.OnPictureSavedListener() {

                                    @Override
                                    public void onPictureSaved(final Uri
                                                                       uri) {
                                        pictureFile.delete();
                                        camera.startPreview();
                                        view.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
                                    }
                                });
                    }
                });
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private static File getOutputMediaFile(final int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    private void switchFilterTo(final GPUImageFilter filter) {
        if (mFilter == null
                || (filter != null && !mFilter.getClass().equals(filter.getClass()))) {
            mFilter = filter;
            mGPUImage.setFilter(mFilter);
            mFilterAdjuster = new FilterAdjuster(mFilter);
        }
    }

    @Override
    public void onProgressChanged(final SeekBar seekBar, final int progress,
                                  final boolean fromUser) {
        if (mFilterAdjuster != null) {
            mFilterAdjuster.adjust(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
    }

    private class CameraLoader {

        private int mCurrentCameraId = 0;
        private Camera mCameraInstance;

        public void onResume() {
            setUpCamera(mCurrentCameraId);
        }

        public void onPause() {
            releaseCamera();
        }

        public void switchCamera() {
            releaseCamera();
            mCurrentCameraId = (mCurrentCameraId + 1) % mCameraHelper.getNumberOfCameras();
            setUpCamera(mCurrentCameraId);
        }

        private void setUpCamera(final int id) {
            mCameraInstance = getCameraInstance(id);
            Camera.Parameters parameters = mCameraInstance.getParameters();
            // TODO adjust by getting supportedPreviewSizes and then choosing
            // the best one for screen size (best fill screen)
            if (parameters.getSupportedFocusModes().contains(
                    Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            }
            mCameraInstance.setParameters(parameters);

            int orientation = mCameraHelper.getCameraDisplayOrientation(
                    ActivityCamera.this, mCurrentCameraId);
            CameraInfo2 cameraInfo = new CameraInfo2();
            mCameraHelper.getCameraInfo(mCurrentCameraId, cameraInfo);
            boolean flipHorizontal = cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT;
            mGPUImage.setUpCamera(mCameraInstance, orientation, flipHorizontal, false);
        }

        /** A safe way to get an instance of the Camera object. */
        private Camera getCameraInstance(final int id) {
            Camera c = null;
            try {
                c = mCameraHelper.openCamera(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }

        private void releaseCamera() {
            mCameraInstance.setPreviewCallback(null);
            mCameraInstance.stopPreview();
            mCameraInstance.release();
            mCameraInstance = null;
        }
    }
    private class ConnectThread extends Thread {
        private final BluetoothDevice mmDevice;
        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;
            // 得到一个bluetoothsocket
            try {
                mSocket = device.createRfcommSocketToServiceRecord
                        (uuid);
                Log.i("ii","mSocket create!");
            } catch (IOException e) {
                Log.e(TAG, "create() failed", e);
                textView.setText("创建连接失败");
                mSocket = null;
                reserch();
                this.interrupt();
            }
        }

        public void run() {
            Log.i(TAG, "BEGIN mConnectThread");
            try {
                // socket 连接,该调用会阻塞，直到连接成功或失败
                mSocket.connect();
            } catch (IOException e) {
                try {//关闭这个socket
                    mSocket.close();
                    Log.i(TAG, "mSocket connect fail");
                    textView.setText("连接失败");
                    reserch();
                    this.interrupt();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
                return;
            }
            // 启动连接线程
            connectedThread=new ConnectedThread(device,mSocket);
            connectedThread.start();
        }

        public void cancel() {
            try {
                mSocket.close();
                textView.setText("连接断开");
                this.interrupt();
            } catch (IOException e) {
                Log.e(TAG, "close() of connect socket failed", e);
            }
        }
    }
    private class ConnectedThread extends Thread{
        private BluetoothDevice mmDevice;
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        public ConnectedThread(BluetoothDevice mmDevice,BluetoothSocket mmSocket){
            this.mmDevice=mmDevice;
            this.mmSocket=mmSocket;
        }
        public void run() {
            // 监听输入流
            while (true) {
                try {
                    byte[] buffer = new byte[200];
                    // 读取输入流
                    mmInStream = mmSocket.getInputStream();
                    int bytes = mmInStream.read(buffer);
                    // 发送获得的字节的ui activity
                    if(bytes>10) {
                        Message msg = mHandler.obtainMessage();
                        Bundle bundle = new Bundle();
                        bundle.putByteArray("1", buffer);
                        msg.setData(bundle);
                        mHandler.sendMessage(msg);
                    }
                } catch (IOException e) {
                    Log.e(TAG, "disconnected", e);
                    connectionLost();
                    break;
                }
            }
        }
        public void connectionLost(){
                try {
                    mmSocket.close();
                    textView.setText("连接断开");
                    reserch();
                    this.interrupt();

                 }
                catch(IOException e) {
                    e.printStackTrace();
                }

        }
    }

}
