package com.unite_investing.Depreciated;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.unite_investing.Depreciated.Portfolio;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Position;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class BuyPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView fixedPercent;
    private EditText dynamicPercent;
    private Position stock;
    private EditText stockInvestment;
    private double stockDouble;
    private int position;
    private double ethers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_page);
        //fixed percent is for buy orders, dynamic percent is for hold order.
        //visibility toggles depending on selection
        fixedPercent=(TextView)findViewById(R.id.fixedPercent);
        dynamicPercent=(EditText)findViewById(R.id.dynamicPercent);
        fixedPercent.setLayoutParams(dynamicPercent.getLayoutParams());
        dynamicPercent.setLayoutParams(dynamicPercent.getLayoutParams());

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.invest_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView stockPrice=(TextView)findViewById(R.id.stockInfo);
        Intent intent=this.getIntent();
        stock=(Position)intent.getParcelableArrayListExtra("Stock").get(0);
        stockPrice.setText(stock.getResource()+"-Market Price:"+ stock.getPrice());
        stockInvestment=(EditText)findViewById(R.id.editText);

        //gets the ether
        ethers = 0.0;
        getEthers();

    }

    //this is what makes either dynamic percent or fixed percent visible depending on selection
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        position=pos;
        if(pos==0||pos==1){
            fixedPercent.setVisibility(View.VISIBLE);
            dynamicPercent.setVisibility(View.INVISIBLE);
        } else if(pos==2||pos==3){
            fixedPercent.setVisibility(View.INVISIBLE);
            dynamicPercent.setVisibility(View.VISIBLE);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

    //this is where the stock is saved into the portfolio, via local storage. Will add server purchasing later
    public void makePurchase(View v){
        stockDouble = Double.parseDouble(stockInvestment.getText().toString());
        ArrayList<Position> stocks=new ArrayList<>();
        try { //takes the internal file and returns a list of purchased stocks
            FileInputStream fis = openFileInput("stocks");
            ObjectInputStream ois = new ObjectInputStream(fis);
            stocks=new ArrayList((List<Position>) ois.readObject());
            ois.close();
        } catch (FileNotFoundException e){

        }catch (IOException e){

        }catch (ClassNotFoundException e){

        }if(stockInvestment.getText().toString() == null || stockDouble <= 0.0) {

            Toast toast = Toast.makeText(this, "Please enter an investment amount", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else if (stockDouble > ethers) {
            Toast toast = Toast.makeText(this, "You don't have enough ethers", Toast.LENGTH_SHORT);
            toast.show();
            return;
        } else {
            stock.setInvestment(stockDouble);
            ethers -= stockDouble;
            setEthers();
        }
        stock.setType(position+1);
        if (!stocks.contains(stock)) { //checks for duplicates and adds on
            stocks.add(stock);
        } else {
            for (int i = 0; i < stocks.size(); i++) {
                if (stocks.get(i).equals(stock)) {
                    stock.setInvestment(stock.getInvestment() + stocks.get(i).getInvestment());
                    stocks.set(i, stock);
                }
            }
        }
        try { //saves the new list as internal file
            FileOutputStream fos = openFileOutput("stocks", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(stocks);
            oos.close();
        } catch (FileNotFoundException e){

        }catch (IOException e){

        }
        Toast toast =  Toast.makeText(this,"Purchase successful",Toast.LENGTH_SHORT);
        toast.show();
        Intent intent= new Intent(this,Portfolio.class);
        startActivity(intent);
    }

    private void getEthers() {
        //gets the total number of ethers
        try {
            FileInputStream fis = openFileInput("ether");
            ObjectInputStream ois = new ObjectInputStream(fis);
            ethers = ois.readDouble();

            ois.close();

        } catch (FileNotFoundException e){

        }catch (IOException e){

        }
    }

    private void setEthers() {

        try {

            FileOutputStream fos = openFileOutput("ether", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeDouble(ethers);
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
