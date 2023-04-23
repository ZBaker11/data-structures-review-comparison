package csc365;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;

public class GUI {

    proj1 handle;
    public GUI(proj1 proj) { handle = proj; }

    public void createAndShowGUI() { // gui things
        JFrame f= new JFrame();  
        f.setMinimumSize(new Dimension(1800, 1000));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JButton button = new JButton("Get similar");  
        button.setBounds(20,520,150,50);  

        String[] l1 = handle.getReviews();
        final JList<String> list1 = new JList<>(l1);  
        list1.setBounds(0,0, 900,500);  
        // JScrollPane scrollPane1 = new JScrollPane();
        // scrollPane1.setViewportView(list1);
        // list1.setLayoutOrientation(JList.VERTICAL);
        
        DefaultListModel<String> l2 = new DefaultListModel<>();
        JList<String> list2 = new JList<String>(l2);
        list2.setBounds(900,0, 900, 500); 
        // JScrollPane scrollPane2 = new JScrollPane();
        // scrollPane2.setViewportView(list2);
        // list2.setLayoutOrientation(JList.VERTICAL);

        JLabel list1zoom = new JLabel();
        list1zoom.setBounds(20, 600, 1750, 120);
        JLabel list2zoom = new JLabel();
        list2zoom.setBounds(20, 740, 1750, 120);

        f.add(list1); f.add(list2); f.add(button); f.add(list1zoom); f.add(list2zoom);
        // f.add(scrollPane1); f.add(scrollPane2);
        f.setSize(450,450);  
        f.setLayout(null);  
        f.setVisible(true);  

        button.addActionListener(new ActionListener() {  
            @Override
            public void actionPerformed(ActionEvent e) {
                String selected = "";  
                if (list1.getSelectedIndex() != -1) {                       
                    selected = list1.getSelectedValue();  
                    l2.clear();
                    for (String s : handle.getSimilarities(selected)) 
                        l2.addElement(s);
                }  
        }}); 

        for (;;) {
            String selectedReview1 = list1.getSelectedValue();
            String selectedReview2 = list2.getSelectedValue();
            list1zoom.setText("<html>" + selectedReview1 + "</html>");
            list2zoom.setText("<html>" + selectedReview2 + "</html>");
        }
    }
}