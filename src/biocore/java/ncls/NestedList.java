package biocore.java.ncls;

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
    	// what is ntop?
    	return intervals.length - nsub;
    }
    
    public void sortOnIntervals() {
    	java.util.Arrays.sort(intervals, new IntervalComparator());

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

        SubListHeader[] subListHeaders = new SubListHeader[nlists];

        for (int i = 0; i < tmpIntervals.length; i++){

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
        // System.out.println("this subheaders " + this.subHeaders);
        this.subHeaders = subListHeaders;
        // System.out.println("this subheaders " + this.subHeaders);
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


    private int findOverlapStartIndex(int start, int end) {
    	int l = 0;
    	int r = getNtop();
    	int ntop = r;
    	int mid;

    	while (l < r) {

    		mid = (l + r) / 2;
    		if (intervals[mid].end <= start) {
    			l = mid + 1;
    		} else {
    			r = mid;
    		}

    	}

    	if ((l < ntop) && intervals[l].hasOverlap(start, end)) {

    		return l;

    	} else {

    	    return -1;

    	}
    }
    
    private int findSubOverlapStart(int start, int end, int sublist) {
    	
    	int overlapStartIndex = findOverlapStartIndex(start, end);
    	
    	if (overlapStartIndex >= 0) {
    		return overlapStartIndex + subHeaders[sublist].length;
    	} else {
    	    return -1;
    	}
    	
    }


    public Interval[] findOverlaps(int start, int end) {

        int i = findOverlapStartIndex(start, end);
        int sublist = -1; // child, right?
        int subOverlapStart = -1;
        
        int nfound = 0;
        Interval[] overlaps = new Interval[64];
        // 64 could be any number.
        // It should be possible to tweak on a per DB basis for optimization purposes...
        OverlapIterator it = new OverlapIterator(i, getNtop());
        OverlapIterator it2 = null;
        
        while (true) {
        	System.out.println(it);
        	System.out.println((intervals[it.start].hasOverlap(start, end)));
		    while ((it.start >= 0) && (it.start < it.end) && (intervals[it.start].hasOverlap(start, end))) {
		    	overlaps[nfound++] = intervals[it.start]; 
		    	sublist = intervals[it.start++].sublist;
		    	// System.out.println("Sublist:" + sublist);
		    	if (sublist >= 0) {
		    		subOverlapStart = findSubOverlapStart(start, end, sublist);
		    		
		    		if (subOverlapStart >= 0) {
		    			System.out.println("In suboverlap start.");
		    	    	if (it.child != null) {
			    			System.out.println("child not null.");
		    	    		it2 = it.child;
		    	    	} else {
			    			System.out.println("child null.");
		    	    		it2 = new OverlapIterator(-1, -1, it, it2);
		    	    	}
		    	    	
		    	    	it2.start = subOverlapStart;
		    	    	it2.end = subHeaders[sublist].start + subHeaders[sublist].length;
		    	    	
		    	    	it = it2;
		    	    }
		    	}
		    } 

		    if (it.parent != null) {
		    	System.out.println("parent is not null");
		    	it = it.parent;
		    } else {
		    	System.out.println("parent is null");
		    	break;
		    }

        }
        return overlaps;

    } 

}



