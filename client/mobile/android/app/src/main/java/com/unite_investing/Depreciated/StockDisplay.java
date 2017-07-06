package com.unite_investing.Depreciated;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;

import java.util.ArrayList;

//Stock display is currently a page that shows nothing, but has all the info it needs
//WIP
public class StockDisplay extends AppCompatActivity {
    private Position stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.unite_investing.aj.unite_investing.R.layout.activity_stock_display);
        stock =(Position)getIntent().getParcelableArrayListExtra("stock").get(0);

        //sets stock text views
        TextView stockName=(TextView)findViewById(R.id.stockname);
        stockName.setText(stock.getResource());
        TextView stockPrice = (TextView) findViewById(R.id.stockprice);
        stockPrice.setText("Current Price: " + stock.getPrice());
        TextView amountOwned=(TextView)findViewById(R.id.amountOwned);
        amountOwned.setText("Current investment "+stock.getInvestment());
        setTitle(stock.getFullName());
    }
    //on click goes back to portfolio
    public void toPortfolio(View view){
        Intent intent=new Intent(this, Portfolio.class);
        startActivity(intent);
    }
    //on click goes to the BuyPage
    public void toBuy(View v){
        Intent intent= new Intent(this,BuyPage.class);
        ArrayList<Position> toPass= new ArrayList<Position>();
        toPass.add(stock);
        intent.putParcelableArrayListExtra("Stock",toPass);
        startActivity(intent);
    }

}
