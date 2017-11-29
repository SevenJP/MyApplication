package newland.com.myapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import newland.com.authentication.ActivatesDeviceServiceAidl;
import newland.com.authentication.OnStatusListener;

public class MainActivity extends AppCompatActivity {


    private ActivatesDeviceServiceAidl aidl;
    private OnStatusListener listener1;
    private OnStatusListener listener2;
    private boolean isBinded=false;
    ServiceConnection deviceInfoSc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            aidl = ActivatesDeviceServiceAidl.Stub.asInterface(service);
            isBinded=true;
            Log.d("hjp","接口连接成功");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("hjp","接口连接断开");
        }
    };
    private TextView tv1;
    private TextView tv2;
    private boolean isBindInfo = false;
    private Intent implicitIntent;
    private Handler handler;
    private String jsonString;
    private String errMsgS;
    private Button never;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    private void init(){
        initView();
        initEvent();
        bindInfoService();
    }

    private void initView(){
        tv1= (TextView) findViewById(R.id.tv1);
        tv2= (TextView) findViewById(R.id.tv2);
        never= (Button) findViewById(R.id.never);
    }

    private void initEvent(){

        never.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT,
                        -1);
            }
        });

        handler =new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case 112:
                        tv1.setText(jsonString);
                        Toast.makeText(getApplicationContext(),"商户激活", Toast.LENGTH_SHORT).show();
                        break;
                    case 222:
                        tv2.setText(jsonString);
                        Toast.makeText(getApplicationContext(),"权限验证", Toast.LENGTH_SHORT).show();
                        break;
                    case 332:
                        tv1.setText(errMsgS);
                        break;
                    case 442:
                        tv2.setText(errMsgS);
                        break;
                }
            }
        };

        listener1=  new OnStatusListener.Stub() {
            @Override
            public void onSucceed(String json) throws RemoteException {
                Log.d("hjp","aidl服务返回数据："+json);
                jsonString=json;
                Message message=new Message();
                message.what=112;
                handler.sendMessage(message);
            }

            @Override
            public void onError(int errorCode, String errMsg) throws RemoteException {
                Log.d("hjp","aidl服务失败返回数据："+errMsg);
                errMsgS= errMsg;
                Message message=new Message();
                message.what=332;
                handler.sendMessage(message);
            }
        };

        listener2=  new OnStatusListener.Stub() {
            @Override
            public void onSucceed(String json) throws RemoteException {
                Log.d("hjp","aidl服务成功返回数据："+json);
                jsonString=json;
                Message message=new Message();
                message.what=222;
                handler.sendMessage(message);
            }

            @Override
            public void onError(int errorCode, String errMsg) throws RemoteException {
                Log.d("hjp","aidl服务失败返回数据："+errMsg);
                errMsgS= errMsg;
                Message message=new Message();
                message.what=442;
                handler.sendMessage(message);
            }
        };


        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    aidl.activatesDevice("AABB00000000000000000056",listener1);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    aidl.checkPassword("123456",1,"",listener2);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void bindInfoService(){
        if(isBinded){

        }else{
            implicitIntent= new Intent("newland.com.authentication.activate");
//            implicitIntent.setPackage("newland.com.authentication");//服务所在的包名
//            Intent intent = CommonUtil.getExplicitIntent(this,implicitIntent);
            if (implicitIntent == null){
                return;
            }
            isBindInfo = bindService(implicitIntent, deviceInfoSc, Context.BIND_AUTO_CREATE);
            if (isBindInfo){
                Log.i("hjp","接口绑定成功");
            }else {
                Log.i("hjp","接口绑定失败");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isBindInfo){
            unbindService(deviceInfoSc);
        }
    }


}
