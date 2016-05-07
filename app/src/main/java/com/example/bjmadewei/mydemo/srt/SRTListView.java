package com.example.bjmadewei.mydemo.srt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import java.lang.reflect.Method;

/**
 * Created by bjmadewei on 2016/4/28.
 */
public class SRTListView extends ListView {

    static SRTArrayAdapter mAdapter;
    static int lineCount;

    static int mItemHeight;
    static int mFirstVisibleItem;
    static boolean autoScrollEnable = true;

    static int mFocusItem = 0;

    final static int UPDATE = 0;
    final static int DELAY_UPDATE = 1;
    final static int AUTO_UPDATE = 2;

    public SRTController mSRTController = new SRTController();
    public SRTAutoController mSRTAutoController = new SRTAutoController();

    SRTLayout.CallBack mCallBack;
    int mTopMargin;
    int mDifAlpha = 40;

    public SRTListView(Context context) {
        this(context, null, 0, 0);
    }

    public SRTListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);

    }

    public SRTListView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SRTListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
        setDividerHeight(0);
        setVerticalScrollBarEnabled(true);
        setOnScrollListener(new OnSRTScrollListener());
    }

    public class OnSRTScrollListener implements OnScrollListener {
        boolean Inced = false;

        @Override
        public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                autoScrollEnable = true;
                int top = getChildAt(0).getTop();
                if (top != 0) {
                    setSelection(mFirstVisibleItem + 1);
                }
            } else if (scrollState == OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                autoScrollEnable = false;
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            if (!autoScrollEnable && visibleItemCount > 0) {
                dealItemStyle(true);
            }
            mFirstVisibleItem = firstVisibleItem;
            if (visibleItemCount > 0 && !Inced) {
                View item = mAdapter.getView(0, null, SRTListView.this);
                item.measure(0, 0);
                mItemHeight = item.getMeasuredHeight();
                lineCount = mAdapter.getCount();
                Inced = true;
                mFocusItem = 3;
                mAdapter.AddEmptyItemTear(visibleItemCount - mAdapter.AddEmptyItemHead(mFocusItem) + 1);
                mAdapter.notifyDataSetChanged();
                mTopMargin = SRTListView.this.getChildAt(mFocusItem - 1).getTop() + mItemHeight / 2;
                mCallBack.onListViewCrated();
            }
        }
    }

    void setAdapter(SRTArrayAdapter adapter) {
        super.setAdapter(adapter);
        mAdapter = adapter;
    }

    SRTArrayAdapter getListAdapter() {
        return mAdapter;
    }

    int getLineTopMargin() {
        return mTopMargin;
    }

    void registerCallback(SRTLayout.CallBack callBack) {
        mCallBack = callBack;
    }

    void unRegisterCallback() {
        mCallBack = null;
    }

    private void dealItemStyle(boolean focus) {

        int count = 0;
        int color = Color.rgb(101, 101, 101);
        for (count = 2;count < getLastVisiblePosition() - 1; count++) {
            View view = getChildAt(count);
            if (view != null) {
                ViewHolder holder = (ViewHolder) view.getTag();
                holder.srtTv.setTextColor(color);
                holder.timeTv.setTextColor(Color.alpha(0));
            }
        }
        color = Color.argb(101, 101, 101, 101);
        View view = getChildAt(0);
        if (view != null) {
            ViewHolder holder = (ViewHolder) view.getTag();
            holder.srtTv.setTextColor(color);
            holder.timeTv.setTextColor(Color.alpha(0));
        }
        if (focus) {
            color = Color.rgb(SRTLayout.SRTColor.getTextColor().red, SRTLayout.SRTColor.getTextColor().green, SRTLayout.SRTColor.getTextColor().blue);
            ViewHolder holder = (ViewHolder) getChildAt(mFocusItem).getTag();
            holder.srtTv.setTextColor(color);
            holder.timeTv.setTextColor(color);
            color = Color.argb(101, 101, 101, 101);
            view = getChildAt(1);
            if (view != null) {
                holder = (ViewHolder) view.getTag();
                holder.srtTv.setTextColor(color);
                holder.timeTv.setTextColor(Color.alpha(0));
            }
        }
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    if (autoScrollEnable) {
                        dealItemStyle(false);
                        mHandler.removeMessages(DELAY_UPDATE);
                        mHandler.sendEmptyMessageDelayed(DELAY_UPDATE, 300);
                        SRTListView.this.smoothScrollBy(mItemHeight, 1000);
                    }
                    break;
                case DELAY_UPDATE:
                    dealItemStyle(true);
                    break;
                case AUTO_UPDATE:
                    if (autoScrollEnable) {
                        dealItemStyle(false);
                        mHandler.removeMessages(DELAY_UPDATE);
                        mHandler.sendEmptyMessageDelayed(DELAY_UPDATE, 300);
                        SRTListView.this.smoothScrollBy(mItemHeight, 1000);
                    }
                    mSRTAutoController.update();
                    break;
            }

        }
    };

    SRTController getSRTController() {
        return mSRTController;
    }

    public class SRTController {
        /**
         * 点击分享字幕时初始化滚动字幕
         *
         * @param currentSRTPos 当前字幕在TreeMap中对应的Key
         */
        public boolean init(int currentSRTPos) {
            refresh(currentSRTPos);
            return true;
        }

        /**
         * Seek(快进或回看)结束后更新滚动字幕
         *
         * @param currentSRTPos 当前字幕在TreeMap中对应的Key
         */
        public void refresh(int currentSRTPos) {
            mHandler.removeMessages(UPDATE);
            setSelection(currentSRTPos);
        }

        public void update() {
            mHandler.removeMessages(UPDATE);
            mHandler.sendEmptyMessage(UPDATE);
        }

        /**
         * 拖动字幕结束后重置滚动字幕
         *
         * @param currentSRTPos 当前字幕在TreeMap中对应的Key
         */
        public void reset(int currentSRTPos) {
            setSelection(currentSRTPos);
        }

    }

    SRTAutoController getSRTAutoController() {
        return mSRTAutoController;
    }

    public class SRTAutoController {

        int mDelay = 0;
        int mCurrentSRTPos;

        /**
         * 点击分享字幕时初始化滚动字幕
         *
         * @param currentSRTPos 当前字幕在TreeMap中对应的Key
         * @param playedTime    当前字幕所在画面已经开始播放时间
         */
        public boolean init(int currentSRTPos, int playedTime) {
            refresh(currentSRTPos, playedTime);
            return true;
        }

        /**
         * Seek(快进或回看)结束后更新滚动字幕
         *
         * @param currentSRTPos 当前字幕在TreeMap中对应的Key
         * @param playedTime    当前字幕所在画面已经开始播放时间
         */
        public void refresh(final int currentSRTPos, int playedTime) {
            mHandler.removeMessages(AUTO_UPDATE);
            setSelection(currentSRTPos);
            mCurrentSRTPos = currentSRTPos + mFocusItem - 1;
            Class clazz = mAdapter.getItem(mCurrentSRTPos).getClass();
            try {
                Method getBeginTime = clazz.getDeclaredMethod("getBeginTime");
                mDelay = (int) getBeginTime.invoke(mAdapter.getItem(mCurrentSRTPos)) - playedTime;
            } catch (Exception e) {
                throw new RuntimeException("E must implements a public Method 'getBeginTime and 'and return a int!");
            }
            start();
        }

        private void start() {
            mHandler.sendEmptyMessageDelayed(AUTO_UPDATE, mDelay);
        }

        public void pause() {
            mHandler.removeMessages(AUTO_UPDATE);
        }

        public void stop() {
            mHandler.removeMessages(AUTO_UPDATE);
            mDelay = 0;
        }

        void update() {
            mCurrentSRTPos++;
            if(mCurrentSRTPos < lineCount + mFocusItem - 1) {
                try {
                    Class clazz = mAdapter.getItem(mCurrentSRTPos).getClass();
                    Method getBeginTime = clazz.getDeclaredMethod("getBeginTime");
                    mDelay = ((int) getBeginTime.invoke(mAdapter.getItem(mCurrentSRTPos + 1)))
                            - ((int) getBeginTime.invoke(mAdapter.getItem(mCurrentSRTPos)));
                } catch (Exception e) {
                    throw new RuntimeException("E must implements a public Method 'getBeginTime and 'and return a int!");
                }
                mHandler.removeMessages(AUTO_UPDATE);
                mHandler.sendEmptyMessageDelayed(AUTO_UPDATE, mDelay);
            } else if(mCurrentSRTPos < lineCount + mFocusItem){
                mHandler.removeMessages(AUTO_UPDATE);
                mHandler.sendEmptyMessageDelayed(AUTO_UPDATE, mDelay);
            }
        }

        /**
         * 拖动字幕结束后重置滚动字幕
         */
        public void reset() {
            setSelection(mCurrentSRTPos - mFocusItem + 1);
            dealItemStyle(true);
        }

    }
}
