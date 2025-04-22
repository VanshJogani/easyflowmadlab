package com.example.projectwork2;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class PersonalFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private DatePicker datepicker;
    private Button btnanalyze;
    private ArrayAdapter<String> adapter;
    private Spinner spinner_category;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        View view = inflater.inflate(R.layout.fragment_personal, container, false);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        datepicker=view.findViewById(R.id.datepicker);
        ListView listView = view.findViewById(R.id.listViewExpenses);
        Button button = view.findViewById(R.id.addExpenseButton);
        EditText descInput = view.findViewById(R.id.descInput);
        EditText costInput = view.findViewById(R.id.costInput);
        btnanalyze=view.findViewById(R.id.btnanalyze);
        spinner_category=view.findViewById(R.id.spinner_category);

        ArrayList<String> perList = new ArrayList<>();
        ArrayAdapter<String> adap2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, perList);
        listView.setAdapter(adap2);

        ArrayList<String> categoryList =new ArrayList<>();
        categoryList.add("Food");
        categoryList.add("Transportation");
        categoryList.add("Housing");
        categoryList.add("Utilities");
        categoryList.add("Entertainment");
        categoryList.add("Education");
        categoryList.add("Shopping");
        categoryList.add("Health");
        categoryList.add("Miscellaneous");
        ArrayAdapter<String> adapter2=new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item,categoryList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adapter2);


    btnanalyze.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(getContext(), AnalyzePersonal.class);
            intent.putExtra("user_id", sharedViewModel.getUser_id().getValue());
            startActivity(intent);
        }
    });
        button.setOnClickListener(v -> {


            if (!descInput.getText().toString().isEmpty() && !costInput.getText().toString().isEmpty()) {

                int id = sharedViewModel.getUser_id().getValue();
                String desc = descInput.getText().toString();
                double c = Double.parseDouble(costInput.getText().toString());
                String cat = spinner_category.getSelectedItem().toString();

                ContentValues values = new ContentValues();
                values.put("user_id", id);
                values.put("p_exp_desc", desc);
                values.put("amount", c);
                values.put("cat", cat);
                LocalDateTime now;
                String time = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    now = LocalDateTime.now();
                    int hour = now.getHour();
                    int minute = now.getMinute();
                    int second = now.getSecond();
                    time = hour + ":" + minute + ":" + second;
                }
                String date = (datepicker.getYear())+"-"+(datepicker.getMonth()+1)+"-"+(datepicker.getDayOfMonth())+" "+time;
                values.put("created_at", date);
                long newRowId = db.insert("ExpPer", null, values);
                Toast.makeText(getContext(), "newRowId: " + newRowId, Toast.LENGTH_SHORT).show();
                perList.add(desc+"	Rs. "+c+"	"+cat+"   "+date);
                adap2.notifyDataSetChanged();


            /*
            if (!desc.isEmpty() && !costInput.getText().toString().isEmpty()) {


                /*
                sharedViewModel.addExpense(desc, cost, date);
                descInput.setText("");
                costInput.setText("");


            }
            */
            }
            else
                Toast.makeText(getContext(), "Please enter all fields", Toast.LENGTH_SHORT).show();
        });

        /*
        sharedViewModel.getExpenses().observe(getViewLifecycleOwner(), expenses -> {
            adapter.clear();
            adapter.addAll(expenses);
            adapter.notifyDataSetChanged();
        });

         */


        int curr_id = sharedViewModel.getUser_id().getValue();
        Cursor cursor = db.query(
                "ExpPer",
                new String[]{"p_exp_desc", "amount", "cat", "created_at"},
                "user_id = ?",
                new String[]{""+curr_id+""},
                null, null, null
        );

        //perList.clear();
        while (cursor.moveToNext())
        {
            String d = cursor.getString(cursor.getColumnIndexOrThrow("p_exp_desc"));
            double a = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"));
            String c = cursor.getString(cursor.getColumnIndexOrThrow("cat"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("created_at"));
            perList.add(d+"\tRs. "+a+"\t"+c+"\t"+time);
            //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }

        adap2.notifyDataSetChanged();
        cursor.close();

        return view;
    }

}
