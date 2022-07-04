package com.hogl.eregister.connect;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hogl.eregister.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;

public class ChatActivity extends AppCompatActivity {

    TextView statusText, protocolText;
    RecyclerView messagesView;
    Button sendButton;
    ServerClient serverClient;
    LinearLayout messageLayout;
    Button disconnectButton;
    String tag;


    Boolean isHost;
    String hostAddress;

    Activity thisAct;
    Context thisContext;

    String deviceName;
    ArrayList<String> authStrings;
    Integer authStep;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Intent intent = getIntent();
        isHost = intent.getBooleanExtra("isHost", false);
        hostAddress = intent.getStringExtra("hostAddress");
        thisAct = this;
        thisContext = getApplicationContext();

        initComponents();
        setUpServer();
        if (isHost) tag = "CHAT-HOST";
        else tag = "CHAT-CLIENT";


    }

    public interface finishedInterface {
        String completed(SecretKey k);
    }

    public finishedInterface myInterface = (k) -> {

        Log.d("Auth", "AUTH SERVICE DONE!");


        serverClient.setMessagesChangedListener(new ServerClient.messagesChangedListener() {
//            @Override
//            public void onMessagesChangedListener() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        adapter.notifyDataSetChanged();
//                        messagesView.smoothScrollToPosition(adapter.getItemCount());
//                    }
//                });
//            }
        });
        serverClient.setSecured(true, null);
        sendButton.setEnabled(true);
        protocolText.setText("SECURED");
        protocolText.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        return "Done";
    };


    private void setUpServer() {
        //Phone is host
        if (isHost) {
            statusText.setText("Host");
            serverClient = new ServerClient(authStrings);
        }
        //Phone is client
        else {
            statusText.setText("Client");
            serverClient = new ServerClient(hostAddress, authStrings);
        }


        serverClient.start();

        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                disconnect();
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ExecutorService executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        if (true) {
//                            String escapedMessage = messageToSend.replace("\'", "\\'");

                            try {

                                InputStream is = null;

                                File fi = new File("data/data/"+thisContext.getPackageName()+ "/databases/eRegister_database");
                                if(fi.exists()) {
                                    Log.e("&&&&&", "File exists");
                                }
                                is = new FileInputStream(fi);
                                ContentResolver cr = thisContext.getContentResolver();
//                                InputStream inputStream = cr.openInputStream(Uri.parse("data/data/"+thisContext.getPackageName()+ "/databases/eRegister_database"));
//                                is = getAssets().open("transfer_database");
                                byte[] fileBytes = new byte[is.available()];
                                is.read(fileBytes);
                                is.close();

                                int offset = 0;
                                int AttributeDataLen = 244;
//
//                                String encryptedMessage = protocolUtils.encrypt((String.valueOf(fileBytes.length).getBytes(StandardCharsets.UTF_8)), sharedKey);
                                Log.d(tag + "size of byte array : ", String.valueOf(fileBytes.length));
                                String testLength = fileBytes.length+"";
                                byte[] siz = (testLength).getBytes(StandardCharsets.UTF_8);
                                serverClient.write(siz);


                                while (offset < fileBytes.length) {
                                    int size = fileBytes.length - offset;
                                    if (size > AttributeDataLen) {
                                        size = AttributeDataLen;
                                    }
                                    byte[] data = new byte[size];
                                    System.arraycopy(fileBytes, offset, data, 0, size);
                                    offset += size;

//                                    encryptedMessage = protocolUtils.encrypt(data, sharedKey);
                                    Log.d(tag + "-SENT", data.toString());
                                    serverClient.write(data);


                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });

                //Hides the keyboard
//                InputMethodManager imm = (InputMethodManager) thisAct.getSystemService(Activity.INPUT_METHOD_SERVICE);
//                View v = thisAct.getCurrentFocus();
//                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

            }
        });


    }

    private void initComponents() {
        statusText = findViewById(R.id.status_text);
        protocolText = findViewById(R.id.protocol_text);
        messagesView = findViewById(R.id.messages_view);
        sendButton = findViewById(R.id.send_button);
        sendButton.setEnabled(true);
        messageLayout = findViewById(R.id.linearLayoutMessages);
        disconnectButton = findViewById(R.id.disconnect_button);

        messageLayout.setVisibility(View.VISIBLE);

        deviceName = Settings.Global.getString(getContentResolver(), "device_name");
        authStrings = new ArrayList<String>();
        authStep = 0;


    }

    public static void triggerRebirth(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }

        Runtime.getRuntime().exit(0);
    }


    private void disconnect() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                WifiP2pManager manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
                WifiP2pManager.Channel channel = manager.initialize(thisContext, getMainLooper(), null);


                if (channel != null) {

                    if (ActivityCompat.checkSelfPermission(thisContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        //permission check
                    }
                    manager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                        @Override
                        public void onGroupInfoAvailable(WifiP2pGroup group) {
                            if (group != null) {
                                manager.removeGroup(channel, new WifiP2pManager.ActionListener() {

                                    @Override
                                    public void onSuccess() {
                                        Log.d("p2p-disconnect", "removeGroup onSuccess -");
                                        triggerRebirth(thisContext);
                                    }

                                    @Override
                                    public void onFailure(int reason) {
                                        Log.d("p2p-disconnect", "removeGroup onFailure -" + reason);
                                        triggerRebirth(thisContext);
                                    }
                                });
                            } else {
                                triggerRebirth(thisContext);
                            }
                        }
                    });
                } else {
                    Log.d("DISCONNECT", "channel is null");
                }
            }
        });

    }
}