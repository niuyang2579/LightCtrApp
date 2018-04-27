package com.niuyang.project;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class MainActivity extends AppCompatActivity {
    private ImageView btn1;
    private ImageView btn2;
    private ImageView btn3;
    private ImageView btn4;
    private String num1 = "0";
    private String num2 = "0";
    private String num3 = "0";
    private String num4 = "0";

    private Button btn_send;
    private Button btn_get;
    private ImageView btn_con;
    private EditText ed1;
    private EditText ed2;
    private String geted1;
    private TextView text;

    private Socket socket;
    private ExecutorService mThreadPool;
    private OutputStream outputStream;
    private InputStream inputStream;
    private BufferedReader bff;
    private String response;
    private String Con_msg;
    private String Send_msg;
    private String light;
    private Button btn_off;

    public Handler ConHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                Con_msg = bundle.getString("msg");
                text.setText(Con_msg);
            }
        }

    };
    public Handler SendHandler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                Send_msg = bundle.getString("msg");
                Toast.makeText(getApplicationContext(),Send_msg,Toast.LENGTH_SHORT);
            }
        }
    };
    public Handler GetHandler = new Handler(){
        public void handleMessage(Message msg) {
            if (msg.what == 0x11) {
                Bundle bundle = msg.getData();
                light = bundle.getString("msg");
                text.setText(light);
                setLight(light);
            }
        }
    };

    private void setLight(String light){
        if (light.charAt(0) == '1'){
            num1 = "1";
            btn1.setImageResource(R.drawable.btn_on);
        }else{
            num1 = "0";
            btn1.setImageResource(R.drawable.btn_off);
        }
        if (light.charAt(1) == '1'){
            num2 = "1";
            btn2.setImageResource(R.drawable.btn_on);
        }else{
            num2 = "0";
            btn2.setImageResource(R.drawable.btn_off);
        }
        if (light.charAt(2) == '1'){
            num3 = "1";
            btn3.setImageResource(R.drawable.btn_on);
        }else{
            num3 = "0";
            btn3.setImageResource(R.drawable.btn_off);
        }
        if (light.charAt(3) == '1'){
            num4 = "1";
            btn4.setImageResource(R.drawable.btn_on);
        }else{
            num4 = "0";
            btn4.setImageResource(R.drawable.btn_off);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (ImageView)findViewById(R.id.btn1);
        btn2 = (ImageView)findViewById(R.id.btn2);
        btn3 = (ImageView)findViewById(R.id.btn3);
        btn4 = (ImageView)findViewById(R.id.btn4);
        btn_send = (Button)findViewById(R.id.btn_send);
        btn_get = (Button)findViewById(R.id.btn_get);
        btn_con = (ImageView)findViewById(R.id.btn_con);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.edt2);
        text = (TextView)findViewById(R.id.text);
        btn_off = (Button)findViewById(R.id.btn_off);

        CameraData.getInstance().setCon(0);
        CameraData.getInstance().setGet(0);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num1.equals("0")){
                    num1 = "1";
                    btn1.setImageResource(R.drawable.btn_on);
                }else{
                    num1 = "0";
                    btn1.setImageResource(R.drawable.btn_off);
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num2.equals("0")){
                    num2 = "1";
                    btn2.setImageResource(R.drawable.btn_on);
                }else{
                    num2 = "0";
                    btn2.setImageResource(R.drawable.btn_off);
                }
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num3.equals("0")){
                    num3 = "1";
                    btn3.setImageResource(R.drawable.btn_on);
                }else{
                    num3 = "0";
                    btn3.setImageResource(R.drawable.btn_off);
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (num4.equals("0")){
                    num4 = "1";
                    btn4.setImageResource(R.drawable.btn_on);
                }else{
                    num4 = "0";
                    btn4.setImageResource(R.drawable.btn_off);
                }
            }
        });

        btn_con.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CameraData.getInstance().getCon() == 0){
                    Toast.makeText(getApplicationContext(),"发起连接",Toast.LENGTH_SHORT).show();
                    CameraData.getInstance().setCon(1);
                    new ConThread().start();
                }
                else{
                    Toast.makeText(getApplicationContext(),"关闭连接",Toast.LENGTH_SHORT).show();
                    CameraData.getInstance().setCon(0);
                    new CutThread().start();
                }
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"发送控制命令",Toast.LENGTH_SHORT).show();
                if (ed1.getText().toString().length() == 1){
                    geted1 = num1 + num2 + num3 + num4 + ed1.getText().toString();
                    new SendThread().start();
                }else{
                    Toast.makeText(getApplicationContext(), "请按规定输入", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"请求灯光状态",Toast.LENGTH_SHORT).show();
                CameraData.getInstance().setGet(1);
                new GetThread().start();
            }
        });
        btn_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLight("0000");
            }
        });
    }

    class ConThread extends Thread {
        @Override
        public void run() {
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress("192.168.137." + ed2.getText().toString(), 5025));
                if (socket.isConnected())
                    bundle.putString("msg", "连接成功");
                else
                    bundle.putString("msg", "连接失败");
                msg.setData(bundle);
                ConHandler.sendMessage(msg);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }
    class CutThread extends Thread{
        @Override
        public void run(){
            //定义消息
            Message msg = new Message();
            msg.what = 0x11;
            Bundle bundle = new Bundle();
            bundle.clear();
            try {
                socket.close();
                if (socket.isConnected())
                    bundle.putString("msg", "断开失败");
                else
                    bundle.putString("msg", "断开成功");
                msg.setData(bundle);
                ConHandler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class SendThread extends Thread {
        @Override
        public void run() {
            try {
                outputStream = socket.getOutputStream();
                outputStream.write((geted1).getBytes("utf-8"));
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class GetThread extends Thread{
        public void run(){
            try{
                outputStream = socket.getOutputStream();
                outputStream.write(("Get").getBytes("utf-8"));
                outputStream.flush();
                bff = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                while ((response = bff.readLine())!=null){
                    //定义消息
                    Message msg1 = GetHandler.obtainMessage();
                    msg1.what = 0x11;
                    Bundle bundle = new Bundle();
                    bundle.clear();
                    bundle.putString("msg", response);
                    msg1.setData(bundle);
                    //发送消息 修改UI线程中的组件
                    GetHandler.sendMessage(msg1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    class sGetThread extends Thread{
        @Override
        public void run() {
            try {
                bff.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
