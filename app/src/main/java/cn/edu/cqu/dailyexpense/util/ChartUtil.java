package cn.edu.cqu.dailyexpense.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChartUtil {
    class Part{
        float partition; // 占比
        String tagName; // 英文
        String className; // 中文
        public Part(float p, String t, String c){
            partition = p;
            tagName = t;
            className = c;
        }
    }

    private List<String> classNames = Arrays.asList("餐饮","购物","娱乐/社交","医疗/保健","缴费/还款","交通/旅行","其他");
    private List<String> tagNames = Arrays.asList("food","shopping","entertain","medical","pay","transportation","other");
    private String [] colors = {"#FF1403","#FD3C00","#FA581B","#F96701","#F7680C","F78E12","F5C71B"}; // 七种颜色
    private List<Part> parts;

    // 构造函数，传入相应部分的占比和tag，根据tag得到中文名
    public ChartUtil(float[] p, String[] t){
        for(int i = 0; i < p.length; i++){
            Part part = new Part(p[i],t[i],classNames.get(tagNames.indexOf(t[i])));
            parts.add(part);
        }
    }

}
