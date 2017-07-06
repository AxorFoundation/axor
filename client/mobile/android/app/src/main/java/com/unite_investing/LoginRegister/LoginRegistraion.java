// Yeah -- let's not password protect the app yet.
// In the long run the security needs to be tied to the
// private key and the private key recovery process.
// For now it's just a barrier, so let's not have this.

package com.unite_investing.LoginRegister;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.unite_investing.aj.unite_investing.R;
import com.unite_investing.db.SqliteDB;

public class LoginRegistraion extends AppCompatActivity {

    private SqliteDB db;
    EditText user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_registraion);

        db = SqliteDB.getInstance(this);

        user = (EditText) findViewById(R.id.logUsername);


    }

    //logins user in with valid email and password
    public void loginToAcct(View v) {

        String username = user.getText().toString();


        if (!db.validUser(username)) {
            Toast toast = Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    //goes to register page UI
    public void toRegisterPage(View v) {
        Intent intent = new Intent(this, RegisterPage.class);
        startActivity(intent);
    }

}
