package com.example.myapplication.activities.establishment.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.example.myapplication.fragments.CreateEstablishmentFragment;
import com.example.myapplication.fragments.EstablishmentListFragment;
import com.example.myapplication.fragments.TileDataFragment;
import com.example.myapplication.fragments.ProfileFragment;

public class EstablishmentPagerAdapter extends FragmentStateAdapter {

    public EstablishmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new EstablishmentListFragment();
            case 1:
                return new CreateEstablishmentFragment();
            case 2:
                return new TileDataFragment();
            case 3:
                return new ProfileFragment();
            default:
                return new EstablishmentListFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 4;
    }
}
