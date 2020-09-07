import javax.swing.*;
import java.awt.*;

public class WaterBlock extends JComponent {

    int x, y;

    public WaterBlock(int x, int y){
        this.x = x;
        this.y = y;
    }

    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        float cellH = getHeight()/ 100;
        float cellW = getWidth()/ 100;

        g.drawRect(x,y,(int)cellW,(int)cellH);
        g.setColor(Color.BLUE);
        g.fillRect(x,y,(int)cellW,(int)cellH);
    }

}
