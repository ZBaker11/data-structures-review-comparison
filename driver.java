package csc365;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class driver {
    public static void main(String[] args) throws IOException {
    	// PROJ 1 / 2
    	
//        proj1 proj = new proj1();
//        GUI gui = new GUI(proj);
//
//        proj.parseJSON();
//        proj.populateTables();
//        String[] medioids = proj.getMedioids(3);
//        
//        System.out.println("Medioids: ");
//        for (String s : medioids)
//        	System.out.println(s + "\n\n");
//        
//        HashMap<String, ArrayList<String>> clusters = proj.getClusters(medioids);
//        System.out.println("Clusters: ");
//        for (String s : clusters.keySet()) {
//        	String[] clustersArr = clusters.get(s).toArray(String[] :: new);
//        	for (int i = 0; i < clustersArr.length && i < 3; i++)
//        		System.out.println(i + "\n" + clustersArr[i] + "\n");
//        }
        //gui.createAndShowGUI();
        
    	
    	proj3 proj = new proj3();
    	proj.parseJSON();
    	//proj.calcDists();
    	System.out.println(proj.getNumDisjoint());
    	//System.out.println(Arrays.asList(proj.getConnected("IX25aSHBIfYd9fbSKlP4qg")));
    	
    }
}

