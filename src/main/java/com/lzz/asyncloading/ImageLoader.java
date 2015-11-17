package com.lzz.asyncloading;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by liuzhongzhou on 2015/11/16.
 */
public class ImageLoader {

    private LruCache<String, Bitmap> cache;

    private ListView mListView;
    private Set<NewsAsyncTask> mtask;

    @SuppressLint("NewApi")
    public ImageLoader(ListView listView) {
        mListView = listView;
        mtask = new HashSet<>();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        cache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };

    }


    @SuppressLint("NewApi")
    public void addToCache(String url, Bitmap bitmap) {
        if (getfromCache(url) == null) {
            cache.put(url, bitmap);
        }
    }

    @SuppressLint("NewApi")
    public Bitmap getfromCache(String url) {
        return cache.get(url);
    }


    private ImageView icon;
    private String url;
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    if (icon.getTag().equals(url)) {
                        icon.setImageBitmap((Bitmap) msg.obj);
                    }
                    break;
            }

        }
    };

    public void setIcon(ImageView i, final String newsIconUrl) {
        icon = i;
        url = newsIconUrl;
        new Thread() {
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromUrl(newsIconUrl);
                Message msg = new Message();
                msg.what = 0;
                msg.obj = bitmap;
                mHandler.sendMessage(msg);
            }
        }.start();
    }

    private Bitmap getBitmapFromUrl(String newsIconUrl) {
        URL url = null;
        try {
            url = new URL(newsIconUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            BufferedInputStream ins = new BufferedInputStream(conn.getInputStream());
            return BitmapFactory.decodeStream(ins);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setIconbyAsyncTask(ImageView i, final String newsIconUrl) {
        if (getfromCache(newsIconUrl) == null) {
            i.setImageResource(R.drawable.lzz);
        } else {
            if (i.getTag().equals(newsIconUrl)) {
                i.setImageBitmap(getfromCache(newsIconUrl));
            }
        }
    }

    public void setIconbyAsyncTaskUsePosition(int start, int end) {
        for (int i = start; i < end; i++) {
            String url = NewsAdapter.urlarray[i];
            LogUtil.e("lzz","url="+url);
            if (getfromCache(url) == null) {
                NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mtask.add(task);
            } else {
                ImageView mImageView = (ImageView) mListView.findViewWithTag(url);
                mImageView.setImageBitmap(getfromCache(url));
            }
        }


    }

    public void cancelLoad() {
        if(mtask!=null){
            for(NewsAsyncTask task:mtask){
                task.cancel(false);
            }
        }
    }


    class NewsAsyncTask extends AsyncTask<String, Void, Bitmap> {
        //       private ImageView mImageView;
        private String mUrl;

        public NewsAsyncTask(/*ImageView i,*/ String newsIconUrl) {
//            mImageView = i;
            mUrl = newsIconUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap;
            bitmap = getBitmapFromUrl(url);
            if (bitmap != null) {
                addToCache(url, bitmap);
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            ImageView mImageView = (ImageView) mListView.findViewWithTag(mUrl);
            if (mImageView != null && bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            mtask.remove(this);
        }
    }


}
