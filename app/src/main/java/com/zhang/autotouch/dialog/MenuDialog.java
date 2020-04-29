package com.zhang.autotouch.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zhang.autotouch.R;
import com.zhang.autotouch.TouchEventManager;
import com.zhang.autotouch.adapter.TouchPointAdapter;
import com.zhang.autotouch.bean.TouchEvent;
import com.zhang.autotouch.bean.TouchPoint;
import com.zhang.autotouch.utils.DensityUtil;
import com.zhang.autotouch.utils.DialogUtils;
import com.zhang.autotouch.utils.GsonUtils;
import com.zhang.autotouch.utils.SpUtils;
import com.zhang.autotouch.utils.ToastUtil;

import java.util.List;

public class MenuDialog extends BaseServiceDialog implements View.OnClickListener {

    private Button btStop;
    private RecyclerView rvPoints;

    private AddPointDialog addPointDialog;
    private Listener listener;
    private TouchPointAdapter touchPointAdapter;
    private RecordDialog recordDialog;

    public MenuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_menu;
    }

    @Override
    protected int getWidth() {
        return DensityUtil.dip2px(getContext(), 350);
    }

    @Override
    protected int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onInited() {
        setCanceledOnTouchOutside(true);
        findViewById(R.id.bt_exit).setOnClickListener(this);
        findViewById(R.id.bt_add).setOnClickListener(this);
        findViewById(R.id.bt_record).setOnClickListener(this);
        btStop = findViewById(R.id.bt_stop);
        btStop.setOnClickListener(this);
        rvPoints = findViewById(R.id.rv);
        touchPointAdapter = new TouchPointAdapter();
        touchPointAdapter.setOnItemClickListener(new TouchPointAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, TouchPoint touchPoint) {
                btStop.setVisibility(View.VISIBLE);
                dismiss();
                TouchEvent.postStartAction(touchPoint);
                ToastUtil.show("已开启触控点：" + touchPoint.getName());
            }
        });
        rvPoints.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPoints.setAdapter(touchPointAdapter);
        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (TouchEventManager.getInstance().isPaused()) {
                    TouchEvent.postContinueAction();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("啊实打实", "onStart");
        //如果正在触控，则暂停
        TouchEvent.postPauseAction();
        if (touchPointAdapter != null) {
            List<TouchPoint> touchPoints = SpUtils.getTouchPoints(getContext());
            Log.d("啊实打实", GsonUtils.beanToJson(touchPoints));
            touchPointAdapter.setTouchPointList(touchPoints);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_add:
                DialogUtils.dismiss(addPointDialog);
                addPointDialog = new AddPointDialog(getContext());
                addPointDialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MenuDialog.this.show();
                    }
                });
                addPointDialog.show();
                dismiss();
                break;
            case R.id.bt_record:
                dismiss();
                if (listener != null) {
                    listener.onFloatWindowAttachChange(false);
                    if (recordDialog ==null) {
                        recordDialog = new RecordDialog(getContext());
                        recordDialog.setOnDismissListener(new OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                listener.onFloatWindowAttachChange(true);
                                MenuDialog.this.show();
                            }
                        });
                        recordDialog.show();
                    }
                }
                break;
            case R.id.bt_stop:
                btStop.setVisibility(View.GONE);
                TouchEvent.postStopAction();
                ToastUtil.show("已停止触控");
                break;
            case R.id.bt_exit:
                TouchEvent.postStopAction();
                if (listener != null) {
                    listener.onExitService();
                }
                break;

        }
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        /**
         * 悬浮窗显示状态变化
         * @param attach
         */
        void onFloatWindowAttachChange(boolean attach);

        /**
         * 关闭辅助
         */
        void onExitService();
    }
}
