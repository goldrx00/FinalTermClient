package com.example.finaltermclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    protected EditText editIP;
    protected EditText editName;

    private BackPressCloseHandler backPressCloseHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backPressCloseHandler = new BackPressCloseHandler(this);

        editIP = findViewById( R.id.editIP );
        editName = findViewById( R.id.editName );

        //로그인 버튼 생성
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if(editName.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "이름을 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(editIP.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "서버의 IP주소를 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                //세컨드 액티비티 시작
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("client_name", editName.getText().toString());
                intent.putExtra("tracker_IP", editIP.getText().toString());
                startActivity(intent);

                Log.d("", "로그인버튼 클릭");
            }
        });	//loginBtn

    } //onCreate

    protected void onStop()
    {
        System.out.println("메인액티비티 온스탑");
        super.onStop();
    }

    protected void onDestroy()
    {
        System.out.println("메인액티비티 온스탑");
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        backPressCloseHandler.onBackPressed();
    }

    public class BackPressCloseHandler {

        private long backKeyPressedTime = 0;
        private Toast toast;

        private Activity activity;

        public BackPressCloseHandler(Activity context) {
            this.activity = context;
        }

        public void onBackPressed() {
            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                showGuide();
                return;
            }
            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
                activity.finish();
                toast.cancel();
            }
        }

        public void showGuide() {
            toast = Toast.makeText(activity,
                    "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
