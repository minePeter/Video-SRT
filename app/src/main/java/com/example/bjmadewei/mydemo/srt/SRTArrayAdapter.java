package com.example.bjmadewei.mydemo.srt;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bjmadewei.mydemo.R;

import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * Created by bjmadewei on 2016/4/27.
 */
public class SRTArrayAdapter<E> extends ArrayAdapter<E> {
    Context mContext;
    ArrayList<E> mData = null;
    Factory<E> mFactory;
    int mHeadCount = 0;
    int mTearCount = 0;
    int mMargin = 12;

    public SRTArrayAdapter(Context context, ArrayList<E> data, Factory<E> factory) {
        super(context, 0, data);
        mContext = context;
        mData = data;
        mFactory = factory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = newItem();
            ViewHolder holder = new ViewHolder();
            holder.pointIv = (TextView)((LinearLayout)convertView).getChildAt(0);
            holder.srtTv = (TextView)((LinearLayout)convertView).getChildAt(1);
            holder.timeTv = (TextView)((LinearLayout)convertView).getChildAt(2);
            convertView.setTag(holder);

        }
        ViewHolder holder = (ViewHolder) convertView.getTag();

        E data = mData.get(position);
        Class clazz = data.getClass();
        try {
            Method getStrBody = clazz.getDeclaredMethod("getStrBody");
            String strBody = ((String)getStrBody.invoke(data));
            holder.srtTv.setText(strBody);
            if(strBody.equals("")) {
                holder.pointIv.setVisibility(View.INVISIBLE);
                holder.timeTv.setText("");
            } else {
                holder.pointIv.setVisibility(View.VISIBLE);
                Method getTimeBody = clazz.getDeclaredMethod("getTimeBody");
                holder.timeTv.setText(((String)getTimeBody.invoke(data)));
            }
        } catch (Exception e) {
            throw new RuntimeException("E must implements a public Method 'getStrBody and getTimeBody'which return a String!");
        }
//        holder.srtTv.setTextColor(Color.argb(100, SRTLayout.SRTColor.getTextColor().red, SRTLayout.SRTColor.getTextColor().green, SRTLayout.SRTColor.getTextColor().blue));
//        holder.timeTv.setTextColor(Color.alpha(0));
        return convertView;
    }

    private View newItem() {
        View view = new LinearLayout(mContext);

        TextView point = new TextView(mContext);
        LinearLayout.LayoutParams prp = new LinearLayout.LayoutParams(15, 15);
        prp.leftMargin = mMargin;
        point.setLayoutParams(prp);
        point.setBackgroundColor(Color.argb(66,255,255,255));
        ((LinearLayout)view).addView(point);

        TextView tv = new TextView(mContext);
        LinearLayout.LayoutParams tlp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        tlp.topMargin = mMargin;
        tlp.bottomMargin = mMargin;
        tlp.weight = 1;
        tv.setLayoutParams(tlp);
        tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setGravity(Gravity.CENTER);
        tv.setSingleLine();
        ((LinearLayout)view).addView(tv);

        TextView ttv = new TextView(mContext);
        LinearLayout.LayoutParams ttlp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        ttlp.topMargin = mMargin;
        ttlp.bottomMargin = mMargin;
        ttlp.rightMargin = mMargin;
        ttv.setLayoutParams(ttlp);
        ttv.setGravity(Gravity.RIGHT | Gravity.END);
        ttv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() / 4 * 3);
        ((LinearLayout)view).addView(ttv);

        return view;
    }

    public void AddEmptyItemTear(int count) {
        E emptyItem = mFactory.make();
        for(int i = 0; i < count; i ++)
            mData.add(emptyItem);
        mTearCount = count;
    }

    public int AddEmptyItemHead(int count) {
        E emptyItem = mFactory.make();
        for(int i = 0; i < count; i ++)
            mData.add(0,emptyItem);
        mHeadCount = count;
        return count;
    }

    public interface Factory<E> {
        E make();
    }

    @Override
    public int getCount() {
        if(mData == null)
            return 0;
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    void changeData(ArrayList<E> data) {
        mData.clear();
        AddEmptyItemHead(mHeadCount);
        mData.addAll(data);
        AddEmptyItemTear(mTearCount);
    }
}
