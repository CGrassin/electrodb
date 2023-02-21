package fr.charleslabs.electrodb.utils;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class RelativeLayoutStrut extends View {
    public RelativeLayoutStrut(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RelativeLayoutStrut(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMersureSpec, int heightMersureSpec){
        int height = MeasureSpec.getSize(heightMersureSpec);
        setMeasuredDimension(0,height*35/100);
    }
}
