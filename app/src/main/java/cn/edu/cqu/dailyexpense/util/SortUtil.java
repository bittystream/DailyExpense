package cn.edu.cqu.dailyexpense.util;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.edu.cqu.dailyexpense.Record;

public class SortUtil {
    public static void sortByExpenseAscending(List<Record> list){
        Collections.sort(list, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                float e1 = Float.parseFloat(r1.getExpense());
                float e2 = Float.parseFloat(r2.getExpense());
                return (int)(e1*100 - e2*100);
            }
        });
    }

    public static void sortByExpenseDescending(List<Record> list){
        Collections.sort(list, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                float e1 = Float.parseFloat(r1.getExpense());
                float e2 = Float.parseFloat(r2.getExpense());
                return (int)(e2*100 - e1*100);
            }
        });
    }

    public static void sortByDateAscending(List<Record> list){
        Collections.sort(list, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                int y1 = Integer.parseInt(r1.getYear());
                int y2 = Integer.parseInt(r2.getYear());
                int m1 = Integer.parseInt(r1.getMonth());
                int m2 = Integer.parseInt(r2.getMonth());
                int d1 = Integer.parseInt(r1.getDay());
                int d2 = Integer.parseInt(r2.getDay());
                if (y1 != y2) return y1 - y2;
                if (m1 != m2) return m1 - m2;
                return d1 - d2;
            }
        });
    }

    public static void sortByDateDescending(List<Record> list){
        Collections.sort(list, new Comparator<Record>() {
            @Override
            public int compare(Record r1, Record r2) {
                int y1 = Integer.parseInt(r1.getYear());
                int y2 = Integer.parseInt(r2.getYear());
                int m1 = Integer.parseInt(r1.getMonth());
                int m2 = Integer.parseInt(r2.getMonth());
                int d1 = Integer.parseInt(r1.getDay());
                int d2 = Integer.parseInt(r2.getDay());
                if (y1 != y2) return y2 - y1;
                if (m1 != m2) return m2 - m1;
                return d2 - d1;
            }
        });
    }
}
