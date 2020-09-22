import java.awt.*;

/**
 * Thread class that actually runs operations on the permutations
 * @author Edson Shivuri
 */
public class SimRun extends java.lang.Thread{
    //start and end indices
    private int low;
    private int high;

    // the terrain
    private Terrain landData;

    //know if the thread is running or nah
    boolean isRunning;

    //counter to keep track of the timing
    int counter;


    int index;

    /**
     * Constructor for the thread class
     * @param low lower index that is visible to the thread
     * @param high upper index visible to the thread
     * @param landData terrain to analyse
     * @param index index the thread has access to in the finishedStep array of FP
     */
    public SimRun(int low, int high, Terrain landData,int index){
        this.low = low;
        this.high = high;
        this.landData = landData;
        isRunning = true;
        this.counter = 0;
        this.index = index;

    }

    /**
     * Overriden Run method to handle thread logic
     */
    @Override
    public void run(){

        // execute until the user says stop
        while (isRunning){
            //check to see if the counter has been incremented
            if(this.counter == Flow.counter.get()){

                    //iterate over certain portion of the grid
                   for (int i = low; i < high; i++) {

                       //get the row and column index
                       int[] arr  = new int[2];
                       landData.getPermute(i, arr);

                       //get the gridItem in question
                       int x = arr[0];
                       int y = arr[1];

                       //note that we have  to address y as the row and x and column
                       GridItem currentItem = landData.items[x][y];

                       //only execute if we have a non-zero water level
                       if(currentItem.getWaterUnits() > 0) {


                           //find the lowestNeighbor
                           GridItem lowestNeighbor = findLowest(x, y, currentItem);

                           //transfer units from the current item to the lowest neighbor\
                           // note this operation needs to be thread safe hence the synchronized block
                           // note we only do this if there wa a lowest neighbor
                           if (lowestNeighbor != null) {

                               //place a lock on the current item and the lowest neighbor
                               //this ensures thread safety during the operations we perform
                               synchronized (currentItem) {
                                   synchronized (lowestNeighbor) {
                                       //take a water unit from this one and add it to the lowest neighbor
                                       currentItem.removeWater(1);
                                       lowestNeighbor.addWater(1);

                                       //change the color of the lowest  neigbor pixel to a shade of blue
                                       float val = (lowestNeighbor.getWaterSurface() - landData.minh) / (landData.maxh - landData.minh);
                                       Color col = new Color(0, 0, val, 1.0f);
                                       landData.img.setRGB(lowestNeighbor.getColInd(),lowestNeighbor.getRowInd(), col.getRGB());




                                       //reset the color of the current pixel of theres no water on it
                                       if (currentItem.getWaterUnits() == 0) {
                                           landData.resetPixel(currentItem.getColInd(), currentItem.getRowInd());
                                       }else{
                                           //reshade the current pixel
                                           val = (currentItem.getWaterSurface() - landData.minh) / (landData.maxh - landData.minh);
                                           col = new Color(0, val, val, 1.0f);// this aqua color indicates shallow water
                                           landData.img.setRGB(currentItem.getColInd(),currentItem.getRowInd(), col.getRGB());
                                       }

                                       //repaint the panel
                                       Flow.fp.repaint();

                                   }
                               }// end sync block
                           }else if(!inRange(x,y)){

                               // we are at a boundary
                               //set the water to 0 and reset pixel
                               synchronized (currentItem){
                                   //Flow.waterOut.addAndGet(currentItem.getWaterUnits());
                                   //Flow.waterIn.addAndGet(-1*currentItem.getWaterUnits());
                                   currentItem.resetWater();
                                   //System.out.println("Water In: "+Flow.waterIn.get()+"\t Water out: "+Flow.waterOut.get());
                                   landData.resetPixel(currentItem.getColInd(),currentItem.getRowInd());
                               }
                           }
                       }

                   }

                   //increment the counter of the current thread
                   this.counter++;

                   //tell flow that we have finished the step
                   Flow.finishedStep.set(index,1);


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
