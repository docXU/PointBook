package com.debug.xxw.pointbook.viewmodel;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.debug.xxw.pointbook.R;

/**
 * @author xxw
 */
public class TagView extends LinearLayout {
    private TextView tv;
    private Button button;

    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.tag_layout, this);
        tv = findViewById(R.id.tag_text);
        button = findViewById(R.id.tag_button);
    }

    public TextView getTv() {
        return tv;
    }

    public void setTv(TextView tv) {
        this.tv = tv;
    }

    public Button getButton() {
        return button;
    }

    public void setButton(Button button) {
        this.button = button;
    }
}
