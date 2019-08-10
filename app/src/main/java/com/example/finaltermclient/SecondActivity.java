package com.example.finaltermclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity {

    protected TrackerService trackerService;
    protected boolean mBound = false; //트래커 서비스와 바인드되었다면 true;

     //메인 액티비티에서 가져온 트래커 IP와 클라이언트 네임
    protected String trackerIP;
    protected String clientName;

    //트래커 서비스로 넘겨줄 리시브핸들러
    protected RecvHandler recvHandler = new RecvHandler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity);

        //메인 액티비티로부터 클라이언트 이름과 트래커의 IP를 받아온다.
        Intent it = getIntent();
        clientName = it.getStringExtra("client_name");
        trackerIP = it.getStringExtra("tracker_IP");


        //버튼 세팅
        Button refreshBtn = findViewById(R.id.refreshBtn);
        Button makingRoomBtn = findViewById(R.id.makingRoomBtn);

        //새로고침
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("서버에 채팅방리스트 요청");
            }
        });

        //호스트로 채팅방에 입장
        makingRoomBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //채팅 액티비티 시작
                Intent intent = new Intent(SecondActivity.this, ChattingActivity.class);
                //intent.putExtra( "HOST", true);
                intent.putExtra("client_name", clientName);
                startActivity(intent);
            }
        });

        //트래커서버 서비스와 바인딩
        Intent intent = new Intent(SecondActivity.this, TrackerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }


    //채팅 서비스에 바인드
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override //서비스에 바인드되었을 때 실행되는 메소드
        public void onServiceConnected(ComponentName className, IBinder service) {

            TrackerService.LocalBinder binder = (TrackerService.LocalBinder) service;
            trackerService = binder.getService();
            mBound = true;
            trackerService.networkStart(trackerIP,clientName);

            System.out.println("두번째 액티비티 바인딩 성공");
            trackerService.setHandler(recvHandler);

        }

        @Override //서비스에 언바인드되었을 때 실행되는 메소드
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    @Override
    protected void onDestroy()
    {
        System.out.println("세컨드액티비티 온디스트로이");
        unbindService(mConnection);
        super.onDestroy();
    } //onDestroy

    protected void onStop()
    {
        System.out.println("세컨드액티비티 onStop");
        super.onStop();
    }

    protected void onResume()
    {
        System.out.println("세컨드액티비티 onResume");
        super.onResume();
    }

    //서비스에서 데이터를 받아오는 리시브핸들러
    public class RecvHandler extends Handler
    {
        @Override
        public void handleMessage( Message msg )
        {
            switch(msg.what)
            {

            }

        } //handleMessage
    } //RecvHandler
}
