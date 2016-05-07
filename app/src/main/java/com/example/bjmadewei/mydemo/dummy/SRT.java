package com.example.bjmadewei.mydemo.dummy;

import com.example.bjmadewei.mydemo.srt.SRTDataInterface;

public class SRT implements SRTDataInterface {
    private int beginTime = 0;
    private int endTime;
    private String srtBodyCh;
    private String srtBodyEn;

    @Override
    public int getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(int beginTime) {
        this.beginTime = beginTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getSrtBodyCh() {
        return srtBodyCh;
    }

    public void setSrtBodyCh(String srtBodyCh) {
        this.srtBodyCh = srtBodyCh;
    }

    public String getSrtBodyEn() {
        return srtBodyEn;
    }

    public void setSrtBodyEn(String srtBodyEn) {
        this.srtBodyEn = srtBodyEn;
    }

    @Override
    public String getStrBody() {
        String s = "{\\an8}";
        return srtBodyCh.replace(s, "");
    }

    @Override
    public String getTimeBody() {
        StringBuilder sb = new StringBuilder();
        int hh = beginTime / (1000 * 60 * 60);
        int mm = (beginTime - hh * 1000 * 60 * 60) / (1000 * 60);
        int ss = (beginTime - hh * 1000 * 60 * 60  - mm * 60 * 1000) / (1000);

        if (hh > 0) {
            sb.append(hh + ":");
        }
        if (mm < 10 && hh > 0) {
            sb.append("0" + mm + ":");
        } else {
            sb.append(mm + ":");
        }
        if (ss < 10) {
            sb.append("0" + ss);
        } else {
            sb.append(ss);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "" + beginTime + ":" + endTime + " Ch:" + srtBodyCh + " En:" + srtBodyEn;
    }
}
