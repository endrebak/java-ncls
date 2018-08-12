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
    	return this.intervals.length - this.nsub;
    }
    
    public void sortOnIntervals() {
    	java.util.Arrays.sort(this.intervals, new IntervalComparator());

    }

    public void addParentsInplace(){

        int nsub = 0;
        int i = 0;
        int parent;

        while (i < this.intervals.length){

            parent = i;
            i = parent + 1;

            while ((i < this.intervals.length) && (parent >= 0)){

                boolean contained = (this.intervals[i].end > this.intervals[parent].end);

                boolean same = (this.intervals[i].end == this.intervals[parent].end) &&
                    (this.intervals[i].start == this.intervals[parent].start);

                if (same || contained){
                    parent = this.intervals[parent].sublist;
                } else {
                    this.intervals[i].sublist = parent;
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

        Interval[] tmpIntervals = new Interval[this.nsub];

        for (int i = 0; i < this.intervals.length; i++){

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

        SubListHeader[] subListHeaders = new SubListHeader[this.nlists];

        for (int i = 0; i < tmpIntervals.length; i++){

            subListHeaders[i] = new SubListHeader(0, 0);

        }

        int j, parent, k;
        int nbToDelete = 0;
        Interval tmpInterval;
        boolean[] toDelete = new boolean[this.intervals.length];

        for (int i = 0; i < this.nsub; i++){

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

        for (int k = 0; k < this.nsub; k++){
            intervals[j + k] = tmpIntervals[k];
        }

        for (int i = 0; i < this.nlists; i++){

            this.subHeaders[i].start += j;

        }

    }

    public void buildNestedList(){

    	this.sortOnIntervals();
        this.addParentsInplace();

        Interval tmpIntervals[];
        boolean toDelete[];

        if (this.nsub > 0) {

            tmpIntervals = this.setHeaderIndexes();

            toDelete = this.createSubListHeader(tmpIntervals);

            this.removeSublists(toDelete, tmpIntervals);

        }
    }


    private int findOverlapStartIndex(int start, int end) {
    	int l = 0;
    	int r = this.getNtop();
    	int ntop = r;
    	int mid;

    	while (l < r) {

    		mid = (l + r) / 2;
    		if (this.intervals[mid].end <= start) {
    			l = mid + 1;
    		} else {
    			r = mid;
    		}

    	}

    	if ((l < ntop) && this.intervals[end].hasOverlap(start, end)) {

    		return l;

    	} else {

    	    return -1;

    	}
    }


    public void findOverlaps(int start, int end) {

        int i = this.findOverlapStartIndex(start, end);

        Interval[] overlaps = new Interval[64];
        // 64 could be any number.
        // It should be possible to tweak on a per DB basis for optimization purposes...
        OverlapIterator it = new OverlapIterator(i, this.getNtop());

        
    }


}
