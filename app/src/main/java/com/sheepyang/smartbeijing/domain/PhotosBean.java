package com.sheepyang.smartbeijing.domain;

import java.util.ArrayList;
/**
 * 组图对象
 * @author Kevin
 * @date 2015-10-22
 */
public class PhotosBean {

	public PhotosData data;

	public class PhotosData {
		public ArrayList<PhotoNews> news;

		@Override
		public String toString() {
			return "PhotosData{" +
					"news=" + news +
					'}';
		}
	}

	public class PhotoNews {
		public int id;
		public String listimage;
		public String title;

		@Override
		public String toString() {
			return "PhotoNews{" +
					"id=" + id +
					", listimage='" + listimage + '\'' +
					", title='" + title + '\'' +
					'}';
		}
	}

	@Override
	public String toString() {
		return "PhotosBean{" +
				"data=" + data +
				'}';
	}
}
