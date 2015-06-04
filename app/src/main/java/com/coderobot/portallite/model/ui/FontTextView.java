package com.coderobot.portallite.model.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.coderobot.portallite.main.Global;

/**
 * Created by the great Tony on 2015/6/3.
 */
public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);
        init();
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Global global = (Global) getContext().getApplicationContext();
        setTypeface(global.typeface);
    }

}
