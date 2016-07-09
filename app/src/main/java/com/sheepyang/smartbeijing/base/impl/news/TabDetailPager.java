package com.sheepyang.smartbeijing.base.impl.news;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.sheepyang.smartbeijing.R;
import com.sheepyang.smartbeijing.base.BaseMenuDetailPager;
import com.sheepyang.smartbeijing.domain.NewsMenu;
import com.sheepyang.smartbeijing.domain.NewsTabBean;
import com.sheepyang.smartbeijing.global.GlobalConstants;
import com.sheepyang.smartbeijing.ui.NewsDetailActivity;
import com.sheepyang.smartbeijing.utils.CacheUtils;
import com.sheepyang.smartbeijing.utils.SPUtils;
import com.sheepyang.smartbeijing.view.PullToRefreshListView;
import com.sheepyang.smartbeijing.view.TopNewsViewPager;
import com.viewpagerindicator.CirclePageIndicator;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * 页签页面对象
 *
 * @author SheepYang
 * @date 2016-06-25
 */
public class TabDetailPager extends BaseMenuDetailPager {

	private NewsMenu.NewsTabData mTabData;// 单个页签的网络数据
	// private TextView view;

	@ViewInject(R.id.vp_top_news)
	private TopNewsViewPager mViewPager;

	@ViewInject(R.id.indicator)
	private CirclePageIndicator mIndicator;

	@ViewInject(R.id.tv_title)
	private TextView tvTitle;

	@ViewInject(R.id.lv_list)
	private PullToRefreshListView lvList;

	private String mUrl;
	private Handler mHandler;

	private ArrayList<NewsTabBean.TopNews> mTopNews;
	private ArrayList<NewsTabBean.NewsData> mNewsList;

	private String mMoreUrl;// 下一页数据链接
	private NewsAdapter mNewsAdapter;

	public TabDetailPager(Activity activity, NewsMenu.NewsTabData newsTabData) {
		super(activity);
		mTabData = newsTabData;

		mUrl = GlobalConstants.SERVER_URL + mTabData.url;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.pager_tab_detail, null);
		x.view().inject(this, view);

