package biocore.java.ncls;

public class Interval {

    public int start;
    public int end;
    public int sublist;
    // public int index;

    public Interval(int start, int end, int sublist) {
        // TODO Auto-generated constructor stub
        this.start = start;
        this.end = end;
        this.sublist = sublist;
        // this.index = -1;
    }

    @Override
    public String toString(){

        String s = "(" + start + ", " + end + ", " + sublist + ")"; // + ", " + index +
        return s;

    }

    public boolean hasOverlap(int start, int end){

        return (this.start < end) && (start < this.end);

    }


}
