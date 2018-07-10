package com.donkingliang.imageselector;

import android.content.Intent;

public abstract class OnQRPhotoListener<T> implements OnQRPotoScanCallback<T> {
    /**
     * 当点击手动添加时回调
     */
    public void onManual(int requestCode, int resultCode, Intent data) {
    }
}
