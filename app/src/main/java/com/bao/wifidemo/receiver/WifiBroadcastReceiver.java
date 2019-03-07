package com.bao.wifidemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;

import com.bao.wifidemo.R;
import com.bao.wifidemo.utils.WifiControlUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Created by bao on 2018/3/21.
 * wifi状态广播
 */
public class WifiBroadcastReceiver extends BroadcastReceiver
{
    private WifiControlUtils wifiControlUtils;
public static SupplicantState state =   SupplicantState.DISCONNECTED;
    @Override
    public void onReceive(Context context, Intent intent)
    {
        wifiControlUtils = new WifiControlUtils(context);

        //wifi正在改变状态
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            //获取wifi状态
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLING);
            switch (wifiState)
            {
                case WifiManager.WIFI_STATE_DISABLED:
                    //wifi已经关闭
                    LogUtils.d(context.getString(R.string.wifi_state_disabled));
                    ToastUtils.showShort(context.getString(R.string.wifi_state_disabled));
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    //wifi正在关闭
                    LogUtils.d(context.getString(R.string.wifi_state_disabling));
                    ToastUtils.showShort(context.getString(R.string.wifi_state_disabling));
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    //wifi已经开启
                    LogUtils.d(context.getString(R.string.wifi_state_enabled));
                    ToastUtils.showShort(context.getString(R.string.wifi_state_enabled));
                    break;
                case WifiManager.WIFI_STATE_ENABLING:
                    //wifi正在开启
                    LogUtils.d(context.getString(R.string.wifi_state_enabling));
                    ToastUtils.showShort(context.getString(R.string.wifi_state_enabling));
                    break;
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            //网络状态改变
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (NetworkInfo.State.DISCONNECTED.equals(info.getState()))
            {
                //wifi网络连接断开
            } else if (NetworkInfo.State.CONNECTED.equals(info.getState()))
            {
                //获取当前网络，wifi名称
                ToastUtils.showLong(context.getString(R.string.wifi_connected, wifiControlUtils.getWifiInfo().getSSID()));
            }
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(intent.getAction()))
        {
            SupplicantState netNewState = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            state = netNewState;
            if (SupplicantState.FOUR_WAY_HANDSHAKE==netNewState){
                //重新连接wifi,如果密码正确的话,会依次接受以下状态
                // ASSOCIATING正在连接 ASSOCIATED交互  FOUR_WAY_HANDSHAKE四次握手  GROUP_HANDSHAKE组握手 COMPLETED完成
                //如果密码不正确的话一般会经过ASSOCIATING ASSOCIATED FOUR_WAY_HANDSHAKE DISCONNECTED
            }
            //错误码
            int netConnectErrorCode = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, WifiManager.ERROR_AUTHENTICATING);
            LogUtils.i(netNewState+" wifi 密码错误:"+netConnectErrorCode);
        }
    }
}
