package com.baokaicong.android.bmusic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baokaicong.android.bmusic.R;

public class ConfirmDialog extends Dialog {
    private Context context;
    public ConfirmDialog(@NonNull Context context) {
        super(context, R.style.ConfirmDialogStyle);
        this.context=context;
        initView();
    }

    private void initView(){
        View view=View.inflate(context,R.layout.dialog_confirm,null);
        setContentView(view);
    }

    public ConfirmDialog setTitle(String text){
        TextView view=findViewById(R.id.dialog_title);
        view.setText(text);
        return this;
    }

    public ConfirmDialog setContent(String text){
        TextView view=findViewById(R.id.dialog_content);
        view.setText(text);
        return this;
    }

    public ConfirmDialog setCancelButton(String text, View.OnClickListener listener){
        Button button=findViewById(R.id.dialog_cancel);
        button.setText(text);
        button.setOnClickListener(listener);
        return this;
    }

    public ConfirmDialog setConfirmButton(String text, View.OnClickListener listener){
        Button button=findViewById(R.id.dialog_confirm);
        button.setText(text);
        button.setOnClickListener(listener);
        return this;
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
