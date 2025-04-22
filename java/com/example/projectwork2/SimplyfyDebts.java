package com.example.projectwork2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimplyfyDebts extends AppCompatActivity {

    ListView debtList;
    Button example;
    Button create;
    List<String> simplifiedTransactions;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_simplyfy_debts);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        debtList = findViewById(R.id.debtlist);
        example = findViewById(R.id.example);
//        create = findViewById(R.id.create);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        //simplifiedTransactions.add("Hello");




//        example.setText("transactions = [\n" +
//                "        ('A', 'B', 111),\n" +
//                "        ('B', 'A', 25),\n" +
//                "        ('C', 'A', 421),\n" +
//                "        ('B', 'C', 6)\n" +
//                "        ]");



        example.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                simplifiedTransactions = simplifyDebts();
                adapter = new ArrayAdapter<>(SimplyfyDebts.this, android.R.layout.simple_list_item_1, simplifiedTransactions);
                debtList.setAdapter(adapter);
                //adapter.notifyDataSetChanged();
            }
        });
//        create.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                db.execSQL("DROP TABLE IF EXISTS Money");
//                db.execSQL("CREATE TABLE IF NOT EXISTS Money (paid_by INTEGER, payee_user_id INTEGER, pay_amt REAL)");
//                db.execSQL("INSERT INTO Money VALUES (1, 2, 111)");
//                db.execSQL("INSERT INTO Money VALUES (2, 1, 25)");
//                db.execSQL("INSERT INTO Money VALUES (3, 1, 421)");
//                db.execSQL("INSERT INTO Money VALUES (2, 3, 6)");
//            }
//        });
        //debtList.setAdapter(adapter);




        /*
        # Example Usage
        transactions = [
        ('A', 'B', 100),
        ('B', 'A', 20),
        ('C', 'A', 40),
        ('B', 'C', 60)
        ]
         */





    }

    public List<String> simplifyDebts() {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<Integer, Double> netBalance = new HashMap<>();

        // Query to retrieve debts from the database
        String query = "SELECT paid_by, payee_user_id, pay_amt " +
                "FROM Money";

        query = "SELECT payer_id, payee_id, owed_amt " +
                "FROM Owed";



        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int payer = cursor.getInt(0);  // Who paid
                int payee = cursor.getInt(1);  // Who owes
                double amount = cursor.getDouble(2);

                // Update net balance
                netBalance.put(payer, netBalance.getOrDefault(payer, 0.0) + amount);
                netBalance.put(payee, netBalance.getOrDefault(payee, 0.0) - amount);
            } while (cursor.moveToNext());
        }
        cursor.close();
        //db.close();

        return minimizeTransactions(netBalance, db);
    }

    // Algorithm to minimize transactions
    private List<String> minimizeTransactions(Map<Integer, Double> netBalance, SQLiteDatabase db) {
        List<String> simplifiedTransactions = new ArrayList<>();

        // Separate creditors and debtors
        List<int[]> creditors = new ArrayList<>();
        List<int[]> debtors = new ArrayList<>();

        for (Map.Entry<Integer, Double> entry : netBalance.entrySet()) {
            int user = entry.getKey();
            double balance = entry.getValue();
            if (balance > 0) {
                creditors.add(new int[]{user, (int) (balance * 100)}); // Convert to cents to avoid floating errors
            } else if (balance < 0) {
                debtors.add(new int[]{user, (int) (-balance * 100)});
            }
        }

        // Match debtors with creditors
        int i = 0, j = 0;
        while (i < debtors.size() && j < creditors.size()) {
            int debtor = debtors.get(i)[0];
            int debtAmount = debtors.get(i)[1];

            int creditor = creditors.get(j)[0];
            int creditAmount = creditors.get(j)[1];

            int settledAmount = Math.min(debtAmount, creditAmount);

            String dname = "";
            String cname = "";
            Cursor cursor = db.query(
                    "Users",
                    new String[]{"name"},
                    "user_id = ?",
                    new String[]{""+debtor+""},
                    null, null, null
            );
            while (cursor.moveToNext())
            {
                dname = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }
            cursor.close();
            cursor = db.query(
                    "Users",
                    new String[]{"name"},
                    "user_id = ?",
                    new String[]{""+creditor+""},
                    null, null, null
            );
            while (cursor.moveToNext()) {
                cname = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            }


            simplifiedTransactions.add("User " + dname + " pays " + (settledAmount / 100.0) + " to User " + cname);

            // Update remaining amounts
            debtors.get(i)[1] -= settledAmount;
            creditors.get(j)[1] -= settledAmount;

            if (debtors.get(i)[1] == 0) i++; // Move to the next debtor
            if (creditors.get(j)[1] == 0) j++; // Move to the next creditor
        }

        return simplifiedTransactions;
    }
}