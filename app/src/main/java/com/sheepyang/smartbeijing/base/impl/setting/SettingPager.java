package com.sheepyang.smartbeijing.base.impl.setting;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.sheepyang.smartbeijing.base.BasePager;


/**
 * 设置
 *
 * @author SheeyYang
 * @date 2016-06-24
 */
public class SettingPager extends BasePager {

	public SettingPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		// 要给帧布局填充布局对象
		TextView view = new TextView(mActivity);
		view.setText("设置");
		view.setTextColor(Color.RED);
		view.setTextSize(22);
		view.setGravity(Gravity.CENTER);

		flContent.addView(view);

		// 修改页面标题
		tvTitle.setText("设置");

		// 隐藏菜单按钮
		btnMenu.setVisibility(View.GONE);
	}

}
