package com.zhang.autotouch.dialog;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import androidx.annotation.NonNull;

import com.zhang.autotouch.R;
import com.zhang.autotouch.utils.ToastUtil;

/**
 * 录制模式Dialog
 */
public class RecordDialog extends BaseServiceDialog {

    private ImageButton ibFloat;

    public RecordDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_record;
    }

    @Override
    protected int getWidth() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected int getHeight() {
        return WindowManager.LayoutParams.MATCH_PARENT;
    }

    @Override
    protected void onInited() {
        ibFloat = findViewById(R.id.ib_fl);
        ibFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.show("开始录制");
            }
        });
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            //不变暗
            params.dimAmount = 0f;
            //允许点击穿透
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        }
        super.show();
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            Log.d("录制坐标", "x=" + ev.getRawX() + "y=" + ev.getRawY());
        }
        return super.dispatchTouchEvent(ev);
    }
}
