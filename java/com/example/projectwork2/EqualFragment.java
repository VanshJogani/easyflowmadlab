package com.example.projectwork2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link EqualFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EqualFragment extends Fragment {
    int counter = 0;

    ListView groupmemlist;
    String g_name;
    String desc;
    String type;
    int g_id;

    TextView tottext;
    Button submit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_equal, container, false);

        //tottext = view.findViewById(R.id.tottextview);
        submit = view.findViewById(R.id.submit);

        SharedPreferences sharedPreferences;
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        g_id = 1;
        g_name = sharedPreferences.getString("g_name",null);
        desc = sharedPreferences.getString("desc",null);
        type = sharedPreferences.getString("type",null);
        float tot_amt = sharedPreferences.getFloat("tot_amt",0);

        groupmemlist = view.findViewById(R.id.groupmemlist);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

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

        ArrayList<String> checkedTextViews = new ArrayList<>();

        Cursor cursor = db.rawQuery("SELECT Users.user_id, name FROM GMembers JOIN Users ON GMembers.user_id=Users.user_id WHERE group_id=?", new String[]{""+g_id+""});


        checkedTextViews.clear();
        while (cursor.moveToNext())
        {
            String fn = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            checkedTextViews.add(fn);
            //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            //Toast.makeText(MainActivity.this, "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
        }
        cursor.close();


        //checkedTextViews.add(ctv1.getText().toString());
        //checkedTextViews.add(ctv2.getText().toString());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.select_dialog_multichoice,checkedTextViews);
        groupmemlist.setAdapter(adapter);
        groupmemlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        groupmemlist.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                if(groupmemlist.isItemChecked(i))
                    counter++;
                else
                    counter--;
                if(counter == 0)
                {
                    //tottext.setText("Select Atleast One Person");
                }
                else
                    //tottext.setText("Per Person Total is : "+tot_amt/counter);
                    actionMode.setTitle(counter+" Selected");

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if(counter == 0)
                {
                    //tottext.setText("Select Atleast One Person");
                }
                //tottext.setText("Per Person Total is : "+tot_amt/counter);
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> nameList1 = new ArrayList<>();
                double amt[] = new double[checkedTextViews.size()];
                for(int j = 0; j < checkedTextViews.size(); j++)
                {
                    amt[j] = 0;
                    if(groupmemlist.isItemChecked(j))
                        counter++;
                }
                for(int j = 0; j < checkedTextViews.size(); j++)
                {
                    nameList1.add(checkedTextViews.get(j));
                    if(groupmemlist.isItemChecked(j))
                    {
                        amt[j] = tot_amt/counter;
                    }
                    else
                        amt[j] = 0;
                }
                Toast.makeText(requireContext(), "Per Person Cost is : "+(tot_amt/counter), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(requireContext(), GroupExpenseActivity.class);
                intent.putExtra("GROUP_NAME", g_name);
                intent.putExtra("desc", desc);
                intent.putExtra("amt", (double) tot_amt);
                intent.putExtra("amount", amt);
                intent.putExtra("namestring", nameList1);
                intent.putExtra("type", type);
                startActivity(intent);
            }
        });



        return view;
    }
}