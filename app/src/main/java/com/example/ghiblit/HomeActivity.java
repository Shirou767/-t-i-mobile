package com.example.ghiblit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);

        ImageView iconBooking = findViewById(R.id.iconBookingHistory);
        iconBooking.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, BookingDetailsActivity.class);
            startActivity(intent);
        });


        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        userName = getIntent().getStringExtra("user_name");
        Toast.makeText(this, "Xin chào " + userName + "!", Toast.LENGTH_SHORT).show();

        // Gán sự kiện click cho từng ảnh phim
        ImageView imgTotoro = findViewById(R.id.Totoro);
        ImageView imgSpirited = findViewById(R.id.spirit);
        ImageView imgCastle = findViewById(R.id.castle);
        ImageView imgKiki = findViewById(R.id.kiki);

        imgTotoro.setOnClickListener(v -> openMovieDetails(2));      // My Neighbor Totoro
        imgSpirited.setOnClickListener(v -> openMovieDetails(1));    // Spirited Away
        imgCastle.setOnClickListener(v -> openMovieDetails(3));      // Howl's Moving Castle
        imgKiki.setOnClickListener(v -> openMovieDetails(5));        // Kiki's Delivery Service
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_account) {
            Toast.makeText(this, "⚙️ Tài khoản: " + userName, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, AccountInfoActivity.class));

        } else if (id == R.id.menu_movies) {
            startActivity(new Intent(this, MovieListActivity.class));

        } else if (id == R.id.menu_logout) {
            SharedPreferences prefs = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
            prefs.edit()
                    .remove("username")
                    .remove("password")
                    .putBoolean("remember", false)
                    .apply();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

            Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
            finish();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void openMovieDetails(int filmId) {
        Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
        intent.putExtra("film_id", filmId);
        startActivity(intent);

}

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(navigationView)) {
            drawerLayout.closeDrawer(navigationView);
        } else {
            super.onBackPressed();
        }
    }
}