		// 给listview添加头布局
		View mHeaderView = View.inflate(mActivity, R.layout.list_item_header,
				null);
		x.view().inject(this, mHeaderView);// 此处必须将头布局也注入
		lvList.addHeaderView(mHeaderView);
		lvList.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {

			@Override
			public void onRefresh() {
				// 刷新数据
				getDataFromServer();
			}

			@Override
			public void onLoadMore() {
				// 判断是否有下一页数据
				if (mMoreUrl != null) {
					// 有下一页
					getMoreDataFromServer();
				} else {
					// 没有下一页
					Toast.makeText(mActivity, "没有更多数据了", Toast.LENGTH_SHORT)
							.show();
					// 没有数据时也要收起控件
					lvList.onRefreshComplete(true);
				}
			}
		});
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				int headerViewsCount = lvList.getHeaderViewsCount();// 获取头布局数量
				position = position - headerViewsCount;// 需要减去头布局的占位
				System.out.println("第" + position + "个被点击了");

				NewsTabBean.NewsData news = mNewsList.get(position);

				// read_ids: 1101,1102,1105,1203,
				String readIds = SPUtils.getString(mActivity, "read_ids", "");

				if (!readIds.contains(news.id + "")) {// 只有不包含当前id,才追加,
					// 避免重复添加同一个id
					readIds = readIds + news.id + ",";// 1101,1102,
					SPUtils.setString(mActivity, "read_ids", readIds);
				}

				// 要将被点击的item的文字颜色改为灰色, 局部刷新, view对象就是当前被点击的对象
				TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
				tvTitle.setTextColor(Color.GRAY);
				// mNewsAdapter.notifyDataSetChanged();//全局刷新, 浪费性能

				// 跳到新闻详情页面
				Intent intent = new Intent(mActivity, NewsDetailActivity.class);
				intent.putExtra("url", news.url.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL));
				mActivity.startActivity(intent);
			}
		});
		return view;
	}

	/**
	 * 加载下一页数据
	 */
	protected void getMoreDataFromServer() {
		RequestParams params = new RequestParams(mMoreUrl);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				processData(result, true);
				// 收起下拉刷新控件
				lvList.onRefreshComplete(true);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// 请求失败
				ex.printStackTrace();
				Toast.makeText(mActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();

				// 收起下拉刷新控件
				lvList.onRefreshComplete(false);
			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

	@Override
	public void initData() {
		String cache = CacheUtils.getCache(mUrl, mActivity);
		if (!TextUtils.isEmpty(cache)) {
			processData(cache, false);
		}

		getDataFromServer();
	}

	private void getDataFromServer() {
		RequestParams params = new RequestParams(mUrl);
		x.http().get(params, new Callback.CommonCallback<String>() {
			@Override
			public void onSuccess(String result) {
				// 请求成功
				System.out.println("服务器返回结果:" + result);
				processData(result, false);
				CacheUtils.setCache(mUrl, result, mActivity);
				lvList.onRefreshComplete(true);
			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				// 请求失败
				ex.printStackTrace();
				Toast.makeText(mActivity, ex.getMessage(), Toast.LENGTH_SHORT).show();
				lvList.onRefreshComplete(false);
			}

			@Override
			public void onCancelled(CancelledException cex) {

			}

			@Override
			public void onFinished() {

			}
		});
	}

	protected void processData(String result, boolean isMore) {
		Gson gson = new Gson();
		NewsTabBean newsTabBean = gson.fromJson(result, NewsTabBean.class);

		String moreUrl = newsTabBean.data.more;
		if (!TextUtils.isEmpty(moreUrl)) {
			mMoreUrl = GlobalConstants.SERVER_URL + moreUrl;
		} else {
			mMoreUrl = null;
		}

		if (!isMore) {
			// 头条新闻填充数据
			mTopNews = newsTabBean.data.topnews;
			if (mTopNews != null) {
				mViewPager.setAdapter(new TopNewsAdapter());
				mIndicator.setViewPager(mViewPager);
				mIndicator.setSnap(true);// 快照方式展示

				// 事件要设置给Indicator
				mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

					@Override
					public void onPageSelected(int position) {
						// 更新头条新闻标题
						NewsTabBean.TopNews topNews = mTopNews.get(position);
						tvTitle.setText(topNews.title);
					}

					@Override
					public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

					}

					@Override
					public void onPageScrollStateChanged(int state) {

					}
				});

				// 更新第一个头条新闻标题
				tvTitle.setText(mTopNews.get(0).title);
				mIndicator.onPageSelected(0);// 默认让第一个选中(解决页面销毁后重新初始化时,Indicator仍然保留上次圆点位置的bug)
			}

			// 列表新闻
			mNewsList = newsTabBean.data.news;
			if (mNewsList != null) {
				mNewsAdapter = new NewsAdapter();
				lvList.setAdapter(mNewsAdapter);
			}

			if (mHandler == null) {
				mHandler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						int currentItem = mViewPager.getCurrentItem();
						currentItem++;

						if (currentItem > mTopNews.size() - 1) {
							currentItem = 0;// 如果已经到了最后一个页面,跳到第一页
						}

						mViewPager.setCurrentItem(currentItem);

						mHandler.sendEmptyMessageDelayed(0, 3000);// 继续发送延时3秒的消息,形成内循环
					};
				};

				// 保证启动自动轮播逻辑只执行一次
				mHandler.sendEmptyMessageDelayed(0, 3000);// 发送延时3秒的消息

				mViewPager.setOnTouchListener(new View.OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								System.out.println("ACTION_DOWN");
								// 停止广告自动轮播
								// 删除handler的所有消息
								mHandler.removeCallbacksAndMessages(null);
								// mHandler.post(new Runnable() {
								//
								// @Override
								// public void run() {
								// //在主线程运行
								// }
								// });
								break;
							case MotionEvent.ACTION_CANCEL:// 取消事件,
								// 当按下viewpager后,直接滑动listview,导致抬起事件无法响应,但会走此事件
								System.out.println("ACTION_CANCEL");
								// 启动广告
								mHandler.sendEmptyMessageDelayed(0, 3000);
								break;
							case MotionEvent.ACTION_UP:
								System.out.println("ACTION_UP");
								// 启动广告
								mHandler.sendEmptyMessageDelayed(0, 3000);
								break;

							default:
								break;
						}
						return false;
					}
				});
			}
		} else {
			// 加载更多数据
			ArrayList<NewsTabBean.NewsData> moreNews = newsTabBean.data.news;
			mNewsList.addAll(moreNews);// 将数据追加在原来的集合中
			// 刷新listview
			mNewsAdapter.notifyDataSetChanged();
		}
	}

	// 头条新闻数据适配器
	class TopNewsAdapter extends PagerAdapter {

		private ImageOptions imageOptions;

		public TopNewsAdapter() {
			imageOptions = new ImageOptions.Builder()
//					.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
//					.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
					.setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
					.setImageScaleType(ImageView.ScaleType.FIT_XY)//缩放
					.setLoadingDrawableId(R.drawable.topnews_item_default)//加载中默认显示图片
					.setUseMemCache(true)//设置使用缓存
					.setFailureDrawableId(R.drawable.topnews_item_default)//加载失败后默认显示图片
					.build();
		}

		@Override
		public int getCount() {
			return mTopNews.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ImageView view = new ImageView(mActivity);
			// view.setImageResource(R.drawable.topnews_item_default);
//			view.setScaleType(ImageView.ScaleType.FIT_XY);// 设置图片缩放方式, 宽高填充父控件

			String imageUrl = mTopNews.get(position).topimage;// 图片下载链接
			// 下载图片-将图片设置给imageview-避免内存溢出-缓存
			x.image().bind(view, imageUrl.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL), imageOptions);

			container.addView(view);

			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}

	class NewsAdapter extends BaseAdapter {

		private ImageOptions imageOptions;

		public NewsAdapter() {
			imageOptions = new ImageOptions.Builder()
//					.setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))//图片大小
//					.setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
					.setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
					.setImageScaleType(ImageView.ScaleType.FIT_XY)//缩放
					.setLoadingDrawableId(R.drawable.news_pic_default)//加载中默认显示图片
					.setUseMemCache(true)//设置使用缓存
					.setFailureDrawableId(R.drawable.news_pic_default)//加载失败后默认显示图片
					.build();
		}

		@Override
		public int getCount() {
			return mNewsList.size();
		}

		@Override
		public NewsTabBean.NewsData getItem(int position) {
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
				convertView = View.inflate(mActivity, R.layout.list_item_news,
						null);
				holder = new ViewHolder();
				holder.ivIcon = (ImageView) convertView
						.findViewById(R.id.iv_icon);
				holder.tvTitle = (TextView) convertView
						.findViewById(R.id.tv_title);
				holder.tvDate = (TextView) convertView
						.findViewById(R.id.tv_date);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			NewsTabBean.NewsData news = getItem(position);
			holder.tvTitle.setText(news.title);
			holder.tvDate.setText(news.pubdate);
			// 根据本地记录来标记已读未读
			String readIds = SPUtils.getString(mActivity, "read_ids", "");
			if (readIds.contains(news.id + "")) {
				holder.tvTitle.setTextColor(Color.GRAY);
			} else {
				holder.tvTitle.setTextColor(Color.BLACK);
			}
			x.image().bind(holder.ivIcon, news.listimage.replace("http://10.0.2.2:8080/zhbj", GlobalConstants.SERVER_URL), imageOptions);

			return convertView;
		}

	}

	static class ViewHolder {
		public ImageView ivIcon;
		public TextView tvTitle;
		public TextView tvDate;
	}

}
