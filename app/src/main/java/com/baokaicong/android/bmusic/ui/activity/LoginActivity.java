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
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.UserService;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.ui.activity.setting.NetSettingActivity;
import com.baokaicong.android.bmusic.util.ToastUtil;
import com.baokaicong.android.bmusic.util.sql.SettingSQLUtil;
import com.baokaicong.android.cdialog.widget.dialog.LoadingDialog;


public class LoginActivity extends AppCompatActivity {
    private static final String NAME="login";
    private boolean loading=false;
    private static long DOUBLE_CLICK_TIME = 0L;
    private UserService userService;
    private ServiceConnection userCon;
    private SettingSQLUtil settingSQLUtil;
    private LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BMContext.instance().addActivity(NAME,this);
        settingSQLUtil=new SettingSQLUtil(this);
        init();
    }

    private void init(){
        userCon=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                userService=((CustomBinder)service).getService();
                Log.i("登录页面","绑定用户服务");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Intent intent=new Intent(this,UserService.class);
        bindService(intent,userCon,BIND_AUTO_CREATE);
        settingSQLUtil=new SettingSQLUtil(this);
    }

    public void loginEvent(View view){
        EditText username=findViewById(R.id.login_input_username);
        EditText password=findViewById(R.id.login_input_password);
        final User user=new User();
        user.setName(username.getText().toString());
        user.setPassword(password.getText().toString());
        login(user);
    }

    private void login(final User user){
        loading=true;
        //switchLoginEvent();
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setMessage("登陆ing");
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
        userService.login(user,new RequestCallback<String>() {
            @Override
            public void handleResult(Result<String> result) {
                if(result==null || !result.getCode().equals("000000")) {
                    ToastUtil.showText(LoginActivity.this, "用户名或密码错误");
                    return;
                }
                // 设置当前User token
                user.setToken(result.getData());
                // 保存
                settingSQLUtil.insertSetting("USERNAME",user.getName());
                settingSQLUtil.insertSetting("USERPASSWORD",user.getPassword());
                BMContext.instance().setCurrentUser(user);
                getUserInfo(user.getToken());
                //ToastUtil.showText(LoginActivity.this,"登录成功");

            }

            @Override
            public void handleError(Throwable t) {
                loadingDialog.dismiss();
                ToastUtil.showText(LoginActivity.this,"服务异常，请稍后重试");
            }
        });
    }

    private void getUserInfo(String token){
        userService.getUserInfo(token, new RequestCallback<AccountInfo>() {
            @Override
            public void handleResult(Result<AccountInfo> result) {
                if(loadingDialog!=null)
                    loadingDialog.dismiss();
                BMContext.instance().getUser().setUserInfo(result.getData());
                turn();
            }

            @Override
            public void handleError(Throwable t) {
                if(loadingDialog!=null)
                    loadingDialog.dismiss();
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
        if(!loading){
            findViewById(R.id.login_button_text).setVisibility(View.GONE);
            findViewById(R.id.login_button_gif).setVisibility(View.VISIBLE);

        }else{
            findViewById(R.id.login_button_text).setVisibility(View.VISIBLE);
            findViewById(R.id.login_button_gif).setVisibility(View.GONE);
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
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            // 设置两秒内连续两次back则退出
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
        if(settingSQLUtil!=null){
            settingSQLUtil.close();
        }
        unbindService(userCon);
    }
}
