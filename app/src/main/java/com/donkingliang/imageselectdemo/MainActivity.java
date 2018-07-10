package com.donkingliang.imageselectdemo;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.donkingliang.imageselectdemo.adapter.ImageAdapter;
import com.donkingliang.imageselector.OnQRPhotoListener;
import com.donkingliang.imageselector.QRPhotoManager;
import com.donkingliang.imageselector.utils.ImageSelectorUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE = 0x00000011;
    private static final int REQUEST_CAMERA_CODE_HEAD = 0x00000022;

    private RecyclerView rvImage;
    private ImageAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvImage = findViewById(R.id.rv_image);
        rvImage.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new ImageAdapter(this);
        rvImage.setAdapter(mAdapter);

        findViewById(R.id.btn_single).setOnClickListener(this);
        findViewById(R.id.btn_limit).setOnClickListener(this);
        findViewById(R.id.btn_unlimited).setOnClickListener(this);
        findViewById(R.id.btn_clip).setOnClickListener(this);
        findViewById(R.id.btn_t_s).setOnClickListener(this);
        findViewById(R.id.btn_limit_other).setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册onActivityResult
        QRPhotoManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data);

//        if (requestCode == REQUEST_CODE && data != null) {
//            ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
//            mAdapter.refresh(images);
//        }
//
//        if (requestCode == REQUEST_CAMERA_CODE_HEAD) {
//            if (resultCode == Activity.RESULT_OK) {
//
//                imagePath = ImageUtlis.getPath(this, imageUri);
//                mAdapter.add(imagePath);
//                ImageUtlis.ScannerByReceiver(getApplicationContext(), imagePath);
//            }
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_t_s:
                BottomMenuDialog dialog = new BottomMenuDialog.BottomMenuBuilder()
                        .addItem("拍照", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startCamera(REQUEST_CAMERA_CODE_HEAD);
                            }
                        })
                        .addItem("相册中选择", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 跳转到图片选择器
                                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE,
                                        false, 0, mAdapter.getImages()); // 把已选的传入。
                            }
                        })
                        .addItem("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).build();
                dialog.show(getSupportFragmentManager());

                break;
            case R.id.btn_single:
                //单选
                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, true, 0);
                break;

            case R.id.btn_limit:
                //多选(最多9张)
                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9);
//                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9, mAdapter.getImages()); // 把已选的传入。
                break;

            case R.id.btn_unlimited:
                //多选(不限数量)
//                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE);
//                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, mAdapter.getImages()); // 把已选的传入。
                //或者
//                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 0);
                ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE,
                        false, 0, mAdapter.getImages()); // 把已选的传入。
                break;

            case R.id.btn_clip:
                //单选并剪裁
                ImageSelectorUtils.openPhotoAndClip(MainActivity.this, REQUEST_CODE);
            case R.id.btn_limit_other:

                BottomMenuDialog dialog1 = new BottomMenuDialog.BottomMenuBuilder()
                        .addItem("拍照", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                QRPhotoManager.getInstance()
                                        .with(MainActivity.this)
                                        .setCallback(new OnQRPhotoListener<String>() {
                                            @Override
                                            public void onCompleted(String result) {
                                                mAdapter.add(result);
                                            }

                                            @Override
                                            public void onError(Throwable errorMsg) {

                                            }

                                            @Override
                                            public void onCancel() {

                                            }
                                        })
                                        .startPhotoCamera(REQUEST_CAMERA_CODE_HEAD);
                            }
                        })
                        .addItem("相册中选择", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 跳转到图片选择器
                                QRPhotoManager.getInstance()
                                        .with(MainActivity.this)
                                        .setCallback(new OnQRPhotoListener<ArrayList<String>>() {
                                            @Override
                                            public void onCompleted(ArrayList<String> result) {
                                                mAdapter.refresh(result);
                                            }

                                            @Override
                                            public void onError(Throwable errorMsg) {

                                            }

                                            @Override
                                            public void onCancel() {

                                            }
                                        })
                                        .startPhotograph(REQUEST_CODE,mAdapter.getImages(),20);
                            }
                        })
                        .addItem("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }).build();
                dialog1.show(getSupportFragmentManager());
                break;
        }
    }

    Uri imageUri = null;//图片路径

    String imagePath = null;

    public void startCamera(int ok) {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File imageFile = new File(path, getDataString() + ".png");
        try {
            if (imageFile.exists()) {
                imageFile.delete();
            }
            imageFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //将File对象转换为Uri并启动照相程序
        imageUri = Uri.fromFile(imageFile);
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); //照相
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); //指定图片输出地址
        startActivityForResult(intent, ok); //启动照相
    }

    public static String getDataString() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        return time.format(nowTime);
    }

}
