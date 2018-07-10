package com.donkingliang.imageselector;

public interface OnQRPotoScanCallback<T> {

    /**
     * 完成的时候会回调此方法，结果存在于result中
     *
     * @param result 扫描结果
     */
    void onCompleted(T result);

    /**
     * 当过程出错的时候会回调
     *
     * @param errorMsg 错误信息
     */
    void onError(Throwable errorMsg);

    /**
     * 当被取消的时候回调
     */
    void onCancel();

}
