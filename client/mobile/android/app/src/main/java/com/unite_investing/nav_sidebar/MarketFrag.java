package com.unite_investing.nav_sidebar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unite_investing.CustomOnClick;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Map;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;

/**
 * Created by peter on 12/8/16.
 */
public class MarketFrag extends Fragment {

    private static final int NUMBEROFFIELDS=3;
    private int scrollID;
    private ArrayList<Position> market;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_market, container, false);
        Navigation nav = (Navigation) getActivity();
        ActionBarDrawerToggle toggle = nav.getToggle();

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        this.context=this.getActivity();
        //necessary just so keyboard isnt always showing

        market=new ArrayList<Position>();
        Resources r = getResources();
        String[] marketNames = r.getStringArray(R.array.starter_stocks);
        for(String string: marketNames){
            market.add(new Position(string));
        }

        //set up autocomplete search bar
        AutoCompleteTextView autoview =(AutoCompleteTextView) getActivity().findViewById(R.id.marketSearch);
        autoview.clearFocus();
        autoview = (AutoCompleteTextView) getActivity().findViewById(R.id.marketSearch);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.test_list_item, marketNames);
        autoview.setAdapter(adapter);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new YahooStockInfo().execute();
            }
        });

    }


    //Button that takes you to the view stock page
    public void viewStock(View view,int stockNumber){
        viewStock(view,market.get(stockNumber));
    }


    //Creates intent to take you directly to stock view page
    private void viewStock(View view,final Position stockInfo){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try { //TODO this makes two separate calls, should just need one-> pass in string
                    Stock temp = YahooFinance.get(stockInfo.getResource());
                    stockInfo.setPrice(temp.getQuote(true).getPrice().doubleValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //pass resource to next frag
                Position resource = stockInfo;
                Bundle bundle = new Bundle();
                bundle.putParcelable("res", resource);
                StockViewFrag stockFrag = new StockViewFrag();
                stockFrag.setArguments(bundle);
                //load new frag
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fmTransaction = fm.beginTransaction();
                ((Navigation) getActivity()).getToggle().setDrawerIndicatorEnabled(false);
                fmTransaction.replace(R.id.content, stockFrag);
                fmTransaction.addToBackStack(null);
                fmTransaction.commit();
            }
        });
    }


    //This is what refreshes the stock prices via the yahoo api.
    //Has to be done in seperate thread since its web based
    public void refreshStocks(View v){
        new YahooStockInfo().execute();
    }

    /*
    //This is used to search for specific stocks for example can type in msft to bring up microsoft
    public void search(View v){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TextView textView = (TextView) getActivity().findViewById(R.id.marketSearch);
                    final String search = textView.getText().toString();
                    if (YahooFinance.get(search).getName() == null) {
                        textView.clearFocus();
                        Toast toast = Toast.makeText(context, "Resource does not exist", Toast.LENGTH_SHORT);
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
    */

    /*This inner class runs a background thread that loads the stock prices
    * when the Market UI is started
    * TODO may need to reconsider using a intentservice
    */
    private class YahooStockInfo extends AsyncTask<Void, Void, Void> {
        //this method makes the loading circle visible
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LinearLayout scrollView=(LinearLayout) getActivity().findViewById(com.unite_investing.aj.unite_investing.R.id.scrollView);
            scrollView.removeAllViews();
            getActivity().findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
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
                FileOutputStream fos = getActivity().openFileOutput("market", Context.MODE_PRIVATE);
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

            setStockPrices(MarketFrag.this, MarketFrag.this.context);
            getActivity().findViewById(R.id.loadingPanel).setVisibility(View.GONE);

        }
    }

    //This method set the scroll view for all the stocks and provides prices and fees
    private void setStockPrices(MarketFrag tempMarket, Context tempContext) {
        LinearLayout scrollView=(LinearLayout) getActivity().findViewById(com.unite_investing.aj.unite_investing.R.id.scrollView);
        Display display = ((WindowManager) getActivity().getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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
