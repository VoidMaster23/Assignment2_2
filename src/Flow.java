import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Flow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static JPanel g;

	//array of threads
	static SimRun[] threads;

	// start timer
	private static void tick(){
		startTime = System.currentTimeMillis();
	}
	
	// stop timer, return time elapsed in seconds
	private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}
	
	public static void setupGUI(int frameX,int frameY,Terrain landdata) {
		
		Dimension fsize = new Dimension(800, 800);
    	JFrame frame = new JFrame("Waterflow"); 
    	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	frame.getContentPane().setLayout(new BorderLayout());

    	// give the GUI a native feel
    	try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.PAGE_AXIS)); 
   
		fp = new FlowPanel(landdata);
		fp.setPreferredSize(new Dimension(frameX,frameY));
		g.add(fp);
		Graphics2D g2 = landdata.img.createGraphics();


		// to do: add a MouseListener, buttons and ActionListeners on those buttons
	   	
		JPanel b = new JPanel();
	    b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

	    //Reset Button
		JButton resetB = new JButton("Reset");
		resetB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// reset the water value for each gridItem , make sure no other thread can do operations there
				// note this may be a problem  if you try to reset during run
				// might cause a deadlock
				// but if it does then check here
				synchronized (landdata.items){
					for (int i = 0; i < landdata.dimy ; i++) {
						for (int j = 0; j < landdata.dimx; j++) {
							landdata.items[i][j].resetWater();
						}
					}
				}
				System.out.println("clicked");
				fp = new FlowPanel(landdata); // reset the ting
				fp.setPreferredSize(new Dimension(frameX,frameY)); //not sure why
				g.remove(fp);
				g.repaint();
				g.add(fp);
			}
		});

		//Pause Button
		JButton pauseB = new JButton("Pause");

		//play button
		JButton playB = new JButton("Play");
		playB.addActionListener(e -> {
			for (int i = 1; i <= 4; i++) {
				int low  = ((i-1)/4)*landdata.permute.size();
				int high = (i/4)*landdata.permute.size();
				threads[i-1] = (new SimRun(low,high,landdata));
				threads[i-1].start();
			}
		});


		JButton endB = new JButton("End");;
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(e -> {
			//threads to stop
			for (int i = 0; i < 4; i++) {
				threads[i].isRunning = false;
			}
			frame.dispose();
		});

		//add the buttons
		b.add(resetB);
		b.add(pauseB);
		b.add(playB);
		b.add(endB);
		g.add(b);

//		fp.addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                super.mouseClicked(e);
//                fp.graphics.drawRect(e.getX(),e.getY(),fp.getWidth()/100,fp.getHeight()/100);
//                fp.graphics.setColor(Color.BLUE);
//                System.out.println(fp.graphics.toString());
//                fp.graphics.fillRect(e.getX(),e.getY(),fp.getWidth()/100,fp.getHeight()/100);
//                fp.land.items[e.getX()][e.getY()].addWater(100);
//                System.out.println(fp.land.items[e.getX()][e.getY()].toString());
//            }
//        });
    	
		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        Thread fpt = new Thread(fp);
        fpt.start();
	}
	
		
	public static void main(String[] args) {
		Terrain landdata = new Terrain();
		
		// check that number of command line arguments is correct
		if(args.length != 1)
		{
			System.out.println("Incorrect number of command line arguments. Should have form: java -jar flow.java intputfilename");
			System.exit(0);
		}
				
		// landscape information from file supplied as argument
		// 
		landdata.readData(args[0]);
		
		frameX = landdata.getDimX();
		frameY = landdata.getDimY();
		SwingUtilities.invokeLater(()->setupGUI(frameX, frameY, landdata));
		
		// to do: initialise and start simulation
		System.out.println(landdata.permute.size());

		//thread
		threads = new SimRun[4];

	}
}
