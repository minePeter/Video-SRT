package com.example.bjmadewei.mydemo;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bjmadewei.mydemo.dummy.SRT;
import com.example.bjmadewei.mydemo.dummy.SubtitleManager;
import com.example.bjmadewei.mydemo.srt.SRTArrayAdapter;
import com.example.bjmadewei.mydemo.srt.SRTLayout;
import com.example.bjmadewei.mydemo.srt.SRTListView;


public class SRTFragment extends Fragment implements SRTLayout.CallBack, View.OnClickListener {

    String path1 = "ch.srt";
    String path2 = "en.srt";
    int count = 0;
    SRTLayout layout;
    SubtitleManager mSubtitleManager;

    TextView btn_ch;
    TextView btn_en;
    ImageView srt_close;

    boolean isCh = true;

    private SRTListView.SRTController mSRTController;
    private SRTListView.SRTAutoController mSRTAutoController;

    class SRTFactory implements SRTArrayAdapter.Factory<SRT> {
        @Override
        public SRT make() {
            SRT emptySRT = new SRT();
            emptySRT.setSrtBodyCh("");
            return emptySRT;
        }
    }

    // 模拟播放器
    private Handler mHandler = new Handler() {

        int delay = 0;
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    delay = mSubtitleManager.getSRTList().get(Integer.valueOf(count)).getBeginTime();
                    mHandler.sendEmptyMessageDelayed(1,delay);
                    break;
                case 1:
                    count ++;
                    delay = mSubtitleManager.getSRTList().get(Integer.valueOf(count)).getBeginTime()
                            - mSubtitleManager.getSRTList().get(Integer.valueOf(count - 1)).getBeginTime();
                    mSRTController.update();
                    mHandler.removeMessages(1);
                    mHandler.sendEmptyMessageDelayed(1,delay);
                    break;
                case 2:
                    mSRTAutoController.reset();
                    break;
            }

        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_srt_scroll, null);
        btn_ch = (TextView)view.findViewById(R.id.btn_ch);
        btn_ch.setOnClickListener(this);
        btn_en = (TextView)view.findViewById(R.id.btn_en);
        btn_en.setOnClickListener(this);
        srt_close = (ImageView) view.findViewById(R.id.srt_close);
        srt_close.setOnClickListener(this);
        layout = (SRTLayout)view.findViewById(R.id.srt_layout);
        init();
        return view;
    }

    void init() {
        mSubtitleManager = new SubtitleManager(path1, this.getActivity());
        SRTArrayAdapter<SRT> adapter = new SRTArrayAdapter<>(this.getActivity(), mSubtitleManager.getSRTList(), new SRTFactory());
        layout.setListAdapter(adapter);
        layout.registerCallback(this);
        layout.setOnDispatchTouchEventListener(
                new
                        SRTLayout.OnDispatchTouchEventListener() {
                            @Override
                            public void OnTouchEventDispatching(MotionEvent ev) {
                                switch (ev.getAction()) {
                                    case MotionEvent.ACTION_DOWN:
                                        mHandler.removeMessages(2);
                                        break;
                                    case MotionEvent.ACTION_UP:
                                        mHandler.sendEmptyMessageDelayed(2, 5 * 1000);
                                        break;
                                }
                            }
                        });

        layout.setSRTTextColor(255, 255, 255);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ch:
                if(isCh)
                    return;
                isCh = true;
                changeLanguage(isCh);
                break;
            case R.id.btn_en:
                if(!isCh)
                    return;
                isCh = false;
                changeLanguage(isCh);
                break;
            case R.id.srt_close:
                break;
        }
    }

    void changeLanguage(boolean changeToCh) {
        if(changeToCh) {
            mSubtitleManager = new SubtitleManager(path1, this.getActivity());
            btn_ch.setTextColor(Color.rgb(133,133,133));
            btn_ch.setBackgroundResource(R.drawable.srt_btn_selected);
            btn_en.setTextColor(Color.rgb(101,101,101));
            btn_en.setBackgroundResource(R.drawable.srt_btn_unselected);
        } else {
            mSubtitleManager = new SubtitleManager(path2, this.getActivity());
            btn_en.setTextColor(Color.rgb(133,133,133));
            btn_en.setBackgroundResource(R.drawable.srt_btn_selected);
            btn_ch.setTextColor(Color.rgb(101,101,101));
            btn_ch.setBackgroundResource(R.drawable.srt_btn_unselected);
        }
        layout.changeLanguage(mSubtitleManager.getSRTList());
    }

    @Override
    public void onListViewCrated() {
        mSRTAutoController = layout.getSRTAutoController();
        int n = mSubtitleManager.getCurrentSubtitlePos(91 * 60 * 1000 + 30 * 1000);
        mSRTAutoController.init(0, 44 * 60 * 1000);
//        count = n;
//        mSRTController = layout.getSRTController();
//        mSRTController.init(count);
//        if(count == 0)
//            SRTFragment.this.mHandler.sendEmptyMessage(0);
//        else
//            SRTFragment.this.mHandler.sendEmptyMessage(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        layout.unRegisterCallback(this);
        layout.destroyView();
    }
}
