package com.debug.xxw.pointbook.model.viewmodel;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.debug.xxw.pointbook.utils.ElasticOutInterpolator;

/**
 * @author xxw
 */
public class SettingView extends ViewGroup {
    private Context mContext;
    private ListView mListView;
    private LinearLayout mHeaderView;
    private GridView mGridView;
    private FloatingActionButton mFab;
    private float mDensity = 1;
    private boolean closeOverlay;

    public SettingView(Context context) {
        this(context, null);
    }

    public SettingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        mDensity = context.getResources().getDisplayMetrics().density;
        init(context);

    }

    public boolean isCloseOverlay() {
        return closeOverlay;
    }

    public SettingView setCloseOverlay(boolean closeOverlay) {
        this.closeOverlay = closeOverlay;
        return this;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View v = getChildAt(i);
            if (v instanceof FloatingActionButton) {
                mFab = (FloatingActionButton) v;
                mFab.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ValueAnimator anim = ObjectAnimator.ofFloat(v, "translationY", -mFab.getHeight() - ((MarginLayoutParams) mFab.getLayoutParams()).bottomMargin, 0);
                        anim.setDuration(200);
                        anim.setInterpolator(new DecelerateInterpolator());
                        anim.addListener(new ButtonListener());
                        anim.start();
                    }
                });
            }

        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (null != mHeaderView) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
        }
        if (null != mListView) {
            measureChild(mListView, widthMeasureSpec, heightMeasureSpec);
        }
        if (null != mFab) {
            measureChild(mFab, widthMeasureSpec, heightMeasureSpec);
        }

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        if (null != mHeaderView) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int bottom = paddingTop + lp.topMargin;
            final int top = bottom - mHeaderView.getMeasuredHeight();
            final int right = left + mHeaderView.getMeasuredWidth();

            mHeaderView.layout(left, top, right, bottom);
        }

        if (null != mListView) {
            MarginLayoutParams lp = (MarginLayoutParams) mListView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = getMeasuredHeight();
            final int right = left + mListView.getMeasuredWidth();
            final int bottom = top + mListView.getMeasuredHeight();
            mListView.layout(left, top, right, bottom);
        }

        if (null != mFab) {
            MarginLayoutParams lp = (MarginLayoutParams) mFab.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = getMeasuredHeight();
            final int right = left + mFab.getMeasuredWidth();
            final int bottom = top + mFab.getMeasuredHeight();
            mFab.layout(left, top, right, bottom);
        }
        startAnimation();
    }

    public void init(Context context) {
        initHeaderView(context);
        initListView(context);
    }

    public int calPxFromDp(int px) {
        return (int) (px * mDensity);
    }

    public void initHeaderView(Context context) {
        mHeaderView = new LinearLayout(context);
        mHeaderView.setOrientation(LinearLayout.VERTICAL);
        mHeaderView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mHeaderView.setPadding(0, calPxFromDp(10), 0, calPxFromDp(20));

        mGridView = new GridView(context);
        mGridView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mGridView.setNumColumns(1);
        mGridView.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        mHeaderView.addView(mGridView);
        addView(mHeaderView);
    }

    public void setUserAdapter(BaseAdapter adapter) {
        if (null != mGridView) {
            mGridView.setAdapter(adapter);
        }
    }

    public void initListView(Context context) {
        mListView = new ListView(context);
        LinearLayout.LayoutParams l = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mListView.setLayoutParams(l);
        addView(mListView);
    }

    public void setListAdapter(BaseAdapter adapter) {
        if (null != mListView) {
            mListView.setAdapter(adapter);
//            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    Toast.makeText(mContext, "点击了元素", Toast.LENGTH_SHORT).show();
//                }
//            });
        }
    }

    public void startAnimation() {
        int animTime = 800;
        if (null != mHeaderView) {
            ValueAnimator headerAnim = ObjectAnimator.ofFloat(mHeaderView, "translationY", 0, mHeaderView.getHeight());
            headerAnim.setDuration(animTime);
            headerAnim.setInterpolator(new ElasticOutInterpolator());
            headerAnim.start();
        }
        if (null != mListView) {
            assert mHeaderView != null;
            ValueAnimator listAnim = ObjectAnimator.ofFloat(mListView, "translationY", 0, mHeaderView.getHeight() - getHeight());
            listAnim.setDuration(animTime);
            listAnim.setInterpolator(new ElasticOutInterpolator());
            listAnim.start();
        }

        if (null != mFab) {
            ValueAnimator fabAnim = ObjectAnimator.ofFloat(mFab, "translationY", 0, -mFab.getHeight() - ((MarginLayoutParams) mFab.getLayoutParams()).bottomMargin);
            fabAnim.setDuration(animTime);
            fabAnim.setInterpolator(new ElasticOutInterpolator());
            fabAnim.start();
        }
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    public static class LayoutParams extends MarginLayoutParams {

        LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        LayoutParams(int width, int height) {
            super(width, height);
        }

        @SuppressWarnings({"unused"})
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    public class ButtonListener implements Animator.AnimatorListener {

        @Override
        public void onAnimationStart(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            Activity settingActivity = (Activity) mContext;
            Intent intent = settingActivity.getIntent();
            Bundle data = new Bundle();
            data.putBoolean("closeOverlay", closeOverlay);
            intent.putExtras(data);
            // 设置SecondActivity的结果码(resultCode)，并设置在当前结束后退回去的Activity
            settingActivity.setResult(100, intent);
            settingActivity.finish();
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    }

}
