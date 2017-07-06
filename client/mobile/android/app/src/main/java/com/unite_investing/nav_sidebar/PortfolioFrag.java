package com.unite_investing.nav_sidebar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unite_investing.CustomOnClick;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;
import com.unite_investing.db.SqliteDB;

import java.util.ArrayList;

/**
 * Created by peter on 12/8/16.
 */
public class PortfolioFrag extends Fragment {

    private static final int NUMBEROFFIELDS=3;
    private ArrayList<Position> stocks;
    private SqliteDB db;
    private String wallet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frag_portfolio, container, false);



        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //TODO should be in on createview????
        //loads stock info from db------------
        db = SqliteDB.getInstance(getActivity());
        wallet = getActivity().getIntent().getExtras().getString("demo");
        stocks = db.getAllPositions(wallet);
        //---------------------------------------

        LinearLayout scrollView=(LinearLayout) getActivity().findViewById(R.id.scrollView);
        Display display = ((WindowManager) getActivity().getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //Draws the dynamic scroll view
        if(stocks!=null) {
            int width = size.x / NUMBEROFFIELDS;
            for (int i = 0; i < stocks.size(); i++) {
                LinearLayout l = new LinearLayout(this.getActivity());
                l.setOrientation(LinearLayout.HORIZONTAL);
                //makes different colored stocks depending on type of order
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
                        Button button = new Button(this.getActivity());
                        button.setOnClickListener(new CustomOnClick(i, this));
                        button.setText(stocks.get(i).getResource());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(button, lp);
                    } else if (j == 1) {
                        TextView et = new TextView(this.getActivity());
                        et.setText("$" + stocks.get(i).getPrice());
                        et.setGravity(Gravity.CENTER);
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    } else if (j == 2) {
                        TextView et = new TextView(this.getActivity());
                        et.setText("" + stocks.get(i).getInvestment());
                        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(width, AbsListView.LayoutParams.WRAP_CONTENT);
                        l.addView(et, lp);
                    }
                }
                scrollView.addView(l);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //Takes you to the stock view page of selected stock
    public void viewStock(View view, int stockNumber){
        //pass resource to next frag
        Position resource = stocks.get(stockNumber);
        Bundle bundle = new Bundle();
        bundle.putParcelable("res", resource);
        StockViewFrag stockFrag = new StockViewFrag();
        stockFrag.setArguments(bundle);
        //load new frag
        FragmentManager fm = getFragmentManager();
        FragmentTransaction fmTransaction = fm.beginTransaction();
        ((Navigation)getActivity()).getToggle().setDrawerIndicatorEnabled(false);
        fmTransaction.replace(R.id.content, stockFrag);
        fmTransaction.addToBackStack(null);
        fmTransaction.commit();
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        bundle.putParcelableArrayList("stocks",stocks);
        super.onSaveInstanceState(bundle);
    }

}
