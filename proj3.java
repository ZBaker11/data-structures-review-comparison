package csc365;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class proj3 {

	HashMap<String, Double> lats = new HashMap<String, Double>();
	HashMap<String, Double> longs = new HashMap<String, Double>();
	String[] businesses; 
	
    public void parseJSON() {
        JSONParser parser = new JSONParser();

        try (BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\zachb\\eclipse-workspace\\csc365\\yelp_dataset\\yelp_academic_dataset_business.json"))) {
            for (int i = 0; i < 10_000; i++) { // file reading things 
                String line = br.readLine();
                JSONObject o = (JSONObject) parser.parse(line); // each line is a JSONObject
                lats.put(o.get("business_id").toString(), Double.parseDouble(o.get("latitude").toString()));
                longs.put(o.get("business_id").toString(), Double.parseDouble(o.get("longitude").toString()));
            }
            businesses = lats.keySet().toArray(new String[0]);
        }
        catch(IOException | ParseException e) {
          System.out.println(e);
        }
    }
    
    public void calcDists() throws IOException {
    	System.out.println("calculating distances... ");
  
	    String path = "C:\\Users\\zachb\\eclipse-workspace\\csc365\\distances\\";
	    BufferedWriter writer;
	    
    	for (int i = 0; i < businesses.length; i++) {
        	HashMap<String, Double> dists = new HashMap<String, Double>();
    		String id1 = businesses[i];
    		for (int j = 0; j < businesses.length; j++) {
    			if (j == i) {
    				j++;
    				if (j >= businesses.length)
    					break;
    			}
    			String id2 = businesses[j];
    			dists.put(id2, calcDist(id1, id2));
    		}
    		
    		String[] closest = {"", "", "", ""};
    		dists.put("", 99999.0);
    		for (Entry<String, Double> entry : dists.entrySet()) {
    			for (int j = 0; j < closest.length; j++) {
    				if (entry.getValue() < dists.get(closest[j])) {
    					closest[j] = entry.getKey();
    					if (dists.get(closest[j]) == 0) {
    						System.out.println(calcDist(id1, closest[j]));
    					}
    					break;
    				}
    			}
    		}
    		
    		File tempFile = new File(path + id1);
        	if (!tempFile.exists()) {
        		writer = new BufferedWriter(new FileWriter(tempFile));
        		for (int j = 0; j < closest.length; j++) {
        			writer.write(closest[j]);
        			writer.newLine();
        			writer.append(dists.get(closest[j]).toString());
        			writer.newLine();
        		}
        		writer.close();
        	}
    	}
    	System.out.println("done!");
    }
    
    
    public String[] closestFour(String id) throws FileNotFoundException {
    	String path = "C:\\Users\\zachb\\eclipse-workspace\\csc365\\distances\\" + id;
    	File f = new File(path);
    	Scanner scanner = new Scanner(f);
    	
    	String[] closestFour = new String[4];
    	for (int i = 0; i < 4; i++) {
    		closestFour[i] = scanner.nextLine();
    		scanner.nextLine();
    	}

    	scanner.close();
    	return closestFour;
    }

    public String[] getReachable(String start) throws FileNotFoundException {
    	LinkedList<String> queue = new LinkedList<String>();
    	queue.addAll(Arrays.asList(closestFour(start)));
    	HashSet<String> connected = new HashSet<String>();
    	
    	while (!queue.isEmpty()) {
    		String next = queue.poll();
    		connected.add(next);
    		for (String s : Arrays.asList(closestFour(next))) {
    		    if (!connected.contains(s) && !queue.contains(s))
    		    	queue.add(s);
    		}
    	}
    	return connected.toArray(new String[0]);
    }
    
    public int getNumDisjoint() throws FileNotFoundException {
    	Set<String> businesses = lats.keySet();
    	LinkedList<String> queue = new LinkedList<String>();
    	queue.addAll(businesses);
    	
    	HashSet<HashSet<String>> checked = new HashSet<HashSet<String>>();
    	
    	while (!queue.isEmpty()) {
    		List<String> set = Arrays.asList(getReachable(queue.poll()));
    		HashSet<String> merged = null;
    		for (String s : set) {
    			for (HashSet<String> setIt : checked) {
    				if (merged == null && setIt.contains(s)) {
    					setIt.addAll(set);
    					merged = setIt;
    				}
    				else if (merged != null && setIt.contains(s)) {
    					merged.addAll(setIt);
    					setIt = null; 
    				}
    			}
    		}
    		if (merged == null)
    			checked.add(new HashSet<String>(set));
    		queue.removeAll(set);
    	}
    	for (HashSet<String> set : checked) {
    		if (set == null) {
    			checked.remove(set);
    			System.out.println("hi");
    		}
    	}

    	
    	return checked.size();
    }
    
    
    public Double calcDist(String id1, String id2) {
    	if (id1 == id2)
    		return 0.; 
    	
    	final double R = 6371.0088; // Radius of the earth
    	
    	double lat1 = lats.get(id1);
    	double lat2 = lats.get(id2);
    	double long1 = lats.get(id1);
    	double long2 = lats.get(id2);
    	
    	 Double latDistance = toRad(lat2-lat1);
    	 Double lonDistance = toRad(long2-long1);
    	 Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + 
    	 Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
    	 Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    	 Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    	 Double distance = R * c;
    	 
    	 return distance;
    }
    
    private static Double toRad(Double value) {
    	 return value * Math.PI / 180;
    }
    
}

