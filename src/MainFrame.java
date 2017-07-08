import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private MemoryPanel memoryPanel;
	
	private JPanel algoPane;
	private JPanel processPane;
	private JPanel holePane;
	private JPanel resetPane;
	private JPanel holeSizesPane;
	private JPanel holeSizesSubmitPane;
	
	private JFrame holeFrame;
	private JFrame processFrame;
	
	private JTextField holeField;
	private myTextField processField;
	private myTextField holeSizeField[];
	private myTextField processIDField;

	private JLabel holeLabel;
	private JLabel processLabel;
	private JLabel title;
	
	private JButton holeButton;
	private JButton processButton;
	private JButton resetButton;
	private JButton algoButton;
	private JButton holeFrameButton;
	private JButton resetMemoryButton;
	private JButton showProcessesButton;
	
	private JTable acceptedProcessTable;
	private JTable rejectedProcessTable;
	
	private ButtonGroup algoGroup;
	private JRadioButton algoRadioButton[];
	
	private int noOfHoles;
	private int holeSizes[];
	private int choosenAlgorithm;
	private int processSize, processID;
	int count = 0;
	ArrayList<Integer[]> acceptedProcessesList;
	ArrayList<Integer[]> rejectedProcessesList;
	
	Object[] acceptedTableColumn = new Object[]{"Accepted Process ID", "Accepted Process Size"};
	Object[] rejectedTableColumn = new Object[]{"Rejected Process ID", "Rejected Process Size"};
	
	private boolean exceptionOccurred = false;
	private final Color activeBackground = new Color(0x212121);
	private final Color deactiveBackground = Color.DARK_GRAY;
	
	private Dimension dim;
	
	public MainFrame(){
		
		acceptedProcessesList = new ArrayList<Integer[]>();
		rejectedProcessesList = new ArrayList<Integer[]>();
		
		algoPane = new JPanel();
		processPane = new JPanel();
		holePane = new JPanel();
		resetPane = new JPanel();
		
		holeField = new JTextField();
		processField = new myTextField("Process Size");
		processIDField = new myTextField("Process ID");
		
		holeLabel = new JLabel("Enter No. of Holes: ");
		processLabel = new JLabel("Process: ");
		title = new JLabel("<html><h1 style=\"font-size:2em;\">MEMORY ALLOCATION<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ALGORITHMS</h1></html>");
		
		holeButton = new JButton("Submit");
		processButton = new JButton("Add");
		algoButton = new JButton("Submit");
		resetButton = new JButton("Reset");
		holeFrameButton = new JButton("Submit");
		resetMemoryButton = new JButton("Reset Memory");
		showProcessesButton = new JButton("Show Processes");
		
		algoGroup = new ButtonGroup();
		algoRadioButton = new JRadioButton[4];
		algoRadioButton[0] = new JRadioButton("First Fit");
		algoRadioButton[1] = new JRadioButton("Next Fit");
		algoRadioButton[2] = new JRadioButton("Worst Fit");
		algoRadioButton[3] = new JRadioButton("Best Fit");
		for(int i=0; i<4; ++i){
			algoGroup.add(algoRadioButton[i]);
			algoRadioButton[i].setForeground(Color.WHITE);
		}
		
		initComponents();
		
		disableComponents(processPane);
		disableComponents(algoPane);
		
		holeButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					noOfHoles = Integer.parseInt(holeField.getText());
					if(noOfHoles > 0){
						holeSizes = new int[noOfHoles];
						
						holeFrame = new JFrame();
						holeSizesPane = new JPanel();
						holeSizesSubmitPane = new JPanel();
						holeFrame.setResizable(false);
						
						holeFrame.setSize(400, 400);
						holeFrame.setLayout(null);
						holeFrame.setBackground(activeBackground);
						
						dim = Toolkit.getDefaultToolkit().getScreenSize();
						holeFrame.setLocation(dim.width/2-holeFrame.getSize().width/2, dim.height/2-holeFrame.getSize().height/2);
						
						holeSizesPane.setSize(holeFrame.getWidth()-5, holeFrame.getHeight()-100);
						holeSizesPane.setBackground(activeBackground);
						holeSizesSubmitPane.setSize(holeFrame.getWidth(), 70);
						TitledBorder tb = BorderFactory.createTitledBorder("Enter the Hole Sizes");
						tb.setTitleColor(Color.WHITE);
						holeSizesPane.setBorder(tb);
						holeSizesSubmitPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
						
						holeSizesPane.setLayout(new FlowLayout(FlowLayout.CENTER));
						holeSizesSubmitPane.setLayout(new FlowLayout(FlowLayout.CENTER));
						holeSizesSubmitPane.setBackground(activeBackground);
						holeFrame.getRootPane().setDefaultButton(holeFrameButton);
						
						holeSizesSubmitPane.add(holeFrameButton);
						holeSizeField = new myTextField[noOfHoles];
						for(int i=0; i<noOfHoles; ++i){
							holeSizeField[i] = new myTextField(Integer.toString(i+1));
							holeSizeField[i].setColumns(5);
							holeSizesPane.add(holeSizeField[i]).setSize(20, 20);
						}
						holeFrame.add(holeSizesPane).setLocation(0, 0);
						holeFrame.add(holeSizesSubmitPane).setLocation(0, holeSizesPane.getHeight());
						
						holeSizesPane.setVisible(true);
						holeFrame.setVisible(true);
					}else{
						JOptionPane.showMessageDialog(MainFrame.this, "At least 1 hole should be there!");
					}
					
				}catch(Exception e){
					JOptionPane.showMessageDialog(MainFrame.this, "Please Provide only Integer!");
				}
			}
		});
		
		holeFrameButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exceptionOccurred = false;
				for(int i=0; i<noOfHoles; ++i){
					try{
						holeSizes[i] = Integer.parseInt(holeSizeField[i].getText());
					}catch(Exception e1){
						JOptionPane.
						showMessageDialog(holeFrame, "Please input only Integers! Integer not found at Field No.: " + (i+1));
						exceptionOccurred = true;
					}
				}
			
				if(!exceptionOccurred){
					memoryPanel = new MemoryPanel(holeSizes);
					addMemoryPanel();
					enableComponents(algoPane);
					disableComponents(holePane);
					holeFrame.dispose();
				}
			}
		});
		
		algoButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				choosenAlgorithm = -1;
				for(int i=1; i<=4; ++i){
					if(algoRadioButton[i-1].isSelected()){
						choosenAlgorithm = i;
					}
				}
				
				if(choosenAlgorithm != -1){
					disableComponents(algoPane);
					enableComponents(processPane);
				}
			}
		});
		
		processButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try{
					processID = Integer.parseInt(processIDField.getText());
					processSize = Integer.parseInt(processField.getText());
					memoryPanel.addProcess(processID, processSize, choosenAlgorithm, acceptedProcessesList, rejectedProcessesList);
					MainFrame.this.repaint();
					MainFrame.this.revalidate();
				}catch(NumberFormatException e){
					JOptionPane.showMessageDialog(MainFrame.this, "Only Integers are allowed!");
				}
			}
		});
		
		resetButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				holeField.setText("");
				processField.setText("");
				count = 0;
				for(int i = 0; i<4; ++i){
					algoRadioButton[i].setSelected(false);
				}
				
				if(memoryPanel != null){
					MainFrame.this.remove(memoryPanel);
					MainFrame.this.repaint();
					MainFrame.this.revalidate();
					memoryPanel = null;
				}
				
				acceptedProcessesList.clear();
				rejectedProcessesList.clear();
				
				enableComponents(holePane);
				disableComponents(processPane);
				disableComponents(algoPane);
			}
		});
		
		showProcessesButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				processFrame = new JFrame();
				
				int i = 0;
				Object[][] acceptedProcesses = new Object[acceptedProcessesList.size()][2];
				for(Integer[] ac : acceptedProcessesList){
					acceptedProcesses[i++] = ac.clone();
				}
				
				i = 0;
				Object[][] rejectedProcesses = new Object[rejectedProcessesList.size()][2];
				for(Integer[] ac : rejectedProcessesList){
					rejectedProcesses[i++] = ac.clone();
				}
				
				acceptedProcessTable = new JTable(acceptedProcesses, acceptedTableColumn);
				rejectedProcessTable = new JTable(rejectedProcesses, rejectedTableColumn);
				JScrollPane accScrollPane = new JScrollPane(acceptedProcessTable);
				JScrollPane rejScrollPane = new JScrollPane(rejectedProcessTable);
				
				processFrame.setSize(400, 600);
				processFrame.setLayout(null);
				processFrame.setResizable(false);
				processFrame.setLocation(dim.width/2-processFrame.getSize().width/2, dim.height/2-processFrame.getSize().height/2);
				processFrame.add(accScrollPane)
							.setBounds(0, 0, processFrame.getWidth(), processFrame.getHeight()/2);
				processFrame.add(rejScrollPane)
							.setBounds(0, processFrame.getHeight()/2, processFrame.getWidth(), processFrame.getHeight()/2);
				
				processFrame.setVisible(true);
			}
		});
		
		resetMemoryButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				memoryPanel.resetMemory();
				acceptedProcessesList.clear();
				rejectedProcessesList.clear();
				enableComponents(algoPane);
				disableComponents(processPane);
			}
		});
		
	}
	
	private void initComponents(){
		this.setSize(1366, 661);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(null);
		this.setBackground(deactiveBackground);
		title.setOpaque(true);
		title.setBackground(new Color(0x212121));
		title.setForeground(Color.WHITE);
		title.setHorizontalAlignment(SwingConstants.CENTER);
		this.setResizable(false);
		this.getRootPane().setDefaultButton(holeButton);
		this.add(title).setBounds(
				0, (int) ( MainFrame.this.getHeight() * 0.095f)*0, 
				MainFrame.this.getWidth(), (int) (MainFrame.this.getHeight() * 0.095f)*2);
		
		/*
		 * holePane
		 */
		holePane.setLayout(new FlowLayout(FlowLayout.CENTER));
		holePane.setSize(MainFrame.this.getWidth(), (int) (MainFrame.this.getHeight() * 0.095f));
		TitledBorder tb = BorderFactory.createTitledBorder("Holes");
		tb.setTitleColor(Color.WHITE);
		tb.setTitleJustification(TitledBorder.CENTER);
		holePane.setBorder(tb);
		holePane.setBackground(new Color(0x212121));
		holeField.setPreferredSize(new Dimension(150, 30));
		holeLabel.setForeground(Color.WHITE);
		holePane.add(holeLabel).setBounds(20, 20, 150, 30);
		holePane.add(holeField).setBounds(170, 20, 150, 30);
		holePane.add(holeButton).setBounds(400, 20, 100, 30);
		holePane.setVisible(true);
		
		/*
		 * algoPane
		 */
		algoPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		algoPane.setSize(MainFrame.this.getWidth(), (int) (MainFrame.this.getHeight() * 0.095f));
		tb = BorderFactory.createTitledBorder("Algorithms");
		tb.setTitleJustification(TitledBorder.CENTER);
		tb.setTitleColor(Color.WHITE);
		algoPane.setBorder(tb);
		for(int i=0; i<4; ++i){
			algoRadioButton[i].setOpaque(false);
			algoPane.add(algoRadioButton[i]);
		}
		algoPane.add(algoButton, FlowLayout.TRAILING);
		algoPane.setVisible(true);
		
		
		/*
		 * processPane
		 */
		processPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		processPane.setSize(MainFrame.this.getWidth(), (int) (MainFrame.this.getHeight() * 0.095f));
		tb = BorderFactory.createTitledBorder("Add Process");
		tb.setTitleJustification(TitledBorder.CENTER);
		tb.setTitleColor(Color.WHITE);
		processPane.setBorder(tb);
		processLabel.setForeground(Color.WHITE);
		processPane.add(processLabel);
		processPane.add(processIDField).setPreferredSize(new Dimension(150, 30));;
		processPane.add(processField).setPreferredSize(new Dimension(150, 30));
		processPane.add(processButton);
		processPane.add(showProcessesButton);
		processPane.setVisible(true);
		
		/*
		 * resetPane
		 */
		resetPane.setLayout(null);
		resetPane.setSize(MainFrame.this.getWidth(),50);
		resetButton.setSize(100, 30);
		resetMemoryButton.setSize(100, 30);
		resetPane.setBackground(activeBackground);
		resetPane.add(resetButton)
				.setLocation(resetPane.getWidth()/2-resetButton.getWidth(), 
						resetPane.getHeight()/2-resetButton.getHeight()/2);
		resetPane.add(resetMemoryButton).setLocation(resetPane.getWidth()/2 + 5, 
				resetPane.getHeight()/2-resetMemoryButton.getHeight()/2);;
		resetPane.setVisible(true);
		
		this.add(holePane).setLocation(0, (int) (MainFrame.this.getHeight() * 0.095f)*2);
		this.add(algoPane).setLocation(0, (int) (MainFrame.this.getHeight() * 0.095f)*3);;
		this.add(processPane).setLocation(0, (int) (MainFrame.this.getHeight() * 0.095f)*4);;
		this.add(resetPane).setLocation(0, 582);
		
		this.setVisible(true);
	}
	
	private void disableComponents(Container c){
		c.setBackground(deactiveBackground);
		for(Component C : c.getComponents()){
			C.setEnabled(false);
		}
	}
	
	private void enableComponents(Container c){
		c.setBackground(activeBackground);
		for(Component C : c.getComponents()){
			C.setEnabled(true);
		}
		if(c == holePane){
			this.getRootPane().setDefaultButton(holeButton);
		}else if(c == processPane){
			this.getRootPane().setDefaultButton(processButton);
		}else if(c == algoPane){
			this.getRootPane().setDefaultButton(algoButton);
		}
	}
	
	private void addMemoryPanel(){
		memoryPanel.setSize(MainFrame.this.getWidth(), (int) (MainFrame.this.getHeight() * 0.4f));
		this.add(memoryPanel).setLocation(0, (int) (MainFrame.this.getHeight() * 0.095f)*5);
		this.repaint();
		this.revalidate();
	}
	
}

@SuppressWarnings("serial")
class myTextField extends JTextField implements FocusListener{
	
	String i;
	public myTextField(String i){
		this.i = i;
		this.setText(i);
		this.addFocusListener(this);
		this.setForeground(Color.gray);
	}

	@Override
	public void focusGained(FocusEvent e) {
		if(this.getForeground() == Color.GRAY){
			this.setText("");
			this.setForeground(Color.BLACK);
		}
	}
	
	@Override
	public void focusLost(FocusEvent e) {
		try{
			Integer.parseInt(this.getText());
		}catch(NumberFormatException e1){
			this.setForeground(Color.GRAY);
			this.setText(i);
		}
	}
	
}
