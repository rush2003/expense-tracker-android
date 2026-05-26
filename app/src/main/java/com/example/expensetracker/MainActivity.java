package com.example.expensetracker;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.expensetracker.fragments.AddExpenseFragment;
import com.example.expensetracker.fragments.HistoryFragment;
import com.example.expensetracker.fragments.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.expensetracker.fragments.StatsFragment;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        WindowInsetsControllerCompat controller = new WindowInsetsControllerCompat(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        // Load home fragment by default
        loadFragment(new HomeFragment());

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (id == R.id.nav_add) {
                fragment = new AddExpenseFragment();
            } else if (id == R.id.nav_stats) {
                fragment = new StatsFragment();
            } else {
                fragment = new HistoryFragment();
            }
            loadFragment(fragment);
            return true;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}