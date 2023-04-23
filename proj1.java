package csc365;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class proj1 { 

    int numWords = 0; // total number of words inc. duplicates
    static ArrayList<String> reviews = new ArrayList<String>(); // list of reviews
    HashMap<String, HT> wc = new HashMap<String, HT>(); // word counts for all review strings
    HashMap<String, Integer> docsContaining = new HashMap<String, Integer>(); // cache for idf
    HashMap<String, String> similars = new HashMap<String, String>(); // cache for similarity
    HashMap<String, Double> Ksim = new HashMap<String, Double>(); // hashmap of each review's average similarity to all other reivews

    public String[] getReviews() {
        return reviews.toArray(new String[0]);
    }

    public void parseJSON() {
        JSONParser parser = new JSONParser();

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\zachb\\eclipse-workspace\\csc365\\yelp_dataset\\yelp_academic_dataset_review.json"))) {
            for (int i = 0; i < 10_000; i++) { // file reading things 
                String line = br.readLine();
                JSONObject o = (JSONObject) parser.parse(line); // each line is a JSONObject
                reviews.add(o.get("text").toString());
            }
        }
        catch(IOException | ParseException e) {
          System.out.println(e);
        }
    }


    public void populateTables() {
        for (String review : reviews) { // get per-review and total word counts 
            HT wordCount = new HT();
            for (String word : review.split(" ")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                wordCount.add(word);
                numWords++;
            }
            wc.put(review, wordCount);
        }
    }
    
    public void populatePersistent() throws IOException {
		    BufferedWriter writer;
		    String path = "C:\\Users\\zachb\\eclipse-workspace\\csc365\\persistent\\";
		    
		    System.out.println("Creating persistent hash stores... ");
		    
		    for (String r : reviews) {
				String ext = String.valueOf(r.hashCode());
				
				File tempFile = new File(path + ext);
		    	if (!tempFile.exists()) {
					writer = new BufferedWriter(new FileWriter(tempFile));
			    	String[] similars = getSimilarities(r);
					writer.append(String.valueOf(Ksim.get(r)) + "\n"); // put down average similarity to all other reviews	
			
			    	for (int i = 0; i < 20; i++)
			    		writer.append(similars[i]);
			    	writer.close();
		    	}
		    }
		    
		    System.out.println("Done!");
    }
    
    public HashMap<String, ArrayList<String>> getClusters(String[] medioids) {
    	HashMap<String, ArrayList<String>> clusters = new HashMap<String, ArrayList<String>>();
    	
    	for (String s : medioids) 
    		clusters.put(s, new ArrayList<String>());
    	
    	for (String r : reviews) {
    		double similarity = 0; 
    		String closest = medioids[0];
    		for (int i = 0; i < medioids.length; i++) {
    			double tempSim = getSimilarity(r, medioids[i]);
    			if (tempSim > similarity) {
    				similarity = tempSim;
    				closest = medioids[i];
    			}
    		}
    		clusters.get(closest).add(r);
    	}
    	return clusters;
    }
    
    public String[] getMedioids(int n) { // gets the n number of reviews with highest average similarity(tf-idf) scores
    	String path = "C:\\Users\\zachb\\eclipse-workspace\\csc365\\persistent\\";
    	HashMap<String, Double> highestN = new HashMap<String, Double>(); 
    	highestN.put("", 0.0); // starting value
    	
    	for (String r : reviews) {
    		String ext = String.valueOf(r.hashCode());
    		File f = new File(path + ext);
    		
    		try {
				Scanner scanner = new Scanner(f);
				if (scanner.hasNext()) {
					double k = Double.parseDouble(scanner.nextLine());
					for (Double d : highestN.values()) {
						if (k > d) {
							highestN.put(r, k);
							if (highestN.size() > n)
								removeLowest(highestN);
							break;
						}
					}
				}
				scanner.close();
				
			} catch (FileNotFoundException e) { e.printStackTrace(); }
    	}
    	return highestN.keySet().toArray(String[] :: new);
    }
    
    
    public void removeLowest(HashMap<String, Double> h) {    // MODIFIES ARGUMENT	
    	String lowest = h.keySet().stream().toArray(String[] :: new)[0]; // removes element with smallest double in hashmap<String, double>
    	for (String s : h.keySet()) 
    		if (h.get(s) < h.get(lowest))
    			lowest = s;
    	h.remove(lowest);
    }

    public double getSimilarity(String review1, String review2) { // returns a double; higher = more similar
        double similarity = 0; 
        HT wc1 = wc.get(review1);
        HT wc2 = wc.get(review2);
        
        for (Object word : wc1.keySet()) {
            int count = wc2.get(word);
            if (count > 0) {
                similarity += ((double) count / wc2.size) * getIDF(word.toString());
            }
        }
        return similarity; 
    }


    public double getIDF(String word) { // returns how rare a word is based on occurences in reviews
        return Math.log((double) reviews.size() / docsContaining(word));
    }


    public int docsContaining(String word) { // returns number of reviews containing word
        if (docsContaining.get(word) != null) 
            return docsContaining.get(word);// use cache 

        int count = 0;
        for (HT table : wc.values()) 
            if (table.contains(word))
                count++;

        docsContaining.put(word, count);
        return count;
    }


    public String[] getSimilarities(String review1) { // returns sorted list of similar reviews based on given review
    	double total = 0;
        HashMap<String, Double> similarities = new HashMap<String, Double>();
        
        for (String review2 : reviews) {
        	double similarity = getSimilarity(review1, review2);
            similarities.put(review2, similarity);
            total += similarity;
        }

        Ksim.put(review1, total / reviews.size());
        
        HashMap<String, Double> sortedMap = sortHashMapByValues(similarities);
        return sortedMap.keySet().stream().skip(1).toArray(String[] :: new); // skip the first, which will always be the same review
    }


    /*
    method to reverse sort a hashmap by values, since similarities are stored in a hashmap<String, Double>, 
    where string is another review, and double is the similarity metric.
    */
    public LinkedHashMap<String, Double> sortHashMapByValues(HashMap<String, Double> map) { 
        List<String> mapKeys = new ArrayList<String>(map.keySet());
        List<Double> mapValues = new ArrayList<Double>(map.values());
        Collections.sort(mapValues, Collections.reverseOrder());
        Collections.sort(mapKeys, Collections.reverseOrder());


        LinkedHashMap<String, Double> sortedMap = new LinkedHashMap<>();

        Iterator<Double> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Double val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                Double comp1 = map.get(key);
                Double comp2 = val;

                if (comp1.equals(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }

        return sortedMap;
    }
}