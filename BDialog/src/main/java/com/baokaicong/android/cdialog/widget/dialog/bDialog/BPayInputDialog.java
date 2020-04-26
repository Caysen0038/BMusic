package com.baokaicong.android.cdialog.widget.dialog.bDialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.baokaicong.android.cdialog.R;
import com.baokaicong.android.cdialog.inputView.PasswordInputView;
public class BPayInputDialog {

    private Context context;
    private Dialog dialog;
    private PasswordInputView pwdInputView;//密码输入框
    private TextView titleTv;//标题
    private ImageView cancelImg;//取消按钮

    public BPayInputDialog(Context context) {
        this.context = context;
    }

    public BPayInputDialog Builder() {
        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.CustomerDialogTheme);
        dialog.setContentView(R.layout.dialog_input_password);
        cancelImg = (ImageView) dialog.findViewById(R.id.img_close);
        titleTv = (TextView) dialog.findViewById(R.id.tv_name);
        pwdInputView = (PasswordInputView) dialog.findViewById(R.id.input_password);
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        return this;
    }

    /**
     * 设置标题
     *
     * @param title
     * @return
     */
    public BPayInputDialog setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            titleTv.setText(title);
        }
        return this;
    }

    /**
     * 获取密码输入内容
     *
     * @param watcher
     * @return
     */
    public BPayInputDialog setResultListener(final ResultListener watcher) {
        pwdInputView.setShadowPasswords(false);
        pwdInputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6){
                    watcher.onResult(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return this;
    }

    /**
     * 取消监听
     *
     * @param listener
     * @return
     */
    public BPayInputDialog setCancelListener(View.OnClickListener listener) {
        cancelImg.setOnClickListener(listener);
        return this;
    }

    public BPayInputDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public BPayInputDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public void show() {
        //显示键盘
        if (android.os.Build.VERSION.SDK_INT < 14) {
            InputMethodManager imm = (InputMethodManager) context
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            if (pwdInputView != null) {
                imm.showSoftInput(pwdInputView, InputMethodManager.SHOW_IMPLICIT);
            }
        } else {
            showIMEOtherWay(pwdInputView);
        }
        dialog.show();
    }

    /**
     * 其他方式显示键盘
     *
     * @param view
     */
    public static void showIMEOtherWay(final View view) {
        (new Handler()).postDelayed(new Runnable() {
            public void run() {
                view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                view.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
            }
        }, 200);
    }

    public void dismiss() {
        dialog.dismiss();
    }

    public interface ResultListener{
        void onResult(String result);
    }
}
