import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Thread class that actually runs operations on the permutations
 * @author Edson Shivuri
 */
public class SimRun extends java.lang.Thread {
    //start and end indices
    private int low;
    private int high;

    // the terrain
    private Terrain landData;

    //know if the thread is running or nah
    boolean isRunning;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * Constructor for the thread class
     * @param low lower index that is visible to the thread
     * @param high upper index visible to the thread
     * @param landData terrain to analyse
     */
    public SimRun(int low, int high, Terrain landData){
        this.low = low;
        this.high = high;
        this.landData = landData;
        isRunning = true;
    }

    /**
     * Overriden Run method to handle thread logic
     */
    @Override
    public void run(){

        // execute until the user says stop
        while (isRunning){
            for (int i = low; i < high; i++) {
                //get the row and column index
               // System.out.println(i);
                int[] arr  = new int[2];
                landData.getPermute(i, arr);

                //get the gridItem in question
                int x = arr[0];
                int y = arr[1];

                //note that we have  to address y as the row and x and column
                GridItem currentItem = landData.items[x][y];

                if(currentItem.getWaterUnits() != 0) {
                    //System.out.println(x);
                   // System.out.println(y);
                    //System.out.println(currentItem);

                    //find the lowestNeighbor
                    GridItem lowestNeighbor = findLowest(x, y, currentItem);
                    //System.out.println(lowestNeighbor);
                    //System.exit(0);
                    //transfer units from the current item to the lowest neighbor\
                    // note this operation needs to be thread safe hence the synchronized block
                    // note we only do this if there wa a lowest neighbor
                    if (lowestNeighbor != null) {
                        synchronized (currentItem) {
                            synchronized (lowestNeighbor) {
                                //take a water unit from this one and add i
                                //System.out.println(currentItem.toString());
                                currentItem.removeWater(1);
                                lowestNeighbor.addWater(1);
                             //   landData.img.setRGB(currentItem.getColInd(),currentItem.getRowInd(), Color.BLUE.getRGB());
                                landData.img.setRGB(lowestNeighbor.getColInd(),lowestNeighbor.getRowInd(), Color.BLUE.getRGB());

                                //System.exit(0);
                                if (currentItem.getWaterUnits() == 0) {
                                    landData.resetPixel(currentItem.getColInd(), currentItem.getRowInd());
                                }
                                Flow.fp.repaint();
                                //System.out.println(currentItem.toString());
                            }
                        }// end sync block
                    }else if(!inRange(x,y)){
                        // we are at a boundary
                        //set the water to 0 and reset pixel
                        synchronized (currentItem){
                            currentItem.resetWater();
                            landData.resetPixel(currentItem.getColInd(),currentItem.getRowInd());
                        }
                    }
                }

            }
        }
        
    }

    /**
     * Method that finds the GridItem that has a height strictly lower than the current GridItem
     * @param x Column Index
     * @param y Row Index
     * @param toCheck Current GridItem
     * @return the lowest neighbor or null
     */
    public GridItem findLowest(int x, int y, GridItem toCheck){
        GridItem lowest = toCheck;

        //make sure we're not at a limit before figuring out where the water goes
        if(inRange(x,y)){
            //iterate the immediate rows
            for (int i = y-1; i <= y+1 ; i++) {

                //iterate the immediate columns
                for(int j = x-1; j <= x+1; j++){
                    // check if this thing is the lowest
                    if(landData.items[j][i].getWaterSurface() < lowest.getWaterSurface()){
                        lowest = landData.items[j][i];
                    }
                }

            }
        }




        //once the lowest has been obtained check if its not the same as toCheck
        if(toCheck.getWaterSurface() == lowest.getWaterSurface()){
            lowest = null;
        }



        return lowest;
    }

    /**
     * Method to check if the current grid position is not at a boundary
     * @param x column index
     * @param y row index
     * @return true if not at a boundary and false otherwise
     */
    public boolean inRange(int x, int y){

        boolean xinRange = x != 0 && x != landData.dimx-1; // check if we are not at the x boundaries
        boolean yinRange = y != 0 && y != landData.dimy-1; // check if we are not at the y boundaries

        return xinRange && yinRange; // combine results
    }



}
