package com.example.todaynut.ui.home;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.todaynut.MainActivity;
import com.example.todaynut.R;
import com.example.todaynut.databinding.FragmentHomeBinding;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    CalendarView calendarView;
    ToggleButton toggleButton;

    SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
    String sDate = form.format(new Date());


    MainActivity mainActivity = new MainActivity();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        calendarView = (CalendarView) root.findViewById(R.id.calendarView);
        toggleButton = (ToggleButton) root.findViewById(R.id.todayNutCheck);

        if(searchDB(sDate) > 0) {
            toggleButton.setChecked(true);
        } else {
            toggleButton.setChecked(false);
        }

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int day) {
                String date  = year + "-" + (month+1) + "-" +day;
                Log.i("TAG", "클릭 날짜 : " + date);
                sDate = date;

                if(searchDB(sDate) > 0) {
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setChecked(false);
                }
            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String toastMessage;

                if(compoundButton.isChecked()){
                    toastMessage = "오늘 견과류를 드셨군요!";
                    insertDB(getRecentPresetNum(), sDate);

                } else {
                    toastMessage = "오늘 견과류를 안드셨군요!";
                    deleteDB(sDate);
                }

                Toast.makeText(root.getContext(), toastMessage, Toast.LENGTH_LONG).show();

            }
        });


        return root;
    }
    // 당일 먹었는지 확인
    public int searchDB(String sDate){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getReadableDatabase();
        Log.i("TAG", sDate);
        Cursor res = sqlDB.rawQuery("select * from nutHistory where nut_date = '" + sDate + "'", null);
        int count = res.getCount();
        Log.i("TAG", "쿼리문 : " + res.toString() + "결과 : " + count);
        sqlDB.close();
        res.close();
        //Toast.makeText(getActivity(), "searchDB", Toast.LENGTH_LONG).show();
        return count;
    }

    public int getRecentPresetNum(){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getReadableDatabase();
        String sql = "select * from nutPreset order by preset_no desc limit 1";
        Cursor cursor = sqlDB.rawQuery(sql, null);
        int id = 0;
        cursor.moveToFirst();
        id = cursor.getInt(0);
        Log.i("TAG", "preset_no : " + id);
        cursor.close();
        sqlDB.close();
        return id;
    }

    // 오늘 먹음
    public void insertDB(int i, String sDate){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("insert into nutHistory values("+ i +",'" + sDate + "')");
        sqlDB.close();
        //Toast.makeText(getActivity(), "Inserted", Toast.LENGTH_LONG).show();
    }
    // 오늘 안먹음
    public void deleteDB(String sDate){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        sqlDB.execSQL("delete from nutHistory where nut_date = '" + sDate + "'");
        sqlDB.close();
        //Toast.makeText(getActivity(), "deleted", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}