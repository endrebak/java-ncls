package biocore.java.ncls;

import java.util.Arrays;

import biocore.java.ncls.IntervalComparator;


public class NestedList {

    Interval[] intervals;
    SubListHeader[] subHeaders;
    int nsub;
    int nlists;


    public NestedList(Interval[] intervals) {
        // TODO Auto-generated constructor stub
        this.intervals = intervals;
        this.subHeaders = null;
        this.nsub = -1;
        this.nlists = -1;
    }

    private int getNtop() {
    	return intervals.length - nsub;
    }
    
    public void sortOnIntervals() {
    	java.util.Arrays.sort(intervals, new IntervalComparator());

    }

    public static void sortOnSubLists(Interval[] tmpIntervals) {
    	java.util.Arrays.sort(tmpIntervals, new SubListComparator());

    }
    
    public void addParentsInplace(){

        int nsub = 0;
        int i = 0;
        int parent;

        while (i < intervals.length){

            parent = i;
            i = parent + 1;

            while ((i < intervals.length) && (parent >= 0)){

                boolean contained = (intervals[i].end > intervals[parent].end);

                boolean same = (intervals[i].end == intervals[parent].end) &&
                    (intervals[i].start == intervals[parent].start);

                if (same || contained){
                    parent = intervals[parent].sublist;
                } else {
                    intervals[i].sublist = parent;
                    nsub++;
                    parent = i;
                    i++;
                }
            }
        }

        this.nsub = nsub;

    }

    public Interval[] setHeaderIndexes(){

        int parent;
        int nlists = 0;
        int j = 0;

        Interval[] tmpIntervals = new Interval[nsub];

        for (int i = 0; i < intervals.length; i++){

            parent = intervals[i].sublist;

            if (parent >= 0) {

                tmpIntervals[j++] = new Interval(i, -1, parent);

                if (intervals[parent].sublist < 0){
                    intervals[parent].sublist = nlists++;
                }

            }

            intervals[i].sublist = -1;
        }

        this.nlists = nlists;

        return tmpIntervals;
    }

    public boolean[] createSubListHeader(Interval[] tmpIntervals){

    	sortOnSubLists(tmpIntervals);
        SubListHeader[] subListHeaders = new SubListHeader[nlists];
        for (int i = 0; i < nlists; i++){

            subListHeaders[i] = new SubListHeader(0, 0);

        }

        int j, parent, k;
        Interval tmpInterval;
        boolean[] toDelete = new boolean[intervals.length];

        for (int i = 0; i < nsub; i++){

            j = tmpIntervals[i].start;
            parent = tmpIntervals[i].sublist;

            tmpInterval = intervals[j];
            tmpIntervals[i] = new Interval(tmpInterval.start, tmpInterval.end, tmpInterval.sublist);

            k = intervals[parent].sublist;

            if (subListHeaders[k].length == 0) {
                subListHeaders[k].start = i;
            }

            subListHeaders[k].length += 1;

            toDelete[j] = true;

        }
        this.subHeaders = subListHeaders;
        return toDelete;
    }

    public void removeSublists(boolean[] toDelete, Interval[] tmpIntervals){

        int j = 0;
        for (int i = 0; i < toDelete.length; i++) {

            if (!toDelete[i]){
                if (j < i){
                    intervals[j] = intervals[i];
                }
                j++;
            }
        }

        for (int k = 0; k < nsub; k++){
            intervals[j + k] = tmpIntervals[k];
        }

        for (int i = 0; i < nlists; i++){

            subHeaders[i].start += j;

        }

    }

    public void buildNestedList(){

    	sortOnIntervals();
        addParentsInplace();

        Interval tmpIntervals[];
        boolean toDelete[];

        if (nsub > 0) {

            tmpIntervals = setHeaderIndexes();
           
            toDelete = createSubListHeader(tmpIntervals);

            removeSublists(toDelete, tmpIntervals);

        }
    }


    private int findOverlapStartIndex(int start, int end, int lidx, int ridx) {
    	
    	int ntop = ridx + 1;
    	int mid;

    	while (lidx < ridx) {

    		mid = (lidx + ridx) / 2;
    		if (intervals[mid].end <= start) {
    			lidx = mid + 1;
    		} else {
    			ridx = mid;
    		}

    	}

    	// System.out.println("intervals[lidx] " + intervals[lidx]);
    	
    	if ((lidx < ntop) && intervals[lidx].hasOverlap(start, end)) {
    		return lidx;

    	} else {

    	    return -1;

    	}
    }
    
    private int findPreviousIndex(int start, int end, int lidx, int ridx) {
    	
    	int mid;

    	while (lidx < ridx) {

    		mid = (lidx + ridx) / 2;
    		if (intervals[mid].end <= start) {
    			lidx = mid + 1;
    		} else {
    			ridx = mid;
    		}

    	}

    	// System.out.println("intervals[lidx] " + intervals[lidx]);
    	

    	return lidx;


    }
    
    private int findSubOverlapStart(int start, int end, int sublist) {
    	
    	// System.out.println("subHeaders[sublist].start " + subHeaders[sublist].start);
    	// System.out.println("subHeaders[sublist].end " + (subHeaders[sublist].start + subHeaders[sublist].length - 1));

    	int overlapStartIndex = findOverlapStartIndex(start, end, subHeaders[sublist].start, 
    			subHeaders[sublist].start + subHeaders[sublist].length - 1) - subHeaders[sublist].start;

    	// System.out.println("overlapStartIndex " + overlapStartIndex);

    	if (overlapStartIndex >= 0) {
    		return overlapStartIndex + subHeaders[sublist].start;
    	} else {
    	    return -1;
    	}
    	
    }


