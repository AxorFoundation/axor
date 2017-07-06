package com.unite_investing.nav_sidebar;


import android.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.SqliteDB;

/**
 * A simple {@link Fragment} subclass.
 */
public class DepositEtherFrag extends Fragment {
    private SqliteDB db;
    private String wallet;
    EditText input;

    public DepositEtherFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frag_deposit_ether, container, false);

        //change nav button to back carrot and returns to wallet when pushed
        ActionBarDrawerToggle toggle = ((Navigation)getActivity()).getToggle();
        if (((Navigation) getActivity()).getActBar() != null) {
            ((Navigation) getActivity()).getActBar().setDisplayHomeAsUpEnabled(true);
        }

        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                returnToWallet();

            }
        });

        Button depositEther = (Button) view.findViewById(R.id.fragDeposit);
        input = (EditText) view.findViewById(R.id.etherInput);
        depositEther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double ether = Double.parseDouble(input.getText().toString());
                Toast toast;
                if (db.updateEther(wallet, ether)) {
                    toast = Toast.makeText(getActivity(), "New Amount Deposited!", Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    toast = Toast.makeText(getActivity(), "Could not deposit", Toast.LENGTH_SHORT);
                    toast.show();
                }
                returnToWallet();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //????
        setHasOptionsMenu(true);

        db = SqliteDB.getInstance(getActivity());
        wallet = getActivity().getIntent().getExtras().getString("demo");

    }

    //pops back stack back to wallet UI after
    private void returnToWallet() {
        getFragmentManager().popBackStack();
        ((Navigation) getActivity()).getActBar().setDisplayHomeAsUpEnabled(false);
        ((Navigation)getActivity()).getToggle().setDrawerIndicatorEnabled(true);
    }

    private void etherDeposit(View view) {
        EditText input = (EditText) view.findViewById(R.id.etherInput);
        double ether = Double.parseDouble(input.getText().toString());

        Toast toast;
        if (db.updateEther(wallet, ether)) {
            toast = Toast.makeText(getActivity(), "New Amount Deposited!", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            toast = Toast.makeText(getActivity(), "Could not deposit", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
