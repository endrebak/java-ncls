package biocore.java.ncls;


public class OverlapIterator {

    int start;
    int end;

    OverlapIterator parent;
    OverlapIterator child;

    public OverlapIterator(int start, int end){

        this.start = start;
        this.end = end;

    }

    @Override
    public String toString(){

        String s = "(" + start + ", " + end + ", " + parent + ", " + child + ")";
        return s;

    }
    
    public OverlapIterator(int start, int end, OverlapIterator parent, OverlapIterator child){

        this.parent = parent;
        this.child = child;

    }
}
