package com.hogl.eregister.connect;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.SecretKey;

public class ServerClient extends Thread{

    String hostAddress;
    Boolean isServer;
    Socket socket;
    ServerSocket servSocket;
    String tag;
    //TextView messageText;
    public InputStream inStream;
    public OutputStream outStream;

    ArrayList<String> authStrings;

    Boolean secured;

    //Constructor for server
    public ServerClient(ArrayList<String> _authStrings){
        isServer = true;
        secured = true;
        authStrings = _authStrings;
        tag = "SERVCLIENT-HOST";

    }

    //Constructor for client
    public ServerClient(String hostAdd, ArrayList<String> _authStrings){
        isServer = false;
        hostAddress = hostAdd;
        socket = new Socket();
        secured = true;
        authStrings = _authStrings;
        tag = "SERVCLIENT-CLIENT";
    }

    public void setSecured(Boolean _secured, SecretKey _key){
        secured = true;
//        sharedKey = _key;
    }

    public interface messagesChangedListener {
//        public void onMessagesChangedListener();
    }

    private messagesChangedListener messagesChanged;

    public void setMessagesChangedListener(messagesChangedListener mcl){
        this.messagesChanged = mcl;
    }

    public void writeProtocol(byte[] bytes){
        if(outStream != null){
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try  {
                        outStream.write(bytes);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        else{
            Log.d("p2p", "Out stream is null");
        }
    }

    public void write(byte[] bytes){
        try {
            if(outStream != null){
                outStream.write(bytes);
            }
            else{
                Log.d("p2p", "outstream is null");
            }
        } catch (IOException e) {
            Log.d("WRITEERROR","FAILED to write");
        }
    }

    @Override
    public void run(){
        try {
            if(isServer){
                servSocket = new ServerSocket(8888);
                socket = servSocket.accept();
            } else{
                Log.d("p2p","Client trying to open socket");
                try {
                    Thread.sleep(600);

                    try {
                        socket.connect(new InetSocketAddress(hostAddress, 8888), 1500);
                    } catch (ConnectException e) {
                        Log.d("p2p", "Failed to open socket, trying again");
                        Log.d("p2p",e.toString());
                        Thread.sleep(700);
                        socket.connect(new InetSocketAddress(hostAddress, 8888), 1500);
                    }
                }catch (InterruptedException e){
                    Log.d("p2p", "Thread sleeps error");
                }


            }
            inStream = socket.getInputStream();
            outStream = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
//        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
//                byte[] buffer = new byte[4096];
                int bytes = 0;

                byte[] buffer = null;
                int numberofBytes = 0;
                int index = 0;
                boolean flag = true;

                while(socket != null){

                    if (flag) {
                        index = 0;
                        numberofBytes = 0;
                        buffer = null;
                        try {
                            byte[] temp = new byte[inStream.available()];
                            if (inStream.read(temp) > 0) {
                                String rec = new String(temp);
                                numberofBytes = Integer.parseInt(rec);
                                if(numberofBytes > 0){
                                    buffer = new byte[numberofBytes];
                                    flag = false;
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch(NumberFormatException e){
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            byte[] data = new byte[inStream.available()];
                            int numbers = inStream.read(data);

                            System.arraycopy(data, 0, buffer, index, numbers);
                            index += numbers;
                            if (index == numberofBytes) {
                                Log.e("%%%%%%", data.length + " " + numberofBytes);

                                File f1 = new File("data/data/com.hogl.eregister/databases/test2");
                                Boolean y = f1.exists();
                                FileOutputStream fos = null;
                                fos = new FileOutputStream("data/data/com.hogl.eregister/databases/test2");
                                fos.write(buffer);
                                fos.close();
                                flag = true;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }
        });
    }
}
