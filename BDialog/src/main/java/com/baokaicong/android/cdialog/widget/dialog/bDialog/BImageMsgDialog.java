package com.baokaicong.android.cdialog.widget.dialog.bDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.baokaicong.android.cdialog.R;
import com.baokaicong.android.cdialog.utils.DensityUtils;


public class BImageMsgDialog {
    private Context      context;
    private TextView     msgTv;
    private ImageView    logoImg;
    private ImageView    cancelImg;
    private Dialog       dialog;
    private LinearLayout lLayout_bg;
    private Display      display;
    private boolean      showMsg       = false;
    private boolean      showImageLogo = false;
    private boolean      showCancelImg = false;

    public BImageMsgDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        display = windowManager.getDefaultDisplay();
    }

    public BImageMsgDialog builder() {
        // 获取Dialog布局
        View view = LayoutInflater.from(context).inflate(R.layout.toast_network_connet_dialog,
                null);

        // 获取自定义Dialog布局中的控件
        lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
        msgTv = (TextView) view.findViewById(R.id.tv_msg);
        msgTv.setVisibility(View.GONE);
        logoImg = (ImageView) view.findViewById(R.id.image_logo);
        logoImg.setVisibility(View.GONE);
        cancelImg = (ImageView) view.findViewById(R.id.img_cancel);
        cancelImg.setVisibility(View.GONE);

        // 定义Dialog布局和参数
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);

        // 调整dialog背景大小
        lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display.getWidth() * 0.85),
                LayoutParams.WRAP_CONTENT));

        return this;
    }

    public BImageMsgDialog setMsg(String msg) {
        showMsg = true;
        if (!TextUtils.isEmpty(msg)) {
            msgTv.setText(msg);
        } else {
            msgTv.setText("");
        }
        return this;
    }

    public BImageMsgDialog setImageLogo(Drawable msg) {
        showImageLogo = true;
        if (msg != null) {
            logoImg.setImageDrawable(msg);
        }
        return this;
    }

    public BImageMsgDialog setImageLogoSize(int width, int height) {
        if (width > 0 && height > 0) {
            LayoutParams lp = (LayoutParams) logoImg.getLayoutParams();
            lp.width = DensityUtils.dp2px(context, width);
            lp.height = DensityUtils.dp2px(context, height);
            logoImg.setLayoutParams(lp);
        }
        return this;
    }

    public BImageMsgDialog setImageLogoMargin(int top, int bottom) {
        if (top > 0 && bottom > 0) {
            LayoutParams lp = (LayoutParams) logoImg.getLayoutParams();
            lp.setMargins(0, top, 0, bottom);
            logoImg.setLayoutParams(lp);
        }
        return this;
    }

    public BImageMsgDialog setMsgTvMargin(int top, int bottom) {
        if (top > 0 && bottom > 0) {
            LayoutParams lp = (LayoutParams) msgTv.getLayoutParams();
            lp.setMargins(0, top, 0, bottom);
            msgTv.setLayoutParams(lp);
        }
        return this;
    }

    public ImageView getLogoImg() {
        showImageLogo = true;
        return logoImg;
    }

    public BImageMsgDialog setCancelButton(final View.OnClickListener listener) {
        showCancelImg = true;
        cancelImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public BImageMsgDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public BImageMsgDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    private void setLayout() {

        if (showMsg) {
            msgTv.setVisibility(View.VISIBLE);
        }

        if (showImageLogo) {
            logoImg.setVisibility(View.VISIBLE);
        }

        if (showCancelImg) {
            cancelImg.setVisibility(View.VISIBLE);
        }
    }

    public void show() {
        setLayout();
        dialog.show();
    }

    public void dismiss() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
