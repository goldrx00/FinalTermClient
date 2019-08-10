package com.example.finaltermclient;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import javax.crypto.Cipher;

public class ChattingActivity extends AppCompatActivity {

    //public static Activity chattingActivity;
    //채팅 서비스
    protected TrackerService mService;
    protected boolean mBound = false;
    //이 클라이언트의 이름
    protected String clientName;
    //액티비티로 정보를 넘겨주는 리시브 핸들러
    protected RecvHandler recvHandler = new RecvHandler();
    //protected SendAsyncTask sendAsyncTask = new SendAsyncTask();

    protected EditText chatEdit;
    protected ListView chatBoard;

    //채팅창을 나타낼 리스트뷰의 어댑터
    protected ArrayList<String> message;
    protected ArrayAdapter<String> arrayadapter;

    protected boolean isHost; //호스트인지 게스트인지
    protected String hIP; //호스트 IP

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_activity);

        //chattingActivity = ChattingActivity.this;
        //세컨드액티비티에서 인텐트 정보 받아옴
        Intent it = getIntent();
        clientName = it.getStringExtra("client_name");

        chatEdit = findViewById(R.id.chatEdit);
        chatBoard = findViewById(R.id.chatBoard);
        Button chatBtn = findViewById(R.id.chatBtn);

        message = new ArrayList<String>();
        arrayadapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item_custom, message);
        chatBoard.setAdapter(arrayadapter);

        //채팅서비스에 바인딩
        Intent intent = new Intent(ChattingActivity.this, TrackerService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        //채팅 메시지 전송 버튼
        chatBtn.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                final String data = chatEdit.getText().toString();

                if (!chatEdit.getText().toString().equals(""))
                {
                    try {
                        new SendAsyncTask().execute(data);

//                        new Thread(new Runnable() {
//                            public void run() {
//                                mService.writeUTF(data);
//                            }
//                        }).start();

                        arrayadapter.add(data);
                        chatBoard.setSelection(arrayadapter.getCount() - 1);
                        chatEdit.setText("");
                        System.out.println("채팅메시지 송신");
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }); //chatBtn

    } //onCreate()

    //채팅서비스에 바인드
    private ServiceConnection mConnection = new ServiceConnection() {
        // @Override //바인드되었을 때
        public void onServiceConnected(ComponentName className, IBinder service) {
            TrackerService.LocalBinder binder = (TrackerService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            mService.setHandler(recvHandler);
        }

        // @Override //언바인드 되었을 때
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    //	채팅서비스에서 데이터 받는 리시브 핸들러
    public class RecvHandler extends Handler
    {
        @Override
        public void handleMessage( Message msg )
        {
            switch(msg.what)
            {
                case 1:	//채팅 메시지
                    try {
                        String data = (String) msg.obj;
                        arrayadapter.add(data);
                        chatBoard.setSelection(arrayadapter.getCount() - 1);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

            } //switch

        } //handleMessage
    } //RecvHandler

    public class SendAsyncTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Integer doInBackground(String... params) {
            if(params != null){
                //for(String s : params){
                    mService.writeUTF(params[0]);
            }
           // mService.writeUTF(params);
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... params) {
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
        }
    }


    @Override
    protected void onDestroy()
    {
        System.out.println("채팅 액티비티 온디스트로이");
        unbindService(mConnection);
        super.onDestroy();
    } //onDestroy

    protected void onStop()
    {
        System.out.println("채팅액티비티 온스탑");
        super.onStop();
    }

    protected void onResume()
    {
        System.out.println("채팅액티비티 onResume");
        if(mBound) //채팅 서비스에 바인드되어있다면 리시브 핸들러를 넘겨준다.
            mService.setHandler(recvHandler);
        super.onResume();
    }

}
