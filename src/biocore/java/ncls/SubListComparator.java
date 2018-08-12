package biocore.java.ncls;

import java.util.Comparator;


// def _sort_on_sublists_then_start(a, b):

//    if a.sublist < b.sublist:
//        return -1
//    elif a.sublist > b.sublist:
//        return 1
//    elif a.start < b.start:
//        return -1
//    elif a.start > b.start:
//        return 1
//    else:
//        return 0


public class SubListComparator implements Comparator<Interval> {
	  public int compare(Interval i1, Interval i2) {

	      if (i1.sublist < i2.sublist){
	          return -1;
	      } else if (i1.sublist > i2.sublist){
	          return 1;
	      } else if (i1.start < i2.start){
	          return -1;
	      } else if (i1.start > i2.start){
	          return 1;
	      } else {
	          return 0;
	      }
	  }
	}