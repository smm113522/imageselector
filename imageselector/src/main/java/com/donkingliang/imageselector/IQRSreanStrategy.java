package com.donkingliang.imageselector;

import android.content.Intent;

abstract class IQRSreanStrategy {

    /**
     * 发起照相
     *
     * @param requestCode
     */
    abstract void startPhotograph(int requestCode);

    /**
     * 发起相册
     *
     * @param requestCode
     */
    abstract void startPhotoCamera(int requestCode);

    /**
     * 结果回调
     *
     * @param requestCode 请求码
     * @param resultCode  结果码
     * @param data        数据
     */
    abstract void onActivityResult(int requestCode, int resultCode, Intent data);
}
