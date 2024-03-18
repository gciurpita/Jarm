//
// basis for java graphic animation
//

import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

import java.time.*;

import java.util.Timer;
import java.util.TimerTask;

import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.imageio.ImageIO;

// -----------------------------------------------------------------------------
// steam locomotive backend

public class Jarm extends JPanel
        implements MouseListener, KeyListener
{
    JFrame frame = new JFrame ();

    final int Wid = 800;
    final int Ht  = 600;

    int       keyVal;

    final int Nservo       = 3;
    int       ang  []      = new int [Nservo];
    int       posX []      = new int [Nservo];
    int       posY []      = new int [Nservo];

    int       SegLen []    = { 300, 200, 50 };
    int       X0           = 100;
    int       Y0           = Ht / 2;
    int       servoIdx     = 0;

    // ----------------------------------------------------
    public Jarm ()
            throws IOException, IllegalArgumentException
    {
        addKeyListener (this);
        addMouseListener (this);

        System.out.println ("Jarm:");

        reset ();
    }

    // ----------------------------------------------------
    private void startup ()
    {
        frame.setContentPane (this);

        this.setPreferredSize (new Dimension (Wid, Ht));

        frame.pack ();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);
        frame.setVisible (true);

        // position app near top center of screen
        Rectangle r = frame.getBounds();        // window size
        frame.setBounds (900, 0, r.width, r.height);

        frame.setTitle ("Jarm");
    }

    // ----------------------------------------------------
    public static void main (String [] args)
            throws IOException, IllegalArgumentException
    {
        Jarm basic = new Jarm ();
        basic.startup ();
    }

    // ----------------------------------------------------
    public void mousePressed (MouseEvent ev)
    {
        int  x       = (int) ev.getX();
        int  y       = (int) ev.getY();
        System.out.format ("mousePressed:\n");

        requestFocusInWindow ();
    }

    public void mouseClicked  (MouseEvent e) { }
    public void mouseEntered  (MouseEvent e) { }
    public void mouseExited   (MouseEvent e) { }
    public void mouseReleased (MouseEvent e) { }

    // ----------------------------------------------------
    public void keyPressed  (KeyEvent e) { }
    public void keyReleased (KeyEvent e) { }

    public void keyTyped    (KeyEvent e)
    {
        byte[]  buf     = new byte [10];

        char c = e.getKeyChar();

        switch (c)  {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
            keyVal = 10*keyVal + c - '0';
            break;

        case '-':
            keyVal = -keyVal;
            break;

        case 'a':
            if (keyVal <= 180)
                ang [servoIdx] = keyVal;
            keyVal = 0;
            break;

        case 's':
            if (keyVal < Nservo)
                servoIdx = keyVal;
            keyVal = 0;
            break;

        case 'x':
            if (keyVal < Wid)
                posX [servoIdx] = keyVal;
            keyVal = 0;
            break;

        case 'y':
            if (keyVal < Wid)
                posY [servoIdx] = keyVal;
            keyVal = 0;
            break;

        case 'z':
            reset ();
            break;

        case '\n':
            break;

        default:
            System.out.format ("keyTyped: %c\n", c);
            break;
        }
        repaint ();
    }

    // ----------------------------------------------------
    private void computePos ()
    {
        double rad  = 0;
        posX [0]    = X0 + SegLen [0];
        for (int n = 0; n < Nservo; n++)  {
            rad      += Math.toRadians (ang [n]);
            double s  = -Math.sin (rad);
            double c  =  Math.cos (rad);

            if (0 == n)  {
                posX [n] = X0 + (int)(c * SegLen [n]);
                posY [n] = Y0 + (int)(s * SegLen [n]);
            }
            else {
                posX [n] = posX [n-1] + (int)(c * SegLen [n]);
                posY [n] = posY [n-1] + (int)(s * SegLen [n]);
            }
        }
    }

    // ----------------------------------------------------
    private void reset ()
    {
        posX [0] = X0 + SegLen [0];
        for (int n = 0; n < Nservo; n++)  {
            ang   [n] = 0;
            posY  [n] = Y0;
            if (0 < n)
                posX [n] = posX [n-1] + SegLen [n];
        }
    }

    // ----------------------------------------------------
 // @Override
    public  void paintComponent (Graphics g)
    {
     // System.out.println ("paintComponent:");

        Graphics2D  g2d = (Graphics2D) g;

        // clear background
        g2d.setColor (new Color(0, 32, 0));
        g2d.fillRect (0, 0, Wid, Ht);

        // text
        int fSize = 14;
        g2d.setFont (new Font ("Courier", Font.PLAIN, fSize));
        g2d.setColor (Color.white);

        int  yTxt = fSize;
        for (int n = 0; n < Nservo; n++)  {
            String fmt = "servo %d: ang %4d, x %6d, y %6d";
            if (n == servoIdx)
                fmt = "servo %d: ang %4d, x %6d, y %6d <";

            String t = String.format (fmt, n, ang [n], posX [n], posY [n]);
            g2d.drawString (t, 10, yTxt);
            yTxt += fSize;
        }

        if (0 != keyVal)  {
            String t = String.format ("val %4d", keyVal);
            g2d.drawString (t, 10, yTxt);
        }

        // draw something
        final int OvalHt  = 20;
        final int OvalWid = 20;

        computePos ();

        g2d.setColor (Color.red);
        int  x = X0;
        int  y = Y0;

        for (int n = 0; n < Nservo; n++)  {
            g2d.drawOval (x - OvalWid/2, y - OvalHt/2, OvalWid, OvalHt);
            g2d.drawLine (x, y, posX [n], posY [n]);
            x = posX [n];
            y = posY [n];
        }
    }
}
