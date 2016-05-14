import com.github.lemmingswalker.*;
import com.github.lemmingswalker.processing.*;

PContourFinder pContourFinder;

PImage img;


void setup() {
  size(1600,1200);
  smooth();

  pContourFinder = new PContourFinder(this);

  img = loadImage("testImage.png");

}

void draw() {
  background(img);

  pContourFinder.setThreshold(map(mouseX, 0, width, 0, 256));
  pContourFinder.scan(img);
  
  stroke(255,0,0);
  noFill();
  
  pContourFinder.drawContours();
  
  pContourFinder.report();

}