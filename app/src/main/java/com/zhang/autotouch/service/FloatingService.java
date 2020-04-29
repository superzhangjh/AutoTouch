package com.zhang.autotouch.service;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.zhang.autotouch.R;
import com.zhang.autotouch.dialog.MenuDialog;
import com.zhang.autotouch.utils.DensityUtil;
import com.zhang.autotouch.utils.WindowUtils;

/**
 * 悬浮窗
 */
public class FloatingService extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private MenuDialog menuDialog;
    private WindowManager.LayoutParams floatLayoutParams;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mFloatingView = creatView(R.layout.layout_window);
        //设置WindowManger布局参数以及相关属性
        int d = DensityUtil.dip2px(this, 50);
        floatLayoutParams = WindowUtils.newWmParams(d, d);
        //初始化位置
        floatLayoutParams.gravity = Gravity.TOP | Gravity.START;
        floatLayoutParams.x = WindowUtils.getScreenWidth(this) - DensityUtil.dip2px(this, 80);
        floatLayoutParams.y = WindowUtils.getScreenHeight(this) - DensityUtil.dip2px(this, 200);
        //获取WindowManager对象
        mWindowManager = WindowUtils.getWindowManager(this);
        addViewToWindow(mFloatingView, floatLayoutParams);
        //FloatingView的拖动事件
        mFloatingView.setClickable(true);
                mFloatingView.setOnTouchListener(new View.OnTouchListener() {
            private int x;
            private int y;
            //是否在移动
            private boolean isMoving;
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = (int) event.getRawX();
                        y = (int) event.getRawY();
                        isMoving = false;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        int nowX = (int) event.getRawX();
                        int nowY = (int) event.getRawY();
                        int moveX = nowX - x;
                        int moveY = nowY - y;
                        if (Math.abs(moveX) > 0 || Math.abs(moveY) > 0) {
                            isMoving = true;
                            floatLayoutParams.x += moveX;
                            floatLayoutParams.y += moveY;
                            //更新View的位置
                            mWindowManager.updateViewLayout(mFloatingView, floatLayoutParams);
                            x = nowX;
                            y = nowY;
                            return true;
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            onShowSelectDialog();
                            return true;
                        }
                        break;
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void onShowSelectDialog() {
        //弹出菜单弹窗
        hideDialog(menuDialog);
        if (menuDialog == null) {
            menuDialog = new MenuDialog(this);
            menuDialog.setListener(new MenuDialog.Listener() {
                @Override
                public void onFloatWindowAttachChange(boolean attach) {
                    if (attach) {
                        addViewToWindow(mFloatingView, floatLayoutParams);
                    } else {
                        removeViewFromWinddow(mFloatingView);
                    }
                }

                @Override
                public void onExitService() {
                    stopSelf();
                }
            });
        }
        menuDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeViewFromWinddow(mFloatingView);
        hideDialog(menuDialog);
    }

    private void hideDialog(Dialog dialog) {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void addViewToWindow(View view, WindowManager.LayoutParams params) {
        if (mWindowManager != null) {
            mWindowManager.addView(view, params);
        }
    }

    private void removeViewFromWinddow(View view) {
        if (mWindowManager != null && view != null && view.isAttachedToWindow()) {
            mWindowManager.removeView(view);
        }
    }

    private <T extends View> T creatView(int layout) {
        return (T) LayoutInflater.from(this).inflate(layout, null);
    }
}
