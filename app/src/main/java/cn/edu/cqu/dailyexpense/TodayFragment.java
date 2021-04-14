package cn.edu.cqu.dailyexpense;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cn.edu.cqu.dailyexpense.util.SortUtil;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TodayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TodayFragment extends Fragment {

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

    public TodayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TodayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
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

    private ListView listView;
    private TodayRecordAdapter adapter;
    private List<Record> list;
    private TextView todayHint, todayExpenseSum;
    private float expenseSum;

    private void selectData(){
        list.clear();
        String classification, usage, year, month, day, expense, id;
        DatabaseHelper databaseHelper = new DatabaseHelper(mContext,"record",null,1);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        int todayYear, todayMonth, todayDay;
        Calendar calendar = new GregorianCalendar();
        todayYear = calendar.get(Calendar.YEAR);
        todayMonth = calendar.get(Calendar.MONTH) + 1;
        todayDay = calendar.get(Calendar.DAY_OF_MONTH);
        String sql = "select * from record where year="+String.valueOf(todayYear)+" and month="+String.valueOf(todayMonth)+" and day="+String.valueOf(todayDay);
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
                expenseSum += Float.parseFloat(expense);
                list.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }
        else Log.i(TAG, "selectData: cursor is null");
        db.close();
        if (list.size() < 1) todayHint.setText("今天还未开始记账！");
        else todayHint.setText("今日账单流水");
        todayExpenseSum.setText(String.valueOf(expenseSum+"元"));
    }

    // 只显示!无操作!!!!!!!!
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_today, container, false);
        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,InputActivity.class);
                startActivity(intent);
            }
        });

        todayExpenseSum = root.findViewById(R.id.today_expense_sum);
        listView = root.findViewById(R.id.today_list_view);
        todayHint = root.findViewById(R.id.today_hint);
        list = new ArrayList<Record>();
        adapter = new TodayRecordAdapter(list,mContext);
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        expenseSum = 0;
        selectData();
        adapter.notifyDataSetChanged();
    }
}