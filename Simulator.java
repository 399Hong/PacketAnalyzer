

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import javax.swing.table.AbstractTableModel;
import java.lang.Object;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;


/**
 *
 * @author yi-yunzhang
 */
@java.lang.SuppressWarnings({"all", "unchecked"})
public class Simulator extends JFrame {
    
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private JFrame frame;
    private JPanel filterPanel;
    //private JPanel tablePanel;
    
    // component varibles for menu <START>
    private JMenuBar menuBar;
    private JMenu file;
    private JMenuItem fileChooser;
    private JMenuItem quit;
    //private JMenuItem save;

    private JFileChooser chooser;
    // component varibles for menu <END>
    
    
    // component variables for Button <START>
    private JRadioButton srcHosts;
    private JRadioButton DestoHosts;
    private ButtonGroup group;
    private JComboBox hostSelector;
    // component variables for Button <END>
    
    private JTable table;
    private JScrollPane scrollPane;
    
    
    private Object[] uniqueSrcHosts;
    private Object[] uniqueDestoHosts;
    private DefaultComboBoxModel srcModel;
    private DefaultComboBoxModel DestoModel;
    private dataTableModel srcTableModel;
    private dataTableModel DestoTableModel;
    private SimulatorData data;
    
    
    String fileName;
     
    /**
    * Constructor for the simulator class.
    * it sets up the GUI.
    */
    public Simulator() {
        
        // setting up the Frame
        frame = new JFrame("Simulator");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 500);
        frame.setMinimumSize(new Dimension(450, 200));
        frame.setVisible(true);
        frame.pack();
        //Setting up the Panel
        initMenu();
        initDatafilter();
        initTablePanel();
        
    }
    /**
    * Initializer for the Menu bar section of the GUI.
    * it sets up the menu bar and menu items.
    */
    public void initMenu(){
        
        menuBar = new JMenuBar();
        file = new JMenu("File");
        fileChooser = new JMenuItem("Open trace file");
        quit = new JMenuItem("Quit");
        //save = new JMenuItem("Save");
        System.out.println("hi");
        frame.setJMenuBar(menuBar);
        menuBar.add(file);
        file.add(fileChooser);
        file.add(quit);
        //file.add(save);
        
        
        fileChooser.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                chooser = new JFileChooser();
                //FileNameExtensionFilter filter = new FileNameExtensionFilter(
                //   "JPG & GIF Images", "jpg", "gif");
                //chooser.setFileFilter(filter);

                int returnVal = chooser.showOpenDialog(null);
                
                if(returnVal == JFileChooser.APPROVE_OPTION) {
                    // DISPLAY RADIOBUTTION IF FILE SELECTED
                    srcHosts.setEnabled(true);
                    DestoHosts.setEnabled(true);
                    frame.repaint();
                    
                    fileName = chooser.getSelectedFile().getAbsolutePath();
                    System.out.println("You chose to open this file: " +
                        chooser.getSelectedFile().getAbsolutePath());
                    //comboBox setup
                    data = new SimulatorData(new File(fileName));
                    uniqueDestoHosts = data.getUniqueSortedDestHosts();
                    uniqueSrcHosts = data.getUniqueSortedSourceHosts();
                    
                    //update combobox data
                    srcModel = new DefaultComboBoxModel(uniqueSrcHosts);
                    DestoModel = new DefaultComboBoxModel(uniqueDestoHosts);
                    
                    hostSelector.setVisible(true);
                    
                    loadTableContent();
                    setRowColour(table);
                    
                    
                        
                    
                    
                    
                    
                }
                
                
            }


        
        });
        quit.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        
        });
        
    
    
    }
    /**
    * Initializer for the data filter section of the GUI
    * it sets up the radio buttons and combo box.
    */
    public void initDatafilter(){
        
        // setting up format and position of the datafilter panel
        filterPanel = new JPanel();
        //filterPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        filterPanel.setBackground(Color.WHITE);
        frame.add(filterPanel, BorderLayout.NORTH);
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
       
        //filterPanel.setPreferredSize(new Dimension(200, 100));
       
        
        
        srcHosts = new JRadioButton("Source Hosts");
        srcHosts.setSelected(true);
        DestoHosts = new JRadioButton("Destination Hosts");
        group = new ButtonGroup();
        
        hostSelector = new JComboBox();
        
        group.add(srcHosts);
        group.add(DestoHosts);
        
        
        filterPanel.add(srcHosts);
        filterPanel.add(DestoHosts);
        filterPanel.add(hostSelector);
        //updating comboBox according to radiobutton
        srcHosts.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               
                hostSelector.setModel(srcModel); 
                
                String IP = String.valueOf(hostSelector.getSelectedItem());
               
                Packet[] p =  data.getTableData(IP, true);
                //System.out.println(p.toString());
                srcTableModel = new dataTableModel(p,true);
                table.setModel(srcTableModel);

                
                
                
            }
        
        
        });
        
        DestoHosts.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               
                hostSelector.setModel(DestoModel); 
                
                String IP = String.valueOf(hostSelector.getSelectedItem());
                Packet[] p = data.getTableData(IP, false);
                //System.out.println(p.toString());
                
                DestoTableModel = new dataTableModel(p,false);
                table.setModel(DestoTableModel);
                
            }
        
        });
        
        hostSelector.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
              loadTableContent();
            }
            
        
        
        });
        
        
        srcHosts.setEnabled(false);
        DestoHosts.setEnabled(false);
        hostSelector.setVisible(false);
       
        
        
        
        
    }
    /**
    * Initializer for the table section of the GUI.
    * it sets up the scroll pane and table .
    */
    public void initTablePanel() {
        
        String[] columnNames = {"Time Stamp", "IP Address", "Packet Size"};
        Object[][] empty = {};
        
        
        table = new JTable(empty,columnNames);
        table.setFillsViewportHeight(true);
        scrollPane = new JScrollPane(table);
        
        //tablePanel = new JPanel();  
        frame.add(scrollPane,BorderLayout.CENTER);
        //tablePanel.setBackground(Color.BLACK);

        
        
    }
    /**
    * Inherited class no comments required according to the description.
    */
    public class dataTableModel extends AbstractTableModel{
        int totalSize = 0;
        double avgSize = 0;
        int row;
        int col = 3;
        Object[][] data;
        boolean src;
        Packet[] packet;
        // getSourceHost(), getDestinationHost(), getTimeStamp(), and getIpPacketSize();
        public dataTableModel(Packet[] packet, boolean setup){
            this.packet = packet;
            src = setup;
            row = packet.length;
            // 2 additional row for total size and avg size
            data = new Object[row+2][col];
            int entryRow = 0;
            if(setup){
                for(Packet p: packet){
                    totalSize += p.getIpPacketSize();
                    data[entryRow][0] = new Double(p.getTimeStamp());
                    data[entryRow][1] = p.getDestinationHost();
                    data[entryRow++][2] = p.getIpPacketSize(); 
                }
            }else{  
                for(Packet p: packet){
                    totalSize += p.getIpPacketSize();
                    data[entryRow][0] = new Double(p.getTimeStamp());
                    data[entryRow][1] = p.getSourceHost();
                    data[entryRow++][2] = p.getIpPacketSize(); 
                }

            }
            row = packet.length;
            avgSize = totalSize/row;
            row += 2;
            data[entryRow++][2] = totalSize;
            data[entryRow][2] = avgSize;


        }
        @Override
        public int getColumnCount(){return col;}
        @Override
        public int getRowCount(){return row;}
        @Override
        public Object getValueAt(int row,int col){return data[row][col];}
        @Override
        public String getColumnName(int column){
            if(src){
                String[] columnNames = {"Time Stamp", "Destination IP Address", "Packet Size"};
                return columnNames[column];
              
            }else{
                String[] columnNames = {"Time Stamp", "Source IP Address", "Packet Size"};
                return columnNames[column];
            }
            
            
        }
        //public void setRowColour(int row) {
        //    fireTableRowsUpdated(row, row);
        //}
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex){
            //System.out.println(rowIndex);
            //System.out.println(row);
            return (rowIndex < row-2)&& (columnIndex == 2);
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            
            //System.out.println(packet[rowIndex].sequence);
            try {
                replaceSize(packet[rowIndex].sequence, String.valueOf(data[rowIndex][2]), (String)aValue);
                data[rowIndex][2] = Integer.parseInt((String) aValue);
            } catch (IOException ex) {
                Logger.getLogger(Simulator.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        


        
}
    /**
    * Setting up the row color of the table.
    * it sets up the scroll pane and table .
    * @param  table the table that is being changed
    */
    public void setRowColour(JTable table){
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer(){
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {   
            int rowCt = table.getRowCount();
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            //System.out.println(c);
            table.setSelectionBackground(Color.red);
            c.setBackground(row % 2 == 0 ?new Color(230,230,230): Color.WHITE);
            if (row == rowCt-1 || row == rowCt-2){
                c.setBackground(new Color(255,255,224));
            }
            return c;
        }
        });
    }
    /**
    * loading up table contents and combo box.
    * it sets up table and combo box based on the selected radio button.
    */
    public void loadTableContent(){
        
        if(srcHosts.isSelected()){
            if(hostSelector.getModel()!= srcModel){
                hostSelector.setModel(srcModel);
            }

            String IP = String.valueOf(hostSelector.getSelectedItem());
            Packet[] p = data.getTableData(IP, true);
            //System.out.println(p.toString());
            srcTableModel = new dataTableModel(p,true);
            table.setModel(srcTableModel);
        }else{
            if(hostSelector.getModel()!= DestoModel){
               hostSelector.setModel(DestoModel);
            }
            String IP = String.valueOf(hostSelector.getSelectedItem());
            Packet[] p = data.getTableData(IP, false);

            DestoTableModel = new dataTableModel(p,false);
            table.setModel(DestoTableModel);

        }
    }
    /**
    * Replace packet size at a certain entry
    * it changes the packet size of a certain entry and update it with user input
    * @param sequence  the entry/sequence number for looking for the correct data in file 
    * @param originalSize it is a redundant param for testing 
    * @param newSize  new packet size that the user inputted
    */
    public void replaceSize(String sequence, String originalSize,String newSize) throws IOException{
        
        Path path = Paths.get(fileName);
        ArrayList<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));

        for (int i = 0; i < fileContent.size(); i++) {
            Packet p = new Packet(fileContent.get(i));
            // looking for entry that matches the sequence number
            if (p.sequence.equals(sequence)) {
                
                // change packet size
                p.data[7] = newSize;
                
                String newData =  String.join("\t", p.data);
                
                fileContent.set(i,newData);
                
                
                break;
            }
        }

        Files.write(path, fileContent, StandardCharsets.UTF_8);
        
        // updating data for displaying correct data
        data = new SimulatorData(new File(fileName));
        loadTableContent();
    
    
    }
    /**
    * comparing two IP addresses
    */
    class Host implements Comparable<Host>{
    private String s;
  
    /**
    * Constructor for setting up a host
    * @param s an IP address
    */
    public Host(String s){
        this.s = s;
    }
    /**
    * return host in a string format.
    * @return   return host in a string format eg: "192.168.0.24"
    */
    public String toString(){
        return this.s;
    }
    /**
    * split each IP address into 4 section and compare two IP address each section
    * by each section.
    * @param  other the IP address is compared to.
    * @return   a negative integer, zero, or a positive integer as this object
    * is less than, equal to, or greater than the specified object.
    */
    public int compareTo(Host other){
        if(s.toString()== other.toString()){
          return 0;
    
        }else{
           String[] thisStr = s.toString().split("\\.");
           String[] otherStr = other.toString().split("\\.");
           for(int i = 0; i < 4;i++){
               int thisIP = Integer.parseInt(thisStr[i]);
               int otherIP = Integer.parseInt(otherStr[i]);
               if (thisIP>otherIP){
                   return 1;
                }else if (thisIP<otherIP){
                    return -1;
                }
            }
         }
        return 0;
    }
    
    
}
    
    /**
    * process a string and turn it into a more organized format
    */
    class Packet {
    private String sourceHost,destoHost;
    private double timeStamp;
    private int packetSize = 0;
    private String sequence;
    String[] data;
    /**
    * The constructor : process a string of information and re-organize data.
    * re-organize data into an array and separate the information based on their
    * sequence, source/desto host. time stamp and packet set. it also detects for
    * valid packets
    * @param s an string of information that may contain valid information.
    */
    public Packet(String s){
        data = s.split("\\t");
        try{
        sequence = data[0];
        sourceHost = data[2];
        destoHost = data[4];
        timeStamp = Double.parseDouble(data[1]);
        packetSize = Integer.parseInt(data[7]);
        }catch(Exception e){}
    }
    /**
    * getter of source host 
    * @return source host
    */
    public String getSourceHost(){return sourceHost;}
    /**
    * getter of destination host 
    * @return destination host
    */
    public String getDestinationHost(){return destoHost;}
    /**
    * getter of time stamp
    * @return time stamp
    */
    public double getTimeStamp(){return timeStamp;}
    /**
    * getter of packet size
    * @return packet size
    */
    public int getIpPacketSize(){return packetSize;}
    /**
    * setter of source host
    * @param sourceHost source host
    */
    public void setSourceHost(String sourceHost){ this.sourceHost = sourceHost;}
    /**
    * setter of destination host.
    * @param destoHost destination host
    */
    public void setDestinationHost(String destoHost){this.destoHost = destoHost;}
    /**
    * setter of time stamp.
    * @param timeStamp 
    */
    public void setTimeStamp(double timeStamp){this.timeStamp = timeStamp;}
    /**
    * setter of packet size.
    * @param packetSize 
    */
    public void setIpPacketSize(int packetSize){this.packetSize = packetSize;}
    /**
    * return packet in a string format.
    * @return   return packet in this format "src=, dest=, time=, size="
    */
    public String toString(){
        return String.format("src=%s, dest=%s, time=%.2f, size=%d",sourceHost,destoHost,timeStamp,packetSize);
    }
    
}   
    /**
    * process data from file
    */
    class SimulatorData {
    File file;
    ArrayList<Packet> packets = new ArrayList<Packet>();
    ArrayList<String> uniqueSortedSrcHosts = new ArrayList<String>();
    ArrayList<Host> uniqueSortedSrcHostsWithHostObject = new ArrayList<Host>();
    
    ArrayList<String> uniqueSortedDestoHosts = new ArrayList<String>();
    ArrayList<Host> uniqueSortedDestoHostsWithHostObject = new ArrayList<Host>();
    
    ArrayList<Packet> srcPackets = new ArrayList<Packet>();
    ArrayList<Packet> destoPackets = new ArrayList<Packet>();
    
    Packet[] packetForDisplay; 
    /**
    * processing the file.
    * processes the file and obtain all valid packet entry
    * @param file  file that needs to be processed
    */
    public SimulatorData(File file){
        this.file = file;
        try{
            Scanner in = new Scanner(this.file);
            while(in.hasNextLine()){
                Packet p = new Packet(in.nextLine());
                //System.out.println(p);
                if(!"".equals(p.getSourceHost()) && !"".equals(p.getDestinationHost()) && p.getIpPacketSize()!= 0 ){
                packets.add(p);}
                
            }
                in.close();
            
        }catch(java.io.FileNotFoundException e){
            System.out.println(e.toString()); 
        }
        
        
        
    }
    
    //public  ArrayList<Packet> getValidPackets(){
    //    return packets;
    //}
    
    /**
    * process all packets and gets unique sorted source hosts.
    * @return           an array unique sorted source hosts.
    */
    public Object[] getUniqueSortedSourceHosts(){
        
        for(Packet p:packets){
            
            String srcHost = p.getSourceHost();
            if (!uniqueSortedSrcHosts.contains(srcHost)){
                uniqueSortedSrcHosts.add(srcHost);
                uniqueSortedSrcHostsWithHostObject.add(new Host(srcHost));
            }
                
        }
        
        Collections.sort(uniqueSortedSrcHostsWithHostObject);
        return uniqueSortedSrcHostsWithHostObject.toArray();
        
    }
    /**
    * process all packets and gets unique sorted destination hosts.
    * @return           an array unique sorted destination hosts.
    */
    public Object[] getUniqueSortedDestHosts(){   
        
        for(Packet p:packets){
            
            String srcHost = p.getDestinationHost();
            if (!uniqueSortedDestoHosts.contains(srcHost)){
                uniqueSortedDestoHosts.add(srcHost);
                uniqueSortedDestoHostsWithHostObject.add(new Host(srcHost));
            }
                
        }
        
        Collections.sort(uniqueSortedDestoHostsWithHostObject);
        return uniqueSortedDestoHostsWithHostObject.toArray();
        
    }
    /**
    * process all packets and re-organize data.
    * re-organize data(with a given IP address)into a format that 
    * can be used by the table model.
    * @param ip an ip address that user is wish to process.
    * @param isSrcHost  source hose or destination host.
    * @return           an array of packets that the user required.
    */
    public Packet[] getTableData(String ip,boolean isSrcHost ){
        
        // avoid piling up
        srcPackets.clear();
        destoPackets.clear();
        if(isSrcHost){
            for(Packet p:packets){
            String srcHost = p.getSourceHost();
             
                if (ip.equals(srcHost)){
                    srcPackets.add(p);
                }
            }
            
        Object[] p = srcPackets.toArray();
        //System.out.println(srcPackets.size());
        return  Arrays.copyOf(p, p.length, Packet[].class);
                
        }else{
            for(Packet p:packets){
            String destoHost = p.getDestinationHost();
                if (ip.equals(destoHost)){
                    destoPackets.add(p);
                }
            }
        Object[] p = destoPackets.toArray();
        return  packetForDisplay = Arrays.copyOf(p, p.length, Packet[].class);
        
        }
        
        
    }
        
        
} 
    
    /**
    * run the GUI on a new thread
    */
    public static void main(String[] args) {
        
   	javax.swing.SwingUtilities.invokeLater(new Runnable() {
           public void run() {
               new Simulator();
            
           }
       });
    
    
    
    }
}


