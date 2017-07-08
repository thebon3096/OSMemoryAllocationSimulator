import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class BlockPanel extends JPanel implements Runnable{
	
	private int width, height, fromX, fromY, widthX, processSize, processID;
	Thread t;
	public static final int NONE = 0;
	public static final int CORRECT = 1;
	public static final int INCORRECT = 2;
	
	public BlockPanel(int fromX, int fromY, int width, int height, int processSize, int processID, Color color, Color borderColor){
		this.width = width;
		this.height = height;
		this.fromX = fromX;
		this.fromY = fromY;
		this.processSize = processSize;
		this.processID = processID;
		this.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, borderColor));
		this.setBackground(color);
		t = new Thread(this);
	}
	
	public BlockPanel startAnimation(){
		t.start();
		this.repaint();
		return this;
	}
	
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.setColor(Color.WHITE);
		
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
		AffineTransform orig = g2d.getTransform();
		g2d.rotate(-Math.PI/2);
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(processID != -1){
			g2d.drawString("P"+processID, -65, width-5);
			g2d.drawString(":", -45, width-5);
		}
		if(processSize != -1){
			g2d.drawString(processSize+"K", -40, width-5);
		}
		g2d.setTransform(orig);
	}

	@Override
	public void run() {
		widthX = 0;
		while(widthX < width){
			widthX += 1;
			this.setBounds(fromX, fromY, widthX, height);
			this.repaint();
			this.revalidate();
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	void updateHoleValue(int Hole){
		this.processSize = Hole;
		this.repaint();
		this.revalidate();
	}
	
	public Thread getThread(){
		return t;
	}
	
}
