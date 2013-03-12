import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileReader;

public class NBSortClassifier {

	/* fields */
	Hashtable<String, Integer> C;
	Hashtable<String, String> corpus;
	ArrayList<String> label_list;
	int i = 0;
	
	/* methods */
	
	/*
	 * Constructor
	 */
	public NBSortClassifier() {
		this.C = new Hashtable<String, Integer>();
		this.corpus = new Hashtable<String, String>();
		this.label_list = new ArrayList<String>();
	}
	
	/*
	 * Build the corpus
	 */
	public void buildCorpus(String test_dataset) {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(test_dataset));
			
			String doc;
			while((doc = br.readLine()) != null) {
				Vector<String> tokens = tokenizeDoc(doc);
				Iterator<String> itr = tokens.iterator();
				itr.next();	// escape the list of labels
				
				while(itr.hasNext()) {
					String token = itr.next();
					if(!this.corpus.containsKey(token))
						this.corpus.put(token, "");
				}
			}
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	/*
	 * Build the needed hashtable for counters, as well as the label_list
	 * Read input from stdin
	 */
	public void buildC() {
		
		String key;
		Integer counter;
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try{
			String line;
			
			/* setup meta data, and the label_list */
			while((line = in.readLine()) != null && line.charAt(0) == '*') {
				//System.out.println("Read one line: " + line);
				
				key = line.substring(0, line.indexOf(":")).trim();
				counter = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length()).trim());
				
				
				/* put to label_list */
				if(key.startsWith("*ld*", 0)) {
					//System.out.println("label number: " + this.label_list.size());
					String label = key.substring(4).trim();
					int i = 0;
					for(; i < label_list.size(); i++) {
						if(label_list.get(i).equals(label))
								break;
					}
					if(i == label_list.size())
						label_list.add(label);
				}
				
				/* keep every meta data */
				this.C.put(key, counter);
			}
			
			/* deal with THE single line, stupid... */
			key = line.substring(0, line.indexOf(":")).trim();
			counter = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length()).trim());
			this.C.put(key, counter);
			
			/* normal cases: "Y=y, X=x" */
			while((line = in.readLine()) != null) {
				
				key = line.substring(0, line.indexOf(":")).trim();
				counter = Integer.parseInt(line.substring(line.indexOf(":") + 1, line.length()).trim());
				
				/* check whether this word is in corpus */
				String word = key.substring(key.indexOf("\t") + 1);
				if(this.corpus.containsKey(word)) 
					this.C.put(key, counter);
				
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/*
	 * Classify one doc
	 * Return:
	 * 	0  - miss
	 *  1  - hit
	 */
	public int classifyDoc(String doc) {
		
		int hit = 0;
		
		/* tokenize the doc at first */
		Vector<String> tokens = tokenizeDoc(doc);
		
		/* init logs for each label */
		Hashtable<String, Double> ht = new Hashtable<String, Double>();
		for(int i = 0; i < this.label_list.size(); i++) 
			ht.put(label_list.get(i), 0.0);

		/* calculate log for each label */
		Iterator<String> labelItr = ht.keySet().iterator();
		while(labelItr.hasNext()) {
			
			String label = labelItr.next();
			double log = 0.0;
			
			Iterator<String> tkItr = tokens.iterator();
			tkItr.next();	// escape the string of tokens
			while(tkItr.hasNext()) {
				double tokenCounter = 0;
				double size_corpus = this.C.get("*tw*");
				String token = tkItr.next();
				String tokenKey = label + "\t" + token;
				if(!this.C.containsKey(tokenKey))
					tokenCounter = 1;
				else
					tokenCounter = this.C.get(tokenKey) + 1;
				log += Math.log(tokenCounter / (this.C.get("*lw* " + label) + (double)size_corpus));
			}
			log += Math.log((this.C.get("*ld* " + label) + (double)1) / (this.C.get("*td*") + (double)this.label_list.size()));
			
			ht.put(label, log);
		}
		
		/* find the label with maximum log value */
		String max_label = "";
		double max_log = Double.parseDouble(Integer.MIN_VALUE + "");
		Iterator<String> itr = ht.keySet().iterator();
		while(itr.hasNext()) {
			String label = itr.next();
			double log = ht.get(label);
			if(log > max_log) {
				max_label = label;
				max_log = log;
			}
		}
		
		/* print out the stuff */
		System.out.println(doc.substring(0, 
				doc.indexOf("\t")) + "\t" + max_label + "\t" + ht.get(max_label));
		
		/* determine whether hit or not */
		/* get valid labels we need */
		String[] all_labels = doc.substring(0, doc.indexOf('\t')).split(",");
		for(int i = 0; i < all_labels.length; i++) {
			if(all_labels[i].equals(max_label)) {
				hit = 1;
				break;
			}
		}
		
		return hit;
		
	}
	
	/*
	 * Print the label list
	 */
	public void printLabelList() {
		String print = "Labels: ";
		for(int i = 0; i < this.label_list.size(); i++) {
			print += this.label_list.get(i) + " ";
		}
		System.out.println(print);
	}
	
	/*
	 * Print the hashtable C to stdout
	 */
	public void printHashtable() {
		
		Iterator<String> itr = this.C.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			System.out.println(key + "	:	" + C.get(key));
		}
		
	}
	
	/*
	 * Print the corpus to stdout
	 */
	public void printCorpus() {
		
		Iterator<String> itr = this.corpus.keySet().iterator();
		while(itr.hasNext()) {
			String key = itr.next();
			System.out.println(key + "	:	" + C.get(key));
		}
		
	}
	
	public void print(String str) {
		System.out.println(str);
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
	
	public static void main(String[] args) {
		
		if(args.length != 1) {
			System.out.println("Please provide one test file");
			return;
		}
		
		try {

			/* init the classifier */
			NBSortClassifier cf =  new NBSortClassifier();
			
			/* build the corpus of test dataset */
			cf.buildCorpus(args[0]);
			
			/* read from stdin: get meta data and the hashtable C */
			cf.buildC();
			
			/* tear down the corpus */
			cf.corpus.clear();
			
			/* classify each doc one by one */
			BufferedReader br = new BufferedReader(new FileReader(args[0]));
			String doc;
			int num_hit = 0;
			int num_doc = 0;
			while((doc = br.readLine()) != null) {
				int decision = cf.classifyDoc(doc);
				if(decision == -1) 
					continue;
				else if(decision == 0) {
					num_doc ++;
					continue;
				}
				else{
					num_hit++;
					num_doc++;
				}
			}
			br.close();
			
			/* print accuracy */
			System.out.println("Percent Accuracy: " + 
					num_hit + "/" + num_doc + " = " + (double)num_hit / (double)num_doc);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
