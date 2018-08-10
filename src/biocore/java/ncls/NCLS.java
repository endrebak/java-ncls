package biocore.java.ncls;

import biocore.java.ncls.Interval;



public class NCLS {

	 private static void printArray(Interval[] intervals){
	        for (int i = 0; i < intervals.length; i++){
	        System.out.println(intervals[i]);
	        }
	    }

    //private static NestedList buildNestedList(){
    //}


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Interval[] intervals = new Interval[] {
        		new Interval(0, 60, -1),
        		new Interval(40, 50, -1),
        		new Interval(30, 50, -1)};
        
        
        System.out.println("Hello World");
        // Interval hi = new Interval(1, 2, 3);        

        NestedList nestedList = new NestedList(intervals);
        // java.util.Arrays.sort(intervals, new IntervalComparator());
        System.out.println("Hiya!");

        printArray(nestedList.intervals);
        System.out.println("Hiya 2!");
        printArray(nestedList.intervals);
        nestedList.sortOnIntervals();
        System.out.println("Hiya 3!");
        printArray(nestedList.intervals);
        nestedList.addParentsInplace();
        printArray(nestedList.intervals);
    }


}
