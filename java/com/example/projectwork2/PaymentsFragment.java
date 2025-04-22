package com.example.projectwork2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PaymentsFragment extends Fragment {

    private Button btn,btnocr;
    private Button checkbtn;
    private Spinner spinner_name;
    private ImageButton refresh;
    EditText name,upiid,amount,note;
    TextView tv;
    private SharedViewModel sharedViewModel;
    int payee_id;

    @Nullable
    @Override

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payments, container, false);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        spinner_name=view.findViewById(R.id.spinner_name);
        refresh=view.findViewById(R.id.refresh);
        ArrayList<String> nameList =new ArrayList<>();
//        nameList.add("Vedansh");
//        nameList.add("Vansh");
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        //creates null pointer exception
        int curr_id = sharedViewModel.getUser_id().getValue();
        Cursor cursor = db.query(
                "Friends JOIN Users ON Friends.user2_id = Users.user_id",
                new String[]{"name"},
                "user1_id = ?",
                new String[]{""+curr_id+""},
                null, null, null
        );

        nameList.clear();
        while (cursor.moveToNext())
        {
            String fn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            nameList.add(fn);
            //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }
        cursor.close();

        ArrayAdapter<String> adapter=new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,nameList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_name.setAdapter(adapter);


        spinner_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String name = spinner_name.getItemAtPosition(i).toString();
                Cursor cursor1 = db.query(
                        "Users",
                        new String[]{"upi_id", "user_id"},
                        "name = ?",
                        new String[]{""+name+""},
                        null, null, null
                );
                while (cursor1.moveToNext())
                {
                    String upi_id = cursor1.getString(cursor1.getColumnIndexOrThrow("upi_id"));
                    upiid.setText(upi_id);
                    payee_id = cursor1.getInt(cursor1.getColumnIndexOrThrow("user_id"));
                    //Toast.makeText(requireContext(), "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
                }
                cursor1.close();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //tv=view.findViewById(R.id.tv);
        //btnocr=view.findViewById(R.id.btnocr);
        btn=view.findViewById(R.id.btn);

       // name=view.findViewById(R.id.name);
        amount=view.findViewById(R.id.amount);
        upiid=view.findViewById(R.id.upiid);
        note=view.findViewById(R.id.note);
        note.setText("easyflowpayment");
        checkbtn=view.findViewById(R.id.checkbtn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!amount.getText().toString().isEmpty() && !upiid.getText().toString().isEmpty() && !note.getText().toString().isEmpty()) {



                    ContentValues values;
                    values = new ContentValues();
                    values.put("payee_id", payee_id);
                    values.put("payer_id", curr_id);
                    values.put("owed_amt", Double.parseDouble(amount.getText().toString()));
                    db.insert("Owed", null, values);
                    Toast.makeText(requireContext(), "Payment of Rs."+amount.getText().toString()+" sent to "+upiid.getText().toString(), Toast.LENGTH_SHORT).show();

                    values = new ContentValues();
                    values.put("payee_id", payee_id);
                    values.put("payer_id", curr_id);
                    values.put("amt", Double.parseDouble(amount.getText().toString()));
                    db.insert("Settle", null, values);

                    String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
                    int GOOGLE_PAY_REQUEST_CODE = 123;

                    Uri uri =
                            new Uri.Builder()
                                    .scheme("upi")
                                    .authority("pay")
                                    .appendQueryParameter("pa", upiid.getText().toString())
//                                    .appendQueryParameter("pn", name.getText().toString())
//                                    .appendQueryParameter("mc", "your-merchant-code")
//                                    .appendQueryParameter("tr", "your-transaction-ref-id")
                                    .appendQueryParameter("tn", note.getText().toString())
                                    .appendQueryParameter("am", amount.getText().toString())
                                    .appendQueryParameter("cu", "INR")
//                                    .appendQueryParameter("url", "your-transaction-url")
                                    .build();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(uri);
                    intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
                    startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
                }
            }
        });

        checkbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SimplyfyDebts.class);
                startActivity(intent);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = db.query(
                        "Friends JOIN Users ON Friends.user2_id = Users.user_id",
                        new String[]{"name"},
                        "user1_id = ?",
                        new String[]{""+curr_id+""},
                        null, null, null
                );

                nameList.clear();
                while (cursor.moveToNext())
                {
                    String fn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    nameList.add(fn);
                    //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
                }
                adapter.notifyDataSetChanged();
                cursor.close();
            }
        });



        /*
        btnocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1=new Intent(requireContext(), MainActivity3.class);
                startActivity(intent1);
            }
        });

        //sharing payment
        tv.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(requireContext(), MainActivity2.class);
                startActivity(intent);
            }
        });
         */


        return view;
    }
}
