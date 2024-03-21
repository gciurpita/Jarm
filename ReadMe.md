<h3>Multi Segment Arm and Sin/Cosine Teach Tool </h3>

<img src=arm.PNG align=right width=500>

The app provides the framework and graphics
to draw a multi-segment robotic arm given joint angles
that are determined by code written by students.
In other words,
the student adds code to <code>compute()</code> and
runs the code to see that the graphics illustrate
the desired result.

<p>
The exercise involves understanding
<ul>
 <li> the programming language,
 <li> writing code that determines the angles, and
 <li> uses geometric and trig functions
</ul>

<p>
The joint angles are in and array, <code>ang []</code>.
The number of segments is defined by <code>Nservo</code> and
the segment lengths are defined by <code>SegLen</code>
which can be changed.

<table width=100%> <tr><td> </table>
<img src=Jarm.PNG align=right width=500>
<p>
However,
since some students may not be familiar
with geometric or trigonometric relationships,
the app also has a mode to show their relationships
with a single radial line and
corresponding horizontal and verital components.
This mode provides graphical aids for explaining these concepts,
not for student to write code for.

<p>
In the image at right,
the red line is the radial being discussed,
dashed lines illustrate the horizontal and vertical components,
text can show the values of the three lines and angle,
trigonometric labels for the lines or
the equation showing their relationships.
The vertical line through the sine and cosine waves
corresponds to the angle of the radial.

<!-- ----------------------------------------------------  ----------------- -->
<hr>
<h4> Building and Running the App </h4>

The app can be invoked from a DOS <i>cmd</i> window
with the
<a href=https://docs.oracle.com/goldengate/1212/gg-winux/GDRAD/java.htm#BGBFJHAB>
Java  RunTime Environment</a> installed.
<pre>
java -jar Jarm.jar
</pre>

The app source code can be compiled and invoked
using the following commands.
The compiler requires the
<a href=https://www.oracle.com/java/technologies/downloads>
Java Development Kit (JDK)</a>
<pre>
javac Jarm.java
java Jarm
</pre>

Use Windows explorer to navigate to the directory
where the Jarm app is installed,
press <i>Alt-D</i> and enter <i>cmd</i>
to open a command prompt window
and enter the above commands.
