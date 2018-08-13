package biocore.java.ncls;

import biocore.java.ncls.Interval;

public class NCLS {

	 private static void printSubHeaderArray(SubListHeader[] subheaders){
		 System.out.println("Subheaders");
	        for (int i = 0; i < subheaders.length; i++){
	        System.out.println(i + ": " + subheaders[i]);
	        }
	    }
	
	 public static void printIntervalArray(Interval[] intervals){
		 System.out.println("Intervals");
	        for (int i = 0; i < intervals.length; i++){
	        	if (intervals[i] != null) {
	                System.out.println(i + ": " + intervals[i]);
	        	}
	        }
	    }

	 public static void printHits(Hits hits){
		 System.out.println("Hits");
	        for (int i = 0; i < hits.nfound; i++){
	                System.out.println(i + ": " + hits.queryIndexes[i] + ", " + hits.subjectIndexes[i]);
	        }
	    }
    //private static NestedList buildNestedList(){
    //}


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        Interval[] intervals = new Interval[] {
        		new Interval(90, 115, -1),
        		new Interval(0, 60, -1),
        		new Interval(45, 65, -1),
        		new Interval(40, 50, -1),
        		new Interval(30, 50, -1),
        		new Interval(0, 1, -1),
        		new Interval(31, 49, -1),
        		new Interval(32, 49, -1),
        		new Interval(120, 150, -1),
        		new Interval(130, 135, -1)};
        
            // TODO Auto-generated method stub
            Interval[] intervals2 = new Interval[] {
            		new Interval(14, 53, -1),
            		new Interval(55, 90, -1),
            		new Interval(4, 66, -1)};
        
        //System.out.println("Hello World");
        // Interval hi = new Interval(1, 2, 3);        

        NestedList nestedList = new NestedList(intervals);
        NestedList nestedList2 = new NestedList(intervals2);
        // java.util.Arrays.sort(intervals, new IntervalComparator());
        System.out.println("nestedList " + nestedList.nlists + ", " + nestedList.nsub + ", " + nestedList.intervals.length);
        System.out.println("nestedList2 " + nestedList2.nlists + ", " + nestedList2.nsub + ", " + nestedList2.intervals.length);
        
        //nestedList.buildNestedList();
        
        printIntervalArray(nestedList.intervals);
        printSubHeaderArray(nestedList.subHeaders);

        printIntervalArray(nestedList2.intervals);
        printSubHeaderArray(nestedList2.subHeaders);
        Interval[] results;
        
        // results = nestedList.findOverlaps(30, 31);
        // results = nestedList.findNextNonOverlapping(30, 31, 15);
        // results = nestedList.findPreviousNonOverlapping(134, 137, 3);
        // printIntervalArray(results);
        
        Hits hits = nestedList.findOverlaps(nestedList2);

        System.out.println(hits);
        printHits(hits);
    }


}