    public Interval[] findOverlaps(int start, int end) {

        int i = findOverlapStartIndex(start, end, 0, getNtop() - 1);
        int sublist = -1; // child, right?
        int subOverlapStart = -1;
        
        int nfound = 0;
        Interval[] overlaps = new Interval[64];
        // could be any size.
        // It should be possible to tweak on a per DB basis for optimization purposes...
        OverlapIterator it = new OverlapIterator(i, getNtop());
        OverlapIterator it2 = null;
        
        while (true) {
 
		    while ((it.start < it.end) && (intervals[it.start].hasOverlap(start, end))) { // (it.start >= 0) &&
	        	// System.out.println("it " + it);

		    	if (!(nfound < overlaps.length)) 
		    		overlaps = Arrays.copyOf(overlaps, overlaps.length * 2);

	    		overlaps[nfound++] = intervals[it.start]; 
	    		sublist = intervals[it.start++].sublist;
		    	if (sublist >= 0) {
		    		subOverlapStart = findSubOverlapStart(start, end, sublist);
		    		// if (subOverlapStart >= 0) {

		    	    	if (it.child != null) {
		    	    		it2 = it.child;
		    	    	} else {
			    	    	it2 = new OverlapIterator(-1, -1, it, null);
		    	    	}

		    	    	it2.start = subOverlapStart;   	
		    	    	it2.end = subHeaders[sublist].start + subHeaders[sublist].length;

		    	    	it = it2;
		    	    // }
		    	}
		    } 

		    if (it.parent != null) {
		    	it = it.parent;
		    } else {
		    	break;
		    }

        }
        return overlaps;

    } 

    public Interval[] findNextNonOverlapping(int start, int end, int k) {

        int i = findOverlapStartIndex(start, end, 0, getNtop() - 1);
        int sublist = -1; // child, right?
        int subOverlapStart = -1;
        
        int nfound = 0;
        Interval[] overlaps = new Interval[k];
        // could be any size.
        // It should be possible to tweak on a per DB basis for optimization purposes...
        OverlapIterator it = new OverlapIterator(i, getNtop());
        OverlapIterator it2 = null;
        
        while (true) {
 
		    while ((it.start < it.end) && (nfound < k)) { // (it.start >= 0) && 
		    	// System.out.println("it.start " + it.start);
		    	// System.out.println("intervals[it.start " + intervals[it.start]);
		    	if (!(intervals[it.start].hasOverlap(start, end))) {
		    		overlaps[nfound++] = intervals[it.start]; 
		    	}
	    		sublist = intervals[it.start++].sublist;
	    		// System.out.println("sublist " + sublist);
		    	if (sublist >= 0) {
		    		subOverlapStart = subHeaders[sublist].start;
		    		// System.out.println("subOverlapStart " + subOverlapStart);
		    		//if (subOverlapStart >= 0) {

		    	    	if (it.child != null) {
		    	    		it2 = it.child;
		    	    	} else {
			    	    	it2 = new OverlapIterator(-1, -1, it, null);
		    	    	}

		    	    	it2.start = subOverlapStart;   	
		    	    	it2.end = subHeaders[sublist].start + subHeaders[sublist].length;

		    	    	it = it2;
		    	    //}
		    	}
		    } 

		    if (it.parent != null) {
		    	it = it.parent;	
		    } else { 
		    	break;
		    }

        }
        return overlaps;

    } 
    
    public Interval[] findPreviousNonOverlapping(int start, int end, int k) {

        int i = findPreviousIndex(start, end, 0, getNtop() - 1);
        int sublist = -1; // child, right?
        int subOverlapEnd = -1;
        
        int nfound = 0;
        Interval[] overlaps = new Interval[k];
        // could be any size.
        // It should be possible to tweak on a per DB basis for optimization purposes...
        OverlapIterator it = new OverlapIterator(i, i);
        OverlapIterator it2 = null;
        int nit = 0;
        while (true) {
    		System.out.println("----- it " + it);
            
		    while ((nfound < k) && (it.start <= it.end)) { // (it.start >= 0) && 
		    	System.out.println("--------------------");
		    	System.out.println("it " + it);
		    	System.out.println("it.start " + it.start);
		    	System.out.println("intervals[it.end] " + intervals[it.end]);
	    		System.out.println("----- nit " + nit);

		    	if (!(intervals[it.end].hasOverlap(start, end))) {
		    		overlaps[nfound++] = intervals[it.end]; 
		    	}
	    		sublist = intervals[it.end--].sublist;
	    		System.out.println("sublist " + sublist);
		    	if (sublist >= 0) {
		    		subOverlapEnd = subHeaders[sublist].start + subHeaders[sublist].length - 1;
		    		System.out.println("subOverlapEnd " + subOverlapEnd);
		    		// if (subOverlapStart >= 0) {

	    	    	if (it.child != null) {
	    	    		it2 = it.child;
	    	    	} else {
		    	    	it2 = new OverlapIterator(-1, -1, it, null);
	    	    	}

	    	    	it2.start = subHeaders[sublist].start; // subOverlapStart;   	
	    	    	it2.end = subOverlapEnd;

	    	    	it = it2;
		    	    // }
		    	}
		    } 

		    if (it.parent != null) {
		    	it = it.parent;	
		    } else { 
		    	break;
		    }

        }
        return overlaps;

    } 
}



