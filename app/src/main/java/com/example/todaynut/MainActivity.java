package com.example.todaynut;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.amitshekhar.DebugDB;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.todaynut.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        DebugDB.getAddressLog();


        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        //initializeDB(binding.getRoot());

    }

    public static class myDBHelper extends SQLiteOpenHelper {
        public myDBHelper(Context context){
            super(context, "nutDB.db", null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table nutHistory (nut_List integer, nut_date date);");
            db.execSQL("create table nutPreset (preset_no integer primary key autoincrement, nut_list text, use integer);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists nutHistory");
            db.execSQL("drop table if exists nutPreset");
            onCreate(db);
        }
    }
/*
    public void initializeDB(View view){
        myDBHelper myHelper = new myDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        myHelper.onUpgrade(sqlDB, 1, 2);
        sqlDB.close();
        Toast.makeText(getApplicationContext(), "initialized", Toast.LENGTH_LONG).show();
    }*/
    // 당일 먹었는지 확인
    public int searchDB(Date sDate){
        myDBHelper myHelper = new myDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = myHelper.getReadableDatabase();
        Cursor res = sqlDB.rawQuery("select * from nutHistory where nut_date = " + sDate, null);
        sqlDB.close();
        Toast.makeText(getApplicationContext(), "searchDB", Toast.LENGTH_LONG).show();
        return res.getCount();
    }
    // 오늘 먹음
    public void insertDB(int i, Date sDate){
        myDBHelper myHelper = new myDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("insert into nutHistory values(i," + sDate + ")");
        sqlDB.close();
        Toast.makeText(getApplicationContext(), "Inserted", Toast.LENGTH_LONG).show();
    }
    // 오늘 안먹음
    public void deleteDB(Date sDate){
        myDBHelper myHelper = new myDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("delete from nutHistory where nut_date = " + sDate);
        sqlDB.close();
        Toast.makeText(getApplicationContext(), "deleted", Toast.LENGTH_LONG).show();
    }
    // 견과류 프리셋 등록
    public void insertNutList(ArrayList<String> nutList){
        myDBHelper myHelper = new myDBHelper(MainActivity.this);
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : nutList) {
            stringBuilder.append(item + " ");
        }
        String strAppend = stringBuilder.toString();
        sqlDB.execSQL("insert into nutPreset values(" + strAppend + ", 1)");
        sqlDB.close();
    }

}