package com.bao.wifidemo.socket;

import android.os.Handler;
import android.os.Message;

import com.bao.wifidemo.utils.Constants;
import com.blankj.utilcode.util.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 通过Socket实现
 *
 * @author Administrator
 */
public class ServerLastly implements Runnable {
    private static final String TAG = "tcp_server";
    public static final int SERVER_ARG = 0x11;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    private Handler handler;

    /**
     * 此处不将连接代码写在构造方法中的原因：
     * 我在activity的onCreate()中创建示例，如果将连接代码 写在构造方法中，服务端会一直等待客户端连接，界面没有去描绘，会一直出现白屏。
     * 直到客户端连接上了，界面才会描绘出来。原因是构造方法阻塞了主线程，要另开一个线程。在这里我将它写在了run()中。
     */
    public ServerLastly(Handler handler) {
        this.handler = handler;
//        Log.i(TAG, "Server=======打开服务=========");
//        try {
//            serverSocket=new ServerSocket(8888);
//            clientSocket=serverSocket.accept();
//            Log.i(TAG, "Server=======客户端连接成功=========");
//             InetAddress inetAddress=clientSocket.getInetAddress();
//             String ip=inetAddress.getHostAddress();
//            Log.i(TAG, "===客户端ID为:"+ip);
//            printWriter=new PrintWriter(clientSocket.getOutputStream());
//            bufferedReader=new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    //发数据
    public void send(String data) {
        LogUtils.iTag(TAG, "服务端发送：" + data);
        if (printWriter != null) {
            printWriter.println(data);
            printWriter.flush();
        }else{
            LogUtils.iTag(TAG, "服务端输出流有问题");
        }
    }

    //接数据
    @Override
    public void run() {
        LogUtils.iTag(TAG, "=======打开服务=========");
        try {
            serverSocket = new ServerSocket(Constants.INSTANCE.getHOST_PORT());
            clientSocket = serverSocket.accept();//一直等待连接成功
            LogUtils.iTag(TAG, "======客户端连接成功=========");
            InetAddress inetAddress = clientSocket.getInetAddress();
            String ip = inetAddress.getHostAddress();
            printWriter = new PrintWriter(clientSocket.getOutputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            LogUtils.iTag(TAG, "客户端ID为:" + ip);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
//            if (bufferedReader != null &&!TextUtils.isEmpty(bufferedReader.readLine())) {
//                String result = "";
//                while ((result = bufferedReader.readLine()) != null) {//readLine方法有线程锁,readLine读取了数据并把数据清除
//                    LogUtils.iTag(TAG, "服务端接到的数据为：" + result);
//                    //把数据带回activity显示
//                    Message msg = handler.obtainMessage();
//                    msg.obj = result;
//                    msg.arg1 = SERVER_ARG;
//                    handler.sendMessage(msg);
//                }
//                LogUtils.dTag(TAG, "服务端关闭"+result );
//            }else{
//                LogUtils.iTag(TAG, "服务端关闭");
//                close();
//            }
            if (bufferedReader != null) {
                String result = "";
                char[] chars=new char[8*10];
                while ((result=bufferedReader.readLine())!=null) {  //这个有一个问题是如果有换行只会读取一行再读取一行,不能全部读取过来
                    LogUtils.iTag(TAG, "服务端接到的数据为：" + result);
                    //把数据带回activity显示
                    Message msg = handler.obtainMessage();
                    msg.obj = result;
                    msg.arg1 = SERVER_ARG;
                    handler.sendMessage(msg);
                }
                LogUtils.dTag(TAG, "服务端关闭"+result );
            }else{
                LogUtils.iTag(TAG, "服务端关闭");
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
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}