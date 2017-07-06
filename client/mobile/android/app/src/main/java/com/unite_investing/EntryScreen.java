package com.unite_investing;
/*Very simple entry screen with a single button to advance to portfolio */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.unite_investing.nav_sidebar.Navigation;

public class EntryScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.unite_investing.aj.unite_investing.R.layout.activity_entry_screen);


        //reset
        //this.deleteDatabase("EtherAccounts");
        //SqliteDB db = new SqliteDB(this);
        //Account demo = new Account("demo", "", "wallet");
        ///db.register(demo);
        //------------------------------
    }

    //log in as demo mode
    public void demoMode(View view){
        Intent intent = new Intent(this, Navigation.class);
        intent.putExtra("demo", "wallet");
        startActivity(intent);
    }
}
