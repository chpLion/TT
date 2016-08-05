package DataContext;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.util.LruCache;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by chen on 15/12/25.
 * 对图片进行操作的类
 */
public class ImagesUtil {

    private ImageView mImageView;
    private InputStream mInputStream = null;
    private Context context;
    private final static ImagesUtil imagesUtil = new ImagesUtil();
    //Map<String ,Bitmap> cache = new HashMap<>();
    private int mCacheSize;

    //使用lru缓存算法
    private LruCache<String ,Bitmap> cache;
    private ImagesUtil(){

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        //缓存为最大内存的四分之一
        mCacheSize = maxMemory /4;
        cache = new LruCache<String, Bitmap>(maxMemory){
            //重写获取大小方法

            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
    }
    public static ImagesUtil getInstance(){
        return imagesUtil;
    }
    /**
     * 通过异步加载来加载网络图片
     * @param imageView
     * @param url
     */
    public void getBitmapByAsyncTask(Context context,ImageView imageView, String url,String tag){

        new LoadBitmapAsyncTask(context,imageView,url,tag).execute(url);
    }

    /**
     * 通过异步加载来加载网络图片
     * @param imageView
     * @param url
     */
    public void getBitmapByAsyncTask(Context context,ImageView imageView, String url){

        new LoadBitmapAsyncTask(context,imageView,url,null).execute(url);
    }
    /**
     * 通过URL下载网络图片
     * @param url
     * @return
     */
    public Bitmap getBitmapFromUrl(String urlStr){

        Bitmap bitmap = null;
        if (cache.get(urlStr)!=null){
            //已经有缓存
            return  cache.get(urlStr);
        }
        try {
            URL mUrl = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
            connection.connect();
            InputStream is = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            return bitmap;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 用于加载图片的类
     */
    class LoadBitmapAsyncTask extends AsyncTask<String,Void,Bitmap>{

        private ImageView imageView;
        private Context context;
        private String mTag;//用URL于标识图片的tag
        private String mUrl;
        public LoadBitmapAsyncTask(Context context,ImageView imageView,String url,String mTag) {
            this.imageView = imageView;
            this.context = context;
            this.mUrl = url;
            this.mTag = mTag;
        }


        @Override
        protected Bitmap doInBackground(String... params) {

            return getBitmapFromUrl(params[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap == null){
                Toast.makeText(context,"图片为空",Toast.LENGTH_SHORT).show();
                return;
            }
            //需要获取tag来判断是否是显示在这的imagview中
            if (mTag!=null) {
                if (imageView.getTag().equals(mUrl)){
                    imageView.setImageBitmap(bitmap);
                    cache.put(mUrl,bitmap);
                }
            }
            else{
                this.imageView.setImageBitmap(bitmap);
                cache.put(mUrl,bitmap);
            }
        }
    }


}
