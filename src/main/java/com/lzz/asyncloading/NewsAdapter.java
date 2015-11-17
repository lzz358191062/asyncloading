package com.lzz.asyncloading;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by liuzhongzhou on 2015/11/16.
 */
@TargetApi(Build.VERSION_CODES.M)
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{

    Context mContext;
    List<NewBean> lsbeans;
    private LayoutInflater inflater;
    private ImageLoader mImageLoader;
    public static String[] urlarray;
    public NewsAdapter(Context context, List<NewBean> beans,ListView listView) {
        mContext = context;
        lsbeans = beans;
        inflater = LayoutInflater.from(mContext);
        mImageLoader = new ImageLoader(listView);
        urlarray = new String[lsbeans.size()];
        for(int i=0;i<beans.size();i++){
            urlarray[i] = beans.get(i).newsIconUrl;
        }
        listView.setOnScrollListener(this);
        mFlag = true;
    }

    @Override
    public int getCount() {
        return lsbeans.size();
    }

    @Override
    public Object getItem(int position) {
        return lsbeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item,null);

            holder = new ViewHolder();

            holder.icon = (ImageView) convertView.findViewById(R.id.imageView);
            holder.title = (TextView) convertView.findViewById(R.id.title);
            holder.content = (TextView) convertView.findViewById(R.id.content);
            convertView.setTag(holder);

        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        NewBean bean = lsbeans.get(position);
        holder.icon.setImageResource(R.drawable.lzz);
        holder.icon.setTag(bean.newsIconUrl);
        mImageLoader.setIconbyAsyncTask(holder.icon, bean.newsIconUrl);
        holder.title.setText(bean.newsTitle);
        holder.content.setText(bean.newsContent);
        return convertView;
    }

    private int start;
    private int end;
    private boolean mFlag;
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState == SCROLL_STATE_IDLE){
            //加载相应的数据
            mImageLoader.setIconbyAsyncTaskUsePosition(start,end);
            LogUtil.e("lzz","this is a status change start="+start+",end="+end);
        }else{
            //停止加载相应的数据
            mImageLoader.cancelLoad();
        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        start = firstVisibleItem;
        end = firstVisibleItem + visibleItemCount;
        LogUtil.e("lzz","this is onscroll");
        if(mFlag && visibleItemCount>0){
            mImageLoader.setIconbyAsyncTaskUsePosition(start,end);
            mFlag = false;
        }
    }


    class ViewHolder{
        TextView title,content;
        ImageView icon;
    }
}
