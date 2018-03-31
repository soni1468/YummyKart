package com.example.pranshu.yummyrestaurant;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class HelplineActivity extends AppCompatActivity {
    Button email,num1,num2;
    Typeface font;
    TextView emailus,contactus,quote,copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helpline);

     /*   Toolbar toolBar = (Toolbar) findViewById(R.id.app_bar);
        toolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);   */


        email = (Button) findViewById(R.id.helplineEmail);
        num1 = (Button) findViewById(R.id.helplinePhone1);
        num2 = (Button) findViewById(R.id.helplinePhone2);
        emailus = (TextView) findViewById(R.id.helplineEmailUs);
        contactus = (TextView) findViewById(R.id.helplineContactUs);
        quote = (TextView) findViewById(R.id.helplineQuote);
        copyright = (TextView) findViewById(R.id.helplineCopyright);

        font = Typeface.createFromAsset(getAssets(),"fonts/trench.ttf");

        emailus.setTypeface(font);
        contactus.setTypeface(font);
        quote.setTypeface(font);
        email.setTypeface(font);
        num1.setTypeface(font);
        num2.setTypeface(font);
        copyright.setTypeface(font);

      /*  toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });   */
    }

    public void SendEmail(View v){
        Intent email = new Intent(Intent.ACTION_SEND);
        email.setData(Uri.parse("mailto:"));
        email.setType("message/rfc822");
        email.putExtra(Intent.EXTRA_EMAIL,new String[]{"yummykartindia@gmail.com"});
        startActivity(Intent.createChooser(email,"Choose an email client :"));
    }

    public void CallNum1(View v){
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:7504428223"));
        startActivity(call);
    }
    public void CallNum2(View v){
        Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:7064009917"));
        startActivity(call);
    }
}
