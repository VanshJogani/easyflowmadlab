package com.example.projectwork2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ViewPagerAdapter adapter;
    private SharedViewModel sharedViewModel;
   // String username;
//    //public MainActivity(){
//        username=getIntent().getStringExtra("username");
//    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);


        String username1=getIntent().getStringExtra("username");


        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
//        String myString = "Hello from MainActivity!";
        int id = getIntent().getIntExtra("user_id", -1);
        sharedViewModel.setMyString(username1);
        sharedViewModel.setUser_id(id);

        int[] tabIcons = {R.drawable.baseline_person_24, R.drawable.baseline_groups_24, R.drawable.baseline_account_balance_wallet_24, R.drawable.baseline_account_balance_24}; // Replace with your icon resources
        String[] tabTitles = {"Personal", "Groups", "Payments", "Account"};

        for (int i = 0; i < tabTitles.length; i++) {
            TabLayout.Tab tab = tabLayout.newTab();
            View tabView = LayoutInflater.from(this).inflate(R.layout.tab_with_icon_and_text, null);

            ImageView tabIcon = tabView.findViewById(R.id.tab_icon);
            TextView tabText = tabView.findViewById(R.id.tab_text);

            tabIcon.setImageDrawable(ContextCompat.getDrawable(this, tabIcons[i]));
            tabText.setText(tabTitles[i]);

            tab.setCustomView(tabView);
            tabLayout.addTab(tab);
        }
        adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            View tabView = LayoutInflater.from(this).inflate(R.layout.tab_with_icon_and_text, null);
            ImageView tabIcon = tabView.findViewById(R.id.tab_icon);
            TextView tabText = tabView.findViewById(R.id.tab_text);
            switch (position) {
                case 0:
//                    tab.setText("Personal");


                    tabIcon.setImageDrawable(ContextCompat.getDrawable(this, tabIcons[0]));
                    tabText.setText(tabTitles[0]);
                    tab.setCustomView(tabView);
                    break;
                case 1:
//                    tab.setText("Groups");
                    tabIcon.setImageDrawable(ContextCompat.getDrawable(this, tabIcons[1]));
                    tabText.setText(tabTitles[1]);
                    tab.setCustomView(tabView);
                    break;
                case 2:
//                    tab.setText("Payments");
                    tabIcon.setImageDrawable(ContextCompat.getDrawable(this, tabIcons[2]));
                    tabText.setText(tabTitles[2]);
                    tab.setCustomView(tabView);
                    break;
                case 3:
//                    tab.setText("Account");
                    tabIcon.setImageDrawable(ContextCompat.getDrawable(this, tabIcons[3]));
                    tabText.setText(tabTitles[3]);
                    tab.setCustomView(tabView);
                    break;
            }
        }).attach();


    }
}
