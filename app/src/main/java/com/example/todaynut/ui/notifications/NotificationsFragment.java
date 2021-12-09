package com.example.todaynut.ui.notifications;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.todaynut.MainActivity;
import com.example.todaynut.R;
import com.example.todaynut.databinding.FragmentNotificationsBinding;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ListView listView;
    ArrayList<Nut_item> items;
    Button preset_button;
    MainActivity mainActivity = new MainActivity();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initItems();
        listView = root.findViewById(R.id.listView);

        Nut_Adapter nutAdapter = new Nut_Adapter(items);
        listView.setAdapter(nutAdapter);

        preset_button = (Button) root.findViewById(R.id.confirm);
        ArrayList<String> loadedNut = loadPreset(nutAdapter);
        int count = loadedNut.size();
        if(loadedNut.get(0) == ""){

        }
        else {
            for (int i = 0; i < count; i++) {
                nutAdapter.check(Integer.parseInt(loadedNut.get(i)));
            }
        }
        preset_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPreset(nutAdapter);
            }
        });


        return root;
    }

    private void addPreset(Nut_Adapter nutAdapter){
        ArrayList<String> nutList = new ArrayList<>();
        int count = nutAdapter.getCount(); //전체 몇개인지 세기
        for(int i=0; i<count;i++){
            if(nutAdapter.isChecked(i)){
                nutList.add(String.valueOf(nutAdapter.getItemId(i)));
                Log.d("nutList", String.valueOf(nutAdapter.getItemId(i)));
            }

        }
        if(nutList.size() == 0)
        {
            Toast.makeText(getActivity(), "견과류를 선택해주세요", Toast.LENGTH_SHORT).show();
        }
        else{
            insertNutList(nutList);
        }
    }

    private ArrayList loadPreset(Nut_Adapter nutAdapter){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getReadableDatabase();

        String sql = "select * from nutPreset order by preset_no desc limit 1";
        Cursor cursor = sqlDB.rawQuery(sql, null);
        String nuts = "";
        while(cursor.moveToNext()){
            String id = cursor.getString(0);
            nuts = cursor.getString(1);
        }
        ArrayList<String> loadNut = new ArrayList<String>(Arrays.asList(nuts.split(" ")));
        Log.i("TAG", "쿼리문 : " + cursor.toString() + "결과 : " + loadNut);

        cursor.close();
        sqlDB.close();
        return loadNut;
    }

    private void initItems(){
        items = new ArrayList<Nut_item>();
        TypedArray arrayText = getResources().obtainTypedArray(R.array.nuts);
        for(int i=0; i<arrayText.length(); i++){
            String s = arrayText.getString(i);
            boolean b = false;
            Nut_item item = new Nut_item(b, s);
            items.add(item);
        }
        arrayText.recycle();
    }

    // 견과류 프리셋 등록
    public void insertNutList(ArrayList<String> nutList){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getWritableDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        for (String item : nutList) {
            stringBuilder.append(item + " ");
        }
        String strAppend = stringBuilder.toString();
        sqlDB.execSQL("insert into nutPreset(nut_list, use) values('" + strAppend + "', 1)");
        sqlDB.close();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}