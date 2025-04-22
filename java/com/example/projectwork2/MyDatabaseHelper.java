package com.example.projectwork2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyDatabase.db";
    private static final int DATABASE_VERSION = 4;

    private static final String CREATE_USER_TABLE =
            "CREATE TABLE Users (" +
                    "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT NOT NULL, " +
                    "email TEXT UNIQUE," +
                    "phone INTEGER UNIQUE," +
                    "pass_hash TEXT NOT NULL," +
                    "upi_id TEXT NOT NULL,"+
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";
    private static final String CREATE_GROUP_TABLE =
            "CREATE TABLE Groups(" +
                    "group_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "g_name TEXT NOT NULL," +
                    "g_created_at DATETIME DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_GMEM_TABLE =
            "CREATE TABLE GMembers (" +
                    "group_id INTEGER NOT NULL, " +
                    "user_id INTEGER NOT NULL," +
                    "joined_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "PRIMARY KEY (group_id, user_id)," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (group_id) REFERENCES Groups(group_id) ON DELETE CASCADE);";

    private static final String CREATE_EXPENSE_TABLE =
            "CREATE TABLE Expenses (" +
                    "exp_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "group_id INTEGER NOT NULL, " +
                    "amount REAL," +
                    "exp_desc TEXT," +
                    "created_user_id INTEGER NOT NULL," +
                    "paid_by INTEGER NOT NULL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (group_id) REFERENCES Groups(group_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (paid_by) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (created_user_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_EXP_SHARE_TABLE =
            "CREATE TABLE ExpShare (" +
                    "share_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "exp_id INTEGER NOT NULL, " +
                    "payee_user_id INTEGER NOT NULL," +
                    "pay_amt REAL," +
                    "FOREIGN KEY (exp_id) REFERENCES Expenses(exp_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (payee_user_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_PERSONAL_TABLE =
            "CREATE TABLE ExpPer (" +
                    "p_exp_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "user_id INTEGER NOT NULL, " +
                    "amount REAL," +
                    "p_exp_desc TEXT," +
                    "cat TEXT," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_SETTLE_TABLE =
            "CREATE TABLE Settle (" +
                    "settle_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "payer_id INTEGER NOT NULL, " +
                    "payee_id INTEGER NOT NULL," +
                    "amt REAL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (payer_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (payee_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_OUTGRP_TABLE =
            "CREATE TABLE OutGroup (" +
                    "out_group_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "added_user_id INTEGER NOT NULL, " +
                    "o_payer_id INTEGER NOT NULL," +
                    "o_payee_id INTEGER NOT NULL," +
                    "o_amt REAL," +
                    "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                    "FOREIGN KEY (added_user_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (o_payer_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (o_payee_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_OWED_TABLE =
            "CREATE TABLE Owed (" +
                    "payee_id INTEGER NOT NULL, " +
                    "payer_id INTEGER NOT NULL," +
                    "owed_amt REAL," +
                    "FOREIGN KEY (payer_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (payee_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    private static final String CREATE_FRIENDS_TABLE =
            "CREATE TABLE Friends(" +
                    "user1_id INTEGER NOT NULL, " +
                    "user2_id INTEGER NOT NULL," +
                    "PRIMARY KEY(user1_id, user2_id)," +
                    "FOREIGN KEY (user1_id) REFERENCES Users(user_id) ON DELETE CASCADE," +
                    "FOREIGN KEY (user2_id) REFERENCES Users(user_id) ON DELETE CASCADE);";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_PERSONAL_TABLE);
        db.execSQL(CREATE_EXPENSE_TABLE);
        db.execSQL(CREATE_EXP_SHARE_TABLE);
        db.execSQL(CREATE_SETTLE_TABLE);
        db.execSQL(CREATE_GMEM_TABLE);
        db.execSQL(CREATE_GROUP_TABLE);
        db.execSQL(CREATE_OUTGRP_TABLE);
        db.execSQL(CREATE_OWED_TABLE);
        db.execSQL(CREATE_FRIENDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Users");
        db.execSQL("DROP TABLE IF EXISTS Groups");
        db.execSQL("DROP TABLE IF EXISTS GMembers");
        db.execSQL("DROP TABLE IF EXISTS Expenses");
        db.execSQL("DROP TABLE IF EXISTS ExpShare");
        db.execSQL("DROP TABLE IF EXISTS ExpPer");
        db.execSQL("DROP TABLE IF EXISTS Settle");
        db.execSQL("DROP TABLE IF EXISTS OutGroup");
        db.execSQL("DROP TABLE IF EXISTS Owed");
        db.execSQL("DROP TABLE IF EXISTS Friends");
        onCreate(db);
    }

}

