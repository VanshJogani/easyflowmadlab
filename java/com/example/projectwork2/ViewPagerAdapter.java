package com.example.projectwork2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new PersonalFragment();
            case 1:
                return new GroupsFragment();
            case 2:
                return new PaymentsFragment();
            case 3:
                return new AccountFragment();
            default:
                return new PersonalFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
