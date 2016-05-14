import com.github.lemmingswalker.processing.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

import java.util.List;


// ContourMap by Cedric Kiefer
// http://www.onformative.com/lab/creating-contour-maps/





PGraphics pg;

float levels = 200;                   // number of contours
float elevation = 100;                 // total getHeight of the 3d model

float colorStart =  0;               // Starting degree of color range in HSB Mode (0-360)
float colorRange =  160;             // color range / can also be negative

PContourFinder contourFinder;
PContourCreator pBlobCreator;

boolean showBorder = true;

float maxDist = 1;

PeasyCam cam;


public void setup() {
    size(1000, 700, OPENGL);

    pg = createGraphics(128, 128, P2D);
    pg.noSmooth();

    pg.beginDraw();
    pg.background(255, 0, 0);
    pg.fill(255);
    pg.noStroke();
    pg.ellipse(pg.width / 2, pg.height / 2, pg.width, pg.height);
    pg.endDraw();

    pg.loadPixels();

    cam = new PeasyCam(this, 200);
    colorMode(HSB, 360, 100, 100);


    contourFinder = new PContourFinder(this);
    
    contourFinder.setScanIncrementX(16)
    .setScanIncrementY(16);
            



    //contourFinder.getBlobData().setComputeEdgeData(true);
    pBlobCreator = new PContourCreator();

    pBlobCreator.setComputeEdgeData(true)
    .setMinContourWidth(0)
    .setMinContourHeight(0);

    contourFinder.setContourCreator(pBlobCreator);

}



public void draw() {

   // contourFinder.reset();

    if (keyPressed) createNoiseImage(pg.pixels, pg.width, pg.height, 0.015f, 0.3f*frameCount);
    pg.updatePixels();



    background(0);

    pushMatrix();
    translate(-pg.width/2, -pg.height/2);

    noFill();

    int totalScanTime = 0;

    int totalEdgeVectorBenchMarkTime = 0;

    for (int i = 0; i < levels; i++) {

        int threshold = (int)map(i, 0, levels, 0, 255);
    
        //contourFinder.reset();
        contourFinder.setThreshold(threshold);

        int start = millis();
        contourFinder.scan(pg);
        totalScanTime += millis()-start;
        pushMatrix();
        translate(0, 0, (float)i * (elevation/levels));
        stroke((i/levels*colorRange)+colorStart, 100, 100);


        // benchmark
        int start2 = millis();
        for (PContour blob : pBlobCreator.getBlobs()) {
            List<PVector> vecs = blob.getEdgeVectors();
        }
        totalEdgeVectorBenchMarkTime += millis() - start2;

        if (showBorder) {
            //contourFinder.drawBlobs(g);
            pBlobCreator.drawBlobs(g);
        }
        else {


            for (PContour blob : pBlobCreator.getBlobs()) {
                
                //blob.normalize();
                
                List<PVector> vecs = blob.getEdgeVectors();

                beginShape();

                PVector pv = vecs.get(vecs.size()-1);
                PVector cv = vecs.get(0);
                
                // this connects the end with the start (we can't use endShape(CLOSE))
                  if (pv.x != 1 && pv.x != pg.width-2 && pv.y != 1 && pv.y != pg.height-2 ) {
                    vertex(pv.x, pv.y);
                  }
                 

                for (int j = 0; j < vecs.size(); j++) {
                    
                    pv = cv;
                    cv = vecs.get(j);

                    // ellipse:
                    if (dist(cv.x, cv.y, pv.x, pv.y ) > maxDist || dist(cv.x, cv.y, pg.width/2, pg.height/2) > pg.width/2-2) {
                    // rect:
                    
                    //if (cv.x == 1 || cv.x == pg.width-2 || cv.y == 1 || cv.y == pg.height-2 ) {
                     // if (pv.x == 1 || pv.x == pg.width-2 || pv.y == 1 || pv.y == pg.height-2 ) {
                        endShape();
                        beginShape();

                        continue;
                    }

                    vertex(cv.x, cv.y);

                }

                endShape();

            }
        }
        popMatrix();
    }



    popMatrix();

    cam.beginHUD();
    fill(255);
    text(frameRate, 20, 20);
    text("totalScanTime ms: "+totalScanTime, 20, 40);
    text("totalEdgeVectorBenchMarkTime ms: "+totalEdgeVectorBenchMarkTime, 20, 60);
    image(pg, width - pg.width, 0);
    cam.endHUD();
}


public void createNoiseImage(int[] pixels, int w, int h, float scale, float offset) {
    int x, y;

    int a; // alpha
    int v; // value

    a = 255 << 24;

    //int red = color(255, 0, 0);

    for (int i = 0; i <pixels.length; i++) {

        //if (pixels[i] == red ) continue;

        x = i % w;
        y = (i - x) / w;
        float extraZScale = 1.3f;
        v = (int)(noise((x+frameCount)*scale, (y)*scale, offset*scale*extraZScale)*255);
        v = constrain((int)map(v, 50, 200, 0, 255), 0, 255);
        pixels[i] = a | v << 16 | v << 8 | v;
    }
}

public void keyPressed() {

    if (key == 'b' || key == 'B') {
        showBorder = !showBorder;
    }
    else if (key == '1' || key == '!') {
        maxDist = 1f;
    }
    else if (key == '2' || key == '@') {
        maxDist = 1.5f;
    }

}