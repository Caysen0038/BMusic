package com.baokaicong.android.bmusic.ui.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.bean.User;
import com.baokaicong.android.bmusic.bean.AccountInfo;
import com.baokaicong.android.bmusic.consts.IntentTag;
import com.baokaicong.android.bmusic.consts.SettingField;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.UserService;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.activity.setting.NetSettingActivity;
import com.baokaicong.android.bmusic.util.BEAUtil;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;
import com.baokaicong.android.cdialog.widget.dialog.LoadingDialog;


public class LoginActivity extends AppCompatActivity {
    private static final String NAME="login";
    private boolean loading=false;
    private static long DOUBLE_CLICK_TIME = 0L;
    private UserService userService;
    private ServiceConnection userCon;
    private PropertySQLUtil propertySQLUtil;
    private EditText username,password;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_login);
        BMContext.instance().addActivity(NAME,this);
        propertySQLUtil =new PropertySQLUtil(this);

        init();
    }

    private void init(){
        username=findViewById(R.id.login_input_username);
        password=findViewById(R.id.login_input_password);
        userCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                userService=((CustomBinder)service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                userService=null;
            }
        };
        Intent intent=new Intent(this,UserService.class);
        bindService(intent,userCon,BIND_AUTO_CREATE);
        propertySQLUtil =new PropertySQLUtil(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String name=getIntent().getStringExtra(SettingField.USER_NAME);
        String pass=getIntent().getStringExtra(SettingField.USER_PASSWORD);
        username.setText(name);
        password.setText(pass);
    }

    public void loginEvent(View view){
        final User user=new User();
        user.setName(username.getText().toString());
        user.setPassword(password.getText().toString());
        login(user);
    }

    private void login(final User user){
        loading=true;
        switchLoginEvent();
        userService.login(user,new RequestCallback<String>() {
            @Override
            public void handleResult(Result<String> result) {
                loading=false;
                switchLoginEvent();
                if(result==null || !result.getCode().equals("000000")) {
                    ToastUtil.showText(LoginActivity.this, "用户名或密码错误");
                    return;
                }
                // 设置当前User token
                user.setToken(result.getData());
                // 保存
                String pass= BEAUtil.BEA(user.getPassword(),user.getName());
                propertySQLUtil.insertProperty(SettingField.USER_NAME,user.getName());
                propertySQLUtil.insertProperty(SettingField.USER_PASSWORD,pass);
                BMContext.instance().setCurrentUser(user);
                getUserInfo(user.getToken());
            }

            @Override
            public void handleError(Throwable t) {
                //loadingDialog.dismiss();
                loading=false;
                switchLoginEvent();
                ToastUtil.showText(LoginActivity.this,"服务异常，请稍后重试");
            }
        });
    }

    private void getUserInfo(String token){
        userService.getUserInfo(token, new RequestCallback<AccountInfo>() {
            @Override
            public void handleResult(Result<AccountInfo> result) {
                BMContext.instance().getUser().setUserInfo(result.getData());
                turn();
            }

            @Override
            public void handleError(Throwable t) {
                ToastUtil.showText(LoginActivity.this,"请求用户信息错误");
                turn();
            }

            private void turn(){
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                BMContext.instance().removeActivity(NAME);
                startActivity(intent);
                finish();
            }
        });
    }

    private void switchLoginEvent(){
        if(loading){
            findViewById(R.id.login_button_text).setVisibility(View.GONE);
            findViewById(R.id.login_button_gif).setVisibility(View.VISIBLE);
            username.setEnabled(false);
            password.setEnabled(false);
        }else{
            findViewById(R.id.login_button_text).setVisibility(View.VISIBLE);
            findViewById(R.id.login_button_gif).setVisibility(View.GONE);
            username.setEnabled(true);
            password.setEnabled(true);
        }
    }

    /**
     * 响应按键
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(loading){
            loading=false;
            switchLoginEvent();
            return true;
        }
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - DOUBLE_CLICK_TIME) > 2000) {
                ToastUtil.showText(LoginActivity.this, "再按一次退出");
                DOUBLE_CLICK_TIME = System.currentTimeMillis();
            } else {
                BMContext.instance().removeActivity(NAME);
                BMContext.instance().finishAllActivity();
                this.finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent!=null && intent.getBooleanExtra(IntentTag.APP_EXIT,false)){
            this.finish();
        }
    }

    public void openNetEvent(View view){
        Intent intent=new Intent(this, NetSettingActivity.class);
        intent.putExtra(IntentTag.NEED_SAVE,true);
        startActivity(intent);
        overridePendingTransition(R.anim.bottom_enter_slip, R.anim.top_exit_slip);
    }

    public void openIntroEvent(View view){
        ToastUtil.showText(this,"使用说明请至官网http://www.baokaicong.com查阅");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(propertySQLUtil !=null){
            propertySQLUtil.close();
        }
        if(userService!=null){
            unbindService(userCon);
        }
    }
}
