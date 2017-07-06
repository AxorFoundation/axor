package com.unite_investing.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


/**
 * Created by peter on 11/4/16.
 */
public class SqliteDB extends SQLiteOpenHelper{


    //Database
    private static final Integer VERSION = 1;
    private static final String DBNAME = "EtherAccounts";
    private static Context dbContext;
    private static SqliteDB sInstance;

    //Table
    private static final String TABLE_ACCOUNTS = "accounts";
    private static final String TABLE_POSITIONS = "positions";

    //Accounts Column
    private static final String FULLNAME = "fullname";
    private static final String EMAIL = "email";
    private static final String ETHER = "ether";
    private static final String WALLET = "wallet";

    //POSITIONS Column
    private static final String RESOURCE = "resource";
    private static final String NAME = "name";
    private static final String POSITION = "position";
    private static final String AMOUNT = "amount";
    private static final String ORIGPRICE = "origprice";

    //Create table statement
    private static final String CREATE_ACCOUNT = "Create Table "
            + TABLE_ACCOUNTS + "(" + WALLET + " TEXT," + EMAIL + " TEXT NOT NULL,"
            + FULLNAME + " TEXT," + ETHER + " INTEGER)";

    private static final String CREATE_POSITIONS = "Create Table "
            + TABLE_POSITIONS + "(" + WALLET + " TEXT," + RESOURCE + " TEXT," + POSITION
            + " INTEGER," + NAME + " TEXT," + ORIGPRICE + " INTEGER," + AMOUNT + " INTEGER, PRIMARY"
            + " KEY(WALLET, RESOURCE, POSITION), FOREIGN KEY (WALLET) REFERENCES TABLE_ACCOUNTS(WALLET))";

    public SqliteDB(Context context) {
        super(context, DBNAME, null, VERSION);
        this.dbContext = context;
    }

    //override abstract methods
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_ACCOUNT);
        sqLiteDatabase.execSQL(CREATE_POSITIONS);
        sqLiteDatabase.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {}

    //reference database for all activities
    public static synchronized SqliteDB getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SqliteDB(context.getApplicationContext());
        }
        return sInstance;
    }

    //adds a new user account to table
    public boolean register(Account acct) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EMAIL, acct.getEmail());
        values.put(ETHER, acct.getEther());
        values.put(FULLNAME, acct.getFullName());
        values.put(WALLET, acct.getWallet());

        return db.insert(TABLE_ACCOUNTS, null, values) != -1;

    }

    //returns true if username is valid
    public boolean validUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {EMAIL};
        //query for email
        Cursor c = db.query(TABLE_ACCOUNTS, columns, EMAIL + " = '" +
                email + "'", null, null, null, null);
        int found = c.getCount();
        return found == 1;
    }

    //return ether value of parameter wallet
    public double etherAmt(String wallet) {
        double res = 0.0;
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {ETHER};
        //query for wallet
        Cursor c = db.query(TABLE_ACCOUNTS, columns, WALLET + " = '" +
                wallet + "'", null, null, null, null);
        if (c != null) {
            c.moveToFirst();
            res = c.getInt(c.getColumnIndex(ETHER));
            c.close();
        }

        return res;
    }

    //adds or subtracts ethers to certain wallet
    //returns false if results in a negative amount
    public boolean updateEther(String wallet, double amount) {
        if (wallet == null) {
            return false;
        }
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues cv = new ContentValues();

        double total = etherAmt(wallet)+amount;
        if (total < 0) {
            return false;
        }
        cv.put(ETHER, total);
        db.update(TABLE_ACCOUNTS, cv, WALLET+"="+wallet, null);
        return true;
    }

    //returns true if position was added to table
    public boolean takePosition(String wallet, String resource, String position, int amt) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(WALLET, wallet);
        cv.put(RESOURCE, resource);
        cv.put(POSITION, position);
        cv.put(AMOUNT, amt);

        return db.insert(TABLE_POSITIONS, null, cv) != -1;
    }

    //adds a new position to table
    public boolean takePosition(String wallet, Position position, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WALLET, wallet);
        values.put(RESOURCE, position.getResource());
        values.put(NAME, position.getFullName());
        values.put(POSITION, position.getType());
        values.put(AMOUNT, position.getInvestment());
        values.put(ORIGPRICE, position.getPrice());

        updateEther(wallet, -amount);
        return db.insert(TABLE_POSITIONS, null, values) != -1;

    }

    public boolean updatePositionAmount(String wallet, Position position, double amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        double total = currentPositionAmt(wallet, position)+amount;
        if (total < 0) {
            return false;
        }
        cv.put(AMOUNT, total);
        db.update(TABLE_POSITIONS, cv, WALLET + " = '" +
                wallet + "' AND " + RESOURCE + " = '" + position.getResource() + "' AND "
                + POSITION + " = '" + position.getType() + "'", null);
        updateEther(wallet, -amount);
        return true;
    }

    private double currentPositionAmt(String wallet, Position position) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {AMOUNT};
        //query for wallet
        Cursor c = db.query(TABLE_POSITIONS, columns, WALLET + " = '" +
                        wallet + "' AND " + RESOURCE + " = '" + position.getResource() + "' AND "
                        + POSITION + " = '" + position.getType() + "'",
                null, null, null, null);
        double found = 0.0;
        if (c != null) {
            if (c.moveToFirst()) {
                found = c.getDouble(c.getColumnIndex(AMOUNT));
            }
            c.close();
        }
        return found;
    }

    //returns a list of all positions taken by a wallet
    public ArrayList<Position> getAllPositions(String wallet) {
        ArrayList<Position> list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {RESOURCE, NAME, AMOUNT, ORIGPRICE, POSITION};

        //query for wallet
        Cursor c = db.query(TABLE_POSITIONS, columns, WALLET + " = '" +
                wallet + "'", null, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    String res = c.getString(c.getColumnIndex(RESOURCE));
                    String name = c.getString(c.getColumnIndex(NAME));
                    Double amt = c.getDouble(c.getColumnIndex(AMOUNT));
                    Double price = c.getDouble(c.getColumnIndex(ORIGPRICE));
                    Integer pos = c.getInt(c.getColumnIndex(POSITION));

                    Position stock = new Position(res, name, price, amt, pos);
                    list.add(stock);
                } while (c.moveToNext());
            }
            c.close();
        }

        return list;
    }


    //returns true if wallet with position is present
    public boolean isDuplicate(String wallet, Position position) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {WALLET};
        //query for wallet
        Cursor c = db.query(TABLE_POSITIONS, columns, WALLET + " = '" +
                wallet + "' AND " + RESOURCE + " = '" + position.getResource() + "' AND "
                + POSITION + " = '" + position.getType() + "'",
                null, null, null, null);
        int found = 0;
        if (c != null) {
            found = c.getCount();
            c.close();
        }
        return found > 0;
    }
}
