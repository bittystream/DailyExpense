package cn.edu.cqu.dailyexpense;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.PathInterpolator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartView extends View {
    public ChartView(Context context) {
        super(context);
        mContext = context;
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    //饼图类
    private static class PieBean {
        private String title;       //标题
        private float rotateAngle;  //旋转角度
        private int color;          //饼图颜色
        private boolean isDivide;   //是否与中心分隔

        public PieBean(String title, float rotateAngle, int color) {
            this(title, rotateAngle, color, false);
        }

        public PieBean(String title, float rotateAngle, int color, boolean isDivide) {
            this.title = title;
            this.rotateAngle = rotateAngle;
            this.color = color;
            this.isDivide = isDivide;
        }


        public String getTitle() { //获取标题
            return title;
        }

        public float getRotateAngle() { //获取旋转角度
            return rotateAngle;
        }

        public int getColor() { //获取颜色
            return color;
        }

        public boolean getIsDivide() { //获取是否被分隔
            return isDivide;
        }
    }
    private Context mContext;
    private DisplayMetrics screenDm;
    private float centerX, centerY, radius;

    private Paint piePaint;
    private List<PieBean> pieBeanList;
    private List<Record> recordList;
    private List<Float> partitionList;
    private List<Float> counter;
    private RectF mRectF;

    public void setRecordList(List<Record> list){
        recordList = list;
    }


    float sum;
    private void addPieBean(){
        // PieBean(String title, float rotateAngle, int color, boolean isDivide)
        counter = Arrays.asList(0f,0f,0f,0f,0f,0f,0f);
        partitionList = Arrays.asList(0f,0f,0f,0f,0f,0f,0f);
        List<String> classNames = Arrays.asList("餐饮","购物","娱乐/社交","医疗/保健","缴费/还款","交通/旅行","其他");
        List<String> tagNames = Arrays.asList("food","shopping","entertain","medical","pay","transportation","other");
        List<String> colorList = Arrays.asList("#fa8000","#ffd500","#f5deb3","#ffe384","#ed9121","#ffff00","#ff6347");
        // 先计数 -- 不同分类下的账单总额
        sum = 0f;
        for (Record record : recordList){
            int idx = tagNames.indexOf(record.getClassification());
            float cnt = counter.get(idx);
            counter.set(idx,cnt+Float.parseFloat(record.getExpense()));
            sum += Float.parseFloat(record.getExpense());
        }

        // 再添加
        for (int i = 0; i < 7; i++){
            float rotateAngle = 360 * counter.get(i) / sum;
            PieBean bean = new PieBean(classNames.get(i),rotateAngle,Color.parseColor(colorList.get(i)));
            pieBeanList.add(bean);
            partitionList.set(i,counter.get(i) / sum);
        }
    }

    private void init(){
        screenDm = mContext.getResources().getDisplayMetrics();
        centerX = screenDm.widthPixels / 2f;
        centerY = screenDm.heightPixels / 3f;
        radius = centerX * 0.6f;
        piePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF(centerX-radius,centerY-radius-radius*0.3f,centerX+radius,centerY+radius-radius*0.3f);
        pieBeanList = new ArrayList<PieBean>();
        partitionList = new ArrayList<Float>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();
        addPieBean();
        float currentStartAngle = -60;
        for (PieBean bean : pieBeanList){
            if (bean.getRotateAngle() == 0f) continue;
            piePaint.setColor(bean.getColor());
            float sweepAngle = bean.getRotateAngle();
            if (bean.getIsDivide()) mRectF.offset(-20,-40);
            // 绘制扇形
            canvas.drawArc(mRectF,currentStartAngle,sweepAngle,true,piePaint);
            currentStartAngle += sweepAngle;

        }

    }
}