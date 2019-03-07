package com.bao.wifidemo.socket;

import android.os.Handler;
import android.os.Message;

import com.bao.wifidemo.utils.Constants;
import com.blankj.utilcode.util.LogUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 通过Socket实现
 *
 * @author Administrator
 */
public class Server implements Runnable {
    private static final String TAG = "tcp_server";
    public static final int SERVER_ARG = 0x11;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream printWriter;
    private DataInputStream bufferedReader;

    private Handler handler;

    /**
     * 此处不将连接代码写在构造方法中的原因：
     * 我在activity的onCreate()中创建示例，如果将连接代码 写在构造方法中，服务端会一直等待客户端连接，界面没有去描绘，会一直出现白屏。
     * 直到客户端连接上了，界面才会描绘出来。原因是构造方法阻塞了主线程，要另开一个线程。在这里我将它写在了run()中。
     */
    public Server(Handler handler) {
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
            try {
                printWriter.writeUTF(data);
                printWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            LogUtils.iTag(TAG, "服务端输出流有问题");
        }
    }

    //接数据
    @Override
    public void run() {
        LogUtils.iTag(TAG, "=======打开服务=========");
        try {
            serverSocket = new ServerSocket(Constants.INSTANCE.getHOST_PORT()); //设置端口,最好不要有跟别的app重复的
            serverSocket.setSoTimeout(0);//设置超时时间为无限制 0,代表无限制 other:代表这个时间段不开启通信会不再接受信息
            clientSocket = serverSocket.accept();//一直等待连接成功
            LogUtils.iTag(TAG, "======客户端连接成功=========");
            InetAddress inetAddress = clientSocket.getInetAddress();
            String ip = inetAddress.getHostAddress();
            printWriter = new DataOutputStream(clientSocket.getOutputStream());
            bufferedReader = new DataInputStream(clientSocket.getInputStream());
            LogUtils.iTag(TAG, "客户端ID为:" + ip);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        try {
            DataInputStream input = new DataInputStream(clientSocket.getInputStream());
            String result = "";
            while ((result = input.readUTF()) != null) {
                LogUtils.iTag(TAG, "服务端接到的数据为：" + result);
//把数据带回activity显示
                Message msg = handler.obtainMessage();
                msg.obj = result;
                msg.arg1 = SERVER_ARG;
                handler.sendMessage(msg);
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