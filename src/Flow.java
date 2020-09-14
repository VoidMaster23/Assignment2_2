import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Flow {
	static long startTime = 0;
	static int frameX;
	static int frameY;
	static FlowPanel fp;
	static JPanel g;
	static  JLabel count;

	//array of threads
	static SimRun[] threads;
	static boolean paused;

	//keep track of which threads finished step
	static AtomicIntegerArray finishedStep;

	//counter
	static AtomicInteger counter;

	//
	static Thread fpt;



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
				//halt execution
				paused = true;


				// reset the water value for each gridItem , make sure no other thread can do operations there
				// note this may be a problem  if you try to reset during run
				// might cause a deadlock
				// but if it does then check here
				synchronized (landdata.items){
					for (int i = 0; i < landdata.dimy ; i++) {
						for (int j = 0; j < landdata.dimx; j++) {
							landdata.items[j][i].resetWater();
							landdata.resetPixel(j,i);
						}
					}
				}

				//stop all threads
                for (int i = 0; i < 4; i++) {
                    threads[i].isRunning = false;
                    finishedStep.set(i,0);
                }

                fp.repaint();

                //make new threads
                threads = new SimRun[4];

                //reset the counter
				synchronized (counter){
					counter.set(0);
					count.setText(Integer.toString(counter.get()));
				}

				paused = false;
			}
		});

		//Pause Button
		JButton pauseB = new JButton("Pause");
		pauseB.addActionListener(e ->{
           //let all threads finish their current step then pause
            paused = true;

		});

		//play button
		JButton playB = new JButton("Play");
		playB.addActionListener(e -> {

            if(!paused) {
//				counter.set(0);
            	//if we're doing a clean start
				synchronized (threads){
					for (int i = 1; i <= 4; i++) {
						int low = (int)((((double)i - 1) / 4.0) * landdata.permute.size());
						int high = (int)(((double)i / 4.0) * landdata.permute.size());
						threads[i - 1] = (new SimRun(low, high, landdata,i-1));
						//finishedStep[i-1].set(false);
						threads[i - 1].start();
					}
				}


			}else{
            	//continue execution
               paused = false;

			}

		});


		JButton endB = new JButton("End");;
		// add the listener to the jbutton to handle the "pressed" event
		endB.addActionListener(e -> {
			//pause the run
			paused = true;

			//threads to stop
			for (int i = 0; i < 4; i++) {
				threads[i].isRunning = false;
			}
			//stop the program
			System.exit(0);
		});

		//Display the current step
		JLabel step = new JLabel("Step ");
		count  = new JLabel(Integer.toString(counter.get()));

		//add the buttons
		b.add(resetB);
		b.add(pauseB);
		b.add(playB);
		b.add(endB);
		b.add(step);
		b.add(count);

		g.add(b);

    	
		frame.setSize(frameX, frameY+50);	// a little extra space at the bottom for buttons
      	frame.setLocationRelativeTo(null);  // center window on screen
      	frame.add(g); //add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
        fpt = new Thread(fp);
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



		//create the threads array
		threads = new SimRun[4];

		//store each step results
		finishedStep = new AtomicIntegerArray(4);

		//initialise the steps to -
		for (int i = 0; i < 4; i++) {
			finishedStep.set(i,0);
		}

		counter = new AtomicInteger(0);
		paused = false;

	}
}
