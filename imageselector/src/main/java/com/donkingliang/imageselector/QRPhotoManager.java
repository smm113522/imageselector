package com.donkingliang.imageselector;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.donkingliang.imageselector.utils.ImageSelectorUtils;
import com.donkingliang.imageselector.utils.ImageUtlis;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QRPhotoManager extends IQRSreanStrategy {

    /**
     * 扫描请求码
     */
    private static final int SCAN_REQUEST_CODE = 410;
    private static final int PHONO_REQUEST_CODE = 510;
    private static QRPhotoManager mQRCodeManager;
    private Activity context;
    private OnQRPhotoListener callback;
    private ArrayList<String> mList = new ArrayList<>();
    /**
     * 当前的请求码
     */
    private int curRequestCode = SCAN_REQUEST_CODE;
    private int photoRequestCode = PHONO_REQUEST_CODE;

    private QRPhotoManager() {
    }

    public static QRPhotoManager getInstance() {
        synchronized (QRPhotoManager.class) {
            if (mQRCodeManager == null) {
                mQRCodeManager = new QRPhotoManager();
            }
        }
        return mQRCodeManager;
    }

    /**
     * 关联调用类
     *
     * @param context
     * @return
     */
    public QRPhotoManager with(Activity context) {
        this.context = context;
        return this;
    }

    /**
     * @return
     */
    public QRPhotoManager scanningQRCode(ArrayList<String> list) {
        if (list != null) {
            this.mList = list;
        }
        return this;
    }

    public QRPhotoManager setCallback(OnQRPhotoListener callback) {
        this.callback = callback;
        return this;
    }

    /**
     * 结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callback == null) {
            return;
        }
        if (requestCode == curRequestCode && resultCode == Activity.RESULT_OK) {//成功
            if (data != null) {
                ArrayList<String> images = data.getStringArrayListExtra(ImageSelectorUtils.SELECT_RESULT);
                if (images == null) {
                    callback.onError(new Throwable("result is null"));
                } else {
                    callback.onCompleted(images);
                }
            } else {
                callback.onError(new Throwable("result is null"));
            }
        } else if (requestCode == curRequestCode && resultCode == Activity.RESULT_CANCELED) {//取消
            callback.onCancel();
        } else if (requestCode == curRequestCode && resultCode == Activity.RESULT_OK) {
            callback.onManual(requestCode, resultCode, data);
        }

        if (requestCode == photoRequestCode && resultCode == Activity.RESULT_OK) {//成功
            imagePath = ImageUtlis.getPath(context, imageUri);

            if (TextUtils.isEmpty(imagePath)) {
                callback.onError(new Throwable("result is null"));
            } else {
                ImageUtlis.ScannerByReceiver(context, imagePath);
                callback.onCompleted(imagePath);
            }
        } else if (requestCode == photoRequestCode && resultCode == Activity.RESULT_CANCELED) {//取消
            callback.onCancel();
        } else if (requestCode == photoRequestCode && resultCode == Activity.RESULT_OK) {
            callback.onManual(requestCode, resultCode, data);
        }
    }

    /**
     * 照片
     *
     * @param requestCode
     */
    public void startPhotograph(int requestCode,int maxSelectCount) {
        // 跳转到图片选择器
        this.curRequestCode = requestCode;
        ImageSelectorUtils.openPhoto(context, requestCode,
                false, maxSelectCount, mList); // 把已选的传入。
    }

    @Override
    public void startPhotograph(int requestCode) {
        // 跳转到图片选择器
        startPhotograph(requestCode);
    }

    /**
     *
     * @param requestCode
     * @param list
     * @param maxSelectCount
     */

    public void startPhotograph(int requestCode, ArrayList<String> list,int maxSelectCount) {
        if (list != null) {
            this.mList = list;
        }
        startPhotograph(requestCode,maxSelectCount);
    }

    /**
     * 照相
     *
     * @param requestCode
     */
    @Override
    public void startPhotoCamera(int requestCode) {
        this.photoRequestCode = requestCode;
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
        context.startActivityForResult(intent, requestCode); //启动照相
    }

    private Uri imageUri = null;//图片路径

    private String imagePath = null;



    private String getDataString() {
        Date nowTime = new Date();
        SimpleDateFormat time = new SimpleDateFormat("yyyyMMddHHmmss");
        return time.format(nowTime);
    }


}
