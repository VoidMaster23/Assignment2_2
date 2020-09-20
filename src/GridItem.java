

/**
 * Class That Represents a block in the grid
 * @author Edson Shivuri
 */
public class GridItem {
   // grid position
    private int rowInd;
    private int colInd;

    //water info
    private int waterUnits;
    private  float height;

    /**
     * Constructor for the GridItem class
     * @param rowInd row number
     * @param colInd column number
     * @param height height value
     */
    public GridItem(int rowInd, int colInd, float height){
        this.rowInd = rowInd;
        this.colInd = colInd;
        this.height = height;
        this.waterUnits = 0;
    }

    // methods safe to keep in the normal way since their values are fixed

    /**
     * Getter method for the row index
     * @return the row number
     */
    public int getRowInd(){
        return this.rowInd;
    }

    /**
     * Getter method for the column index
     * @return the column number
     */
    public int getColInd(){
        return this.colInd;
    }

    /**
     * Getter method for the height
     * @return the height of the current grid item
     */
    public float getHeight() {
        return height;
    }

    // Methods that need to be thread safe

    /**
     * Getter method for the waterUnits
     * @return the number of water units the current grid item has
     */
    synchronized int getWaterUnits(){
        return this.waterUnits;
    }

    /**
     * Method that allows a specified number of water units to be added to the current count
     * @param numUnits the number of water units to be added
     */
    synchronized void addWater(int numUnits){
        this.waterUnits += numUnits;
    }

    /**
     * Method that allows a specified number of water units to be removed from the current count
     * @param numUnits the number of water units to be removed
     */
    synchronized void removeWater(int numUnits){
        this.waterUnits -= numUnits;
    }

    /**
     * Getter method for the current water surface
     * @return the water surface, calculated by summing the height with the water units multipled by 0.01
     */
    synchronized float getWaterSurface(){
        return this.height + this.waterUnits * (0.01f);
    }

    /**
     * Sets the waterUnits back to 0
     */
    synchronized void resetWater(){
        this.waterUnits = 0;
    }


    public String toString(){
        return "Row Index: "+Integer.toString(rowInd) + "\n Column Index: "+Integer.toString(colInd)+
                "\n Height: "+Float.toString(height)+ "\n Water Units: "+Integer.toString(waterUnits)+
                "\n Water Surface: "+ Float.toString(getWaterSurface());
    }


}

