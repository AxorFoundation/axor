package com.unite_investing.Depreciated;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unite_investing.CustomOnClick;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;


public class Market extends AppCompatActivity{
    private static final int NUMBEROFSTOCKS=50;
    private static final int NUMBEROFFIELDS=3;
    private int scrollID;
    private ArrayList<Position> stocks;
    private ArrayList<Position> market;
    private Context context;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(com.unite_investing.aj.unite_investing.R.layout.activity_stock_shop);

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.context=this;
        //necessary just so keyboard isnt always showing
        findViewById(R.id.marketBackToPortfolio).requestFocus();

        market=new ArrayList<Position>();
        Resources r = getResources();
        String[] marketNames = r.getStringArray(R.array.starter_stocks);
        for(String string: marketNames){
            market.add(new Position(string));
        }

        //set up autocomplete search bar
        AutoCompleteTextView autoview =(AutoCompleteTextView)findViewById(R.id.marketSearch);
        autoview.clearFocus();
        autoview = (AutoCompleteTextView) findViewById(R.id.marketSearch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.test_list_item, marketNames);
        autoview.setAdapter(adapter);


        //initialization
        setTitle("Market");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


    }

    @Override
    protected void onStart() {
        super.onStart();
        new YahooStockInfo().execute();
    }

    //button that takes you back to portfolio
    public void toPortfolio(View view){
        Intent intent=new Intent(this,Portfolio.class);
        startActivity(intent);
    }

    //Button that takes you to the view stock page, passing in the stock info
    //The stock info is in a size 1 arraylist because of an issue with the object
    //being both parcelable and serializable, which corresponds to two methods in
    //intent.addExtra(Object)
    //*does another api call*
    public void viewStock(View view,int stockNumber){
        viewStock(view,market.get(stockNumber));
    }


    //Creates intent to take you directly to stock view page
    private void viewStock(View view,final Position stockInfo){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try { //TODO this makes two separate calls, should just need one
                    Stock temp = YahooFinance.get(stockInfo.getResource());
                    stockInfo.setPrice(temp.getQuote(true).getPrice().doubleValue());
                    }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                Intent intent=new Intent(context,StockDisplay.class);
                ArrayList<Position> toPass=new ArrayList<Position>();
                toPass.add(stockInfo); //passes in array with one stock
                intent.putParcelableArrayListExtra("stock",toPass);
                startActivity(intent);
            }
        });
        thread.start();

    }


    //This is what refreshes the stock prices via the yahoo api.
    //Has to be done in seperate thread since its web based
    public void refreshStocks(View v){
        new YahooStockInfo().execute();
    }

    /*
    //Reloads the page once prices are updated
    //Must again be done on seperate thread since it is updating ui
    public void refreshPage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setStockPrices((Market) context, context);

            }
        });

    }
    */

    //This is used to search for specific stocks for example can type in msft to bring up microsoft
    public void search(View v){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextView textView = (TextView) findViewById(R.id.marketSearch);
                    final String search = textView.getText().toString();
                    if (YahooFinance.get(search).getName() == null) {
                        textView.clearFocus();
                        Toast toast = Toast.makeText(context, "Stock does not exist", Toast.LENGTH_SHORT);
                        toast.show();
                    }else { //does one call to yahoo api
                        viewStock(new View(context),new Position(YahooFinance.get(search)));
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /*
    //Method runs on a new thread notifying user that stock was not found
    public void makeToast() {
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(context, "Stock does not exist", Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }
    /*

    /*This inner class runs a background thread that loads the stock prices
    * when the Market UI is started
    */
    private class YahooStockInfo extends AsyncTask<Void, Void, Void> {

        //this method makes the loading circle visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout scrollView=(LinearLayout)findViewById(com.unite_investing.aj.unite_investing.R.id.scrollView);
            scrollView.removeAllViews();
            findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        }

        //gets stock from yahoo api
        @Override
        protected Void doInBackground(Void... voids) {
            String[] sendToAPI=new String[market.size()];
            try {
                for(int i = 0; i < market.size(); i++) {
                    sendToAPI[i]=market.get(i).getResource();
                }

                Map<String, Stock> fromAPI=YahooFinance.get(sendToAPI);

                for(Position stockInfo:market){
                    Stock temp = fromAPI.get(stockInfo.getResource());
                    try {
                        stockInfo.setPrice(temp.getQuote().getPrice().doubleValue());
                        stockInfo.setFullName(temp.getName());
                    } catch (NullPointerException e){
                        System.out.println(temp.getName());
                    }
                }

                //saves stock info as internal file
                FileOutputStream fos = openFileOutput("market", Context.MODE_PRIVATE);
                ObjectOutputStream oos = new ObjectOutputStream(fos);
                oos.writeObject(market);
                oos.close();
                //TODO need to keep trying if fail and time out

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            return null;
        }

        //removes loading circle
        @Override
        protected void onPostExecute(Void avoid) {
            super.onPostExecute(avoid);
            setStockPrices(Market.this, context);
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        }
    }

    //This method set the scroll view for all the stocks and provides prices and fees
    private void setStockPrices(Market tempMarket, Context tempContext) {
        LinearLayout scrollView=(LinearLayout)findViewById(com.unite_investing.aj.unite_investing.R.id.scrollView);
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        scrollID=scrollView.getId();
        if(market!=null) {
            int width = size.x / NUMBEROFFIELDS;
            for (int i = 0; i < market.size(); i++) {
                LinearLayout l = new LinearLayout(tempContext);
                l.setOrientation(LinearLayout.HORIZONTAL);
                for (int j = 0; j < NUMBEROFFIELDS; j++) {
                    if (j == 0) {
                        Button button = new Button(tempContext);
                        button.setOnClickListener(new CustomOnClick(i, tempMarket));
                        button.setText(market.get(i).getResource());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(button, lp);
                    } else if (j == 1) {
                        TextView et = new TextView(tempContext);
                        et.setText("$" + market.get(i).getPrice());
                        //LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        //ll.setMargins(0, 0, 0, 0);
                        //et.setLayoutParams(ll);
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    } else if (j == 2) {
                        TextView et = new TextView(tempContext);
                        et.setText("" + market.get(i).getInvestment());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    }
                }
                scrollView.addView(l);
            }
        }
    }
}
