import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MemoryPanel extends JLayeredPane{
	
	private int holes[], holeCopy[], offsets[], offsetsCopy[];

	private final int FIRST = 1; 
	private final int NEXT = 2; 
	private final int WORST = 3; 
	private final int BEST = 4;
	private final int HEIGHT = 70;
	private final int offsetY = 100;
	private final int blockWidth = 30;
	private final int baseLayer = 0;
	private final int processLayer = 1;
	private final int highlightLayer = 2;

	private float RATIO = 0.5f;
	private int curr, maxHoleIndex, maxHole, minHole, minHoleIndex;
	private int maxOffset, totalHoleSum = 0;
	
	private final Color holeColor = new Color(0x03A9F4);
	private final Color blockColor = new Color(0x303F9F);
	private final Color highlightColor = new Color(0x8BC34A);
	private final Color faultColor = Color.RED;
	private final Color selectionColor = new Color(0x4CAF50);
	private final Color acceptColor = new Color(0x9A1285);
	private final Color backgroundColor = new Color(0x212121);
	private final Color borderColor = Color.BLACK;
	private final BlockPanel baseHolePanels[];
	
	public MemoryPanel(int holes[]){
		TitledBorder titledBorder = BorderFactory.createTitledBorder("Memory");
		titledBorder.setTitleJustification(TitledBorder.CENTER);
		titledBorder.setTitleColor(Color.WHITE);
		
		this.holes = holes;
		this.holeCopy = holes.clone();
		this.setBackground(backgroundColor);
		this.setLayout(null);
		this.setBorder(titledBorder);
		baseHolePanels = new BlockPanel[holes.length];
		
		for(int i=0; i<holeCopy.length; ++i){
			totalHoleSum += holeCopy[i];
		}
		
		RATIO = (float)(1366-100-(holeCopy.length-1)*30)/(float)(totalHoleSum);
		
		System.out.println(RATIO);
		
		curr = 0;
		
		offsets = new int[holes.length];
		offsets[0] = 50;
		for(int i=1; i<holes.length; ++i){
			offsets[i] += (int)(offsets[i-1] + Math.ceil(holes[i-1]*RATIO) + blockWidth);
		}
		offsetsCopy = offsets.clone();
		maxOffset = (int) (offsets[holes.length-1] + holes[holes.length-1]*RATIO);
		
		for(int i=0; i<holes.length; ++i){
			baseHolePanels[i] = new BlockPanel(
												offsetsCopy[i], offsetY, 
												(int) Math.ceil(holes[i]*RATIO), HEIGHT, 
												holes[i], -1,
												holeColor, holeColor);
			this.add(baseHolePanels[i].startAnimation(), new Integer(baseLayer));
			
			if(i != holes.length-1){
				this.add(
						new BlockPanel(
								(int) Math.ceil(offsetsCopy[i]+holes[i]*RATIO), offsetY, 
								blockWidth, HEIGHT, 
								-1, -1,
								blockColor, blockColor).startAnimation(),
						new Integer(baseLayer));
			}
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2d.setColor(new Color(0x212121));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		g2d.drawString("Smallest Hole: " + smallestHole(), 50, this.getHeight()-40);
		
		g2d.setColor(Color.WHITE);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
		g2d.drawString("Largest Hole: " + largestHole(), 250, this.getHeight()-40);
		
		g2d.setColor(holeColor);
		g2d.fillRect(this.getWidth()-340, this.getHeight()-50, 30, 30);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Hole", this.getWidth()-300, this.getHeight()-30);
		
		g2d.setColor(blockColor);
		g2d.fillRect(this.getWidth()-240, this.getHeight()-50, 30, 30);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Block", this.getWidth()-200, this.getHeight()-30);
		
		g2d.setColor(acceptColor);
		g2d.fillRect(this.getWidth()-140, this.getHeight()-50, 30, 30);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Process", this.getWidth()-100, this.getHeight()-30);
		
	}
	
	public boolean addProcess(int processID, int process, int algo, ArrayList<Integer[]> acc, ArrayList<Integer[]> rej){
		switch(algo){
			case FIRST:
				firstFit(process, processID, acc, rej);
				break;
			case NEXT:
				nextFit(process, processID, acc, rej);
				break;
			case WORST:
				worstFit(process, processID, acc, rej);
				break;
			case BEST:
				bestFit(process, processID, acc, rej);
				break;
		}
		return false;
	}

	private void bestFit(final int process, final int processID, final ArrayList<Integer[]> acc, final ArrayList<Integer[]> rej) {
		
		Thread algo = new Thread(new Runnable() {
			
			@Override
			public void run() {
				minHoleIndex = 0;
				minHole = Integer.MAX_VALUE;
				boolean flag = true;
				BlockPanel highlightPanel;
				BlockPanel choosenPanel = new BlockPanel(
						offsetsCopy[0], offsetY, 
						(int) (process*RATIO), HEIGHT,
						process, processID,
						selectionColor, borderColor);
				ValidityPanel vpOld = new ValidityPanel(offsetsCopy[0], offsetY, (int) (process*RATIO), HEIGHT);
				ValidityPanel vpNew;
				
				for(int i=0; i<holeCopy.length; ++i){
					highlightPanel 
							= new BlockPanel(
											offsetsCopy[i], offsetY, 
											(int) (process*RATIO), HEIGHT, 
											process, processID,
											highlightColor, Color.WHITE);
					vpOld = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
					vpNew = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
					if(offsetsCopy[i] != Integer.MAX_VALUE){
						addPanel(highlightPanel);
						tryCatchSleep(1000);
						if(process <= holeCopy[i] && holeCopy[i] <= minHole){
							if(choosenPanel != null){
								removePanel(choosenPanel);
								removeValidityPanel(vpOld);
							}
							choosenPanel = highlightPanel;
							vpOld = vpNew;
							paintPanel(choosenPanel, selectionColor);
							addValidityPanel(vpOld, ValidityPanel.CORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vpOld);
							minHole = holeCopy[i];
							minHoleIndex = i;
						}else{
							paintPanel(highlightPanel, faultColor);
							addValidityPanel(vpNew, ValidityPanel.INCORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vpNew);
							removePanel(highlightPanel);
						}
					}
				}
				
				if(holeCopy[minHoleIndex] >= process){
					paintPanel(choosenPanel, acceptColor);
					removeValidityPanel(vpOld);
					setLayer(choosenPanel, processLayer);
					holeCopy[minHoleIndex] -= process;
					if(holeCopy[minHoleIndex] == 0)	offsetsCopy[minHoleIndex] = Integer.MAX_VALUE;;
					baseHolePanels[minHoleIndex].updateHoleValue(holeCopy[minHoleIndex]);
					offsetsCopy[minHoleIndex] += (process*RATIO);
					flag = false;
					acc.add(new Integer[]{processID, process});
				}
				if(flag){
					rej.add(new Integer[]{processID, process});
				}
			}
		});
		algo.start();
	}

	private void worstFit(final int process, final int processID, final ArrayList<Integer[]> acc, final ArrayList<Integer[]> rej) {

		Thread algo = new Thread(new Runnable() {
			
			@Override
			public void run() {
				boolean flag = true;
				maxHoleIndex = 0;
				maxHole = -1;
				BlockPanel highlightPanel;
				BlockPanel choosenPanel = new BlockPanel(
						offsetsCopy[0], offsetY, 
						(int) (process*RATIO), HEIGHT,
						process, processID,
						selectionColor, borderColor);
				ValidityPanel vpNew;
				ValidityPanel vpOld = new ValidityPanel(offsetsCopy[0], offsetY, (int) (process*RATIO), HEIGHT);
				
				for(int i=0; i<holeCopy.length; ++i){
					highlightPanel 
							= new BlockPanel(
											offsetsCopy[i], offsetY, 
											(int) (process*RATIO), HEIGHT,
											process, processID,
											highlightColor, borderColor);
					vpOld = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
					vpNew = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
					if(offsetsCopy[i] != Integer.MAX_VALUE){
						System.out.println(i);
						addPanel(highlightPanel);
						tryCatchSleep(1000);
						if(process <= holeCopy[i] && holeCopy[i] >= maxHole){
							if(choosenPanel != null){
								removePanel(choosenPanel);
								removeValidityPanel(vpOld);
							}
							choosenPanel = highlightPanel;
							vpOld = vpNew;
							paintPanel(choosenPanel, selectionColor);
							addValidityPanel(vpOld, ValidityPanel.CORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vpOld);
							maxHole = holeCopy[i];
							maxHoleIndex = i;
						}else{
							paintPanel(highlightPanel, faultColor);
							addValidityPanel(vpNew, ValidityPanel.INCORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vpNew);
							removePanel(highlightPanel);
						}
					}
				}
				
				if(holeCopy[maxHoleIndex] >= process){
					paintPanel(choosenPanel, acceptColor);
					removeValidityPanel(vpOld);
					setLayer(choosenPanel, processLayer);
					holeCopy[maxHoleIndex] -= process;
					if(holeCopy[maxHoleIndex] == 0)	offsetsCopy[maxHoleIndex] = Integer.MAX_VALUE;;
					baseHolePanels[maxHoleIndex].updateHoleValue(holeCopy[maxHoleIndex]);
					offsetsCopy[maxHoleIndex] += (process*RATIO);
					flag = false;
					acc.add(new Integer[]{processID, process});
				}
				if(flag){
					rej.add(new Integer[]{processID, process});
				}
			}
		});
		algo.start();
	}

	private void nextFit(final int process, final int processID, final ArrayList<Integer[]> acc, final ArrayList<Integer[]> rej) {

		Thread algo = new Thread(new Runnable() {
			
			int i = 0;
			@Override
			public void run() {
				int seenSoFar = 1;
				boolean flag = true;
				for(i=curr; i<holeCopy.length; i = (i+1)%holeCopy.length){
					BlockPanel highlightPanel 
							= new BlockPanel(
									offsetsCopy[i], offsetY, 
									(int) (process*RATIO), HEIGHT, 
									process, processID,
									highlightColor, borderColor);
					ValidityPanel vp = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
						addPanel(highlightPanel);
						tryCatchSleep(1000);
						if(process <= holeCopy[i]){
							paintPanel(highlightPanel, selectionColor);
							addValidityPanel(vp, ValidityPanel.CORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vp);
							paintPanel(highlightPanel, acceptColor);
							setLayer(highlightPanel, processLayer);
							holeCopy[i] -= process;
							if(holeCopy[i] == 0)	offsetsCopy[i] = Integer.MAX_VALUE;;
							baseHolePanels[i].updateHoleValue(holeCopy[i]);
							offsetsCopy[i] += (process*RATIO);
							curr = i;
							flag = false;
							acc.add(new Integer[]{processID, process});
							break;
						}else{
							paintPanel(highlightPanel, faultColor);
							addValidityPanel(vp, ValidityPanel.INCORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vp);
							if(seenSoFar == holes.length){
								removePanel(highlightPanel);
								break;
							}
						}
						tryCatchSleep(1000);
						removePanel(highlightPanel);
						++seenSoFar;
					
				}
				if(flag)
					rej.add(new Integer[]{processID, process});
			}
		});
		algo.start();
	}

	private void firstFit(final int process, final int processID, final ArrayList<Integer[]> acc, final ArrayList<Integer[]> rej) {
		
		Thread algo = new Thread(new Runnable() {
			
			int i = 0;
			boolean flag = true;
			@Override
			public void run() {
				for(i=0; i<holeCopy.length; ++i){
					BlockPanel highlightPanel = 
							new BlockPanel(
									offsetsCopy[i], offsetY,
									(int) (process*RATIO), HEIGHT,
									process, processID,
									highlightColor, borderColor);
					ValidityPanel vp = new ValidityPanel(offsetsCopy[i], offsetY, (int) (process*RATIO), HEIGHT);
					if(offsetsCopy[i] != Integer.MAX_VALUE && offsetsCopy[i] < maxOffset){
						addPanel(highlightPanel);
						tryCatchSleep(1000);
						if(process <= holeCopy[i]){
							paintPanel(highlightPanel, selectionColor);
							addValidityPanel(vp, ValidityPanel.CORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vp);
							paintPanel(highlightPanel, acceptColor);
							setLayer(highlightPanel, processLayer);
							holeCopy[i] -= process;
							if(holeCopy[i] == 0)	offsetsCopy[i] = Integer.MAX_VALUE;
							baseHolePanels[i].updateHoleValue(holeCopy[i]);
							offsetsCopy[i] += (process*RATIO);
							flag = false;
//							System.out.println("Thread in " + flag[0]);
							acc.add(new Integer[]{processID, process});
							break;
						}else{
							paintPanel(highlightPanel, faultColor);
							addValidityPanel(vp, ValidityPanel.INCORRECT);
							tryCatchSleep(1000);
							removeValidityPanel(vp);
						}
						removePanel(highlightPanel);
					}
				}
				if(flag)
					rej.add(new Integer[]{processID, process});
			}
		});
		algo.start();
	}
	
	void removePanel(BlockPanel highlightPanel){
		MemoryPanel.this.remove(highlightPanel);
		MemoryPanel.this.repaint();
		MemoryPanel.this.revalidate();
		highlightPanel = null;
	}
	
	void addPanel(BlockPanel highlightPanel){
		MemoryPanel.this.add(highlightPanel.startAnimation(), new Integer(highlightLayer));
		highlightPanel.repaint();
		MemoryPanel.this.repaint();
		MemoryPanel.this.revalidate();
		Thread t = highlightPanel.getThread();
		try {
			t.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	void paintPanel(BlockPanel highlightPanel, Color color){
		highlightPanel.setBackground(color);
		highlightPanel.repaint();
		MemoryPanel.this.repaint();
		MemoryPanel.this.revalidate();
	}
	
	void addValidityPanel(ValidityPanel vp, int type){
		vp.setBackground(type);
		this.add(vp, new Integer(3));
		this.repaint();
		this.revalidate();
	}
	
	void removeValidityPanel(ValidityPanel vp){
		this.remove(vp);
		this.repaint();
		this.revalidate();
	}
	
	void tryCatchSleep(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void resetMemory(){
		holeCopy = holes.clone();
		offsetsCopy = offsets.clone();
		this.resetBaseMemoryPanes();
		this.removeAllComponentsFromLayer(processLayer);
	}
	
	private void resetBaseMemoryPanes(){
		int i=0;
		for(BlockPanel bp: baseHolePanels){
			bp.updateHoleValue(holeCopy[i++]);
		}
	}
	
	void removeAllComponentsFromLayer(int layer){
		for(Component c : this.getComponentsInLayer(layer)){
			this.remove(c);
		}
		this.repaint();
		this.revalidate();
	}
	
	int smallestHole(){
		int min = holeCopy[0];
		for(int i=0; i<holeCopy.length; ++i){
			if(min > holeCopy[i]){
				min = holeCopy[i];
			}
		}
		if(min == 0){
			min = Integer.MAX_VALUE;
			for(int i=0; i<holeCopy.length; ++i){
				if( holeCopy[i] != 0 && min > holeCopy[i]){
					min = holeCopy[i];
				}
			}
		}
		return (min == Integer.MAX_VALUE)?0:min;
	}
	
	int largestHole(){
		int max = holeCopy[0];
		for(int i=0; i<holeCopy.length; ++i){
			if(max < holeCopy[i]){
				max = holeCopy[i];
			}
		}
		return max;
	}
	
}

@SuppressWarnings("serial")
class ValidityPanel extends JPanel {
	
	public static final int CORRECT = 1;
	public static final int INCORRECT = 2;
	private Image img;
	int type;
	int width;
	int height;
	
	public ValidityPanel(int fromX, int fromY, int width, int height){
		this.width = width;
		this.height = height;
		this.setSize(width, height);
		this.setBounds(fromX, fromY, width, height);
		this.setOpaque(false);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		// TODO Auto-generated method stub
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(img != null)
			g2d.drawImage(img, this.width/2-20, this.height/2-20, 40, 40, this);
	}
	
	public void setBackground(int type){
		if(type == 1){
			try {
				img = ImageIO.read(this.getClass().getResourceAsStream("correct.png"));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}else{
			try {
				img = ImageIO.read(this.getClass().getResourceAsStream("incorrect.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.repaint();
	}
	
}
