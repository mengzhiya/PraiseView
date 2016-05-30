package com.example.praise.parent;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/***
 * @Description:
 * @csdnblog http://blog.csdn.net/mare_blue
 * @author mare
 * @date 2016年5月30日
 * @time 下午5:51:22
 */
public class PraiseView extends PopupWindow implements IGoodView {

	private String mText = TEXT;

	private int mTextColor = TEXT_COLOR;

	private int mTextSize = TEXT_SIZE;

	private int mFromY = FROM_Y_DELTA;

	private int mToY = TO_Y_DELTA;

	private float mFromAlpha = FROM_ALPHA;

	private float mToAlpha = TO_ALPHA;

	private int mDuration = DURATION;

	private int mDistance = DISTANCE;

	private AnimationSet mAnimationSet;

	private boolean mChanged = false;

	private Context mContext = null;

	private TextView mGood = null;

	public PraiseView(Context context) {
		super(context);
		mContext = context;
		initView();
	}

	private void initView() {
		RelativeLayout layout = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.CENTER_HORIZONTAL);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

		mGood = new TextView(mContext);
		mGood.setIncludeFontPadding(false);
		mGood.setTextSize(TypedValue.COMPLEX_UNIT_DIP, mTextSize);
		mGood.setTextColor(mTextColor);
		mGood.setText(mText);
		mGood.setLayoutParams(params);
		layout.addView(mGood);
		setContentView(layout);

		setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		setFocusable(false);
		setTouchable(false);
		setOutsideTouchable(false);

		mAnimationSet = createAnimation();
	}

	/**
	 * 设置文本
	 *
	 * @param text
	 */
	public void setText(String text) {
		if (TextUtils.isEmpty(text)) {
			throw new IllegalArgumentException("text cannot be null.");
		}
		mText = text;
		mGood.setText(text);
		mGood.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		int w = (int) mGood.getPaint().measureText(text);
		setWidth(w);
		setHeight(mDistance + getTextViewHeight(mGood, w));
	}

	private static int getTextViewHeight(TextView textView, int width) {
		int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.AT_MOST);
		int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		textView.measure(widthMeasureSpec, heightMeasureSpec);
		return textView.getMeasuredHeight();
	}

	/**
	 * 设置文本颜色
	 *
	 * @param color
	 */
	private void setTextColor(int color) {
		mTextColor = color;
		mGood.setTextColor(color);
	}

	/**
	 * 设置文本大小
	 *
	 * @param textSize
	 */
	private void setTextSize(int textSize) {
		mTextSize = textSize;
		mGood.setTextSize(TypedValue.COMPLEX_UNIT_DIP, textSize);
	}

	public void setSrc(String text, int textColor, int textSize, Drawable drawable) {
		if (null != drawable) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
				mGood.setBackground(drawable);
			} else {
				mGood.setBackgroundDrawable(drawable);
			}
			mGood.setText("");
			setWidth(drawable.getIntrinsicWidth());
			setHeight(mDistance + drawable.getIntrinsicHeight());
		} else {
			setTextColor(textColor);
			setTextSize(textSize);
			setText(text);
		}
	}

	/**
	 * 展示
	 *
	 * @param v
	 */
	public void show(View v) {
		if (!isShowing()) {
			int offsetY = -v.getHeight() - getHeight();
			showAsDropDown(v, v.getWidth() / 2 - getWidth() / 2, offsetY);
			if (mAnimationSet == null || mChanged) {
				mAnimationSet = createAnimation();
				mChanged = false;
			}
			mGood.startAnimation(mAnimationSet);
		} else {
			mAnimationSet.cancel();
		}
	}

	/**
	 * 动画
	 *
	 * @return
	 */
	private AnimationSet createAnimation() {
		mAnimationSet = new AnimationSet(true);
		TranslateAnimation translateAnim = new TranslateAnimation(0, 0, mFromY, -mToY);
		AlphaAnimation alphaAnim = new AlphaAnimation(mFromAlpha, mToAlpha);
		mAnimationSet.addAnimation(translateAnim);
		mAnimationSet.addAnimation(alphaAnim);
		mAnimationSet.setDuration(mDuration);
		mAnimationSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (isShowing()) {
					new Handler().post(new Runnable() {
						@Override
						public void run() {
							dismiss();
						}
					});
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}
		});
		return mAnimationSet;
	}
}
