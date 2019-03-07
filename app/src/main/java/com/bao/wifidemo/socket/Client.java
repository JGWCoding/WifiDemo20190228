package com.bao.wifidemo.socket;

import android.os.Handler;
import android.os.Message;

import com.bao.wifidemo.utils.Constants;
import com.blankj.utilcode.util.LogUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * 通过socket实现   socket里面的封装的IO流read和write都会阻塞,等待内容传输过来才会开锁
 *
 * @author Administrator
 */
public class Client implements Runnable {
    private static final String TAG = "tcp_client";
    public static final int CLIENT_ARG = 0x12;
    //超时时间，如果60S没通信，就会接受不了信息,read()不到信息
    private int timeout = 60000;
    private Socket clientSocket;
    private DataOutputStream printWriter;
    private DataInputStream bufferedReader;

    Handler handler;
    private String server_ip;

    public Client(Handler handler, String server_ip) {
        this.handler = handler;
        this.server_ip = server_ip;
    }

    //发数据
    public void send(String data) {
        LogUtils.dTag(TAG, "客户端发送：" + data);
        if (printWriter != null) {
            try {
                printWriter.writeUTF(data);
                printWriter.flush();//可以不用flush
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            LogUtils.dTag(TAG, "客户端发送流出问题了");
        }
    }


    //接收据
    @Override
    public void run() {
        try {
            //连接服务器 ---- 如果服务器没开直接异常了  应该定一个定时器一直重开
            clientSocket = new Socket(server_ip, Constants.INSTANCE.getHOST_PORT());
            clientSocket.setSoTimeout(timeout); //read()方法在超过这个时间段将不再接受信息 0代表无限期
            printWriter = new DataOutputStream(clientSocket.getOutputStream());
            bufferedReader = new DataInputStream(clientSocket.getInputStream());
            LogUtils.dTag(TAG, "=======连接服务器成功=========");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try {
            if (bufferedReader != null) {
                String result = ""; //DataInputStream.readUTF 那么也得DataOutputStream.writeUTF
                while ((result = bufferedReader.readUTF()) != null) {  //bufferedReader.readUTF(有数据让你执行,再次进入会看有没有锁,有锁代表没数据) 有线程锁,会一直等待锁再执行
                    LogUtils.dTag(TAG, "客户端接到的数据为：" + result);
                    //将数据带回acitvity显示
                    Message msg = handler.obtainMessage();
                    msg.arg1 = CLIENT_ARG;
                    msg.obj = result;
                    handler.sendMessage(msg);
                }
                LogUtils.dTag(TAG, "客户端关闭"+result );
            }else {
                LogUtils.dTag(TAG, "客户端关闭" );
                close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (printWriter != null) {
                printWriter.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}