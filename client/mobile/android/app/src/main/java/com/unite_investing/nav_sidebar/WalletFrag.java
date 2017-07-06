package com.unite_investing.nav_sidebar;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.SqliteDB;

/**
 * Created by peter on 12/8/16.
 */
public class WalletFrag extends Fragment {

    private SqliteDB db;
    private String wallet;
    private View resumeView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wallet, container, false);

        this.db = SqliteDB.getInstance(getActivity());
        this.wallet = getActivity().getIntent().getExtras().getString("demo");
        this.resumeView = view;
        //sets the total ethers owned to view
        setEtherView(view);

        //button to go to deposit frag UI
        Button deposit = (Button) view.findViewById(R.id.b_walletToDeposit);
        deposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction fmTransaction = fm.beginTransaction();
                ((Navigation)getActivity()).getToggle().setDrawerIndicatorEnabled(false);
                fmTransaction.replace(R.id.content, new DepositEtherFrag());
                fmTransaction.addToBackStack(null);
                fmTransaction.commit();
            }
        });
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //sets the total ethers owned to view
        setEtherView(resumeView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    //view how much ethers you have
    private double viewEther() {
        return db.etherAmt(wallet);
    }

    private void setEtherView(View view) {
        //sets the total ethers owned to view
        TextView ether = (TextView) view.findViewById(R.id.curEther);
        String setText = "Total Ethers: " + Double.toString(viewEther());
        ether.setText(setText);
    }

}
