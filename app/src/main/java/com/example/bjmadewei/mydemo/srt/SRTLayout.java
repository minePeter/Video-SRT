package com.example.bjmadewei.mydemo.srt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by bjmadewei on 2016/4/29.
 */
public class SRTLayout extends RelativeLayout {

    public SRTListView mListView;
    Context mContext;
    RelativeLayout mLineView;
    SRTCallBack mSRTCallBack;
    int mLineHight = 2;
    int mLineWidth = LayoutParams.MATCH_PARENT;
    OnDispatchTouchEventListener mOnDispatchTouchEventListener;

    List<CallBack> mCallBackList = new ArrayList<>(4);

    public SRTLayout(Context context) {
        this(context, null, 0, 0);
    }

    public SRTLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public SRTLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SRTLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        mListView = new SRTListView(context);
        AbsListView.LayoutParams lap = new AbsListView.LayoutParams(-1, -1);
        mListView.setLayoutParams(lap);
        mListView.setBackgroundColor(Color.alpha(0));
        mListView.setSelector(new Drawable() {
            @Override
            public void draw(Canvas canvas) {}
            @Override
            public void setAlpha(int i) {}
            @Override
            public void setColorFilter(ColorFilter colorFilter) {}
            @Override
            public int getOpacity() {
                return 0;
            }
        });
        mSRTCallBack = new SRTCallBack();
        mListView.registerCallback(mSRTCallBack);
        addView(mListView);

        mLineView = new RelativeLayout(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mLineWidth, DensityUtil.dip2px(mContext, mLineHight));
        mLineView.setLayoutParams(lp);
        mLineView.setBackgroundColor(Color.argb(20, 255, 255, 255));
        addView(mLineView);
    }

    class SRTCallBack implements CallBack {
        @Override
        public void onListViewCrated() {
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mLineView.getLayoutParams();
            lp.topMargin = mListView.getLineTopMargin() - mLineView.getHeight() / 2;
            lp.leftMargin = SRTListView.mItemHeight;
            lp.rightMargin = SRTListView.mItemHeight;
            mLineView.setLayoutParams(lp);
            for(CallBack callBack : mCallBackList) {
                callBack.onListViewCrated();
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(mOnDispatchTouchEventListener != null) {
            mOnDispatchTouchEventListener.OnTouchEventDispatching(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public interface OnDispatchTouchEventListener {
        void OnTouchEventDispatching(MotionEvent ev);
    }

    public void setOnDispatchTouchEventListener(OnDispatchTouchEventListener l) {
        mOnDispatchTouchEventListener = l;
    }

    public void destroyView() {
        mListView.unRegisterCallback();
    }

    public void setListAdapter(SRTArrayAdapter adapter) {
        mListView.setAdapter(adapter);
    }

    public SRTArrayAdapter getListAdapter() {
        return mListView.getListAdapter();
    }

    public SRTListView getListView() {
        return mListView;
    }

    public RelativeLayout getLineView() {
        return mLineView;
    }

    public void setSRTTextColor(int r, int g, int b) {
        SRTColor.setTextColor(r, g, b);
    }

    public void setSRTMargin(int margin) {
        getListAdapter().mMargin = margin;
    }

    public void setDifAlpha(int dif) {
        getListView().mDifAlpha = dif;
    }

    public void setLineHight(int hight) {
        mLineHight = hight;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mLineView.getLayoutParams();
        lp.height = mLineHight;
        mLineView.setLayoutParams(lp);
    }

    public void setLineWidth(int width) {
        mLineWidth = width;
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)mLineView.getLayoutParams();
        lp.width = mLineWidth;
        mLineView.setLayoutParams(lp);
    }

    public interface CallBack {
        void onListViewCrated();
    }

    public void registerCallback(CallBack callBack) {
        mCallBackList.add(callBack);
    }
    public void unRegisterCallback(CallBack callBack) {
        mCallBackList.remove(callBack);
    }

    public SRTListView.SRTController getSRTController() {
        return mListView.mSRTController;
    }

    public SRTListView.SRTAutoController getSRTAutoController() {
        return mListView.mSRTAutoController;
    }

    public boolean changeLanguage (ArrayList data) {
        if(data!=null && data.size() > 0) {
            getListAdapter().changeData(data);
            getListAdapter().notifyDataSetChanged();
            return true;
        }
        return false;
    }

    public void autoScrollEnable(boolean isEnable) {
        SRTListView.autoScrollEnable = isEnable;
    }

    static class SRTColor {
        int red;
        int green;
        int blue;

        private SRTColor(int r, int g, int b) {
            red = r;
            green = g;
            blue = b;
        }

        private static SRTColor insatnce = null;

        static SRTColor getTextColor() {
            if(insatnce == null)
                insatnce = new SRTColor(255, 255, 255);
            return insatnce;
        }

        static void setTextColor(int r, int g, int b) {
            insatnce = new SRTColor(r, g, b);
        }
    }
}
