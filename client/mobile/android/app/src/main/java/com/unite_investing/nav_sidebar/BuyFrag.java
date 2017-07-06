package com.unite_investing.nav_sidebar;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;
import com.unite_investing.db.SqliteDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class BuyFrag extends Fragment implements AdapterView.OnItemSelectedListener {
    private TextView fixedPercent;
    private EditText dynamicPercent;
    private Position resource; //that user is taking
    private EditText amtEntered;
    private int position;
    private double ethers;
    private SqliteDB db;
    private String wallet;

    public BuyFrag() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buy, container, false);
        db = SqliteDB.getInstance(getActivity());
        wallet = getActivity().getIntent().getExtras().getString("demo");

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

        //fixedPercent=(TextView) view.findViewById(R.id.fixedPercent);
        dynamicPercent=(EditText) view.findViewById(R.id.dynamicPercent);
        //fixedPercent.setLayoutParams(dynamicPercent.getLayoutParams());
        dynamicPercent.setLayoutParams(dynamicPercent.getLayoutParams());

        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                R.array.invest_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //set up text views
        TextView stockPrice=(TextView)view.findViewById(R.id.stockInfo);
        resource = getArguments().getParcelable("res");
        stockPrice.setText(resource.getResource()+"-Market Price:"+ resource.getPrice());
        amtEntered =(EditText)view.findViewById(R.id.editText);

        //gets the ether
        ethers = getEthers();

        Button buy = (Button) view.findViewById(R.id.button6);
        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double cost = Double.parseDouble(amtEntered.getText().toString());

                if(amtEntered.getText().toString() == null || cost <= 0.0) {
                    Toast toast = Toast.makeText(getActivity(), "Please enter an investment amount",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else if (cost > ethers) {
                    Toast toast = Toast.makeText(getActivity(), "You don't have enough ethers",
                            Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                } else {
                    resource.setInvestment(cost);
                }
                resource.setType(position+1);
                if (db.isDuplicate(wallet, resource)) { //add to existing resource
                    db.updatePositionAmount(wallet, resource, cost);
                } else { //add new resource
                    db.takePosition(wallet, resource, cost);
                }
                Toast toast =  Toast.makeText(getActivity(),"Purchase successful",Toast.LENGTH_SHORT);
                toast.show();

                //pops backstack to market
                getFragmentManager().popBackStack("jump", 0);
                ((Navigation) getActivity()).getActBar().setDisplayHomeAsUpEnabled(false);
                ((Navigation)getActivity()).getToggle().setDrawerIndicatorEnabled(true);
            }
        });
        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        position=pos;
        if(pos==0||pos==1){
            //fixedPercent.setVisibility(View.VISIBLE);
            dynamicPercent.setVisibility(View.INVISIBLE);
        } else if(pos==2||pos==3){
            //fixedPercent.setVisibility(View.INVISIBLE);
            dynamicPercent.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    //gets user ethers from database table
    private double getEthers() {
        return db.etherAmt(wallet);
    }

    //updates input amount to database table
    private boolean setEthers(double amt) {
        return db.updateEther(wallet, amt);
    }

    //private method moved into oncreateview
    //purchases shares of resource and updates database tables
    private void makePurchase(View v) {
        double cost = Double.parseDouble(amtEntered.getText().toString());

        if(amtEntered.getText().toString() == null || cost <= 0.0) {
            Toast toast = Toast.makeText(this.getActivity(), "Please enter an investment amount",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (cost > ethers) {
            Toast toast = Toast.makeText(this.getActivity(), "You don't have enough ethers",
                    Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            resource.setInvestment(cost);
        }
        resource.setType(position+1);
        if (db.isDuplicate(wallet, resource)) {
            db.updatePositionAmount(wallet, resource, cost);
        } else {
            db.takePosition(wallet, resource, cost);
        }
        Toast toast =  Toast.makeText(this.getActivity(),"Purchase successful",Toast.LENGTH_SHORT);
        toast.show();
    }

    //pops back stack back to wallet UI after
    private void backCaretButton() {
        getFragmentManager().popBackStack();
    }
}
