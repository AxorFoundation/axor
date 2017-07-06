package com.unite_investing.Depreciated;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unite_investing.CustomOnClick;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
//Display of all owned stocks, no way to add to them  currently
public class Portfolio extends AppCompatActivity {

    private static final int NUMBEROFFIELDS=3;
    private ArrayList<Position> stocks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loads stock info from file
        try {
            FileInputStream fis = openFileInput("stocks");
            ObjectInputStream ois = new ObjectInputStream(fis);
            stocks=new ArrayList((List<Position>) ois.readObject());
            ois.close();
        } catch (FileNotFoundException e){

        }catch (IOException e){

        }catch (ClassNotFoundException e){

        }

        setContentView(com.unite_investing.aj.unite_investing.R.layout.activity_portfolio);
        Toolbar toolbar = (Toolbar) findViewById(com.unite_investing.aj.unite_investing.R.id.toolbar);
        setSupportActionBar(toolbar);
        LinearLayout scrollView=(LinearLayout)findViewById(R.id.scrollView);
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //Draws the dynamic scroll view
        if(stocks!=null) {
            int width = size.x / NUMBEROFFIELDS;
            for (int i = 0; i < stocks.size(); i++) {
                LinearLayout l = new LinearLayout(this);
                l.setOrientation(LinearLayout.HORIZONTAL);
                //makes different colored stocks depending on type of order
                
                // Should define an enum for the type, instead of using numbers.
                switch(stocks.get(i).getType()){
                    case 1: l.setBackgroundColor(Color.rgb(255,160,122));
                       break;
                    case 2: l.setBackgroundColor(Color.rgb(135,206,250));
                        break;
                    case 3: l.setBackgroundColor(Color.YELLOW);
                      break;
                    case 4: l.setBackgroundColor(Color.GRAY);
                      break;
                }
                for (int j = 0; j < NUMBEROFFIELDS; j++) {
                    if (j == 0) {
                        Button button = new Button(this);
                        button.setOnClickListener(new CustomOnClick(i, this));
                        button.setText(stocks.get(i).getResource());
                        // Should define a constant for the 250 and 300 numbers.
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(250, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(button, lp);
                    } else if (j == 1) {
                        TextView et = new TextView(this);
                        et.setText("$" + stocks.get(i).getPrice());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(300, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    } else if (j == 2) {
                        TextView et = new TextView(this);
                        et.setText("" + stocks.get(i).getInvestment());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    }
                }
                scrollView.addView(l);
            }
        }
    }

    //Takes you to portfolio
    public void buyStock(View view){
        Intent intent=new Intent(this,Market.class);
        intent.putParcelableArrayListExtra("stocks",stocks);
        startActivity(intent);
    }

    //Takes you to the stock view page
    public void viewStock(View view,int stockNumber){
        Intent intent=new Intent(this,StockDisplay.class);
        ArrayList<Position> toPass=new ArrayList<Position>();
        toPass.add(stocks.get(stockNumber));
        intent.putParcelableArrayListExtra("stock",toPass);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("stocks",stocks);
        super.onSaveInstanceState(bundle);

    }




}
