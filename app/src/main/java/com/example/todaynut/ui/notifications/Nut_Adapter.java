package com.example.todaynut.ui.notifications;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.todaynut.R;

import java.util.ArrayList;

public class Nut_Adapter extends BaseAdapter {
    private ArrayList<Nut_item> list;
    // 어댑터 생성시 PreparationFragment.java 에서 만들었던 데이터 객체 리스트를 초기화
    Nut_Adapter(ArrayList<Nut_item> i){
        list = i;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public String getItem(int position) {
        return list.get(position).ItemString;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    public boolean isChecked(int position) {
        return list.get(position).checked;
    }
    public void check(int position) {list.get(position).checked = true;}
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.nut_listview_item, parent, false);
        }
        TextView tv_preparation = convertView.findViewById(R.id.item_text);
        CheckBox checkBox = convertView.findViewById(R.id.checkbox);
        checkBox.setChecked(list.get(position).checked);
        tv_preparation.setText(list.get(position).ItemString);
        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                boolean newState = !list.get(position).isChecked();
                list.get(position).checked = newState;
            }
        });
        checkBox.setChecked(isChecked(position));
        return convertView;
    }
}
