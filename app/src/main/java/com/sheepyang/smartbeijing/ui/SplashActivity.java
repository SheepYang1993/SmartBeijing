package com.sheepyang.smartbeijing.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.sheepyang.smartbeijing.R;
import com.sheepyang.smartbeijing.utils.SPUtils;

public class SplashActivity extends BaseActivity {

    private RelativeLayout rl_root;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initView();
    }

    private void initView() {
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        RotateAnimation animRotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animRotate.setDuration(1000);
        animRotate.setFillAfter(true);

        ScaleAnimation animScale = new ScaleAnimation(0,1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animScale.setDuration(1000);
        animScale.setFillAfter(true);

        AlphaAnimation animAlpha = new AlphaAnimation(0, 1);
        animAlpha.setDuration(2000);
        animAlpha.setFillAfter(true);

        AnimationSet animSet = new AnimationSet(true);
        animSet.addAnimation(animRotate);
        animSet.addAnimation(animScale);
        animSet.addAnimation(animAlpha);
        rl_root.setAnimation(animSet);

        animSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                boolean isFirstEnter = SPUtils.getBoolean(SplashActivity.this, "is_first_enter", true);
                Intent intent;
                if (isFirstEnter) {
                    //新手引导页
                    intent = new Intent(getApplicationContext(), GuideActivity.class);
                } else {
                    //主页面
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
