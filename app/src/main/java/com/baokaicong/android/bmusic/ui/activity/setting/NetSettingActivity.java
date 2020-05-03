package com.baokaicong.android.bmusic.ui.activity.setting;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.consts.SettingField;
import com.baokaicong.android.bmusic.util.sql.SettingSQLUtil;

public class NetSettingActivity extends AppCompatActivity {
    private static final String NAME="netsetting";
    private String oldHost,oldPort;
    private EditText hostEdit,portEdit;
    private SettingSQLUtil settingSQLUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_net);
        BMContext.instance().addActivity(NAME,this);
        this.settingSQLUtil=new SettingSQLUtil(this);
        initView();
    }

    private void initView(){
        hostEdit=findViewById(R.id.net_host_edit);
        portEdit=findViewById(R.id.net_port_edit);
        oldHost=settingSQLUtil.getSetting(SettingField.HOST_ADDRESS);
        oldPort=settingSQLUtil.getSetting(SettingField.HOST_PORT);
        hostEdit.setText(oldHost);
        portEdit.setText(oldPort);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            saveSetting();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void testButtonEvent(View view){
        testRequest();
    }

    private void testRequest(){
//        final ConnectingDialog connectingDialog = new ConnectingDialog(this);
//        connectingDialog.setMessage("网络环境");
//        connectingDialog.setCanceledOnTouchOutside(false);
//        connectingDialog.show();
//        String url=hostEdit.getText().toString()+":" +portEdit.getText().toString() +"/";
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

    private void saveSetting(){
        String host=hostEdit.getText().toString();
        String port=portEdit.getText().toString();
        if(!host.equals(oldHost)){
            settingSQLUtil.updateSetting(SettingField.HOST_ADDRESS,host);
        }
        if(!port.equals(oldPort)){
            settingSQLUtil.updateSetting(SettingField.HOST_PORT,port);
        }
        BMContext.instance().removeActivity(NAME);
        finish();
        //overridePendingTransition(R.anim.top_enter_slip, R.anim.bottom_exit_slip);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(settingSQLUtil!=null){
            settingSQLUtil.close();
        }
    }
}
