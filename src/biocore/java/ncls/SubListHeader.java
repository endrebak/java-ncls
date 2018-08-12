package biocore.java.ncls;

public class SubListHeader {

    int start;
    int length;

    public SubListHeader(int start, int length){
        this.start = start;
        this.length = length;
    };
    
    @Override
    public String toString(){

        String s = "(" + start + ", " + length + ")";
        return s;

    }

}
