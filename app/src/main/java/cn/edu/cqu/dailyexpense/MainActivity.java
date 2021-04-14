package cn.edu.cqu.dailyexpense;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final String TAG = "MainActivity";
    private FragmentManager fragmentManager;
    private TodayFragment todayFragment;
    private RecordFragment recordFragment;
    private StatFragment statFragment;
    private FrameLayout frameLayout;
    private int unselectedColor = Color.GRAY; // 未选中的灰色

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GridLayout gridLayout = findViewById(R.id.main_navi_bar);
        // 添加点击事件监听器
        for (int i = 0; i < gridLayout.getChildCount(); i++){
            gridLayout.getChildAt(i).setOnClickListener(this);
        }

        // 创建时默认显示第一个fragment(今日)
        selectedColor(gridLayout.getChildAt(0));
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        todayFragment = new TodayFragment();
        fragmentTransaction.add(R.id.fragment,todayFragment);
        fragmentTransaction.commit();

    }

    void clearColor(){
        GridLayout gridLayout = findViewById(R.id.main_navi_bar);
        for (int i = 0; i < gridLayout.getChildCount(); i++){
            View view = gridLayout.getChildAt(i);
            TextView icon = (TextView)((ViewGroup)view).getChildAt(0);
            TextView text = (TextView)((ViewGroup)view).getChildAt(1);
            icon.setTextColor(unselectedColor);
            text.setTextColor(unselectedColor);
        }
    }

    // 为选中的navibar item更改颜色
    void selectedColor(View view){
        TextView icon = (TextView)((ViewGroup)view).getChildAt(0);
        TextView text = (TextView)((ViewGroup)view).getChildAt(1);
        icon.setTextColor(getResources().getColor(R.color.colorPrimary,null));
        text.setTextColor(getResources().getColor(R.color.colorPrimary,null));
    }

    @Override
    public void onClick(View view) {
        // fragment的切换
        switch (view.getId()){
            case R.id.today_item:
                Log.i(TAG, "onClick: today clicked");
                FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
                fragmentTransaction1.replace(R.id.fragment,todayFragment);
                fragmentTransaction1.commit();
                break;
            case R.id.record_item:
                Log.i(TAG, "onClick: record clicked");
                if (recordFragment == null) recordFragment = new RecordFragment();
                FragmentTransaction fragmentTransaction2 = fragmentManager.beginTransaction();
                fragmentTransaction2.replace(R.id.fragment,recordFragment);
                fragmentTransaction2.commit();
                break;
            case R.id.stat_item:
                Log.i(TAG, "onClick: stat clicked");
                if (statFragment == null) statFragment = new StatFragment();
                FragmentTransaction fragmentTransaction3 = fragmentManager.beginTransaction();
                fragmentTransaction3.replace(R.id.fragment,statFragment);
                fragmentTransaction3.commit();
                break;
        }
        clearColor();
        selectedColor(view);
    }

    public void createMenu(ContextMenu menu){
        menu.add("修改");
        menu.add("删除");
    }
}