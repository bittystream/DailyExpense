package cn.edu.cqu.dailyexpense;

public class Record {
    private String _id,_classification, _usage, _year, _month, _day, _expense;
    public Record(String id, String classification, String usage, String year, String month, String day, String expense){
        _id = id;
        _classification = classification;
        _usage = usage;
        _year = year;
        _month = month;
        _day = day;
        _expense = expense;
    }
    public String getId(){return _id;}
    public String getClassification(){return _classification;}
    public String getUsage(){return _usage;}
    public String getYear(){return _year;}
    public String getMonth(){return _month;}
    public String getDay(){return _day;}
    public String getExpense(){return _expense;}
}
