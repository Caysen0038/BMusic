package com.baokaicong.android.bmusic.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import com.baokaicong.android.bmusic.BMContext;
import com.baokaicong.android.bmusic.R;
import com.baokaicong.android.bmusic.bean.Result;
import com.baokaicong.android.bmusic.bean.User;
import com.baokaicong.android.bmusic.bean.AccountInfo;
import com.baokaicong.android.bmusic.consts.SettingField;
import com.baokaicong.android.bmusic.service.UserService;
import com.baokaicong.android.bmusic.service.binder.CustomBinder;
import com.baokaicong.android.bmusic.service.request.RequestCallback;
import com.baokaicong.android.bmusic.util.BEAUtil;
import com.baokaicong.android.bmusic.util.sql.PropertySQLUtil;
import com.baokaicong.android.bmusic.util.ToastUtil;

public class BootSplashActivity extends AppCompatActivity {
    private static final String NAME="boot";
    private ServiceConnection userCon;
    private UserService userService;
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_splash);
        BMContext.instance().addActivity(NAME,this);
        handler=new Handler();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new Thread(()-> {
            if (BMContext.instance().getUser() == null) {
                autoLogin();
            } else {
                turnAcitivty(MainActivity.class);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(userCon!=null){
            unbindService(userCon);
        }
    }

    private void autoLogin(){
        PropertySQLUtil propertySQLUtil =new PropertySQLUtil(this);
        String name= propertySQLUtil.getProperty(SettingField.USER_NAME);
        String password= propertySQLUtil.getProperty(SettingField.USER_PASSWORD);
        if(name==null || name.length()==0 || password==null || password.length()==0){
            turnAcitivty(LoginActivity.class);
        }else{
            password= BEAUtil.DBEA(password,name);
            loginLocalUser(new User().setName(name).setPassword(password));
        }
        propertySQLUtil.close();
    }

    private void loginLocalUser(final User user){
        userCon= new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                userService = ((CustomBinder) service).getService();
                login(user);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                userService=null;
            }
        };
        Intent intent=new Intent(this, UserService.class);
        bindService(intent, userCon,BIND_AUTO_CREATE);

    }

    private void login(final User user){
        userService.login(user, new RequestCallback<String>() {
            @Override
            public void handleResult(Result<String> result) {
                if (result == null || !result.getCode().equals("000000")) {
                    turnAcitivty(LoginActivity.class);
                    return;
                }
                user.setToken(result.getData());
                BMContext.instance().setCurrentUser(user);
                getUserInfo(user.getToken());
            }
            @Override
            public void handleError(Throwable t) {
                turnAcitivty(LoginActivity.class);
            }
        });
    }
    private void getUserInfo(String token){
        userService.getUserInfo(token, new RequestCallback<AccountInfo>() {
            @Override
            public void handleResult(Result<AccountInfo> result) {
                BMContext.instance().getUser().setUserInfo(result.getData());
                turnAcitivty(MainActivity.class);
            }

            @Override
            public void handleError(Throwable t) {
                ToastUtil.showText(BootSplashActivity.this,"网络貌似不畅通了~~");
                turnAcitivty(MainActivity.class);
            }
        });
    }

    private void turnAcitivty(Class<?> clazz){
        handler.post(()-> {
            Intent intent=new Intent(BootSplashActivity.this,clazz);
            BMContext.instance().removeActivity(NAME);
            startActivity(intent);
            finish();
        });

    }


}
