package com.sheepyang.smartbeijing.base.impl.news.menu;

import android.app.Activity;
import android.support.v7.widget.ViewUtils;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheepyang.smartbeijing.R;
import com.sheepyang.smartbeijing.base.BaseMenuDetailPager;
import com.sheepyang.smartbeijing.domain.PhotosBean;
import com.sheepyang.smartbeijing.global.GlobalConstants;
import com.sheepyang.smartbeijing.utils.CacheUtils;
import com.sheepyang.smartbeijing.utils.MyBitmapUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 菜单详情页-组图
 *
 * @author Kevin
 * @date 2015-10-18
 */
public class PhotosMenuDetailPager extends BaseMenuDetailPager implements View.OnClickListener {

    @ViewInject(R.id.lv_photo)
    private ListView lvPhoto;
    @ViewInject(R.id.gv_photo)
    private GridView gvPhoto;

    private ArrayList<PhotosBean.PhotoNews> mNewsList;

    private ImageButton btnPhoto;

    public PhotosMenuDetailPager(Activity activity, ImageButton btnPhoto) {
        super(activity);
        btnPhoto.setOnClickListener(this);// 组图切换按钮设置点击事件
        this.btnPhoto = btnPhoto;
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_photos_menu_detail, null);
        x.view().inject(this, view);
        return view;
    }



    @Override
    public void initData() {
        String cache = CacheUtils.getCache(GlobalConstants.PHOTOS_URL, mActivity);
        if (!TextUtils.isEmpty(cache)) {
            processData(cache);
        }

        getDataFromServer();
    }

    private void getDataFromServer() {
        RequestParams params = new RequestParams(GlobalConstants.PHOTOS_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                processData(result);
                CacheUtils.setCache(GlobalConstants.PHOTOS_URL, result, mActivity);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                // 请求失败
                ex.printStackTrace();
                Toast.makeText(mActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    protected void processData(String result) {
        Gson gson = new Gson();
        PhotosBean photosBean = gson.fromJson(result, PhotosBean.class);

        mNewsList = photosBean.data.news;

        lvPhoto.setAdapter(new PhotoAdapter());
        gvPhoto.setAdapter(new PhotoAdapter());// gridview的布局结构和listview完全一致,
        // 所以可以共用一个adapter
    }

    class PhotoAdapter extends BaseAdapter {

        private ImageOptions imageOptions;
        private MyBitmapUtils bitmapUtil;
        public PhotoAdapter() {
            bitmapUtil = new MyBitmapUtils();
            imageOptions = new ImageOptions.Builder()
//					.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
//					.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                    .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                    .setImageScaleType(ImageView.ScaleType.FIT_XY)//缩放
                    .setLoadingDrawableId(R.drawable.pic_item_list_default)//加载中默认显示图片
                    .setUseMemCache(true)//设置使用缓存
                    .setFailureDrawableId(R.drawable.pic_item_list_default)//加载失败后默认显示图片
                    .build();
        }

        @Override
        public int getCount() {
            return mNewsList.size();
        }

        @Override
        public PhotosBean.PhotoNews getItem(int position) {
            return mNewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(mActivity,
                        R.layout.list_item_photos, null);
                holder = new ViewHolder();
                holder.ivPic = (ImageView) convertView
                        .findViewById(R.id.iv_pic);
                holder.tvTitle = (TextView) convertView
                        .findViewById(R.id.tv_title);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            PhotosBean.PhotoNews item = getItem(position);

            holder.tvTitle.setText(item.title);

//            x.image().bind(holder.ivPic, item.listimage.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL), imageOptions);
            //自定义的BitmapUtil，三级缓存
            bitmapUtil.display(PhotosMenuDetailPager.this.mActivity, holder.ivPic, item.listimage.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL));
            return convertView;
        }

    }

    static class ViewHolder {
        public ImageView ivPic;
        public TextView tvTitle;
    }

    private boolean isListView = true;// 标记当前是否是listview展示

    @Override
    public void onClick(View v) {
        if (isListView) {
            // 切成gridview
            lvPhoto.setVisibility(View.GONE);
            gvPhoto.setVisibility(View.VISIBLE);
            btnPhoto.setImageResource(R.drawable.icon_pic_list_type);

            isListView = false;
        } else {
            // 切成listview
            lvPhoto.setVisibility(View.VISIBLE);
            gvPhoto.setVisibility(View.GONE);
            btnPhoto.setImageResource(R.drawable.icon_pic_grid_type);

            isListView = true;
        }
    }

}