package com.sheepyang.smartbeijing.domain;

import java.util.ArrayList;

/**
 * 页签详情数据对象
 * 
 * @author SheepYang
 * @date 2016-06-25
 */
public class NewsTabBean {

	public NewsTab data;

	public class NewsTab {
		public String more;
		public ArrayList<NewsData> news;
		public ArrayList<TopNews> topnews;

		@Override
		public String toString() {
			return "NewsTab{" +
					"more='" + more + '\'' +
					", news=" + news +
					", topnews=" + topnews +
					'}';
		}
	}

	// 新闻列表对象
	public class NewsData {
		public int id;
		public String listimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;

		@Override
		public String toString() {
			return "NewsData{" +
					"id=" + id +
					", listimage='" + listimage + '\'' +
					", pubdate='" + pubdate + '\'' +
					", title='" + title + '\'' +
					", type='" + type + '\'' +
					", url='" + url + '\'' +
					'}';
		}
	}

	// 头条新闻
	public class TopNews {
		public int id;
		public String topimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;

		@Override
		public String toString() {
			return "TopNews{" +
					"id=" + id +
					", topimage='" + topimage + '\'' +
					", pubdate='" + pubdate + '\'' +
					", title='" + title + '\'' +
					", type='" + type + '\'' +
					", url='" + url + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "NewsTabBean{" +
				"data=" + data +
				'}';
	}
}
