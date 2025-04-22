package com.example.projectwork2;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapterSplit extends FragmentStateAdapter {
    public ViewPagerAdapterSplit(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EqualFragment();
            case 1:
                return new UnequalFragment();
            default:
                return new EqualFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
