package cn.edu.cqu.dailyexpense;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.edu.cqu.dailyexpense.util.SortUtil;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "RecordFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    public RecordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
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

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase db;
    private List<Record> recordList;
    private ListView listView;
    private PopupWindow popupWindow;
    private View popupWindowView;
    private RecordAdapter recordAdapter;
    private TextView sortByExpenseText, sortByDateText, filterText, sortByDateAsc, sortByDateDes, sortByExpenseAsc, sortByExpenseDes;
    private int sortedByDate, sortedByExpense; // 0: 未按照该规则排序, 1: 按照该规则升序, 2: 按照该规则降序
    private int position;

    // filter内的控件
    private EditText fromYear, fromMonth, fromDay, toYear, toMonth, toDay, fromExpense, toExpense, usage;
    private TextView filterFood, filterShopping, filterMedical, filterEntertain, filterPay, filterTransportation, filterOther;
    private Button filterClearButton, filterSubmitButton;

    private String filterClass, filterFromYear, filterFromMonth, filterFromDay, filterToYear, filterToMonth, filterToDay, filterToExpense, filterFromExpense, filterUsage;

    void initPopupWindow(View root){
        filterFood = root.findViewById(R.id.filter_food);
        filterShopping = root.findViewById(R.id.filter_shopping);
        filterMedical = root.findViewById(R.id.filter_medical);
        filterEntertain = root.findViewById(R.id.filter_entertain);
        filterPay = root.findViewById(R.id.filter_pay);
        filterTransportation = root.findViewById(R.id.filter_transportation);
        filterOther = root.findViewById(R.id.filter_other);

        filterClearButton = root.findViewById(R.id.filter_clear);
        filterSubmitButton = root.findViewById(R.id.filter_submit);

        fromYear = root.findViewById(R.id.filter_year_from);
        fromMonth = root.findViewById(R.id.filter_month_from);
        fromDay = root.findViewById(R.id.filter_day_from);
        toYear = root.findViewById(R.id.filter_year_to);
        toMonth = root.findViewById(R.id.filter_month_to);
        toDay = root.findViewById(R.id.filter_day_to);
        fromExpense = root.findViewById(R.id.filter_expense_from);
        toExpense = root.findViewById(R.id.filter_expense_to);
        usage = root.findViewById(R.id.filter_usage);

        fromYear.setInputType(InputType.TYPE_CLASS_DATETIME);
        fromMonth.setInputType(InputType.TYPE_CLASS_DATETIME);
        fromDay.setInputType(InputType.TYPE_CLASS_DATETIME);
        toYear.setInputType(InputType.TYPE_CLASS_DATETIME);
        toMonth.setInputType(InputType.TYPE_CLASS_DATETIME);
        toDay.setInputType(InputType.TYPE_CLASS_DATETIME);
        fromExpense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        toExpense.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        filterFood.setOnClickListener(new FilterClassOnClickListener());
        filterShopping.setOnClickListener(new FilterClassOnClickListener());
        filterEntertain.setOnClickListener(new FilterClassOnClickListener());
        filterMedical.setOnClickListener(new FilterClassOnClickListener());
        filterTransportation.setOnClickListener(new FilterClassOnClickListener());
        filterPay.setOnClickListener(new FilterClassOnClickListener());
        filterOther.setOnClickListener(new FilterClassOnClickListener());
        filterSubmitButton.setOnClickListener(new FilterClassOnClickListener());
        filterClearButton.setOnClickListener(new FilterClassOnClickListener());
    }

    private void clearClassButton(){
        filterFood.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterShopping.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterEntertain.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterMedical.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterTransportation.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterPay.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterOther.setBackground(getResources().getDrawable(R.drawable.filter_class__unselected,null));
        filterClass = "";
    }

    void clearEditText(){
        fromYear.setText("");
        fromMonth.setText("");
        fromDay.setText("");
        toYear.setText("");
        toMonth.setText("");
        toDay.setText("");
        fromExpense.setText("");
        toExpense.setText("");
        usage.setText("");
        filterFromYear = "";
        filterFromMonth = "";
        filterFromDay = "";
        filterToYear = "";
        filterToMonth = "";
        filterToDay = "";
        filterFromExpense = "";
        filterToExpense = "";
        filterUsage = "";
    }

    private String getCondition(){
        String sql;
        filterFromYear = fromYear.getText().toString();
        filterFromMonth = fromMonth.getText().toString();
        filterFromDay = fromDay.getText().toString();
        filterToYear = toYear.getText().toString();
        filterToMonth = toMonth.getText().toString();
        filterToDay = toDay.getText().toString();
        filterFromExpense = fromExpense.getText().toString();
        filterToExpense = toExpense.getText().toString();
        filterUsage = usage.getText().toString();
        List<String> con = new ArrayList<>();
        if (filterClass != null && filterClass.length() > 0) con.add("classification='"+filterClass+"'");
        if (filterFromYear != null && filterFromYear.length() > 0) con.add("year>="+filterFromYear);
        if (filterFromMonth != null && filterFromMonth.length() > 0) con.add("month>="+filterFromMonth);
        if (filterFromDay != null && filterFromDay.length() > 0) con.add("day>="+filterFromDay);
        if (filterToYear != null && filterToYear.length() > 0) con.add("year<="+filterToYear);
        if (filterFromExpense != null && filterFromExpense.length() > 0) con.add("expense>="+filterFromExpense);
        if (filterToExpense != null && filterToExpense.length() > 0) con.add("expense<="+filterToExpense);
        if (filterToMonth != null && filterToMonth.length() > 0) con.add("month<="+filterToMonth);
        if (filterToDay != null && filterToDay.length() > 0) con.add("day<="+filterToDay);
        if (filterUsage != null && filterUsage.length() > 0) con.add("usage like '%"+filterUsage+"%'");
        if (con.size()>1) sql = "where " + String.join(" and ",con);
        else if(con.size() == 1) sql = "where "+con.get(0);
        else sql = "";
        return sql;
    }

    class FilterClassOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.filter_clear:
                    clearClassButton();
                    clearEditText();
                    break;
                case R.id.filter_submit:
                    popupWindow.dismiss();
                    onResume();
                    break;
                default:
                    if (view.getTag().toString().equals(filterClass)) {
                        clearClassButton();
                        break;
                    }
                    if (filterClass != null && filterClass.length() > 0) clearClassButton();
                    view.setBackground(getResources().getDrawable(R.drawable.class_button_focus,null));
                    filterClass = view.getTag().toString();
                    break;
            }
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_record, container, false);
        View view = inflater.inflate(R.layout.record_navigator,null);
        FrameLayout frameLayout = root.findViewById(R.id.record_frame_layout);
        frameLayout.addView(view,0);

        popupWindowView = inflater.inflate(R.layout.filter_popup,null,false);
        initPopupWindow(popupWindowView);

        sortByDateText = root.findViewById(R.id.sort_by_date);
        sortByDateText.setOnClickListener(new TextOnClickListener());
        sortByExpenseText = root.findViewById(R.id.sort_by_expense);
        sortByExpenseText.setOnClickListener(new TextOnClickListener());
        filterText = root.findViewById(R.id.filter);
        filterText.setOnClickListener(new TextOnClickListener());
        sortByDateAsc = root.findViewById(R.id.sort_by_date_asc);
        sortByDateDes = root.findViewById(R.id.sort_by_date_des);
        sortByExpenseAsc = root.findViewById(R.id.sort_by_expense_asc);
        sortByExpenseDes = root.findViewById(R.id.sort_by_expense_des);

        sortedByDate = 2; // 默认按日期降序排
        sortedByExpense = 0;
        sortByDateDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
        sortByDateText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));

        listView = root.findViewById(R.id.list_view);
        recordList = new ArrayList<Record>();
        databaseHelper = new DatabaseHelper(root.getContext(),"record",null,1);

        recordAdapter = new RecordAdapter(recordList,this.getContext());
        listView.setAdapter(recordAdapter);
        this.registerForContextMenu(listView);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        clearEditText();
        clearClassButton();

        Log.i(TAG, "onStart: called onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume: called onResume");
        recordList.clear();
        selectData(getCondition());
        if (getCondition() != null && getCondition().length() > 0){
            filterText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
        }
        else filterText.setTextColor(Color.GRAY);

        recordAdapter.notifyDataSetChanged();
        sortByExpenseText.setTextColor(Color.GRAY);
        sortByExpenseAsc.setTextColor(Color.GRAY);
        sortByExpenseDes.setTextColor(Color.GRAY);
        sortByDateAsc.setTextColor(Color.GRAY);
        sortByDateText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
        sortByDateDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
        sortedByDate = 2;
        sortedByExpense = 0;

    }

    class TextOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.sort_by_date:
                    if (sortedByDate == 0){
                        sortByExpenseText.setTextColor(Color.GRAY);
                        sortByExpenseAsc.setTextColor(Color.GRAY);
                        sortByExpenseDes.setTextColor(Color.GRAY);
                        sortByDateText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByDateDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortedByDate = 2;
                        sortedByExpense = 0;
                        SortUtil.sortByDateDescending(recordList);
                    }
                    else if (sortedByDate == 1){
                        sortByDateDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByDateAsc.setTextColor(Color.GRAY);
                        sortedByDate = 2;
                        SortUtil.sortByDateDescending(recordList);
                    }
                    else{
                        sortByDateAsc.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByDateDes.setTextColor(Color.GRAY);
                        sortedByDate = 1;
                        SortUtil.sortByDateAscending(recordList);
                    }
                    break;
                case R.id.sort_by_expense:
                    if (sortedByExpense == 0){
                        sortByDateText.setTextColor(Color.GRAY);
                        sortByDateAsc.setTextColor(Color.GRAY);
                        sortByDateDes.setTextColor(Color.GRAY);
                        sortByExpenseText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByExpenseDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortedByExpense = 2;
                        sortedByDate = 0;
                        SortUtil.sortByExpenseDescending(recordList);
                    }
                    else if (sortedByExpense == 1){
                        sortByExpenseDes.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByExpenseAsc.setTextColor(Color.GRAY);
                        sortedByExpense = 2;
                        SortUtil.sortByExpenseDescending(recordList);
                    }
                    else{
                        sortByExpenseAsc.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                        sortByExpenseDes.setTextColor(Color.GRAY);
                        sortedByExpense = 1;
                        SortUtil.sortByExpenseAscending(recordList);
                    }
                    break;
                case R.id.filter:
                    filterText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
                    popupWindow = new PopupWindow(popupWindowView, ConstraintLayout.LayoutParams.WRAP_CONTENT,ConstraintLayout.LayoutParams.WRAP_CONTENT,true);
                    popupWindow.showAsDropDown(filterText,0,45);
                    break;
            }
            recordAdapter.notifyDataSetChanged();
        }
    }

    // 如果对listview及其中控件设置长按监听器，会造成冲突
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0,0,0,"修改");
        menu.add(0,1,0,"删除");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (info == null) Log.i(TAG, "onContextItemSelected: info is null");
        position = info.position;
        Log.i(TAG, "onContextItemSelected: "+recordList.get(position).getId()+"clicked");
        if (item.getItemId() == 0){
            // 跳转到input页面 可修改相关信息
            Intent intent = new Intent(getContext(), InputActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("id",recordList.get(position).getId());
            intent.putExtras(bundle);
            startActivity(intent);
        }
        else{
            // 删除对应项, 弹出Dialog, 确认删除?
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("确认删除？");
            builder.setItems(new String[]{"是", "否"}, new DeleteDialogOnClickListener());
            builder.show();
        }
        return super.onContextItemSelected(item);
    }

    class DeleteDialogOnClickListener implements DialogInterface.OnClickListener{

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == 1){
            } else{
                deleteData(recordList.get(position));
                recordList.remove(position);
                recordAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "删除成功", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void deleteData(Record record){
        String sql = "delete from record where id="+record.getId();
        db = databaseHelper.getWritableDatabase();
        db.execSQL(sql);
        db.close();
        Log.i(TAG, "deleteData: deleted item id = "+record.getId());
    }

    private void selectData(String condition){
        if (condition.length() < 1) filterText.setTextColor(Color.GRAY);
        Log.i(TAG, "selectData: "+condition);
        String classification, usage, year, month, day, expense, id;
        String sql = "select * from record "+condition;
        db = databaseHelper.getWritableDatabase();
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
                expense = cursor.getString(cursor.getColumnIndex("expense")).toString();
                Record record = new Record(id,classification,usage,year,month,day,expense);
                recordList.add(record);
            } while (cursor.moveToNext());
            cursor.close();
        }
        else Log.i(TAG, "selectData: cursor is null");
        db.close();

        SortUtil.sortByDateDescending(recordList);
    }
}