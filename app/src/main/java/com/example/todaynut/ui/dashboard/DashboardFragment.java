package com.example.todaynut.ui.dashboard;

import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.todaynut.MainActivity;
import com.example.todaynut.R;
import com.example.todaynut.databinding.FragmentDashboardBinding;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Arrays;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    float[] nutArray = new float[20];

    PieChart pieChart;
    int[] colorArray = new int[] {Color.LTGRAY, Color.BLUE, Color.RED};

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        int nutCount  = searchNutCount();

        pieChart = (PieChart) root.findViewById(R.id.piechart);

        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(61f);

        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
        TypedArray arrayText = getResources().obtainTypedArray(R.array.nuts);

        for(int i =0; i<nutArray.length; i++){
            if(nutArray[i] > 0f){
                yValues.add(new PieEntry(nutArray[i], arrayText.getString(i)));
            }
        }

        Description description = new Description();
        description.setText("견과류를 " + nutCount + "일 먹었습니다"); //라벨
        description.setTextSize(15);
        pieChart.setDescription(description);

        pieChart.animateY(1000, Easing.EasingOption.EaseInOutCubic); //애니메이션

        PieDataSet dataSet = new PieDataSet(yValues,"견과류");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);


        return root;
    }

    // 당일 먹었는지 확인
    public int searchNutCount(){
        MainActivity.myDBHelper myHelper = new MainActivity.myDBHelper(getActivity());
        SQLiteDatabase sqlDB = myHelper.getReadableDatabase();
        Cursor res = sqlDB.rawQuery("select * from nutHistory", null);
        int count = res.getCount();
        int while_count  = 0;
        Log.i("TAG","개수 : " + count);
        res.moveToFirst();
        while(while_count<count){
            int preset_no = res.getInt(0);
            Log.i("TAG", "preset_num" + preset_no);
            Cursor res2 = sqlDB.rawQuery("select * from nutPreset where preset_no = " + preset_no, null);
            String nuts = "";
            int nut_preset_no = 0;
            while(res2.moveToNext()){
                nut_preset_no = res2.getInt(0);
                nuts = res2.getString(1);
            }
            ArrayList<String> loadNut = new ArrayList<String>(Arrays.asList(nuts.split(" ")));
            int arr_size = loadNut.size();
            for(int i = 0; i<arr_size; i++){
                if(preset_no == nut_preset_no){
                    Log.i("TAG","결과 : " + nut_preset_no + "," + loadNut.get(i));
                    nutArray[Integer.parseInt(loadNut.get(i))] += 1f;
                }
            }
            while_count++;
            res.moveToNext();


        }

        sqlDB.close();
        res.close();
        //Toast.makeText(getActivity(), "searchDB", Toast.LENGTH_LONG).show();
        return count;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}