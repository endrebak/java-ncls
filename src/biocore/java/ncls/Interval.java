package biocore.java.ncls;

public class Interval {

    public int start;
    public int end;
    public int sublist;

    public Interval(int start, int end, int sublist) {
        // TODO Auto-generated constructor stub
        this.start = start;
        this.end = end;
        this.sublist = sublist;
    }

    @Override
    public String toString(){

        String s = "(" + start + ", " + end + ", " + sublist + ")";
        return s;

    }


}
