package com.example.projectwork2;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AccountFragment extends Fragment {
   // private TextView username;
    private Button addFriend;
    private EditText friendName;
    private ListView friendList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);
        //username=view.findViewById(R.id.username);
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        // Observe the LiveData
        sharedViewModel.getMyString().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                // Update the UI with the new string value
                TextView textView = view.findViewById(R.id.username);
                if (textView != null) {
                    textView.setText("Hi, "+s);
                }
            }
        });
        addFriend=view.findViewById(R.id.friend);
        friendName=view.findViewById(R.id.friendname);
        friendList = view.findViewById(R.id.friendlist);

        ArrayList<String>  friends = new ArrayList<>();
        int user1_id = sharedViewModel.getUser_id().getValue();


        Cursor cursor = db.query(
                "Friends JOIN Users ON Friends.user2_id = Users.user_id",
                new String[]{"Users.user_id", "name"},
                "user1_id = ?",
                new String[]{""+user1_id+""},
                null, null, null
        );

        while (cursor.moveToNext())
        {
            String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
            friends.add(name);
            //Toast.makeText(requireContext(), "Name: "+name, Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, friends);
        friendList.setAdapter(adapter);


        String username=sharedViewModel.getMyString().toString();

        addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fname=friendName.getText().toString();
                int fid = 0;
                if(!fname.isEmpty())
                {
                    Cursor c = db.rawQuery("SELECT user_id FROM Users WHERE name=?", new String[]{fname});
                    while (c.moveToNext())
                    {
                        fid = c.getInt(c.getColumnIndexOrThrow("user_id"));
                        //groupList.add(g_name);
                        //Toast.makeText(requireContext(), "Name: "+g_name, Toast.LENGTH_SHORT).show();
                    }
                    int uid = sharedViewModel.getUser_id().getValue();
                    if(fid!=0)
                    {
                        db.execSQL("INSERT INTO Friends (user1_id, user2_id) VALUES (?, ?)", new Integer[]{uid, fid});
                        db.execSQL("INSERT INTO Friends (user1_id, user2_id) VALUES (?, ?)", new Integer[]{fid, uid});
                        Toast.makeText(requireContext(), "Friend Added", Toast.LENGTH_SHORT).show();
                        friends.add(fname);
                        adapter.notifyDataSetChanged();
                    }
                    else
                        Toast.makeText(requireContext(), "Friend Not Found", Toast.LENGTH_SHORT).show();
                    c.close();
                }
                else
                {
                    friendName.setError("Enter Friend Name");
                }
            }
        });

        return view;
    }
}
