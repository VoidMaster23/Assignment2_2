import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class FlowPanel extends JPanel implements Runnable {
	Terrain land;
	Graphics graphics;
	FlowPanel(Terrain terrain) {

		land=terrain;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				//System.out.println(e);
				//graphics.drawRect(e.getX(),e.getY(),getWidth()/100,getHeight()/100);
				//graphics.setColor(Color.BLUE);
				//System.out.println(graphics.toString());
				//graphics.fillRect(e.getX(),e.getY(),getWidth()/100,getHeight()/100);
				//land.items[e.getX()][e.getY()].addWater(100);
				//System.out.println(land.items[e.getX()][e.getY()].toString());
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
			//System.out.println("SET GRAPHICS");
			//System.out.println(graphics.toString());
			//graphics.setColor(Color.CYAN);

//			//draw the grid
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
				}


			}
		}
		this.repaint();
	}

	
	public void run() {	
		// display loop here
		// to do: this should be controlled by the GUI
		// to allow stopping and starting
	    repaint();
	}
}