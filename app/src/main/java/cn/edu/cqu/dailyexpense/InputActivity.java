package cn.edu.cqu.dailyexpense;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class InputActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "InputActivity";
    private String classification, usage;
    private float expense;
    private int year, month, day;
    private EditText usageText, expenseText;
    private DatePicker datePicker;
    private boolean isFromModifyInfo;

    /**
     * 设置时间选择器的分割线颜色
     */
    private void setDatePickerDividerColor(DatePicker datePicker) {
        // Divider changing:

        // 获取 mSpinners
        LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

        // 获取 NumberPicker
        LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
        for (int i = 0; i < mSpinners.getChildCount(); i++) {
            NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

            Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        pf.set(picker, new ColorDrawable(getResources().getColor(R.color.colorPrimaryDark,null)));//设置分割线颜色
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    // 初始化
    void viewInit(){
        usageText = findViewById(R.id.usage_text);
        expenseText = findViewById(R.id.expense_text);
        datePicker = findViewById(R.id.date_picker);
        expenseText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        setDatePickerDividerColor(datePicker);

        Button button = findViewById(R.id.finish_input_button);
        button.setOnClickListener(this);

        GridLayout gridLayout = findViewById(R.id.class_grid);
        for (int i = 0; i < gridLayout.getChildCount(); i++){
            gridLayout.getChildAt(i).setOnClickListener(this);
        }

        // 设置最大显示日期
        datePicker.setMaxDate(System.currentTimeMillis());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        viewInit();

        classification = "food";
        Calendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH) + 1;
        day = calendar.get(Calendar.DAY_OF_MONTH);

        // 修改
        if (getIntent().getExtras() == null || getIntent().getExtras().getString("id") == null) isFromModifyInfo = false;
        else { // 添加
            Log.i(TAG, "onCreate: "+getIntent().getExtras().getString("id"));
            ConstraintLayout c = findViewById(R.id.food); // 默认种类为"餐饮"
            TextView t = (TextView) c.getChildAt(0);
            t.setBackgroundResource(R.drawable.class_button);
            isFromModifyInfo = true;
            fillView();
        }
        // 设置datePicker的显示初始值
        datePicker.init(year, month-1, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                year = datePicker.getYear();
                month = datePicker.getMonth() + 1;
                day = datePicker.getDayOfMonth();
            }
        });
    }

    private void fillView(){
        SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(this, "record",null,1);
        SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();
        String sql = "select * from record where id="+getIntent().getExtras().getString("id");
        Cursor cursor = db.rawQuery(sql,null);
        if (cursor.moveToFirst()){
            Log.i(TAG, "onCreate: cursor is not null");
            do {
                classification = cursor.getString(cursor.getColumnIndex("classification"));
                usage = cursor.getString(cursor.getColumnIndex("usage"));
                year = cursor.getInt(cursor.getColumnIndex("year"));
                month = cursor.getInt(cursor.getColumnIndex("month"));
                day = cursor.getInt(cursor.getColumnIndex("day"));
                expense = cursor.getFloat(cursor.getColumnIndex("expense"));
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();

        usageText.setText(usage);

        expenseText.setText(String.valueOf(expense));

        int id = getResources().getIdentifier(classification,"id",getPackageName());
        ConstraintLayout c = findViewById(id);
        TextView t = (TextView) c.getChildAt(0);
        t.setBackgroundResource(R.drawable.class_button_focus);
        Log.i(TAG, "fillView: "+year+" "+month+" "+day);
    }

    private String getItemId(){
        Calendar calendar = new GregorianCalendar();
        String y = String.valueOf(calendar.get(Calendar.YEAR));
        String m = String.valueOf(calendar.get(Calendar.MONTH)+1);
        String d = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String h = String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
        String mi = String.valueOf(calendar.get(Calendar.MINUTE));
        String s = String.valueOf(calendar.get(Calendar.SECOND));

        if(year == 0) year = Integer.parseInt(y);
        if(month == 0) month = Integer.parseInt(m);
        if(day == 0) day = Integer.parseInt(d);
        return y+m+d+h+mi+s;
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.finish_input_button){
            // 提交输入信息 存入数据库
            usage = usageText.getText().toString();
            String e = expenseText.getText().toString();
            if (usage == null || usage.length() < 1){
                Toast.makeText(this,"您还未输入用途！",Toast.LENGTH_SHORT).show();
                return;
            }
            if (e == null || e.length() < 1){
                Toast.makeText(this,"您还未输入金额！",Toast.LENGTH_SHORT).show();
                return;
            }
            expense = Float.parseFloat(e);
            if (expense <= 0f){
                Toast.makeText(this,"无效的金额！",Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteOpenHelper sqLiteOpenHelper = new DatabaseHelper(this, "record",null,1);
            SQLiteDatabase db = sqLiteOpenHelper.getWritableDatabase();

            // 删除原纪录！重新插入
            if (isFromModifyInfo)
            {
                String sql = "delete from record where id="+getIntent().getExtras().getString("id");
                db.execSQL(sql);
                Log.i(TAG, "onClick: deleted duplicate record");
            }

            ContentValues contentValues = new ContentValues();
            if (isFromModifyInfo) contentValues.put("id",getIntent().getExtras().getString("id"));
            else contentValues.put("id",getItemId());
            contentValues.put("classification",classification);
            contentValues.put("usage",usage);
            contentValues.put("year",year);
            contentValues.put("month",month);
            contentValues.put("day",day);
            contentValues.put("expense",expense);
            Log.i(TAG, "onClick: date:"+year+"-"+month+"-"+day);

            db.insert("record",null,contentValues);
            db.close();
            Log.i(TAG, "onClick: finish input");
            String showText = "提交成功";
            if (isFromModifyInfo) showText = "修改成功";
            Toast.makeText(this, showText, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            int id = getResources().getIdentifier(classification,"id",getPackageName());
            ConstraintLayout c1 = findViewById(id);
            TextView t1 = (TextView)c1.getChildAt(0);
            t1.setBackgroundResource(R.drawable.class_button);

            classification = view.getTag().toString();
            TextView t2 = (TextView)((ConstraintLayout)view).getChildAt(0);
            t2.setBackgroundResource(R.drawable.class_button_focus);
            Log.i(TAG, "onClick: "+classification);
        }
    }
}