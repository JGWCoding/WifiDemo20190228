package com.bao.wifidemo.activity

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.bao.wifidemo.R
import com.bao.wifidemo.socket.Client
import com.bao.wifidemo.socket.Server
import com.bao.wifidemo.utils.WifiControlUtils
import com.blankj.utilcode.util.LogUtils
//知道wifi地址通信,让手机与设备直接通信 例如简单的控制灯开关,音乐播放
//server端:存储信息data {id:xxxxx(设备序列号),ip:xxxxx(wifi连接地址)}
//phone: wifi  app端
//devices: wifi

//devices( 开启一个ServerSocket(没网也可以自动生成ip) ) ---> phone app通过扫描序列号(连接ServerSocket)ClientSocket连接,向devices设置无线有网wifi name password
//devices连接无线wifi 向server传递视频信息,手机通过server端拉下视频信息
class WifiImChatActivity : AppCompatActivity(), View.OnClickListener {
    var server: Server? = null;
    var client: Client? = null;
    var serverHandler: Handler? = null;
    var clientHandler: Handler? = null;
    var et_message: EditText? = null;
    var tv_message: TextView? = null;
    var dataText: StringBuilder? = StringBuilder();
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_im_chat)
        serverHandler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                LogUtils.i("server接收到：" + msg!!.obj)
                dataText!!.append("\n"+"server recvier:" + msg!!.obj + "\n")
                tv_message!!.text = dataText.toString()
            }
        }
        clientHandler = object : Handler() {
            override fun handleMessage(msg: Message?) {
                super.handleMessage(msg)
                LogUtils.i("client接收到：" + msg!!.obj)
                dataText!!.append("\n"+"client recvier:" + msg!!.obj + "\n")
                tv_message!!.text = dataText.toString()
            }
        }
        findViewById<Button>(R.id.tv_tcp_server).setOnClickListener(this@WifiImChatActivity)
        findViewById<Button>(R.id.tv_tcp_client).setOnClickListener(this@WifiImChatActivity)
        et_message = findViewById(R.id.et_message);
        tv_message = findViewById(R.id.textView);
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_tcp_server -> {
                //是否开启服务端
                if (server == null) {
                    server = Server(serverHandler)
                    Thread(server).start()
                } else {  //发送信息
                    if (!TextUtils.isEmpty(et_message!!.text)) {
                        Thread(object : Runnable {
                            override fun run() {
                                serverSend();
                            }
                        }).start()
                    }
                }
            }
            R.id.tv_tcp_client -> {
                if (client == null) {
                    client = Client(clientHandler, WifiControlUtils.getWIFILocalIpAdress(this@WifiImChatActivity));
                    Thread(client).start()
                } else {
                    if (!TextUtils.isEmpty(et_message!!.text)) {
                        Thread(object : Runnable {
                            override fun run() {
                                clientSend();
                            }
                        }).start()
                    }
                }
            }
        }
    }

    private fun serverSend() {
        dataText!!.append("server send:" + et_message!!.text + "\t")
        server!!.send(et_message!!.text.toString());
//        tv_message!!.text = dataText.toString()
    }

    private fun clientSend() {
        dataText!!.append("client send:" + et_message!!.text + "\t")
        client!!.send(et_message!!.text.toString());
//        tv_message!!.text = dataText.toString()
    }

    override fun onDestroy() {
        super.onDestroy()
        server?.close();
        client?.close() //不为空就执行close
    }

}