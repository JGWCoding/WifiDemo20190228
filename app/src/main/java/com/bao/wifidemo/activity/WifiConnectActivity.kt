package com.bao.wifidemo.activity

import android.content.Context
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bao.wifidemo.R
import com.bao.wifidemo.base.BaseRecyclerAdapter
import com.bao.wifidemo.base.BaseRecyclerViewFragment
import com.bao.wifidemo.receiver.WifiBroadcastReceiver
import com.bao.wifidemo.utils.ThreadUtils
import com.bao.wifidemo.utils.WifiControlUtils
import com.bao.wifidemo.utils.WifiControlUtils.WIFI_CIPHER_WAP
import com.blankj.utilcode.util.LogUtils
import java.util.*

open class WifiConnectActivity : AppCompatActivity() {
    var wifiBroadcastReceiver: WifiBroadcastReceiver? = null
    var wifiControlUtils: WifiControlUtils? = null;
    var wifiInfo: WifiInfo? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi_connect)
        val beginTransaction = supportFragmentManager.beginTransaction()
        beginTransaction.add(R.id.fragment, MyFragment())
        beginTransaction.commitAllowingStateLoss()

        //动态注册wifi状态广播
        wifiBroadcastReceiver = WifiBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)
        registerReceiver(wifiBroadcastReceiver, intentFilter)

        wifiControlUtils = WifiControlUtils(this)
        wifiInfo = wifiControlUtils!!.getWifiInfo()
        wifiControlUtils!!.createWifiLock()
        wifiControlUtils!!.acquireWifiLock()
    }

    override fun onDestroy() {
        super.onDestroy()
        wifiControlUtils!!.releaseWifilock()
        unregisterReceiver(wifiBroadcastReceiver)
    }

    class MyFragment : BaseRecyclerViewFragment<ScanResult>() {
        var wifiControlUtils: WifiControlUtils? = null;
        var items1 = listOf(
                "12345678",
                "123456789",
                "1234567890",
                "12345678910",
                "0123456789",
                "9876543210",
                "012345678",
                "01234567",
                "87654321",
                "876543210",
                "987654321",
                "76543210",
                "88888888",
                "888888888",
                "00000000",
                "66668888",
                "88886666",
                "11223344",
                "147258369", //  手机wifi常用设置密码
                "Toppine123",   //测试
                "11111111",
                "11110000",
                "22222222",
                "33333333",
                "44444444",
                "55555555",
                "66666666",
                "77777777",
                "99999999",
                "86868686");

        override fun onItemClick(position: Int, itemId: Long) {
            var mItems = wifiControlUtils!!.wifiList;
            activity?.title = "正在连接wifi";
            ThreadUtils.executeByCpu(object : ThreadUtils.Task<String>() {
                override fun doInBackground(): String? {
                    for (bean in items1) {
                        for (item in mItems) {
                            var tempState = WifiBroadcastReceiver.state;
                            LogUtils.e(item.SSID + " @=@ " + bean + " state:" + WifiBroadcastReceiver.state)
                            wifiControlUtils!!.createNetWork(item.SSID, bean, WIFI_CIPHER_WAP);
                            while (tempState != WifiBroadcastReceiver.state || WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) { //连接成功状态直接返回
                                    return "";
                                }
                                if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {  //连接不成功直接睡眠1s后退出循环
                                    SystemClock.sleep(100);
                                    break;
                                }
                            }
                            //如果是在连接中的状态一直睡眠20ms获取连接状态
                            while (WifiBroadcastReceiver.state==SupplicantState.SCANNING||WifiBroadcastReceiver.state == SupplicantState.ASSOCIATED || WifiBroadcastReceiver.state == SupplicantState.FOUR_WAY_HANDSHAKE || WifiBroadcastReceiver.state == SupplicantState.ASSOCIATING || WifiBroadcastReceiver.state == SupplicantState.GROUP_HANDSHAKE) {
                                SystemClock.sleep(20);
                                if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {//连接不成功状态直接退出循环
                                    break;
                                }
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {//连接成功状态直接返回
                                    return "";
                                }
                            }
                        }
                    }
                    return "";
                }

                override fun onSuccess(result: String?) {
                    if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {
                        activity?.title = "简单连接wifi成功";
                    }else{
                        activity?.title = "简单连接wifi失败";
                    }
                }

                override fun onCancel() {
                }

                override fun onFail(t: Throwable?) {
                }
            })
            ThreadUtils.executeByCpu(object : ThreadUtils.Task<String>() {
                override fun doInBackground(): String? {
                    for (i in 10000000000..100000000000) {
                        for (item in mItems) {
                            var tempState = WifiBroadcastReceiver.state;
                            LogUtils.e(item.SSID + " @=@ " + i + " state:" + WifiBroadcastReceiver.state)
                            wifiControlUtils!!.createNetWork(item.SSID, i.toString(), WIFI_CIPHER_WAP);
                            while (tempState != WifiBroadcastReceiver.state || WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) { //连接成功状态直接返回
                                    return "";
                                }
                                if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {  //连接不成功直接睡眠1s后退出循环
                                    SystemClock.sleep(100);
                                    break;
                                }
                            }
                            //如果是在连接中的状态一直睡眠20ms获取连接状态
                            while (WifiBroadcastReceiver.state==SupplicantState.SCANNING||WifiBroadcastReceiver.state == SupplicantState.ASSOCIATED || WifiBroadcastReceiver.state == SupplicantState.FOUR_WAY_HANDSHAKE || WifiBroadcastReceiver.state == SupplicantState.ASSOCIATING || WifiBroadcastReceiver.state == SupplicantState.GROUP_HANDSHAKE) {
                                SystemClock.sleep(20);
                                if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {//连接不成功状态直接退出循环
                                    break;
                                }
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {//连接成功状态直接返回
                                    return "";
                                }
                            }
                        }
                    }
                    return ""
                }

                override fun onSuccess(result: String?) {
                    if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {
                        activity?.title = "11号码连接wifi成功";
                    }else{
                        activity?.title = "11号码连接wifi失败";
                    }
                }

                override fun onCancel() {
                }

                override fun onFail(t: Throwable?) {
                }
            })
        }

        override fun getRecyclerAdapter(): BaseRecyclerAdapter<ScanResult> {
            return MyAdapter(this@MyFragment.mContext, BaseRecyclerAdapter.NEITHER);
        }

        override fun requestData(isRefreshing: Boolean) {
            if (isRefreshing) {
                if (wifiControlUtils == null) {
                    wifiControlUtils = WifiControlUtils(context)
                }
                wifiControlUtils!!.scanWifi();
                var wifiList = wifiControlUtils!!.wifiList;
                Collections.sort<ScanResult>(wifiList, kotlin.Comparator { o1, o2 -> o2.level - o1.level })
                mAdapter.resetItem(wifiList);
                onRequestSuccess()
                onNoRequest()
            }
        }

        class MyAdapter(context: Context, mode: Int) : BaseRecyclerAdapter<ScanResult>(context, mode) {
            var wifiControlUtils: WifiControlUtils? = null
            var items1 = listOf(
                    "12345678",
                    "123456789",
                    "1234567890",
                    "12345678910",
                    "0123456789",
                    "9876543210",
                    "012345678",
                    "01234567",
                    "87654321",
                    "876543210",
                    "987654321",
                    "76543210",
                    "88888888",
                    "888888888",
                    "00000000",
                    "66668888",
                    "88886666",
                    "11223344",
                    "147258369", //  手机wifi常用设置密码
                    "Toppine123",   //测试
                    "11111111",
                    "11110000",
                    "22222222",
                    "33333333",
                    "44444444",
                    "55555555",
                    "66666666",
                    "77777777",
                    "99999999",
                    "86868686");

            override fun onBindDefaultViewHolder(holder: RecyclerView.ViewHolder?, item: ScanResult?, position: Int) {
                holder!!.itemView?.findViewById<TextView>(R.id.textView1)!!.setText(item!!.SSID + " 强度:" + item!!.level);
                holder!!.itemView?.findViewById<TextView>(R.id.textView2)!!.setOnClickListener(object : View.OnClickListener {
                    override fun onClick(view: View) {
                        holder!!.itemView?.findViewById<TextView>(R.id.textView2)!!.setText("连接wifi中")
                        wifiControlUtils = WifiControlUtils(mContext);
                        ThreadUtils.executeByCpu(object : ThreadUtils.Task<String>() {
                            override fun doInBackground(): String? {
                                for (bean in items1) {
                                    var tempState = WifiBroadcastReceiver.state;
                                    LogUtils.e(item.SSID + " @=@ " + bean + " state:" + WifiBroadcastReceiver.state)
                                    wifiControlUtils!!.createNetWork(item.SSID, bean, WIFI_CIPHER_WAP);
                                    while (tempState != WifiBroadcastReceiver.state || WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {
                                        if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) { //连接成功状态直接返回
                                            return "";
                                        }
                                        if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {  //连接不成功直接睡眠1s后退出循环
                                            SystemClock.sleep(100);
                                            break;
                                        }
                                    }
                                    //如果是在连接中的状态一直睡眠20ms获取连接状态
                                    while (WifiBroadcastReceiver.state==SupplicantState.SCANNING||WifiBroadcastReceiver.state == SupplicantState.ASSOCIATED || WifiBroadcastReceiver.state == SupplicantState.FOUR_WAY_HANDSHAKE || WifiBroadcastReceiver.state == SupplicantState.ASSOCIATING || WifiBroadcastReceiver.state == SupplicantState.GROUP_HANDSHAKE) {
                                        SystemClock.sleep(20);
                                        if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {//连接不成功状态直接退出循环
                                            break;
                                        }
                                        if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {//连接成功状态直接返回
                                            return "";
                                        }
                                    }
                                }
                                return ""
                            }

                            override fun onSuccess(result: String?) {
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {
                                    holder!!.itemView?.findViewById<TextView>(R.id.textView2)!!.setText("连接wifi成功")
                                }else{
                                    holder!!.itemView?.findViewById<TextView>(R.id.textView2)!!.setText("连接wifi失败")
                                }
                            }

                            override fun onCancel() {
                            }

                            override fun onFail(t: Throwable?) {
                            }
                        })

                    }
                })
                holder!!.itemView?.findViewById<TextView>(R.id.textView3)!!.setOnClickListener(object :View.OnClickListener{
                    override fun onClick(view:View) {
                        holder!!.itemView?.findViewById<TextView>(R.id.textView3)!!.setText("连接wifi中")
                        ThreadUtils.executeByCpu(object : ThreadUtils.Task<String>() {
                            override fun doInBackground(): String? {
                                for (i in 10000000000..100000000000) {
                                    var tempState = WifiBroadcastReceiver.state;
                                    LogUtils.e(item.SSID + " @=@ " + i + " state:" + WifiBroadcastReceiver.state)
                                    wifiControlUtils!!.createNetWork(item.SSID, i.toString(), WIFI_CIPHER_WAP);
                                    while (tempState != WifiBroadcastReceiver.state || WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {
                                        if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) { //连接成功状态直接返回
                                            return "";
                                        }
                                        if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {  //连接不成功直接睡眠1s后退出循环
                                            SystemClock.sleep(100);
                                            break;
                                        }
                                    }
                                    //如果是在连接中的状态一直睡眠20ms获取连接状态
                                    while (WifiBroadcastReceiver.state==SupplicantState.SCANNING||WifiBroadcastReceiver.state == SupplicantState.ASSOCIATED || WifiBroadcastReceiver.state == SupplicantState.FOUR_WAY_HANDSHAKE || WifiBroadcastReceiver.state == SupplicantState.ASSOCIATING || WifiBroadcastReceiver.state == SupplicantState.GROUP_HANDSHAKE) {
                                        SystemClock.sleep(20);
                                        if (WifiBroadcastReceiver.state == SupplicantState.DISCONNECTED) {//连接不成功状态直接退出循环
                                            break;
                                        }
                                        if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {//连接成功状态直接返回
                                            return "";
                                        }
                                    }
                                }
                                return ""
                            }

                            override fun onSuccess(result: String?) {
                                if (WifiBroadcastReceiver.state == SupplicantState.COMPLETED) {
                                    holder!!.itemView?.findViewById<TextView>(R.id.textView3)!!.setText("连接wifi成功")
                                }else{
                                    holder!!.itemView?.findViewById<TextView>(R.id.textView3)!!.setText("连接wifi失败")
                                }
                            }

                            override fun onCancel() {
                            }

                            override fun onFail(t: Throwable?) {
                            }
                        })
                    }
                })
            }

            override fun onCreateDefaultViewHolder(parent: ViewGroup?, type: Int): RecyclerView.ViewHolder {
                return MyHolder(LayoutInflater.from(parent!!.getContext()).inflate(R.layout.recycler_item, parent, false));
            }

            class MyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
            }
        }
    }

}
