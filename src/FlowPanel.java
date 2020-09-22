import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
	Graphics graphics;

	/**
	 * Constructor for the class. it also attaches the mouse listener to the flow panel
	 * @param terrain the terrain that needs to be represented
	 */
	FlowPanel(Terrain terrain) {

		land=terrain;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				fillNeighbors(e.getX(),e.getY());

			}
		});

	}
		
	// responsible for painting the terrain and water
	// as images
	@Override
    protected void paintComponent(Graphics g) {

		int width = getWidth();
		int height = getHeight();
		  
		super.paintComponent(g);
		
		// draw the landscape in greyscale as an image
		if (land.getImage() != null){
			g.drawImage(land.getImage(), 0, 0, null);
			graphics = getGraphics();

//

		}



	}

	/**
	 * Fills a 7x7 selection of gridItems with water adn repaints
	 * @param x column index
	 * @param y row index
	 */
	public void fillNeighbors(int x, int y){
		for(int i = x-3; i <= x+3; i++){
			for(int j = y-3; j <= y+3; j++){

				synchronized (land){
					land.img.setRGB(i,j,Color.BLUE.getRGB());
					land.items[i][j].addWater(3);
					//Flow.waterIn.addAndGet(3);
				}


			}
		}
		//System.out.println("Water In: "+Flow.waterIn.get()+"\t Water out: "+Flow.waterOut.get());
		this.repaint();
	}

	/**
	 * Run method for flow panel. This is responsible fro making sure that every thread has looped through each portion of the permute the same amount of times as the other threads,
 	 */
	public void run() {	
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
		while (true){
			while(!Flow.paused){
				if(Flow.finishedStep.get(0) == 1 && Flow.finishedStep.get(1) == 1 && Flow.finishedStep.get(2) == 1 && Flow.finishedStep.get(3) == 1 ){
					synchronized (Flow.finishedStep){
						for (int i = 0; i < 4; i++) {
							Flow.finishedStep.set(i,0);
						}
					}
					Flow.counter.incrementAndGet();
					Flow.count.setText(Integer.toString(Flow.counter.get()));

				}

			}
		}
	}
}