package com.example.finaltermclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class TrackerService extends Service {
    public TrackerService() {
    }

    //트래커 서버와 연결되는 소켓
    protected Socket socket;
    protected DataOutputStream output;
    protected DataInputStream input;

    //트래커 서버 IP와 이 클라이언트의 이름
    protected String sIP;
    protected String clientName;

   // protected RecvThread recvThread = new RecvThread();
    protected Thread recvThread = new Thread(new RecvThread());

    //세컨드액티비티에서 받아온 핸들러
    protected Handler handler;

    //서비스와 액티비티를 연결해주는 바인더
    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        TrackerService getService() {
            return TrackerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        Log.i("", "트래커 서비스 온바인드");
        return mBinder;
    }

    @Override
    public void onCreate() {
        Log.i("","트래커서비스 온크리에이트");
        super.onCreate();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("","트래커서비스 온언바인드");
        return super.onUnbind(intent);
    }

    public void onDestroy()
    {
        Log.i("","트래커서비스 온디스트로이");
        //트래커서비스가 종료되면 소켓을 닫는다.
        try {
            if( null != socket && socket.isConnected() )
            {
                socket.close();
            } //if
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }

    //트래커 서버에 접속하는 메소드
    public void networkStart(String recvIP, String name)
    {
        sIP = recvIP;
        clientName = name;

        //RecvThread recvThread = new RecvThread();
        recvThread.start();

    }

    //세컨드액티비티에서 리시브핸들러를 받아온다.
    public void setHandler(Handler handler)
    {
        this.handler = handler;
    }

    //트래커서버와 통신하는 쓰레드
    public class RecvThread	implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                //트래커 서버에 접속
                socket = new Socket(sIP, 8088);
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                output.writeUTF(clientName);

                handler.post(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(),"트래커 접속 완료", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.i("트래커서비스","포스트 러너블 끝나는지 확인");

                Message handleMsg;

                while( true )
                {
                    handleMsg = handler.obtainMessage();
                    handleMsg.what = 1;

                    String ss = input.readUTF();
                    handleMsg.obj = ss;
                    System.out.println(ss);
                    handler.sendMessage( handleMsg );
                    //break;
                } //while

            }
            catch( IOException e)
            {
                System.out.println("소켓 연결 실패");
                e.printStackTrace();
            } //try..catch..

        } //run
    } //CNetworkRecvThread

    public void writeUTF(String str)
    {
        try {
            System.out.println("11 " + str);
            output.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
