package com.sheepyang.smartbeijing.domain;

import java.util.ArrayList;

/**
 * 分类信息封装
 * 
 * 使用Gson解析时,对象书写技巧: 1. 逢{}创建对象, 逢[]创建集合(ArrayList) 2. 所有字段名称要和json返回字段高度一致
 * 
 * @author Kevin
 * @date 2015-10-18
 */
public class NewsMenu {

	public int retcode;
	public ArrayList<Integer> extend;
	public ArrayList<NewsMenuData> data;

	// 侧边栏菜单对象
	public class NewsMenuData {
		public int id;
		public String title;
		public int type;

		public ArrayList<NewsTabData> children;

		@Override
		public String toString() {
			return "NewsMenuData{" +
					"id=" + id +
					", title='" + title + '\'' +
					", type=" + type +
					", children=" + children +
					'}';
		}
	}

	// 页签的对象
	public class NewsTabData {
		public int id;
		public String title;
		public int type;
		public String url;

		@Override
		public String toString() {
			return "NewsTabData{" +
					"id=" + id +
					", title='" + title + '\'' +
					", type=" + type +
					", url='" + url + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "NewsMenu{" +
				"retcode=" + retcode +
				", extend=" + extend +
				", data=" + data +
				'}';
	}
}
