package com.example.pranshu.yummyrestaurant;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RegisterActivity extends AppCompatActivity {
    Button butOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        butOk = (Button) findViewById(R.id.buttonRegisterOk);
    }

    public void Ok(View v){
        Intent login = new Intent(this,LoginActivity.class);
        startActivity(login);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent login = new Intent(this,LoginActivity.class);
        startActivity(login);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
