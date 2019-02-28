package com.bao.wifidemo.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.bao.wifidemo.R
import com.bao.wifidemo.utils.WifiControlUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils

/**
 * Created by bao on 2018/3/21.
 * Tcp通信Activity
 */
class WifiTcpActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        val HOST_IP = "ip"
    }

    private var et_server_ip: EditText? = null //?跟在后面代表可以为null.不写默认不能为null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.wifi_tcp_activity)


        et_server_ip = findViewById(R.id.et_server_ip)  //et_server_ip?.setText("");  代表的意思是如果对象为空就不执行了  相当于if(et_server_ip!=null){et_server_ip.setText("")}
        findViewById<TextView>(R.id.tv_tcp_server).setOnClickListener(this@WifiTcpActivity)
        findViewById<TextView>(R.id.tv_tcp_client).setOnClickListener(this@WifiTcpActivity)

        //toast本机ip地址
        ToastUtils.showLong(NetworkUtils.getIPAddress(true))
        LogUtils.e(WifiControlUtils.getWifiRouteIPAddress(this))
        et_server_ip!!.setText(WifiControlUtils.getWifiRouteIPAddress(this))
    }

    override fun onClick(view: View?) {
        when (view!!.id) {  //代表检测空指针异常 相当于代码 if(view==null){throws NullException}else{when (view.id) ...}
            R.id.tv_tcp_server -> {
                startActivity(Intent(this@WifiTcpActivity, TcpServerActivity::class.java))
            }
            R.id.tv_tcp_client -> {
                var intent = Intent(this@WifiTcpActivity, TcpClientActivity::class.java)
                intent.putExtra(HOST_IP, et_server_ip!!.text.toString())
                startActivity(intent)
            }
        }
    }
}
