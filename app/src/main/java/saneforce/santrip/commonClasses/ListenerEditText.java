package saneforce.santrip.commonClasses;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

import androidx.appcompat.widget.AppCompatEditText;

public class ListenerEditText extends AppCompatEditText {
    private KeyImeChange keyImeChangeListener;

    public ListenerEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListenerEditText(Context context) {
        super(context);
    }

    public void setKeyImeChangeListener(KeyImeChange listener){
        keyImeChangeListener = listener;
    }

    public interface KeyImeChange {
        void onKeyIme(int keyCode, KeyEvent event);
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event){
        if(keyImeChangeListener != null){
            keyImeChangeListener.onKeyIme(keyCode, event);
        }
        return false;
    }
}
