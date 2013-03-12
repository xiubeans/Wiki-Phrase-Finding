import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NBSortMerger {
	
	Hashtable<String, Integer> C;
	int MAX_HEAP = 50000000;	// heap no more than 80 MB
	
	public NBSortMerger() {
		this.C = new Hashtable<String, Integer>();
	}
	
	/* 
	 * Merge items with same key,
	 * And put the merged item to hashtable C
	 */
	public void mergeCounters(ArrayList<String> items) {
				
		if(items.size() == 0)
			return;
		
		/* init thie item in hashtable */
		String key = items.get(0).substring(0, items.get(0).indexOf(":")).trim();
		this.C.put(key, 0);
		
		/* merge counters for the same key */
		for(int i = 0; i < items.size(); i++) {
			String item = items.get(i);
			Integer counter = Integer.parseInt(item.substring(item.indexOf(":") + 1, item.length()).trim());
			this.C.put(key, this.C.get(key) + counter);
		}
		
	}
	
	/*
	 * Flush all keys to the stdout
	 */
	public void flushCountersToStdout() {
		
		/* print out counters */
		Iterator<String> itr = this.C.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			System.out.println(key + "	:	" + C.get(key));
		}
		
		/* clear the hashtable */
		this.C.clear();
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		String item = "";
		String cur_key = "";
		String last_key = "";
		ArrayList<String> items = new ArrayList<String>();
		
		try {
			NBSortMerger mg = new NBSortMerger();
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));			
			
			/* loop read output from trainer */
			while((item = in.readLine()) != null) {
				cur_key = item.substring(0, item.indexOf(":")).trim();
				
				/* if this key is the same as last one */
				if(cur_key.equals(last_key))
					items.add(item);
				
				/* else need merge */
				else {

					mg.mergeCounters(items);

					if(Runtime.getRuntime().totalMemory() > mg.MAX_HEAP)
						mg.flushCountersToStdout();
					
					items.clear();
					last_key = cur_key;
					items.add(item);
				}
			}
			mg.mergeCounters(items);
			mg.flushCountersToStdout();	
			
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println("cur_item is " + item);
		}
		
	}

}
