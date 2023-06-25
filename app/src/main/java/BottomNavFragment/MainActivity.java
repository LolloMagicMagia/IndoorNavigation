package BottomNavFragment;


import android.content.SharedPreferences;


import android.content.Context;

import android.os.Bundle;



import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.osmdroidex2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import dataAndRelation.EdificiDatabase;


public class MainActivity extends AppCompatActivity {
    private EdificiDatabase edificiDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();

        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        AppBarConfiguration appBarConfiguration=new AppBarConfiguration.Builder(
                R.id.fragmentOSM, R.id.fragmentIndoor, R.id.fragmentNovita).build();

        NavigationUI.setupWithNavController(bottomNavigationView,navController);

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
    }

}



