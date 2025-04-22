package com.example.projectwork2;

// DisplayActivity.java
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class AnalyzePersonal extends AppCompatActivity {

    private EditText editTextName;
    private Spinner spin_cat;
    private Button buttonShowChart;
    private GraphView barChartView;
    TextView tvtot;
    MyDatabaseHelper dbHelper;
    SQLiteDatabase db;
   // private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_personal);

        dbHelper = new MyDatabaseHelper(AnalyzePersonal.this);
        db = dbHelper.getWritableDatabase();
        //editTextName = findViewById(R.id.editTextName);
        buttonShowChart = findViewById(R.id.buttonShowChart);
        barChartView = findViewById(R.id.barChartView);
        tvtot=findViewById(R.id.tvtot);
        spin_cat=findViewById(R.id.spin_name);
       // dbHelper = new DatabaseHelper(this);
        ArrayList<String> categoryList=new ArrayList<>();
        categoryList.add("All");
        categoryList.add("Jan");
        categoryList.add("Feb");
        categoryList.add("March");
        categoryList.add("April");
        categoryList.add("May");
        categoryList.add("June");
        categoryList.add("July");
        categoryList.add("August");
        categoryList.add("Sept");
        categoryList.add("Oct");
        categoryList.add("Nov");
        categoryList.add("Dec");

        ArrayAdapter<String> adapter=new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,categoryList);
        spin_cat.setAdapter(adapter);
        buttonShowChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String name = editTextName.getText().toString().trim();
//                if (name.isEmpty()) {
//                    editTextName.setError("Please enter a name");
//                    return;
//                }
                showPersonMonthlyChart();
            }
        });
    }

    private void showPersonMonthlyChart() {
        SharedViewModel sharedViewModel;
        sharedViewModel = new ViewModelProvider(AnalyzePersonal.this).get(SharedViewModel.class);
        String name = sharedViewModel.getMyString().getValue();
        String cat = spin_cat.getSelectedItem().toString();
        List<Float> values = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        int curr_id = getIntent().getIntExtra("user_id", -1);
        Cursor cursor = db.query(
                "ExpPer",
                new String[]{"sum(amount)", "cat"},
                "user_id = ?",
                new String[]{""+curr_id+""},
                "cat",null, null
        );

        //perList.clear();
        double total=0;
        while (cursor.moveToNext())
        {
            //String d = cursor.getString(cursor.getColumnIndexOrThrow("p_exp_desc"));
            double a = cursor.getDouble(cursor.getColumnIndexOrThrow("sum(amount)"));
            String c = cursor.getString(cursor.getColumnIndexOrThrow("cat"));
            values.add((float) a);
            labels.add(c);
            total+=a;
            //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }
        cursor.close();

//        Cursor cursor = dbHelper.getPersonMonthlyExpenses(name);
//        if (cursor.getCount() == 0) {
//            Toast.makeText(this, "No expenses found for " + name, Toast.LENGTH_SHORT).show();
//            barChartView.setData(new ArrayList<>(), new ArrayList<>());
//            return;
//        }
//
//        while (cursor.moveToNext()) {
//            labels.add(cursor.getString(0)); // Month (YYYY-MM)
//            values.add(cursor.getFloat(1));  // Total expense
//        }
//        cursor.close();

        tvtot.setText("Total expenses: "+total);
        barChartView.setData(values, labels);
    }
}