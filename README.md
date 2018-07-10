# ImageSelector

一句话搞定 图片选择

这是是我使用的情况是这样的。。你就会发现很有趣的东西。。
 private static final int REQUEST_CODE = 0x00000011;
    private static final int REQUEST_CAMERA_CODE_HEAD = 0x00000022;

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



	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //注册onActivityResult
        QRPhotoManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data); 
    }



## 总结使用

			选择照相或者图片都是这样方法。

			QRPhotoManager.getInstance()
                        .with(MainActivity.this)
						如果是相册就返回来这些
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
					这个是一张图片 放回来的，放回来图片地址 照相机或者单选一张
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
						这是放回来的参数 和 图片数组，以及 一共可以选择多少张
                        .startPhotograph(REQUEST_CODE,mAdapter.getImages(),20)
						这个是照相机的使用
                        .startPhotoCamera(REQUEST_CAMERA_CODE_HEAD);


## 扩展 你也可以按照下面的进行选择图片



Android图片选择器，仿微信的图片选择器的样式和效果。支持图片的单选、限数量的多选和不限数量的多选。支持图片预览和图片文件夹的切换。支持图片单选并剪裁。

先上效果图：

![相册](https://github.com/donkingliang/ImageSelector/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/%E7%9B%B8%E5%86%8C.jpg)  ![文件夹](https://github.com/donkingliang/ImageSelector/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/%E6%96%87%E4%BB%B6%E5%A4%B9.jpg)  ![预览](https://github.com/donkingliang/ImageSelector/blob/master/%E6%95%88%E6%9E%9C%E5%9B%BE/%E9%A2%84%E8%A7%88.jpg)

**1、引入依赖**

在Project的build.gradle在添加以下代码

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
			// 如果你使用的是1.4.0或更早的版本，这句可以不用。
			maven { url 'https://maven.google.com' }
		}
	}
```
在Module的build.gradle在添加以下代码

```
	compile 'com.github.donkingliang:ImageSelector:1.5.1'
```
1.5.0版本中使用了Glide 4.x的版本，由于Glide 3.x版本和4.x版本在使用上有所差异，如果你的项目使用了Glide 3.x版本，而又不想升级到4.x,那么你也可以使用ImageSelector:1.4.0版本，它和1.5.0版本之间只是Glide版本的差异而已。

**2、配置AndroidManifest.xml**
```xml
//储存卡的读取权限
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

//图片选择Activity
<activity android:name="com.donkingliang.imageselector.ImageSelectorActivity"
	//去掉Activity的ActionBar。
	//使用者可以根据自己的项目去配置，不一定要这样写，只要不Activity的ActionBar去掉就可以了。
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"
    //横竖屏切换处理。
    //如果要支持横竖屏切换，一定要加上这句，否则在切换横竖屏的时候会发生异常。
    android:configChanges="orientation|keyboardHidden|screenSize"/>
    
//图片预览Activity
<activity android:name="com.donkingliang.imageselector.PreviewActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar"
    android:configChanges="orientation|keyboardHidden|screenSize"/>

//图片剪切Activity
<activity
    android:name="com.donkingliang.imageselector.ClipImageActivity"
    android:theme="@style/Theme.AppCompat.Light.NoActionBar" />
```
**3、调起图片选择器**

ImageSelector支持图片的单选、限数量的多选和不限数量的多选。在调起图片选择器的时候需要告诉选择器，是那种情况。为了方便大家的使用，我在项目中提供了一个工具类，可以方便地调起选择器。
调起选择器只需要简单的一句代码就可以了。
```java
 //单选
 ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, true, 0);

//限数量的多选(比喻最多9张)
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9);
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 9, selected); // 把已选的传入。

//不限数量的多选
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE);
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, selected); // 把已选的传入。
//或者
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 0);
ImageSelectorUtils.openPhoto(MainActivity.this, REQUEST_CODE, false, 0, selected); // 把已选的传入。

//单选并剪裁
ImageSelectorUtils.openPhotoAndClip(MainActivity.this, REQUEST_CODE);
```
REQUEST_CODE就是调用者自己定义的启动Activity时的requestCode，这个相信大家都能明白。selected可以在再次打开选择器时，把原来已经选择过的图片传入，使这些图片默认为选中状态。

**4、接收选择器返回的数据**

在Activity的onActivityResult方法中接收选择器返回的数据。
```java
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
	    //获取选择器返回的数据
            ArrayList<String> images = data.getStringArrayListExtra(
            ImageSelectorUtils.SELECT_RESULT);
        }
    }
```
ImageSelectorUtils.SELECT_RESULT是接收数据的key。数据是以ArrayList的字符串数组返回的，就算是单选，返回的也是ArrayList数组，只不过这时候ArrayList只有一条数据而已。ArrayList里面的数据就是选中的图片的文件路径。

想要了解ImageSelector的实现思路和核心代码的同学请看这里：[Android 实现一个仿微信的图片选择器](http://blog.csdn.net/u010177022/article/details/70147243)
