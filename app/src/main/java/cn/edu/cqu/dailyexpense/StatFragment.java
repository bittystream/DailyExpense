package cn.edu.cqu.dailyexpense;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    public StatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment StatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static StatFragment newInstance(String param1, String param2) {
        StatFragment fragment = new StatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    private ChartView chartView;
    private TabLayout tabLayout;
    private List<Record> recordList;
    private List<Float> partitionList, sumByClassList;
    private int selectType = 0;
    private float sum = 0;
    private TextView statSum, statFood, statShopping, statEntertain, statMedical, statPay, statTransportation, statOther;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_stat, container, false);
        tabLayout = root.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("本周"),true);
        tabLayout.addTab(tabLayout.newTab().setText("本月"),false);
        tabLayout.addTab(tabLayout.newTab().setText("本年"),false);
        tabLayout.setTabTextColors(Color.GRAY,getResources().getColor(R.color.colorPrimaryDark,null));
        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i(TAG, "onTabSelected: select tab "+tab.getText());
                if (tab.getText().toString().equals("本周")){
                    selectType = THIS_WEEK;
                } else if (tab.getText().toString().equals("本月")){
                    selectType = THIS_MONTH;
                } else {// 本年
                    selectType = THIS_YEAR;
                }
                onResume();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
        initView(root);
        recordList = new ArrayList<Record>();
        return root;
    }

    void initView(View root){
        chartView = root.findViewById(R.id.chart_view);
        statSum = root.findViewById(R.id.stat_sum);
        statFood = root.findViewById(R.id.stat_food);
        statShopping = root.findViewById(R.id.stat_shopping);
        statEntertain = root.findViewById(R.id.stat_entertain);
        statMedical = root.findViewById(R.id.stat_medical);
        statPay = root.findViewById(R.id.stat_pay);
        statTransportation = root.findViewById(R.id.stat_transportation);
        statOther = root.findViewById(R.id.stat_other);
    }

    @Override
    public void onResume() {
        super.onResume();
        String s = "";
        tabLayout.getTabAt(selectType).select();
        if (selectType == THIS_WEEK) s = "本周";
        else if (selectType == THIS_MONTH) s = "本月";
        else s = "本年";
        selectData(selectType);
        chartView.setRecordList(recordList);
        chartView.invalidate();
        drawLegend();
        s += "账单总额为"+String.valueOf(sum)+"元";
        statSum.setText(s);
    }
    private void drawLegend(){
        statFood.setText(String.format("餐饮共支出%s元，占比%s%%",sumByClassList.get(0), (String.valueOf(partitionList.get(0) * 100)+"000").substring(0,4)));
        statShopping.setText(String.format("购物共支出%s元，占比%s%%", sumByClassList.get(1), (String.valueOf(partitionList.get(1) * 100)+"000").substring(0,4)));
        statEntertain.setText(String.format("娱乐/社交共支出%s元，占比%s%%", sumByClassList.get(2), (String.valueOf(partitionList.get(2) * 100)+"000").substring(0,4)));
        statMedical.setText(String.format("医疗/保健共支出%s元，占比%s%%", sumByClassList.get(3), (String.valueOf(partitionList.get(3) * 100)+"000").substring(0,4)));
        statPay.setText(String.format("缴费/还款共支出%s元，占比%s%%", sumByClassList.get(4), (String.valueOf(partitionList.get(4) * 100)+"000").substring(0,4)));
        statTransportation.setText(String.format("交通/旅行共支出%s元，占比%s%%", sumByClassList.get(5), (String.valueOf(partitionList.get(5) * 100)+"000").substring(0,4)));
        statOther.setText(String.format("其他共支出%s元，占比%s%%", sumByClassList.get(6), (String.valueOf(partitionList.get(6) * 100)+"000").substring(0,4)));
    }



    private static int THIS_WEEK = 0;
    private static int THIS_MONTH = 1;
    private static int THIS_YEAR = 2;

    private void selectData(int t){
        Calendar calendar = new GregorianCalendar();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;
        int thisDay = calendar.get(Calendar.DAY_OF_MONTH);
        int [] daysOfMonth = {31,28,31,30,31,30,31,31,30,31,30,31};
        int startYear, startMonth, startDay;
        String condition = "";

        if (t == THIS_WEEK){
            if (thisDay < 7){ // 七天前在上个月
                if (thisMonth == 1){ // 七天前在去年
                    startYear = thisYear - 1;
                    startMonth = 12;
                } else{ // 七天前还是在今年
                    startYear = thisYear;
                    startMonth = thisMonth - 1;
                }
                startDay = daysOfMonth[thisMonth] + thisDay - 6;
            } else{ // 七天前在这个月
                startYear = thisYear;
                startMonth = thisMonth;
                startDay = thisDay - 6;
            }

        } else if (t == THIS_MONTH){
            if (thisMonth == 1){ // 一个月前在去年
                startYear = thisYear - 1;
                startMonth = 12;
            } else{ // 一个月前还是在今年
                startYear = thisYear;
                startMonth = thisMonth - 1;
            }
            startDay = thisDay;
        } else{
            startYear = thisYear - 1;
            startMonth = thisMonth;
            startDay = thisDay;
        }
        recordList.clear();
        sum = 0;
        sumByClassList = Arrays.asList(0f,0f,0f,0f,0f,0f,0f);
        partitionList = Arrays.asList(0f,0f,0f,0f,0f,0f,0f);
        List<String> tagNames = Arrays.asList("food","shopping","entertain","medical","pay","transportation","other");
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext,"record",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        Log.i(TAG, "selectData: "+startDay + " "+startMonth+" "+startYear);
        String sql = "select * from record where ";
        if (selectType == THIS_YEAR){
            sql = sql + "(year=" + thisYear + " and month=" + thisMonth + " and day<=" + thisDay +
                    ") or (year=" + thisYear + " and month<" + thisMonth +
                    ") or (year=" + startYear + " and month=" + startMonth + " and day>=" + thisDay +
                    ") or (year=" + startYear + " and month>" + startMonth + ")";
        }
        else {
            sql = sql + "(year=" + startYear + " and month=" + startMonth + " and day>=" + startDay +
                    ") or (year=" + thisYear + " and month=" + thisMonth + " and day<=" + thisDay + ")";
        }
        Log.i(TAG, "selectData: sql="+sql);
        String classification, usage, year, month, day, expense, id;
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            Log.i(TAG, "onCreate: cursor is not null");
            do {
                id = cursor.getString(cursor.getColumnIndex("id"));
                classification = cursor.getString(cursor.getColumnIndex("classification"));
                usage = cursor.getString(cursor.getColumnIndex("usage"));
                year = cursor.getString(cursor.getColumnIndex("year"));
                month = cursor.getString(cursor.getColumnIndex("month"));
                day = cursor.getString(cursor.getColumnIndex("day"));
                expense = cursor.getString(cursor.getColumnIndex("expense"));
                Record record = new Record(id,classification,usage,year,month,day,expense);
                recordList.add(record);
                int idx = tagNames.indexOf(classification);
                sumByClassList.set(idx,sumByClassList.get(idx)+Float.parseFloat(expense));
                sum += Float.parseFloat(expense);
            } while (cursor.moveToNext());
            cursor.close();
        }
        for (int i = 0; i < 7; i++){
            partitionList.set(i,sumByClassList.get(i)/sum);
        }

        Log.i(TAG, "selectData: 共"+recordList.size()+"条记录");
        db.close();
    }


}