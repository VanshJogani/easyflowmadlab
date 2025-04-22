package com.example.projectwork2;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class GroupExpenseActivity extends AppCompatActivity {

    private TextView splittext;
    private Button back;

    private SharedViewModel sharedViewModel;
    private ArrayAdapter<String> adapter;
    private DatePicker datePicker;
    private List<String> expenseList = new ArrayList<>();
    private String groupName;
    private Spinner spinner1,spinner2;
    int g_id;
    int u_id;
    Button ocrbt;
    String type;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.groupactivity_menu, menu);
        return true;
    }

@Override
    public boolean onOptionsItemSelected(@NonNull MenuItem menuItem) {
        // Handle menu item selection
        int id = menuItem.getItemId();
        if (id == R.id.AddFriend) {

            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Add Friend");
            alert.setMessage("Enter name of friend");
            final EditText input = new EditText(this);
            alert.setView(input);
            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    String value = input.getText().toString();
                    MyDatabaseHelper dbHelper = new MyDatabaseHelper(GroupExpenseActivity.this);
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    Cursor c = db.rawQuery("SELECT user_id FROM Users WHERE name=?", new String[]{value});
                    while (c.moveToNext()) {
                        int fid = c.getInt(c.getColumnIndexOrThrow("user_id"));
                        try {
                            db.execSQL("INSERT into GMembers (group_id, user_id) VALUES (?, ?)", new Integer[]{g_id, fid});
                        }
                        catch (Exception e)
                        {
                            Toast.makeText(GroupExpenseActivity.this, "Already a member", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            alert.show();
            return true;
            // Handle settings click
            //return true;
        } else if (id == R.id.RemoveFriend) {
            // Handle search click
            return true;
        }
    return super.onOptionsItemSelected(menuItem); // Return false to allow the activity to handle it if needed
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_expense);


        splittext = findViewById(R.id.splittext);

        ocrbt = findViewById(R.id.ocrbt);
        //back=findViewById(R.id.back);
        groupName = getIntent().getStringExtra("GROUP_NAME");
        double amt = getIntent().getDoubleExtra("amt", 0);//get amount from image (ocr activity)
        type = getIntent().getStringExtra("type");
        if(type == null)
            type = "";

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ListView listViewExpenses = findViewById(R.id.listViewExpenses);
        EditText inputDesc = findViewById(R.id.inputDesc);
        inputDesc.setText(getIntent().getStringExtra("desc"));

        EditText inputCost = findViewById(R.id.inputCost);
        inputCost.setText(String.valueOf(amt));
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int uid= getIntent().getIntExtra("created_by",-1);
//                Cursor cursor2 = db.query(
//                        "Users",
//                        new String[]{"name"},
//                        "user_id = ?",
//                        new String[]{""+uid+""},
//                        null, null, null
//                );
//
//                String name="";
//                while (cursor2.moveToNext())
//                {
//                    name=cursor2.getString(cursor2.getColumnIndexOrThrow("name"));
//                }
//                Intent intent=new Intent(getApplicationContext(), MainActivity.class);
//                intent.putExtra("username",name);
//                intent.putExtra("user_id",uid);
//                startActivity(intent);
//
//            }
//        });
        Button addExpenseButton = findViewById(R.id.buttonAddExpense);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        spinner1=findViewById(R.id.spinner1);
        spinner2=findViewById(R.id.spinner2);
        // Adapter setup
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, expenseList);
        listViewExpenses.setAdapter(adapter);


        List<String> paidbyList = new ArrayList<>();
        Cursor cursor2 = db.query(
                "GMembers JOIN Users ON GMembers.user_id = Users.user_id JOIN Groups ON GMembers.group_id = Groups.group_id",
                new String[]{"GMembers.user_id", "Groups.group_id","Groups.g_name", "name",},
                "g_name = ?",
                new String[]{""+groupName+""},
                null, null, null
        );

        while (cursor2.moveToNext())
        {
            String name = cursor2.getString(cursor2.getColumnIndexOrThrow("name"));
            paidbyList.add(name);
            //Toast.makeText(GroupExpenseActivity.this, "Name: "+name, Toast.LENGTH_SHORT).show();
        }

        cursor2.close();

        //paidbyList.add("Vedansh");
        //paidbyList.add("Vansh");
        //paidbyList.add("Adit");
        //paidbyList.add("Anirudh");


        List<String> splitbyList = new ArrayList<>();
        splitbyList.add("All");
        splitbyList.add("Custom");
        /*
        cursor2 = db.query(
                "GMembers JOIN Users ON GMembers.user_id = Users.user_id JOIN Groups ON GMembers.group_id = Groups.group_id",
                new String[]{"GMembers.user_id", "Groups.group_id","Groups.g_name", "name",},
                "g_name = ?",
                new String[]{""+groupName+""},
                null, null, null
        );

        while (cursor2.moveToNext())
        {
            String name = cursor2.getString(cursor2.getColumnIndexOrThrow("name"));
            splitbyList.add(name);
            //Toast.makeText(GroupExpenseActivity.this, "Name: "+name, Toast.LENGTH_SHORT).show();
        }



        cursor2.close();

         */
        //splitbyList.add("Vedansh");
        //splitbyList.add("Vansh");
        //splitbyList.add("Adit");
        //splitbyList.add("Anirudh");

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, paidbyList );//spinner1 is paidby
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, splitbyList );//spinner2 is splitby

        adapter2.setDropDownViewResource(android.R.layout.select_dialog_singlechoice);
        spinner1.setAdapter(adapter2);
        adapter3.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
        spinner2.setAdapter(adapter3);

        if(type.equals("Custom"))
            spinner2.setSelection(1);
        else
            spinner2.setSelection(0);

        // Observe existing expenses
        /*
        sharedViewModel.getExpenses().observe(this, data -> {
            expenseList.clear();
            if (data != null) {
                expenseList.addAll(data);
            }
            adapter.notifyDataSetChanged();
        });
         */
        Cursor cursor1 = db.rawQuery("Select group_id from Groups where g_name='"+groupName+"'", new String[]{});

        while (cursor1.moveToNext())
        {
            g_id = cursor1.getInt(cursor1.getColumnIndexOrThrow("group_id"));
        }
        cursor1.close();

        Cursor cursor = db.query(
                "Expenses",
                new String[]{"group_id", "amount", "exp_desc", "paid_by","created_at"},
                "group_id = ?",
                new String[]{""+g_id+""},
                null, null, null
        );

        while (cursor.moveToNext())
        {
            double amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String des = cursor.getString(cursor.getColumnIndexOrThrow("exp_desc"));
            String summary = "Amount: "+amount+"    Desc: "+des;
            expenseList.add(summary);
            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }


        //datePicker=findViewById(R.id.datepicker);
        // Add expense to group
        addExpenseButton.setOnClickListener(view -> {
            double cost;
            String desc = inputDesc.getText().toString().trim();
            try {
                cost = Double.parseDouble(inputCost.getText().toString().trim());
            }
            catch (NumberFormatException e) {
                Toast.makeText(this, "Enter Valid Cost", Toast.LENGTH_SHORT).show();
                return;
            }
            //boolean cost=true;
            if (!desc.isEmpty()) {
                /*
                int  dayOfMonth=datePicker.getDayOfMonth();
                int month=datePicker.getMonth();
                int year=datePicker.getYear();

                 */
                //String date=dayOfMonth+"/"+(month+1)+"/"+year;
                expenseList.add("Amount: "+cost+"    Desc: "+desc);
                adapter.notifyDataSetChanged();

                int created_by = getIntent().getIntExtra("created_by", 0);
                int paid_by_uid = 0;

                String pby = spinner1.getSelectedItem().toString();
                String sby = spinner2.getSelectedItem().toString();

                Cursor cursor3 = db.query(
                        "Users",
                        new String[]{"user_id"},
                        "name = ?",
                        new String[]{""+pby+""},
                        null,null,null);
                while (cursor3.moveToNext())
                {
                    paid_by_uid = cursor3.getInt(cursor3.getColumnIndexOrThrow("user_id"));
                }

                ContentValues values = new ContentValues();
                values.put("group_id", g_id);
                values.put("amount", cost);
                values.put("exp_desc", desc);
                values.put("created_user_id", created_by);
                values.put("paid_by", paid_by_uid);
                long expId = db.insert("Expenses", null,values);
                //Toast.makeText(MainActivity.this, "newRowId: "+newRowId, Toast.LENGTH_SHORT).show();
                int gmemno = 0;

                if(sby.equals("All"))
                {
                    Cursor cursor4 = db.query(
                            "GMembers",
                            new String[]{"user_id"},
                            "group_id = ?",
                            new String[]{""+g_id+""},
                            null, null, null);

                    while (cursor4.moveToNext())
                    {
                        //int uid = cursor4.getInt(cursor4.getColumnIndexOrThrow("user_id"));
                        gmemno++;
                    }
                    cursor4.close();
                    cursor4 = db.query(
                            "GMembers",
                            new String[]{"user_id"},
                            "group_id = ?",
                            new String[]{""+g_id+""},
                            null, null, null);
                    while (cursor4.moveToNext())
                    {
                        int splitby_uid = cursor4.getInt(cursor4.getColumnIndexOrThrow("user_id"));
                        double sp_am = cost/gmemno;
                        values = new ContentValues();
                        values.put("exp_id", expId);
                        values.put("payee_user_id", splitby_uid);
                        values.put("pay_amt", sp_am);
                        db.insert("ExpShare", null, values);

                        values = new ContentValues();
                        values.put("payee_id", splitby_uid);
                        values.put("payer_id", paid_by_uid);
                        values.put("owed_amt", sp_am);
                        db.insert("Owed", null, values);
                    }
                    cursor4.close();
                }
                else if(sby.equals("Custom"))
                {
                    double[] amount = getIntent().getDoubleArrayExtra("amount");
                    ArrayList<String> namestring = getIntent().getStringArrayListExtra("namestring");
                    for(int i = 0; i < namestring.size(); i++)
                    {
                        int mem_id = 0;
                        Cursor cursor4 = db.query(
                                "Users",
                                new String[]{"user_id"},
                                "name = ?",
                                new String[]{""+namestring.get(i)+""},
                                null, null, null);

                        while (cursor4.moveToNext())
                        {
                            mem_id = cursor4.getInt(cursor4.getColumnIndexOrThrow("user_id"));
                        }
                        cursor4.close();
                        values = new ContentValues();
                        values.put("exp_id", expId);
                        values.put("payee_user_id", mem_id);
                        values.put("pay_amt", amount[i]);
                        db.insert("ExpShare", null, values);

                        values = new ContentValues();
                        values.put("payee_id", mem_id);
                        values.put("payer_id", paid_by_uid);
                        values.put("owed_amt", amount[i]);
                        db.insert("Owed", null, values);

                    }


                }

                //sharedViewModel.addExpense(desc, cost,date);
                inputDesc.setText("");
                inputCost.setText("");
                type = "";
                spinner1.setSelection(0);
                spinner2.setSelection(0);
                Toast.makeText(this, "Expense Added", Toast.LENGTH_SHORT).show();
            }
        });

        ocrbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), OcrActivity.class);
                intent.putExtra("GROUP_NAME", groupName);
                intent.putExtra("desc", inputDesc.getText().toString());
                startActivity(intent);
            }
        });
        Toolbar toolbar = findViewById(R.id.fragment_toolbar); // Get the Toolbar
        setSupportActionBar(toolbar); // Set it as the support action bar

        splittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AmountSplitter.class);
                startActivity(intent);
            }
        });

        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(spinner2.getSelectedItem().toString().equals("Custom") && type.equals(""))
                {
                    Intent intent = new Intent(getApplicationContext(), AmountSplitter.class);
                    intent.putExtra("GROUP_NAME", groupName);
                    intent.putExtra("desc", inputDesc.getText().toString());
                    intent.putExtra("tot_amt", Float.parseFloat(inputCost.getText().toString()));
                    intent.putExtra("type", "Custom");
                    startActivity(intent);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }



}
