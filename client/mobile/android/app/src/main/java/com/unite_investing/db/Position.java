package com.unite_investing.db;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import yahoofinance.Stock;

/**
 * Created by AJ on 7/30/2016.
 */
//Simple object used to store stock info in an object
    //Serializable lets it get saved to a file while parceble lets it get passed in an
    //array list between activities
    //this is where any information about stocks that we need saved should be stored.
    //if you want to add anything to it be sure to update the parcel in and write to parcel


public class Position implements Parcelable,Serializable {

    private String resource;
    private String fullName;
    private double price; //original price
    private double investment; //owned
    private int type;
    //1=long,2=short,3=long hold,4=short hold

    public Position(String res, String name, double amt, double investment, int pos){
        this.resource = res;
        this.fullName = name;
        this.price= amt;
        this.investment= investment;
        this.type = pos;
    }

    //pulls info from yahoo api
    public Position(Stock stock){
        this.resource =stock.getSymbol();
        this.fullName = stock.getName();
        this.price=stock.getQuote().getPrice().doubleValue();
        this.investment=0;
        this.type = 0;
    }

    public Position(String resource){
        this.resource = resource;
        this.fullName="";
        this.price=0;
        this.investment=0;
    }

    protected Position(Parcel in) {
        resource = in.readString();
        fullName = in.readString();
        price = in.readDouble();
        investment=in.readDouble();
    }

    /*getters and setters
    *
    */
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getInvestment() {
        return investment;
    }

    public void setInvestment(double investment) {
        this.investment = investment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    /*
    *
    */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position stockInfo = (Position) o;

        if (type != stockInfo.type) return false;
        return resource != null ? resource.equals(stockInfo.resource) : stockInfo.resource == null;

    }

    @Override
    public int hashCode() {
        int result = resource != null ? resource.hashCode() : 0;
        result = 31 * result + type;
        return result;
    }

    //Part of parcelable that needs to be updated if you add more fields
    @Override
    public int describeContents() {
        return 0;
    }

    //update fields here
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(resource);
        dest.writeString(fullName);
        dest.writeDouble(price);
        dest.writeDouble(investment);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };


}