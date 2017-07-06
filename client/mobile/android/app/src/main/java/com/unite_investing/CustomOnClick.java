package com.unite_investing;

import android.view.View;


import com.unite_investing.Depreciated.Market;
import com.unite_investing.Depreciated.Portfolio;
import com.unite_investing.nav_sidebar.PortfolioFrag;
import com.unite_investing.nav_sidebar.MarketFrag;

/**
 * Created by AJ on 7/30/2016.
 */
//this is used to pass activities into an on click method. used to view a stock

public class CustomOnClick implements View.OnClickListener
{
    int stockNumber;
    Market market;
    Portfolio portfolio;
    PortfolioFrag navigation;
    MarketFrag marketFrag;
    public CustomOnClick(int stockNumber,Portfolio activity) {
        this.stockNumber = stockNumber;
        this.portfolio=activity;
    }
    public CustomOnClick(int stockNumber,Market activity) {
        this.stockNumber = stockNumber;
        this.market=activity;
    }
    public CustomOnClick(int stockNumber,PortfolioFrag activity) {
        this.stockNumber = stockNumber;
        this.navigation=activity;
    }
    public CustomOnClick(int stockNumber,MarketFrag activity) {
        this.stockNumber = stockNumber;
        this.marketFrag =activity;
    }

    @Override
    public void onClick(View v)
    {
        if(portfolio!=null) {
            portfolio.viewStock(v, stockNumber);
        }else if (marketFrag != null) {
            marketFrag.viewStock(v, stockNumber);
        } else if (navigation != null) {
            navigation.viewStock(v, stockNumber);
        }else{
            market.viewStock(v,stockNumber);
        }

    }

};
