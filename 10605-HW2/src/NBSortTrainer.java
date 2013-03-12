import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NBSortTrainer{
	
	Hashtable<String, Integer> temC = new Hashtable<String, Integer>();
	int MAX_HEAP = 50000000;	// heap no more than 80 MB
	
	/*
	 * Constructor: init the hashtable as empty
	 */
	public NBSortTrainer() {
		
		this.initHashtable();
		
	}

	public void initHashtable() {
		
		this.temC.clear();
		this.temC.put("*td*", 0);
		this.temC.put("*tw*", 0);
		
	}

	/*
	 * output keys:
	 * 	*: number of training docs
	 *  **: number of total words
	 *  *#<label_name>: used to record types of label
	 */
	public void learnOneDoc(String doc) {
		
		/* get all labels and keep them in hashtable at first */
		String[] all_labels = doc.substring(0, doc.indexOf('\t')).split(",");
					
		/* update "Y=y, X=x" for each label */
		Vector<String> tokens = tokenizeDoc(doc);
		Iterator<String> itr = tokens.iterator();
		itr.next(); 	// escape the the string of labels
		while(itr.hasNext()) {
			String token = itr.next();
			for(int i = 0; i < all_labels.length; i++) {
				String key = all_labels[i] + "\t" + token;
				if(!this.temC.containsKey(key))
					this.temC.put(key, 1);
				else
					this.temC.put(key, this.temC.get(key) + 1);
			}
		}
		
		/* update "Y=y", "Y=*", "Y=*, X=*", and "Y=y, X=*" for each label */
		for(int i = 0; i < all_labels.length; i++) {
			String label = all_labels[i];
			
			/* update "Y=y"	*/
			if(!this.temC.containsKey("*ld* " + label)) 
				this.temC.put("*ld* " + label, 1);
			else
				this.temC.put("*ld* " + label,
						this.temC.get("*ld* " + label) + 1);
			
			/* update "Y=y, X=*" */
			if(!this.temC.containsKey("*lw* " + label)) 
				this.temC.put("*lw* " + label, tokens.size() - 1);
			else
				this.temC.put("*lw* " + label,
						this.temC.get("*lw* " + label) + tokens.size() - 1);
			
			/* update "Y=*" and "Y=*, X=*" globally */ 
			this.temC.put("*td*", this.temC.get("*td*") + 1);
			this.temC.put("*tw*", this.temC.get("*tw*") + tokens.size() - 1);
		}
		
	}
	
	/*
	 * Print counters to stdout
	 * And clear the hashtable in memory
	 */
	public void flushCountersToStdout() {
	
		/* print the hashtable to stdout */
		this.printHashtable();
		
		/* clear counters in hashtable */
		this.initHashtable();
		
	}
	
	/*
	 * Print the hashtable C to stdout
	 */
	public void printHashtable() {
		
		Iterator<String> itr = this.temC.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			System.out.println(key + "	:	" + temC.get(key));
		}
		
	}
	
	/*
	 * Tokenize a document
	 * Parameter: 
	 * 	cur_doc - an input doc
	 * Return:
	 * 	the vector of tokenized items in cur_doc
	 */
	protected static Vector<String> tokenizeDoc(String cur_doc) {
		
        String[] words = cur_doc.split("\\s+");
        Vector<String> tokens = new Vector<String>();
        
        for (int i = 0; i < words.length; i++) {
        	 words[i] = words[i].replaceAll("\\W", "");
             if (words[i].length() > 0) {
            	 tokens.add(words[i]);
             }
       
        }
        
        return tokens;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		
		NBSortTrainer trainer = new NBSortTrainer();
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			
			/* read from stdin line by line, until EOF */
			String doc;
			while((doc = in.readLine()) != null) {
				trainer.learnOneDoc(doc);
				
				/* if out-of-memory, flush */
				if(Runtime.getRuntime().totalMemory() >= trainer.MAX_HEAP) {
					//System.out.println("Now we are using " + Runtime.getRuntime().totalMemory());
					trainer.flushCountersToStdout();
				}
			}
			
			trainer.flushCountersToStdout();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
