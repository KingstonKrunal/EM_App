package com.krunal_shah.emapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.krunal_shah.emapp.Adapters.SectionsPageAdapter;
import com.krunal_shah.emapp.Fragments.BalanceFragment;
import com.krunal_shah.emapp.Fragments.CustomBottomSheetDialogFragment;
import com.krunal_shah.emapp.Fragments.ExpenseFragment;
import com.krunal_shah.emapp.R;


public class MainActivity extends AppCompatActivity {

    public static FloatingActionButton fab;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new CustomBottomSheetDialogFragment().show(getSupportFragmentManager(), "Dialog");
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        SectionsPageAdapter adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new ExpenseFragment(), "Expenses");
        adapter.addFragment(new BalanceFragment(), "Balance");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Toast.makeText(this, "You have selected About menu", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
                finish();

                return true;

            case R.id.logout:
                SharedPreferences sharedPreferences = this.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
        }
        return true;
    }
}