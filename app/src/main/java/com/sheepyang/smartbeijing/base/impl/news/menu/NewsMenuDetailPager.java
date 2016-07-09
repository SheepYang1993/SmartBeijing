package com.sheepyang.smartbeijing.base.impl.news.menu;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.sheepyang.smartbeijing.R;
import com.sheepyang.smartbeijing.base.BaseMenuDetailPager;
import com.sheepyang.smartbeijing.base.impl.news.TabDetailPager;
import com.sheepyang.smartbeijing.domain.NewsMenu;
import com.sheepyang.smartbeijing.ui.MainActivity;
import com.viewpagerindicator.TabPageIndicator;

import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;


/**
 * 菜单详情页-新闻
 * @author SheeyYang
 * @date 2016-06-24
 */
public class NewsMenuDetailPager extends BaseMenuDetailPager implements ViewPager.OnPageChangeListener {

	@ViewInject(R.id.vp_news_menu_detail)
	private ViewPager mViewPager;
	@ViewInject(R.id.indicator)
	private TabPageIndicator mIndicator;

	private ArrayList<NewsMenu.NewsTabData> mTabData;// 页签网络数据
	private ArrayList<TabDetailPager> mPagers;// 页签页面集合

	public NewsMenuDetailPager(Activity activity, ArrayList<NewsMenu.NewsTabData> children) {
		super(activity);
		mTabData = children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_news_menu_detail, null);
		x.view().inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// 初始化页签
		mPagers = new ArrayList<TabDetailPager>();
		for (int i = 0; i < mTabData.size(); i++) {
			TabDetailPager pager = new TabDetailPager(mActivity, mTabData.get(i));
			mPagers.add(pager);
		}

		mViewPager.setAdapter(new NewsMenuDetailAdapter());
		mIndicator.setViewPager(mViewPager);// 将viewpager和指示器绑定在一起.注意:必须在viewpager设置完数据之后再绑定

		// 设置页面滑动监听
		// mViewPager.setOnPageChangeListener(this);
		mIndicator.setOnPageChangeListener(this);// 此处必须给指示器设置页面监听,不能设置给viewpager
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
							   int positionOffsetPixels) {

	}

	@Override
	public void onPageSelected(int position) {
		System.out.println("当前位置:" + position);
		if (position == 0) {
			// 开启侧边栏
			setSlidingMenuEnable(true);
		} else {
			// 禁用侧边栏
			setSlidingMenuEnable(false);
		}

	}

	@Override
	public void onPageScrollStateChanged(int state) {

	}

	/**
	 * 开启或禁用侧边栏
	 *
	 * @param enable
	 */
	protected void setSlidingMenuEnable(boolean enable) {
		// 获取侧边栏对象
		MainActivity mainUI = (MainActivity) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if (enable) {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Event(value = R.id.btn_next, type = View.OnClickListener.class)
	private void nextPage(View view) {
		// 跳到下个页面
		int currentItem = mViewPager.getCurrentItem();
		currentItem++;
		mViewPager.setCurrentItem(currentItem);
	}

	private class NewsMenuDetailAdapter extends PagerAdapter {

		// 指定指示器的标题
		@Override
		public CharSequence getPageTitle(int position) {
			NewsMenu.NewsTabData data = mTabData.get(position);
			return data.title;
		}

		@Override
		public int getCount() {
			return mPagers.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mPagers.get(position);

			View view = pager.mRootView;
			container.addView(view);

			pager.initData();
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
}
