package com.example.projectwork2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

public class GroupsFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private List<String> groupList = new ArrayList<>();
    private ArrayAdapter<String> adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);



        EditText groupname = view.findViewById(R.id.groupname);
        ListView listViewGroups = view.findViewById(R.id.listViewGroups);
        Button addGroupButton = view.findViewById(R.id.buttonAddGroup);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, groupList);
        listViewGroups.setAdapter(adapter);

        MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        int user_id = sharedViewModel.getUser_id().getValue();

        Cursor cursor = db.query(
                "GMembers JOIN Groups ON GMembers.group_id = Groups.group_id",
                new String[]{"GMembers.group_id", "g_name"},
                "user_id = ?",
                new String[]{""+user_id+""},
                null, null, null
        );

        while (cursor.moveToNext())
        {
            String g_name = cursor.getString(cursor.getColumnIndexOrThrow("g_name"));
            groupList.add(g_name);
            //Toast.makeText(requireContext(), "Name: "+g_name, Toast.LENGTH_SHORT).show();
        }

        cursor.close();


        /*
        // Observe existing groups
        sharedViewModel.getGroups().observe(getViewLifecycleOwner(), data -> {
            groupList.clear();
            if (data != null) {
                groupList.addAll(data);
            }
            adapter.notifyDataSetChanged();
        });
         */

        // Add group
        addGroupButton.setOnClickListener(v -> {
            String newGroup = groupname.getText().toString();
            if(newGroup.isEmpty())
                Toast.makeText(requireContext(), "Enter Valid Name", Toast.LENGTH_SHORT).show();
            else {
                sharedViewModel.addGroup(newGroup);

                //MyDatabaseHelper dbHelper = new MyDatabaseHelper(requireContext());
                //SQLiteDatabase db = dbHelper.getWritableDatabase();

                //Add to Groups
                ContentValues values = new ContentValues();
                values.put("g_name", newGroup);
                long newRowId = db.insert("Groups", null,values);
                //Toast.makeText(requireContext(), "newRowId: "+newRowId, Toast.LENGTH_SHORT).show();

                //Add to GMem
                Cursor cursor2 = db.query(
                        "Groups",
                        new String[]{"group_id"},
                        "g_name = ?",
                        new String[]{""+newGroup+""},
                        null, null, null
                );

                while (cursor2.moveToNext())
                {
                    int id = cursor2.getInt(cursor2.getColumnIndexOrThrow("group_id"));

                    values = new ContentValues();
                    values.put("user_id", user_id);
                    values.put("group_id", id);
                    db.insert("GMembers", null, values);
                    //break;
                    //String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                    //Toast.makeText(requireContext(), "Id: "+id+" Name: "+name, Toast.LENGTH_SHORT).show();
                }

                cursor2.close();

                groupList.add(newGroup);
                adapter.notifyDataSetChanged();
            }
        });

        // Open group details
        listViewGroups.setOnItemClickListener((parent, view1, position, id) -> {
            String groupName = groupList.get(position);
            Intent intent = new Intent(getContext(), GroupExpenseActivity.class);
            intent.putExtra("GROUP_NAME", groupName);
            //intent.putExtra()
            intent.putExtra("created_by", user_id);
            startActivity(intent);
        });

        return view;
    }

}
