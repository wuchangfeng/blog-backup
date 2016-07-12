---
title: Bitmap 的加载 和 Cache
toc: true
date: 2016-05-21 20:56:39
tags: android
categories: About Java
description:
feature:
---

Android 开发艺术探索-Bitmap 的加载 和 Cache

扩展阅读[ASimpleCache 缓存框架](https://github.com/yangfuhai/ASimpleCache)

<!--more-->

## 一 . 高效加载 Bitmap

BitMapFactory 提供了四类方法: decodeFile,decodeResource,decodeStream 和 decodeByteArray 分别用于从文件系统,资源,输入流以及字节数组中加载出一个 Bitmap 对象。

高效加载 Bitmap 很简单,即采用 BitMapFactory.options 来加载所需要尺寸图片。BitMapFactory.options 就可以按照一定的采样率来加载缩小后的图片,将缩小后的图片置于 ImageView 中显示。

通过采样率即可高效的加载图片,遵循如下方式获取采样率:

1. 将 BitmapFactory.Options 的 inJustDecodeBounds 参数设置为 true 并加载图片
2. 从 BitmapFactory.Options 中取出图片的原始宽高信息,即对应于 outWidth 和 outHeight 参数
3. 根据采样率的规则并结合目标 View 的所需大小计算出采样率 inSampleSize
4. 将 BitmapFactory.Options 的 injustDecodeBounds 参数设置为 false,然后重新加载图片

通过上述四个步骤,加载出的图片就是最终缩放后的图片,当然也有可能没有缩放。

代码实现如下:

``` java
public Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options = new BitmapFactory.Options();
    options.inJustDecodeBounds = true;
    BitmapFactory.decodeResource(res, resId, options);

    // Calculate inSampleSize
    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res, resId, options);
}

public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
    if (reqWidth == 0 || reqHeight == 0) {
        return 1;
    }

    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    Log.d(TAG, "origin, w= " + width + " h=" + height);
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {
        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and
        // keeps both height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
            inSampleSize *= 2;
        }
    }

    Log.d(TAG, "sampleSize:" + inSampleSize);
    return inSampleSize;
}
```



实际使用就可以像下面这样了,如加载 100*100 的图片大小,就可以像下面这样高效的加载图片了:

``` java
mImageView.setImageBitmap(
decodeSampledBitmapFromResource(getResource(),R.id.myimage,100,100));
```



## 二 . Android 中的缓存策略

目前常用的算法是 LRU，即近期最少使用算法，当缓存存满时，会优先淘汰**近期最少使用的缓存对象**

### 2.1 LruCache

LruCache 是一个泛型类，其内部实现机制是 LinkedHashMap 以**强引用**的方式存储外部的缓存对象，提供了 get() 和 put() 来完成缓存对象的存取。当缓存满了,移除较早的缓存对象,再添加新的。LruCache 是线程安全的。

* 强引用:直接的对象引用
* 软引用:当一个对象只有软引用时,系统内存不足时,会被 gc 回收
* 弱引用:当一个对象只有弱引用时,随时会被回收

### 2.2 DiskLriCache

DiskLruCache 用于实现存储设备缓存,即磁盘缓存。

2.2.1 DiskLruCache 的创建

由于它不属于 Android SDK的一部分,所以不能通过构造方法来创建,提供了 open() 方法用于自身的创建

``` java
public static DiskLruCache open(File directory,int appversion,int valueCount,long maxSize);
```

典型的 DiskLruCache 的创建过程

``` java
private static final Disk_CACHE_SIZE = 1024*1024*50;//50M

File diskCaCheDir = getDiskCacheDir(mContext,"bitmap");
if(!diskCacheDir.exists()){
  diskCacheDir.mkdirs();
}
mDiskLruCache = DiskLruCache.open(diskCaCheDir,1,1,Disk_CACHE_SIZE);
```

第三个参数表示单个节点所对应的数据,一般设置为1即可。

2.2.2 DiskLruCache 的缓存添加
缓存的添加操作是通过 Editor 完成的, Editor 表示一个缓存对象的编辑对象。DiskLruCache 不允许同时编辑一个缓存对象。

2.2.3 DiskLruCache 的缓存查找

缓存查找过程也需要将 url 转换为 key,通过 DiskLruCache 的 get() 得到一个 Snapshot 对象,然后通过该对象即可得到缓存的文件输入流,得到文件输入流即可得到 Bitmap 对象了。为了避免加载过程中 OOM,一般不会直接加载原始图片。在前面介绍通过 BitmapFactory.Options 来加载一张缩放后的图片,但是那种方法对 FileInputStream 的缩放存在问题,原因是 FileInputStream 是一种有序的文件流,而两次 decodeStream 调用影响了文件流的位置属性,导致了第二次 decodeStream 时得到的是 null。为了解决这个问题,可以通过**文件流得到其对应的文件描述符**,然后通过 BitmapFactory.decodeFileDescriptor 方法来加载一张缩放过后的图片。

``` java
 Bitmap bitmap = null;
 String key = hashKeyFormUrl(url);
 DiskLruCache.Snapshot snapShot = mDiskLruCache.get(key);
        if (snapShot != null) {
            FileInputStream fileInputStream = (FileInputStream)snapShot.getInputStream(DISK_CACHE_INDEX);
          	// 获取文件描述符
            FileDescriptor fileDescriptor = fileInputStream.getFD();
          	// 通过 BitmapFactory.decodeFileDescriptor 来加载一张缩放后的图片
            bitmap = mImageResizer.decodeSampledBitmapFromFileDescriptor(fileDescriptor,
                    reqWidth, reqHeight);
            if (bitmap != null) {
                addBitmapToMemoryCache(key, bitmap);
            }
        }

        return bitmap;
    }
```



## 三 . ImageLoader 的实现

具备的功能,即图片的同步加载,异步加载,图片的压缩,内存缓存,磁盘缓存以及网络拉取。

### 3.1 图片压缩功能

如前面所述。

### 3.2 内存缓存和磁盘缓存的实现

选择 LruCache 和 DiskLruCache 来分别完成内存缓存和磁盘缓存的工作

### 3.3 同步加载和异步加载的接口设计

关于同步加载:从 loadBitmap 的实现可以看出,其工作过程遵循如下几个步骤:先试着从内存缓存中读取图片,接着从磁盘缓存中读取图片,最后试着从网络拉取图片。另外该方法不能在主线程中调用,否则就会抛出异常。因为加载图片是一个耗时的操作。

关于异步加载:从 bindBitmap 中可以看出,binfBitmap 会先试着从内存缓存中读取结果,如果成功就直接返回,否则会从**线程池中**去调用 loadBitmap(),当加载成功后，再讲图片,图片地址以及需要绑定的 ImageView 封装成一个 loaderResult 对象,通过 mMainHandler 向主线程发送一个消息,这样就可以在主线程中给 ImageView 设置图片了。图片的异步加载是一个很有用的功能,很多时候调用者不想在单独的线程中以同步的方式来加载图片,并将图片设置给需要的 ImageVIew, 从而ImageLoader 内部需要自己需要在内部线程中加载图片,并且将图片设置给所需要的 ImageView。

**ImageLoader源码可以看[ImageLoader的实现](https://github.com/singwhatiwanna/android-art-res/blob/master/Chapter_12/src/com/ryg/chapter_12/loader/ImageLoader.java)**

##  四 . ImageLoader 的使用

核心是 ImageAdapter , 其中的 getView() 的核心方法如下:

``` java
@Override
public View getView(int position, View convertView, ViewGroup parent) {
         ViewHolder holder = null;
         if (convertView == null) {
                convertView = mInflater.inflate(R.layout.image_list_item,parent, false);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ImageView imageView = holder.imageView;
            final String tag = (String)imageView.getTag();
            final String uri = getItem(position);
            if (!uri.equals(tag)) {
                imageView.setImageDrawable(mDefaultBitmapDrawable);
            }
            if (mIsGridViewIdle && mCanGetBitmapFromNetWork) {
                imageView.setTag(uri);
              	// 这句话将图片的复杂加载过程交给 ImageLoader 了
                mImageLoader.bindBitmap(uri, imageView, mImageWidth, mImageWidth);
            }
            return convertView;
        }

```

对于上述代码 ImageAdapter 来说, ImageLoader 的加载图片的复杂过程，更不需要知道。



**优化列表卡顿现象:**

1. 不要在 getView() 中做加载图片的操作,那样肯定会耗时,像这个例子中一样,交给 ImageLoaer 来实现。
2. 控制异步加载频率, 如果用户刻意的频繁的上下滑动,可能在一瞬间加载几百个异步任务,这样会给线程池造成拥堵。解决的办法是考虑在用户滑动列表时,停止加载图片。等到列表停下来时,在进行异步加载任务。
3. 开启硬件加速:给Activity添加配置android:hardwareAccelerated="true"