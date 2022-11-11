package com.hogl.eregister.connect;


import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private WifiP2pManager.Channel channel;
    private MainActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi P2P is enabled
                Log.d("P2P", "Wifi p2p is enabled");
            } else {
                // Wi-Fi P2P is not enabled
                Log.d("P2P", "Wifi p2p is NOT enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
            if (manager != null) {
                Log.d("P2P", "Requesting peers!");
                //Request permissions if not granted
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("p2p","No Permissions");
                    String[] perms = {
                            "android.permission.ACCESS_FINE_LOCATION",
                    };
                    ActivityCompat.requestPermissions(activity, perms,99);

                    return;
                }
                manager.requestPeers(channel, activity.peerListListener);
            }
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if(manager != null){
                NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if(netInfo.isConnected()){
                    manager.requestConnectionInfo(channel,activity.connectionInfoListener);
                }
                else{
                    activity.connectionStatus.setText("Not connected");
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            Log.d("WIFI", "WIFI State changed!");
        }


    }
}
