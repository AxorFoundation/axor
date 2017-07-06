package com.unite_investing.LoginRegister;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unite_investing.Depreciated.WalletScreen;
import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.Account;
import com.unite_investing.db.SqliteDB;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterPage extends AppCompatActivity {

    private SqliteDB db;
    EditText regName;
    EditText regBday;
    EditText regEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        db = SqliteDB.getInstance(this);

        regName = (EditText) findViewById(R.id.regName);
        regBday = (EditText) findViewById(R.id.regBday);
        regEmail = (EditText) findViewById(R.id.regEmail);



        //listens for if email is valid
        regEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String email = editable.toString();
                if (!db.validUser(email) && validate(email)) {
                    regEmail.setTextColor(Color.BLACK);
                } else {
                    regEmail.setTextColor(Color.RED);
                }
            }
        });

        //this ensures that the date enters is in the correct format by adding dashes
        //in the correct location
        regBday.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                try {
                    String str = charSequence.toString();
                    int length = charSequence.length();

                    //corrects date format when typing fwd and bkwd
                    if ((start == 1 && count == 1 || start == 4 && count == 1)) {
                        str = str + "-";
                        regBday.setText(str);
                    } else if ((start == 2 && count == 1 || start == 5 && count == 1)) {
                        str = str.substring(0, start) + "-" + str.substring(start);
                        regBday.setText(str);
                    } else if (before == 1 && start == 3 && length == 3) {
                        regBday.setText(str.subSequence(0, 2));
                    } else if (before == 1 && start == 6 && length == 6) {
                        regBday.setText(str.subSequence(0, 5));
                    }

                    String sb = regBday.getText().toString();

                    if (!dateFormat(sb)) { //corrects for when user inputs at select index
                        sb = sb.replace("-", "");
                        if (sb.length() > 2 && sb.charAt(2) != '-') {
                            sb = sb.subSequence(0, 2) + "-" + sb.substring(2);
                        }
                        if (sb.length() > 4 && sb.charAt(5) != '-') {
                            sb = sb.subSequence(0, 5) + "-" + sb.substring(5);
                        }
                        regBday.setText(sb);
                    }
                    regBday.setSelection(regBday.getText().length());
                } catch (StringIndexOutOfBoundsException e) {}

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

            private boolean dateFormat(String date) {
                for (int i = 0; i < date.length(); i++) {
                    if (date.length() > 2 && i == 2 && date.charAt(i) != '-') {
                        return false;
                    } else if (date.length() > 4 && i == 5 && date.charAt(i) != '-') {
                        return false;
                    } else if ((i != 2 || i != 5) && (date.charAt(i) <= 0 || date.charAt(i) <= 9)) {
                        return false;
                    }
                }
                return true;
            }
        });


    }


    //creates new account and goes to portfolio UI
    public void createAcct(View view) {
        String etName = regName.getText().toString();
        String etEmail = regEmail.getText().toString();
        String etBday = regBday.getText().toString();


        //verifies all registration fields are valid
        if (db.validUser(etEmail) || !validate(etEmail)) {
            Toast.makeText(this, "Email is already is use or not valid", Toast.LENGTH_SHORT).show();
        } else if (!valDate(etBday)) {
            Toast.makeText(this, "Birthday not in correct format", Toast.LENGTH_SHORT).show();
        } else { //creates a new acct
            Account acct = new Account(etName, etEmail, etBday);
            db.register(acct);

            Intent intent= new Intent(this,WalletScreen.class);
            intent.putExtra("logIn", true);
            intent.putExtra("email", etEmail);
            startActivity(intent);
        }
    }

    //goes back to login page
    public void backToLogin(View v) {
        Intent intent = new Intent(this, LoginRegistraion.class);
        startActivity(intent);
    }

    //verifies email pattern
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    private static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
        return matcher.find();
    }



    //returns true if date format is correct
    private boolean valDate(String date) {
        if (date.length() == 10 && ((date.charAt(2) == '-' && date.charAt(5) == '-'))) {
            //TODO valid range
            return true;
        }
        return false;
    }
}
