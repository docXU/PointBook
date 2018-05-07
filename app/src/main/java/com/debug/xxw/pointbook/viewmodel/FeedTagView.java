package com.debug.xxw.pointbook.viewmodel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debug.xxw.pointbook.R;

/**
 * @author xxw
 */
public class FeedTagView extends LinearLayout {
    private TextView tv;
    private ImageButton button;

    //Tag的数据
    private int tagId;

    public FeedTagView(Context context) {
        super(context);
    }

    public FeedTagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tag_layout, this);
        tv = findViewById(R.id.tag_text);
        button = findViewById(R.id.tag_button);
        button.setVisibility(GONE);
    }


    public void hiddenTv() {
        tv.setVisibility(GONE);
    }

    public void displayBtn() {
        button.setVisibility(VISIBLE);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public ImageButton getButton() {
        return button;
    }

    public void setButton(ImageButton button) {
        this.button = button;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public interface LongClickListener {
        void onLongClick();
    }
}
