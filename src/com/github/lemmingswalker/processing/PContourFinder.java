package com.github.lemmingswalker.processing;

import com.github.lemmingswalker.ContourFinder;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by doekewartena on 12/9/14.
 */
public class PContourFinder extends ContourFinder {

    PApplet p;

    PContourCreator pContourCreator;

    public PContourFinder(PApplet p) {
        this.p = p;

        pContourCreator = new PContourCreator();

        setContourCreator(pContourCreator);
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    // up to the user to load pixels
    public void scan(PImage img) {
        scan(img.pixels, img.width, img.height);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public ArrayList<PContour> getContours() {
        return pContourCreator.getBlobs();
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .


    public void drawContours() {
        pContourCreator.drawBlobs(p.g);
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .



    public void drawScanLines(PGraphics g, int w, int h) {

        if (isScanOverX()) {
            drawScanLinesOverX(g, w, h);
        }
        else {
            drawScanLinesOverY(g, w, h);
        }



    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void drawScanLinesOverX(PGraphics g, int w, int h) {

        /*
        todo, we have to look in the roi
        final boolean overX = true;

        Rectangle wROI = createWorkROI(w, h);

        final int xIncrement = overX ? 1 : getScanIncrementX();
        final int yIncrement = overX ? getScanIncrementY() : 1;
        final int startX = overX ? wROI.x : wROI.x + getScanIncrementX();
        final int startY = overX ? wROI.y + getScanIncrementY() : wROI.y;
        final int maxX = wROI.x + wROI.width;
        final int maxY = wROI.y + wROI.height;


        for (int y = startY; y < maxY; y += yIncrement) {
            g.line(startX, y, maxX, y);
        }
        */

    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void drawScanLinesOverY(PGraphics g, int w, int h) {

        /*
        todo, we have to look in the roi
        final boolean overX = false;

        Rectangle wROI = createWorkROI(w, h);

        final int xIncrement = overX ? 1 : getScanIncrementX();
        final int yIncrement = overX ? getScanIncrementY() : 1;
        final int startX = overX ? wROI.x : wROI.x + getScanIncrementX();
        final int startY = overX ? wROI.y + getScanIncrementY() : wROI.y;
        final int maxX = wROI.x + wROI.width;
        final int maxY = wROI.y + wROI.height;


        for (int x = startX; x < maxX; x += xIncrement) {
            g.line(x, startY, x, maxY);
        }
        */

    }

    // . . . . . . . . . . . . . . . . . . . . . . . .



}
