package com.practice.dbsample;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by spartans on 24/4/16.
 */
public class CardImage extends ImageView {

    boolean flip = false;
    boolean lock = false;
    int imgIndex;

    public CardImage(Context context) {
        super(context);
        setPadding(5, 5, 5, 5);
    }

    public CardImage(Context context, AttributeSet attr) {
        super(context, attr);

    }
}
