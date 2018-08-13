package biocore.java.ncls;

public class Hits {
	
	int[] queryIndexes;
	int[] subjectIndexes;
	int nfound = 0;
	
	public Hits(int startNumber) {
		queryIndexes = new int[startNumber];
		subjectIndexes = new int[startNumber];
	}
	
	public void addIndexes(int query, int subject) {
		
		if (nfound < queryIndexes.length) {
			queryIndexes[nfound] = query;
			subjectIndexes[nfound] = subject;
			nfound++;
		} else {
			queryIndexes = java.util.Arrays.copyOf(queryIndexes, queryIndexes.length * 2);
			subjectIndexes = java.util.Arrays.copyOf(subjectIndexes, subjectIndexes.length * 2);
		}
	}
	
	

}
