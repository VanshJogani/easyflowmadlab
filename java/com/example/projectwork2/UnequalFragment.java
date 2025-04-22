
package com.example.projectwork2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link UnequalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UnequalFragment extends Fragment {

    ListView groupmemlist;
    ListView groupamountlist;
    double remaining;
    TextView amountfintext;
    Button submit;

    String g_name;
    String desc;
    String type;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unequal, container, false);
        SharedPreferences sharedPreferences;
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        g_name = sharedPreferences.getString("g_name",null);
        desc = sharedPreferences.getString("desc",null);
        type = sharedPreferences.getString("type",null);
        float tot_amt = sharedPreferences.getFloat("tot_amt",0);

        amountfintext = view.findViewById(R.id.amountfintext);
        submit = view.findViewById(R.id.submit);

        int len = 0;

        groupmemlist = view.findViewById(R.id.groupmemlistunequal);
        groupamountlist = view.findViewById(R.id.groupamountlist);




        //ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> amtList = new ArrayList<>();
        int g_id = 1;//get group id using something

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //edit from here

        Cursor cursor1 = db.query(
                "Groups",
                new String[]{"group_id"},
                "g_name = ?",
                new String[]{""+g_name+""},
                null, null, null
        );
        while (cursor1.moveToNext())
        {
            g_id = cursor1.getInt(cursor1.getColumnIndexOrThrow("group_id"));
        }
        cursor1.close();

        //edit ends here

        Cursor cursor = db.rawQuery("SELECT Users.user_id, name FROM GMembers JOIN Users ON GMembers.user_id=Users.user_id WHERE group_id=?", new String[]{""+g_id+""});
        while (cursor.moveToNext())
        {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"));
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            //idList.add(id);
            amtList.add("0");
            nameList.add(name);
            len++;
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, nameList);
        groupmemlist.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, amtList);
        groupamountlist.setAdapter(adapter2);

        double[] amt = new double[len];
        for(int j = 0; j < len; j++)
        {
            amt[j] = 0.0;
        }
        remaining = tot_amt;
        amountfintext.setText("Remaining: "+remaining+" out of "+tot_amt);


        groupmemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                AlertDialog.Builder alert = new AlertDialog.Builder(requireContext());
                alert.setTitle("Enter Split Amount");
                alert.setMessage("Enter the amount to be split: ");
                final EditText input = new EditText(requireContext());
                alert.setView(input);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String value = input.getText().toString();
                        amtList.set(pos, value);
                        adapter2.notifyDataSetChanged();
                        amt[pos] = Double.parseDouble(value);
                        remaining = getremaining(amt, tot_amt);
                        amountfintext.setText("Remaining: "+remaining+" out of "+tot_amt);
                    }
                });
                alert.show();

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(remaining == 0)
                {
                    Intent intent = new Intent(requireContext(), GroupExpenseActivity.class);
                    intent.putExtra("GROUP_NAME", g_name);
                    intent.putExtra("desc", desc);
                    intent.putExtra("amt", (double) tot_amt);
                    intent.putExtra("amount", amt);
                    intent.putExtra("namestring", nameList);
                    intent.putExtra("type", type);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(requireContext(), "Please Enter correct amounts", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public double getremaining(double[] amt, double tot_amt)
    {
        double sum = 0;
        for(int i = 0; i < amt.length; i++)
        {
            sum += amt[i];
        }
        return tot_amt - sum;
    }
}