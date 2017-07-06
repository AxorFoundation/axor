package com.unite_investing.Depreciated;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.unite_investing.aj.unite_investing.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * Created by peter on 10/20/16.
 */
public class EtherFAQ extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ether_faq);

    }

    //on click goes to back to wallet
    public void toWallet(View v){
        Intent intent = new Intent(this, WalletScreen.class);
        intent.putExtra("logIn", getIntent().getExtras().getBoolean("logIn"));
        startActivity(intent);
    }

    //deposit ethers
    public void deposit(View view) {
        boolean log = getIntent().getExtras().getBoolean("logIn");

        EditText input = (EditText) findViewById(R.id.inputAmt);
        double ether = Double.parseDouble(input.getText().toString());
        if (log) {
            //insert into db
        } else {
            try {
                FileOutputStream fos = openFileOutput("ether", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeDouble(ether);
                oos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (ether >= 0) {
            Intent intent = new Intent(this, WalletScreen.class);
            intent.putExtra("logIn", log);
            startActivity(intent);
        }



    }
}
