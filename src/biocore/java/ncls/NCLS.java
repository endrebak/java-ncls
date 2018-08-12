package biocore.java.ncls;

import biocore.java.ncls.Interval;

public class NCLS {

	 private static void printSubHeaderArray(SubListHeader[] subheaders){
		 System.out.println("Subheaders");
	        for (int i = 0; i < subheaders.length; i++){
	        System.out.println(subheaders[i]);
	        }
	    }
	
	 public static void printIntervalArray(Interval[] intervals){
		 System.out.println("Intervals");
	        for (int i = 0; i < intervals.length; i++){
	        	if (intervals[i] != null) {
	                System.out.println(intervals[i]);
	        	}
	        }
	    }

    //private static NestedList buildNestedList(){
    //}


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Interval[] intervals = new Interval[] {
        		new Interval(90, 115, -1),
        		new Interval(0, 60, -1),
        		new Interval(40, 50, -1),
        		new Interval(30, 50, -1),
        		new Interval(31, 49, -1),
        		new Interval(32, 49, -1)};
        
        
        //System.out.println("Hello World");
        // Interval hi = new Interval(1, 2, 3);        

        NestedList nestedList = new NestedList(intervals);
        // java.util.Arrays.sort(intervals, new IntervalComparator());
        //System.out.println("Hiya!");
        
        nestedList.buildNestedList();
        
        printIntervalArray(nestedList.intervals);
        printSubHeaderArray(nestedList.subHeaders);

        Interval[] results;
        
        results = nestedList.findOverlaps(30, 32);
        
        printIntervalArray(results);

    }


}
