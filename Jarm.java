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
    double    c    []      = new double [Nservo];   // cos
    double    s    []      = new double [Nservo];   // cos

    int       SegLen []    = { 300, 300, 50 };
    int       x            = Wid / 2;
    int       y            = Ht / 2;
    int       servoIdx     = 0;

    final int M_Trig       = 0;
    final int M_Arm        = 1;
    int  mode              = M_Arm;     // M_Trig;

    final int O_Lbl        =  1;
    final int O_Eq         =  2;
    final int O_Dash       =  4;
    final int O_Wave       =  8;
    final int O_Mouse      = 16;
    final int O_Val        = 32;
    int       option       =  0;

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
        if (0 != (option & O_Mouse))  {
            x = (int) ev.getX();
            y = (int) ev.getY();
        }
        System.out.format ("mousePressed: (%4d, %4d)\n", x, y);

        requestFocusInWindow ();
        repaint ();
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

        case 'A':
            mode = M_Arm;
            break;

        case 'a':
            if (keyVal <= 180)
                ang [servoIdx] = keyVal;
            keyVal = 0;
            break;

        case 'd':
            option ^= O_Dash;
            break;

        case 'e':
            option &= ~(O_Val | O_Lbl);
            option ^= O_Eq;
            break;

        case 'l':
            option &= ~(O_Val | O_Eq);
            option ^= O_Lbl;
            break;

        case 'm':
            option ^= O_Mouse;
            break;

        case 's':
            if (keyVal < Nservo)
                servoIdx = keyVal;
            keyVal = 0;
            break;

        case 'T':
            mode = M_Trig;
            break;

        case 'v':
            option &= ~(O_Eq | O_Lbl);
            option ^= O_Val;
            break;

        case 'w':
            option ^= O_Wave;
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
        posX [0]    = SegLen [0];
        for (int n = 0; n < Nservo; n++)  {
            rad   += Math.toRadians (ang [n]);
            s [n]  = -Math.sin (rad);
            c [n]  =  Math.cos (rad);

            if (0 == n)  {
                posX [n] = (int)(c [n] * SegLen [n]);
                posY [n] = (int)(s [n] * SegLen [n]);
            }
            else {
                posX [n] = posX [n-1] + (int)(c [n] * SegLen [n]);
                posY [n] = posY [n-1] + (int)(s [n] * SegLen [n]);
            }
        }
    }

    // ----------------------------------------------------
    private void reset ()
    {
        posX [0] = SegLen [0];
        for (int n = 0; n < Nservo; n++)  {
            ang   [n] = 0;
            posY  [n] = 0;
            if (0 < n)
                posX [n] = posX [n-1] + SegLen [n];
        }
    }

    // ------------------------------------------------------------------------
 // @Override
    public  void paintComponent (Graphics g)
    {
     // System.out.println ("paintComponent:");

        Graphics2D  g2d = (Graphics2D) g;

        // clear background
        g2d.setColor (new Color(0, 32, 0));
        g2d.fillRect (0, 0, Wid, Ht);

        switch (mode) {
        case M_Arm:
            paintArm (g2d);
            break;

        case M_Trig:
            paintTrig (g2d);
            break;
        }
    }

    // ----------------------------------------------------
    private  void paintWaves (
        Graphics2D g2d,
        int        ang0 )
    {
        int    A   = 50;
        double dA  = 2 * Math.PI / (float)Wid;
        int    dX  = 10;

        int    offset  = 100;
        int    offsetB = 200;
        double ang = -Math.PI;
        int    y0  = offset - (int) (A * Math.sin (ang));

        g2d.setColor (Color.gray);
        g2d.setStroke (new BasicStroke());
        g2d.drawLine (0, offset, Wid, offset);
        int x1 = (int) Wid * (180 + ang0) / 360;
        g2d.drawLine (x1, offset-A, x1, offsetB+A);

        g2d.setColor (Color.white);
        g2d.drawString ("sine",   0, offset);
        g2d.drawString ("cosine", 0, offsetB);

        g2d.setColor (Color.yellow);
        for (int x = 10; x < Wid; x += 10)  {
            ang   = x * dA + Math.PI;
            int y = offset - (int) (A * Math.sin (ang));
            g2d.drawLine (x-dX, y0, x, y);
            y0    = y;
        }

        ang    = -Math.PI;
        y0     = offsetB - (int) (A * Math.cos (ang));

        g2d.setColor (Color.gray);
        g2d.drawLine (0, offsetB, Wid, offsetB);

        g2d.setColor (Color.orange);
        for (int x = 10; x < Wid; x += 10)  {
            ang   = x * dA + Math.PI;
            int y = offsetB - (int) (A * Math.cos (ang));
            g2d.drawLine (x-dX, y0, x, y);
            y0    = y;
        }
    }

    // ----------------------------------------------------
    private  void paintTrig (Graphics2D g2d)
    {
        int  X0  = 100;
        int  Y0  = Ht - 100;

        int  wid = x - X0;
        int  ht  = Y0 - y;

        // draw axis & line
        g2d.setColor (Color.gray);
        g2d.drawLine ( 0, Y0, Wid, Y0);
        g2d.drawLine (X0,  0, X0,  Ht);

        g2d.setColor (Color.red);
        g2d.drawLine (X0, Y0, X0 + wid, Y0 - ht);

        // draw angle arc
        int  len = (int)(Math.sqrt (wid*wid + ht*ht));
        int  ang = (int) Math.toDegrees (Math.atan2 (ht, wid));
        g2d.setColor (Color.green);
        g2d.drawArc  (X0 - len/2, Y0 - len/2, len, len, 0, ang);

        // define string parameters
        int  fSize = 18;
        g2d.setFont    (new Font ("Arial", Font.PLAIN, fSize));

        // draw dashed lines
        if (0 != (option & O_Dash)) {
            Stroke s = new BasicStroke (
                            1.0f,                       // Width
                            BasicStroke.CAP_SQUARE,     // End cap
                            BasicStroke.JOIN_MITER,     // Join style
                            10.0f,                      // Miter limit
                            new float[] {16.0f,20.0f},  // Dash pattern
                            0.0f);                      // Dash phase
            g2d.setStroke (s);

            g2d.setColor (Color.yellow);
            g2d.drawLine (X0 + wid, Y0,      X0 + wid, Y0 - ht);
            g2d.drawLine (X0,       Y0 - ht, X0 + wid, Y0 - ht);

            // draw labels
            g2d.setColor (Color.white);

            double w  = Math.toRadians (ang/2);
            int    x1 = X0 + (int)(len/2 * Math.cos (w));
            int    y1 = Y0 - (int)(len/2 * Math.sin (w));

            if (0 != (option & O_Val))  {
                g2d.drawString (String.valueOf (ht),  X0+wid+10, Y0-ht/2);
                g2d.drawString (String.valueOf (wid), X0+wid/2,  Y0-ht-10);
                g2d.drawString (String.valueOf (len), X0+wid/2,  Y0-ht/2);

                g2d.drawString (String.valueOf (ang)+"\u00B0", x1, y1);
            }
            else if (0 != (option & O_Lbl))  {
                g2d.drawString ("opposite",           X0+wid+10, Y0-ht/2);
                g2d.drawString ("adjacent",           X0+wid/2,  Y0-ht-10);
                g2d.drawString ("hypotenuse",         X0+wid/2,  Y0-ht/2);
                g2d.drawString ("\u03B1"+"\u00B0", x1, y1);
            }
            else if (0 != (option & O_Eq))  {
                String angS = "\u03B1"+"\u00B0";
                g2d.drawString ("hyp * sin("+angS+")", X0+wid+10, Y0-ht/2);
                g2d.drawString ("hyp * cos("+angS+")", X0+wid/2,  Y0-ht-10);
                g2d.drawString ("hyp",                 X0+wid/2,  Y0-ht/2);
                g2d.drawString ("\u03B1"+"\u00B0", x1, y1);
            }
        }

        // draw waveform
        if (0 != (option & O_Wave))
            paintWaves (g2d, ang);
    }

    // ----------------------------------------------------
    private  void paintArm (Graphics2D g2d)
    {
        int  X0  = 100;
        int  Y0  = Ht - 100;

        // text
        int fSize = 14;
        g2d.setFont (new Font ("Courier", Font.PLAIN, fSize));
        g2d.setColor (Color.white);

        // draw text, value for each servo
        int  yTxt = fSize;
        for (int n = 0; n < Nservo; n++)  {
            String fmt = "servo %d: ang %4d, cos %6.3f, sin %6.3f x %6d, y %6d";
            String t   = String.format (
                    fmt, n, ang [n], c [n], s [n], posX [n], posY [n]);
            if (n == servoIdx)
                t += " <";

            g2d.drawString (t, 10, yTxt);
            yTxt += fSize;
        }

        if (0 != keyVal)  {
            String t = String.format ("val %4d", keyVal);
            g2d.drawString (t, 10, yTxt);
        }

        // dimension of box arc resides within, arc centered
        final int OvalHt  = 20;
        final int OvalWid = 20;

        // call student routine
        computePos ();

        // update screen using computed values
        g2d.setColor (Color.red);
        int  x = 0;
        int  y = 0;

        for (int n = 0; n < Nservo; n++)  {
         // g2d.drawOval (X0 + x - OvalWid/2, Y0 + y - OvalHt/2,
            g2d.drawOval (
                X0 + x - OvalWid/2, Y0 - y - OvalHt/2, OvalWid, OvalHt);
            g2d.drawLine (X0 + x, Y0 - y, X0 + posX [n], Y0 - posY [n]);
            x = posX [n];
            y = posY [n];
        }

        // draw help text
        x = Wid - 250;
        y = 0;
        g2d.setFont (new Font ("Courier", Font.PLAIN, fSize));
        g2d.setColor (Color.white);

        g2d.drawString ("  A  Arm mode",               x, y+= fSize);
        g2d.drawString ("# a  set angle",              x, y+= fSize);
        g2d.drawString ("  d  show dashed lines",      x, y+= fSize);
        g2d.drawString ("  e  show equations",         x, y+= fSize);
        g2d.drawString ("  e  show labels",            x, y+= fSize);
        g2d.drawString ("  m  enable mouse selection", x, y+= fSize);
        g2d.drawString ("# s  select servo",           x, y+= fSize);
        g2d.drawString ("  T  Trig mode",              x, y+= fSize);
        g2d.drawString ("  v  show values",            x, y+= fSize);
        g2d.drawString ("  v  show wave",              x, y+= fSize);
        g2d.drawString ("# x  set segment x position", x, y+= fSize);
        g2d.drawString ("# y  set segment y position", x, y+= fSize);
        g2d.drawString ("  z  reset",                  x, y+= fSize);
    }
}
