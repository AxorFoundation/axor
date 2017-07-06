package com.unite_investing.db;

/**
 * Created by peter on 11/4/16.
 */

/**
 * Let's not have the user fill out this kind of form. 
 * The app functoins as an Ethereum wallet. Their private key
 * is their identity. 
 * One of the advantages of our app is that you can 
 * buy axors anonymously.
 *
 * We obviously need to store how much Ether they have
 * and which axors they've bought though...
 * 
 * Let's not do a password for now. We can add some sort
 * of PIN and PIN recovery method later when we have
 * the actual Ethereum wallet functionality in place.
**/
public class Account {

    private String fullName;
    private String email;
    private int ether;
    private String wallet;

    //Constructors
    public Account() {

    }

    public Account(String fullName, String email, String wallet) {
        this.fullName = fullName;
        this.email = email;
        this.ether = 0;
        this.wallet = wallet;
    }

    //Getter and Setters

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getEther() {
        return ether;
    }

    public void setEther(int ether) {
        this.ether = ether;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }


}
