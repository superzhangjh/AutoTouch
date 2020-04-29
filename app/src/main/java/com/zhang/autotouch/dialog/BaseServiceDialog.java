package com.zhang.autotouch.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.NonNull;

import com.zhang.autotouch.R;

/**
 * 在Service中可使用的dialog
 */
public abstract class BaseServiceDialog extends Dialog {

    public BaseServiceDialog(@NonNull Context context) {
        super(context, R.style.NoTitleDialog);
        setContentView(getLayoutId());
        onInited();
    }

    @Override
    public void show() {
        Window window = getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= 26) {
                window.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
            } else {
                window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
            }
            WindowManager.LayoutParams wmParams = window.getAttributes();
            wmParams.width = getWidth();
            wmParams.height = getHeight();
            window.setAttributes(wmParams);
        }
        super.show();
    }

    protected abstract int getLayoutId();

    protected abstract int getWidth();

    protected abstract int getHeight();

    protected abstract void onInited();
}
