package ui.gui;

import logic.Breakthru;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * GUI for tile showing the used time of one player
 */
public class TimePanel extends JPanel{

    public TimePanel(){
    }

    public void paintTime(Graphics g){
        long time = Breakthru.timeAI;
        String timeString = String.format("%d min, %d sec",
                TimeUnit.MILLISECONDS.toMinutes(time),
                TimeUnit.MILLISECONDS.toSeconds(time) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(time))
        );
        g.drawString(timeString, 23, 25);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintTime(g);
    }

}


