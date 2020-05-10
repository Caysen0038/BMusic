package com.baokaicong.android.bmusic.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.baokaicong.android.bmusic.R;

public class InputDialog extends Dialog {
    private Context context;
    private EditText inputView;
    private View layout;
    public InputDialog(@NonNull Context context) {
        super(context, R.style.InputDialogStyle);
        this.context=context;
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initView(){
        layout = View.inflate(context, R.layout.dialog_input, null);
        setContentView(layout);
        inputView=findViewById(R.id.dialog_input);
    }

    public InputDialog setTitle(String text){
        TextView view=findViewById(R.id.dialog_title);
        view.setText(text);
        return this;
    }

    public InputDialog setEditHint(String text){
        inputView.setHint(text);
        return this;
    }

    public InputDialog setCancelButton(String text, View.OnClickListener listener){
        Button button=findViewById(R.id.dialog_cancel);
        button.setText(text);
        button.setOnClickListener(listener);
        return this;
    }

    public InputDialog setConfirmButton(String text, View.OnClickListener listener){
        Button button=findViewById(R.id.dialog_confirm);
        button.setText(text);
        button.setOnClickListener(listener);
        return this;
    }

    public String getInputData(){
        return inputView.getText().toString();
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
