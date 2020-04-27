package com.baokaicong.android.bmusic.ui.activity.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.consts.IntentTag;
import com.baokaicong.android.cdialog.widget.dialog.ConnectingDialog;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetSettingActivity extends AppCompatActivity {
    private static final String NAME="netsetting";
    public static final String TITLE="网络设置";
    private boolean isChanged=false;
    private String oldHost,oldPort;
    private EditText hostEdit,portEdit;
    private boolean isNeedSave=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_net);
        BMContext.instance().addActivity(NAME,this);
        isNeedSave=getIntent().getBooleanExtra(IntentTag.NEED_SAVE,false);
        isChanged=false;
        initView();
    }

    private void initView(){
//        Toolbar toolbar=findViewById(R.id.sub_toolbar);
//        toolbar.setTitle(TITLE);
       // setSupportActionBar(toolbar);
//        toolbar.setNavigationOnClickListener((v)->{backList();});
        hostEdit=findViewById(R.id.net_host_edit);
//        hostEdit.setText(SettingEngine.Instance().getSetting(SettingField.NETWORK_HOST));
//        oldHost= SettingEngine.Instance().getSetting(SettingField.NETWORK_HOST);
        portEdit=findViewById(R.id.net_port_edit);
//        portEdit.setText(SettingEngine.Instance().getSetting(SettingField.NETWORK_PORT));
//        oldPort= SettingEngine.Instance().getSetting(SettingField.NETWORK_PORT);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            BMContext.instance().removeActivity(NAME);
            backList();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void testButtonEvent(View view){
        testRequest();
    }

    private void testRequest(){
        final ConnectingDialog connectingDialog = new ConnectingDialog(this);
        connectingDialog.setMessage("网络环境");
        connectingDialog.setCanceledOnTouchOutside(false);
        connectingDialog.show();
        String url=hostEdit.getText().toString()+":" +portEdit.getText().toString() +"/";
//        TestAPI api= RequestEngine.Instance().buildeRequestAPI(url,TestAPI.class);
//        Call<ResponseBody> call = api.test();
//        call.enqueue(new Callback<ResponseBody>() {
//            @Override
//            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
//                connectingDialog.dismiss();
//                ToastHelper.showText(NetSettingActivity.this,"网络可用");
//
//            }
//            @Override
//            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                connectingDialog.dismiss();
//                ToastHelper.showText(NetSettingActivity.this,"网络不可用");
//            }
//        });
    }

    private void backList(){
        String host=hostEdit.getText().toString();
        String port=portEdit.getText().toString();
        if(!host.equals(oldHost)){
            //SettingEngine.Instance().setSetting(SettingField.NETWORK_HOST,host);
            isChanged=true;
        }
        if(!port.equals(oldPort)){
            //SettingEngine.Instance().setSetting(SettingField.NETWORK_PORT,port);
            isChanged=true;
        }
        if(isChanged && isNeedSave){
            //SettingEngine.Instance().save();
        }
//        setResult(isChanged?Code.CHANGED:Code.NORMAL);
        isChanged=false;
        BMContext.instance().removeActivity(NAME);
        finish();
        //overridePendingTransition(R.anim.top_enter_slip, R.anim.bottom_exit_slip);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null && intent.getBooleanExtra(IntentTag.APP_EXIT,false)){
            this.finish();
        }
    }
}
