package com.github.lemmingswalker.processing;

import com.github.lemmingswalker.ContourCreator;
import processing.core.PApplet;
import processing.core.PConstants;
import processing.core.PShape;

import java.util.ArrayList;

/**
 * Created by doekewartena on 12/12/14.
 */
public class PShapeCreator implements ContourCreator {

    PApplet p;

    public ArrayList<PShape> shapes;

    PShape currentShape;

    public PShapeCreator(PApplet p) {
        this.p = p;
        shapes = new ArrayList<PShape>();
    }

    @Override
    public void startOfScan(int[] pixels, int imageWidth, int imageHeight) {
        shapes.clear();
    }


    @Override
    public void startContour(int startIndex, int[] pixels, int imageWidth, int imageHeight) {
        currentShape = p.createShape();
        currentShape.beginShape();
    }

    @Override
    public void contourCreationFail() {
        currentShape.endShape(PConstants.CLOSE);
    }

    @Override
    public void finishContour(int[] pixels, int imageWidth, int imageHeight) {
       currentShape.endShape(PConstants.CLOSE);
       shapes.add(currentShape);
    }

    @Override
    public void addCorner(int index, int x, int y) {
        currentShape.vertex(x, y);
    }

    @Override
    public void addEdge(int index, int x, int y) {
        // do nothing
    }

    @Override
    public void setMinAndMaxCornerValues(int minXIndex, int minX, int minYIndex, int minY, int maxXIndex, int maxX, int maxYIndex, int maxY) {
        // do nothing
    }

    @Override
    public void isOuterContour(boolean isOuterContour) {
        // do nothing
    }

    @Override
    public void finishOfScan() {
        // do nothing
    }
}
