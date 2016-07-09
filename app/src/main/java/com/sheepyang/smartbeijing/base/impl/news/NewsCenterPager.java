package com.sheepyang.smartbeijing.base.impl.news;

import java.util.ArrayList;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheepyang.smartbeijing.base.BaseMenuDetailPager;
import com.sheepyang.smartbeijing.base.BasePager;
import com.sheepyang.smartbeijing.base.impl.news.menu.InteractMenuDetailPager;
import com.sheepyang.smartbeijing.base.impl.news.menu.NewsMenuDetailPager;
import com.sheepyang.smartbeijing.base.impl.news.menu.PhotosMenuDetailPager;
import com.sheepyang.smartbeijing.base.impl.news.menu.TopicMenuDetailPager;
import com.sheepyang.smartbeijing.domain.NewsMenu;
import com.sheepyang.smartbeijing.global.GlobalConstants;
import com.sheepyang.smartbeijing.ui.MainActivity;
import com.sheepyang.smartbeijing.ui.fragment.LeftMenuFragment;
import com.sheepyang.smartbeijing.utils.CacheUtils;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 * 新闻中心
 *
 * @author Kevin
 * @date 2015-10-18
 */
public class NewsCenterPager extends BasePager {

	private ArrayList<BaseMenuDetailPager> mMenuDetailPagers;// 菜单详情页集合
	private NewsMenu mNewsData;// 分类信息网络数据

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {

		// 修改页面标题
		tvTitle.setText("新闻");

		// 显示菜单按钮
		btnMenu.setVisibility(View.VISIBLE);

		// 先判断有没有缓存,如果有的话,就加载缓存
		String cache = CacheUtils.getCache(GlobalConstants.CATEGORY_URL,
				mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processData(cache);
		}

		// 请求服务器,获取数据
		// 开源框架: XUtils
		getDataFromServer();
	}

	/**
	 * 从服务器获取数据
	 */
	private void getDataFromServer(){
		RequestParams params = new RequestParams(GlobalConstants.CATEGORY_URL);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				// 请求成功
				System.out.println("服务器返回结果:" + result);
				// 解析数据
				processData(result);
				// 写缓存
				CacheUtils.setCache(GlobalConstants.CATEGORY_URL, result, mActivity);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// 请求失败
				ex.printStackTrace();
				Toast.makeText(mActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(mActivity, "cancelled", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinished() {

			}
		});
	}

	/**
	 * 解析数据
	 */
	protected void processData(String json) {
		// Gson: Google Json
		Gson gson = new Gson();
		mNewsData = gson.fromJson(json, NewsMenu.class);
		System.out.println("解析结果:" + mNewsData);

		// 获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		LeftMenuFragment fragment = mainUI.getLeftMenuFragment();

		// 给侧边栏设置数据
		fragment.setMenuData(mNewsData.data);

		// 初始化4个菜单详情页
		mMenuDetailPagers = new ArrayList<BaseMenuDetailPager>();
		mMenuDetailPagers.add(new NewsMenuDetailPager(mActivity, mNewsData.data.get(0).children));
		mMenuDetailPagers.add(new TopicMenuDetailPager(mActivity));
		mMenuDetailPagers.add(new PhotosMenuDetailPager(mActivity, btnPhoto));
		mMenuDetailPagers.add(new InteractMenuDetailPager(mActivity));

		// 将新闻菜单详情页设置为默认页面
		setCurrentDetailPager(0);
	}

	// 设置菜单详情页
	public void setCurrentDetailPager(int position) {
		// 重新给frameLayout添加内容
		BaseMenuDetailPager pager = mMenuDetailPagers.get(position);// 获取当前应该显示的页面
		View view = pager.mRootView;// 当前页面的布局

		// 清除之前旧的布局
		flContent.removeAllViews();

		flContent.addView(view);// 给帧布局添加布局

		// 初始化页面数据
		pager.initData();

		// 更新标题
		tvTitle.setText(mNewsData.data.get(position).title);

		// 如果是组图页面, 需要显示切换按钮
		if (pager instanceof PhotosMenuDetailPager) {
			btnPhoto.setVisibility(View.VISIBLE);
		} else {
			// 隐藏切换按钮
			btnPhoto.setVisibility(View.GONE);
		}
	}

}
