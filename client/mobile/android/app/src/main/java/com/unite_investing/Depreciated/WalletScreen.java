package com.unite_investing.Depreciated;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.SqliteDB;


public class WalletScreen extends AppCompatActivity {
//basically placeholder until we have some sort of server communication

    private SqliteDB db;
    private boolean loggedIn;
    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.unite_investing.aj.unite_investing.R.layout.activity_wallet_screen);

        db = SqliteDB.getInstance(this);
        loggedIn = getIntent().getExtras().getBoolean("logIn");
        if (loggedIn) {
            user = getIntent().getExtras().getString("email");
        }
        TextView ether = (TextView) findViewById(R.id.ether);
        String setText = "Total Ethers: " + Double.toString(viewEther());
        ether.setText(setText);


    }

    //onclick to how to deposit ether UI
    public void toEtherFAQ(View v){
        Intent intent = new Intent(this, EtherFAQ.class);
        intent.putExtra("logIn", loggedIn);
        startActivity(intent);
    }

    //on click goes to portfolio
    public void toPortfolio(View v){
        Intent intent = new Intent(this, Portfolio.class);
        startActivity(intent);
    }

    //view how much ethers you have
    private double viewEther() {
        double total = 0;
        if (loggedIn) {
            total = db.etherAmt(user);
        } else {
            try {
                FileInputStream fis = openFileInput("ether");
                ObjectInputStream ois = new ObjectInputStream(fis);
                total = ois.readDouble();
                ois.close();
            } catch (FileNotFoundException e) {
                return 0;
            } catch (IOException e) {
                return 0;
            }
        }
        return total;
    }
}
