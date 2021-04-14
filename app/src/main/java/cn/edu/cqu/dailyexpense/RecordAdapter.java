package cn.edu.cqu.dailyexpense;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


public class RecordAdapter extends BaseAdapter {
    private List<Record> mRecordList;
    private Context mContext;

    public RecordAdapter(List<Record> recordList, Context context){
        mRecordList = recordList;
        mContext = context;
    }

    @Override
    public int getCount() {
        if (mRecordList == null) return 0;
        return mRecordList.size();
    }

    @Override
    public Object getItem(int i) {
        return mRecordList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = LayoutInflater.from(mContext).inflate(R.layout.record_item,viewGroup,false);
        List<String> classNames = Arrays.asList("餐饮","购物","娱乐/社交","医疗/保健","缴费/还款","交通/旅行","其他");
        List<String> tagNames = Arrays.asList("food","shopping","entertain","medical","pay","transportation","other");
        TextView classText = view.findViewById(R.id.record_class_text);
        TextView classIcon = view.findViewById(R.id.record_icon_text);
        TextView usageText = view.findViewById(R.id.record_usage_text);
        TextView dateText = view.findViewById(R.id.record_date_text);
        TextView expenseText = view.findViewById(R.id.record_expense_text);

        classText.setText(classNames.get(tagNames.indexOf(mRecordList.get(i).getClassification())));
        classIcon.setText(mContext.getResources().getIdentifier(mRecordList.get(i).getClassification(),"string",mContext.getPackageName()));
        usageText.setText(mRecordList.get(i).getUsage());
        String date = mRecordList.get(i).getYear() + "-";
        if (mRecordList.get(i).getMonth().length() < 2) date += "0";
        date += mRecordList.get(i).getMonth() + "-";
        if (mRecordList.get(i).getDay().length() < 2) date += "0";
        date += mRecordList.get(i).getDay();
        dateText.setText(date);
        expenseText.setText(mRecordList.get(i).getExpense());

        return view;
    }
}
