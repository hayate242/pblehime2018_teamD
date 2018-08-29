package com.example.owner.googlemapstest;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

/*** 半透明になるエフェクトのボタン ***/
public class AlphaButton extends android.support.v7.widget.AppCompatButton {
    public AlphaButton(Context context) {
        super(context);
    }

    public AlphaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setPressed(boolean pressed) {
        if(pressed){
            this.setAlpha(0.75f);
        }else{
            this.setAlpha(1.0f);
        }
        super.setPressed(pressed);
    }

}