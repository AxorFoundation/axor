package com.unite_investing.nav_sidebar;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;
import com.unite_investing.db.SqliteDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class StockViewFrag extends Fragment {

    private Position resource;
    private SqliteDB db;
    private String wallet;

    public StockViewFrag() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stock_view, container, false);
        db = SqliteDB.getInstance(getActivity());
        wallet = getActivity().getIntent().getExtras().getString("demo");
        resource = this.getArguments().getParcelable("res");
        //sets resource text views
        TextView stockName=(TextView) view.findViewById(R.id.stockname);
        stockName.setText(resource.getFullName());
        TextView stockPrice = (TextView) view.findViewById(R.id.stockprice);
        stockPrice.setText("Original Price: " + resource.getPrice());
        TextView amountOwned=(TextView) view.findViewById(R.id.amountOwned);
        amountOwned.setText("Current investment: " + resource.getInvestment());
        getActivity().setTitle(resource.getFullName());

        ActionBarDrawerToggle toggle = ((Navigation)getActivity()).getToggle();
        if (((Navigation) getActivity()).getActBar() != null) {
            ((Navigation) getActivity()).getActBar().setDisplayHomeAsUpEnabled(true);
        }
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backCaretButton();
            }
        });

        //set up button for taking a position
        Button toBuyPage = (Button) view.findViewById(R.id.button5);
        toBuyPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pass resource to next frag
                Bundle bundle = new Bundle();
                bundle.putParcelable("res", resource);
                BuyFrag buyFrag = new BuyFrag();
                buyFrag.setArguments(bundle);
                //load new frag
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fmTransaction = fm.beginTransaction();
                ((Navigation) getActivity()).getToggle().setDrawerIndicatorEnabled(false);
                fmTransaction.replace(R.id.content, buyFrag);
                fmTransaction.addToBackStack(null);
                fmTransaction.commit();
            }
        });
        return view;
    }

    //pops back stack back to wallet UI after
    private void backCaretButton() {
        getFragmentManager().popBackStack();
        ((Navigation) getActivity()).getActBar().setDisplayHomeAsUpEnabled(false);
        ((Navigation)getActivity()).getToggle().setDrawerIndicatorEnabled(true);
    }
}
