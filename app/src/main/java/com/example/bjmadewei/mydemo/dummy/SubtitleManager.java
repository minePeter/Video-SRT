package com.example.bjmadewei.mydemo.dummy;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * 字幕解析及管理类，目前支持的格式包括srt, ass/ssa
 *
 * @author MR
 */
public class SubtitleManager {
    private static final String TAG = "SubtitleManager";
    private static final boolean DEBUG = false;
    //格式类型
    private static final int UNKNOW_FORMAT = -1;
    private static final int SRT_FORMAT = 0;
    private static final int ASS_FORMAT = 1;

    private TreeMap<Integer, SRT> mSrtMap;//保存字幕
    private int mSubFormat = UNKNOW_FORMAT;//当前字幕格式

    /**
     * 构建字幕管理器，根据传入的字幕文件进行解析
     *
     * @param srtName 字幕文件路径
     */
    public SubtitleManager(String srtName, Context context) {
        // TODO Auto-generated constructor stub
        long t1 = System.currentTimeMillis();
        if (srtName == null) throw new NullPointerException("subFileSrc can not be null!");
        if (srtName.endsWith("srt")) {
            mSubFormat = SRT_FORMAT;
        } else if (srtName.endsWith("ass")) {
            mSubFormat = ASS_FORMAT;
        }
        if (mSubFormat == UNKNOW_FORMAT) throw new RuntimeException("Unknow format subtitle!");
        //解析字幕
        switch (mSubFormat) {
            case SRT_FORMAT:
                mSrtMap = SrtTool.parseSrt(srtName, context);
                break;
            case ASS_FORMAT:
                mSrtMap = AssTool.parseSrt(srtName);
                break;
        }
        long t2 = System.currentTimeMillis();
        if (DEBUG) Log.d(TAG, "Parser time:" + (t2 - t1));
        if (mSrtMap == null) throw new RuntimeException("Subtitle parse failed!");
    }

    public ArrayList<SRT> getSRTList() {
        return new ArrayList<>(mSrtMap.values());
    }


    /**
     * 根据当前进度获得字幕
     *
     * @param currentPosition 当前进度，millSeconds
     * @return
     */
    public int getCurrentSubtitlePos(long currentPosition) {
        Iterator<Integer> keys = mSrtMap.keySet().iterator();
        Integer key = -1;
        //通过while循环遍历比较
        while (keys.hasNext()) {
            key = keys.next();
            SRT srtbean = mSrtMap.get(key);
            if (srtbean.getBeginTime() <= currentPosition) {
                if (srtbean.getEndTime() >= currentPosition) {
                    return key;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
        if (key > 0)
            return key;
        return -1;
    }
}
