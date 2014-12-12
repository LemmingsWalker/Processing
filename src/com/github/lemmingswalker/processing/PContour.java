package com.github.lemmingswalker.processing;

import com.github.lemmingswalker.SubListGetter;
import com.github.lemmingswalker.ThresholdChecker;
import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static processing.core.PApplet.abs;
import static processing.core.PApplet.dist;
import static processing.core.PConstants.*;

/**
 * Created by doekewartena on 4/30/14.
 */
public class PContour {

    // We need this since a lot of data is stored there
    PContourCreator pBlobCreator;

    int imageWidth;
    int imageHeight;

    // subList
    SubListGetter<PVector> cornerVectorsGetter;
    boolean preparedForUse;

    SubListGetter<PVector> edgeVectorsGetter;
    boolean edgeVectorsComputed;

    protected float minX, minY, maxX, maxY;

    // a containing blob is within the bounds of this blob
    ArrayList<PContour> containingBlobs = new ArrayList<PContour>();
    //boolean containingBlobsComputed;

    // a enclosed blob will be encapsulated by this shape
    ArrayList<PContour> enclosedBlobs = new ArrayList<PContour>();
    boolean enclosedBlobsComputed; // we need this in order to compute it
    // the depth for the enclosed blobs hierarchy
    int depth;

    boolean isOuterContour;

    // a blob has a enclosingParent if it's enclosed by another blob
    // so to have this set computeEnclosedBlobsStep1 has to
    // be called
    PContour enclosingParent;


    boolean normalized;


    // this is used by the blobManager
    // we create a 2 way link
    // to make certain things easier
    // todo, disabled for now until basics work again
    //public ContourInfo blobInfo;


    public PContour(PContourCreator pBlobCreator) {
        init(pBlobCreator);
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    protected void init(PContourCreator pBlobCreator) {

        this.pBlobCreator = pBlobCreator;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    protected static boolean onLine(PVector a, PVector b, float x, float y, float epsilon) {
        return abs(dist(a.x, a.y, x, y) + dist(x, y, b.x, b.y) - dist(a.x, a.y, b.x, b.y)) < epsilon;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    protected void setImageSize(int width, int height) {
        imageWidth = width;
        imageHeight = height;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMinX() {
        return minX;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMaxX() {
        return maxX;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMinY() {
        return minY;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMaxY() {
        return maxY;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getWidth() {
        return getMaxX() - getMinX();
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getHeight() {
        return getMaxY() - getMinY();
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getCenterX() {
        return 0.5f  * getWidth() + getMinX() ;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public float getCenterY() {
        return 0.5f * getHeight() + getMinY();
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    protected void reset() {

        edgeVectorsComputed = false;
        preparedForUse = false;

        imageWidth = -1;
        imageHeight = -1;

        // we don't clear the ArrayLists
        // since we will return a subList
        // but let's set it to null for now

        cornerVectorsGetter = null;
        edgeVectorsGetter = null;

        depth = 0;

        normalized = false;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public boolean edgeHit(float x, float y, float edgeHitEpsilon) {

        // quick test if it's worth it
        if (x < getMinX()-edgeHitEpsilon || x > getMaxX()+edgeHitEpsilon || y < getMinY()-edgeHitEpsilon || y > getMaxY()+edgeHitEpsilon) {
            return false;
        }

        List<PVector> cornerVectors = getCornerVectors();

        // the last one should be connected to the first
        PVector pre = cornerVectors.get(cornerVectors.size() -1);
        for (PVector cur : cornerVectors) {

            if(PContour.onLine(pre, cur, x, y, edgeHitEpsilon)) {
                return true;
            }
            pre = cur;
        }
        return false;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public boolean isOuterContour() {
        return isOuterContour;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean isInnerContour() {
        return !isOuterContour();
    }

 
    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .



    protected void addCorner(int index, int x, int y) {
        pBlobCreator.cornerVectorsDivisor.getNext().set(x, y, 0);
    }
    
    // . . . . . . . . . . . . . . . . . . . . . . .

    protected void addEdge(int index, int x, int y) {
        pBlobCreator.edgeVectorsDivisor.getNext().set(x, y, 0);
    }

    // . . . . . . . . . . . . . . . . . . . . . . .

    public void setMinAndMaxCornerValues(int minXIndex, int minX, int minYIndex, int minY, int maxXIndex, int maxX, int maxYIndex, int maxY) {

        // screw the indexes

        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;

    }

    // . . . . . . . . . . . . . . . . . . . . . . .

    protected void prepareForUse() {

        cornerVectorsGetter = pBlobCreator.cornerVectorsDivisor.getSubListGetter();

        if (pBlobCreator.computeEdgeData) {
            edgeVectorsGetter = pBlobCreator.edgeVectorsDivisor.getSubListGetter();
        }

        preparedForUse = true;

    }



    // . . . . . . . . . . . . . . . . . . . . . . .

    public void normalize() {

        if (normalized) return;

        for (PVector v : getCornerVectors()) {
            v.x /= imageWidth;
            v.y /= imageHeight;
        }
        if (pBlobCreator.isComputeEdgeData()) {
            for (PVector v : edgeVectorsGetter.getSubList()) {
                v.x /= imageWidth;
                v.y /= imageHeight;
            }
        }

        minX /= imageHeight;
        minY /= imageHeight;
        maxX /= imageWidth;
        maxY /= imageHeight;

        normalized = true;

    }

    // . . . . . . . . . . . . . . . . . . . . . . .



    public List<PVector> getEdgeVectors() {

        // todo, some check if setComputeEdgeData was set BEFORE THE SCAN HAPPENED!
        // maybe throw an error
        if (!pBlobCreator.isComputeEdgeData()) {
            System.err.println("Error: computeEdgeData is false, maybe you need to set blobData.setComputeEdgeData(true)?");
            return null;
        }

        return edgeVectorsGetter.getSubList();
    }


    // . . . . . . . . . . . . . . . . . . . . . . .


    public List<PVector> getCornerVectors() {
        return cornerVectorsGetter.getSubList();
    }



    // . . . . . . . . . . . . . . . . . . . . . . .

    public ArrayList<PContour> getContainingBlobs() {
        pBlobCreator.computeContainingBlobs();
        return containingBlobs;
    }


    // . . . . . . . . . . . . . . . . . . . . . . .

    public ArrayList<PContour> getEnclosedBlobs() {
        pBlobCreator.computeEnclosedBlobs();
        return enclosedBlobs;
    }




    // . . . . . . . . . . . . . . . . . . . . . . .


    protected void addContainingBlob(PContour blobToAdd, int debugDepth) {
        // It should already be sure that this blob really contains the blob to add.
        // This method takes care of the hierarchy

        //PApplet.println(debugDepth);

        if (blobToAdd == this) {
            return;
        }

        if (containingBlobs.size() == 0) {
            containingBlobs.add(blobToAdd);
            return;
        }

        // first check if we already have a containing blob that
        // can hold the one we like to add
        Rectangle2D r1 = new Rectangle.Float();
        r1.setRect(blobToAdd.getMinX(), blobToAdd.getMinY(), blobToAdd.getWidth(), blobToAdd.getHeight());

        Rectangle2D r2 = new Rectangle.Float();

        for (PContour b : containingBlobs) {
            r2.setRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());

            if (r2.contains(r1)) {
                b.addContainingBlob(blobToAdd, debugDepth+1);
                return;
            }

        }

        // it can also be that one OR MORE of the containing blobs can fit in the blob we like to add
        for (int i = containingBlobs.size()-1; i >= 0; i--) {
            PContour b = containingBlobs.get(i);
            r2.setRect(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());

            if (r1.contains(r2)) {
                containingBlobs.remove(i);
                // no need to do it recursive since we know
                // the rest will fits as well
                //blobToAdd.addContainingBlob(b);
                // so instead we use
                blobToAdd.containingBlobs.add(b);
            }

        }
        containingBlobs.add(blobToAdd);


    }



    // . . . . . . . . . . . . . . . . . . . . . . .

    protected void computeDepth(int depth) {

        this.depth = depth;

        for (PContour b : enclosedBlobs) {
            b.computeDepth(depth+1);
        }

    }


    // . . . . . . . . . . . . . . . . . . . . . . .

    public int getDepth() {
        pBlobCreator.computeEnclosedBlobs();
        return depth;

    }

    // . . . . . . . . . . . . . . . . . . . . . . .

    public boolean hasParent() {
        pBlobCreator.computeEnclosedBlobs();
        return enclosingParent != null;
    }


    // . . . . . . . . . . . . . . . . . . . . . . .

    public boolean hasChildren() {
        pBlobCreator.computeEnclosedBlobs();
        return enclosedBlobs.size() > 0;
    }

    // =============================================================
    // =================== D R A W   M E T H O D S =================
    // =============================================================

    public void draw(PGraphics g) {

        g.beginShape();

        for (PVector v : getCornerVectors()) {
            g.vertex(v.x, v.y);
        }
        g.endShape(PConstants.CLOSE);
    }


    // . . . . . . . . . . . . . . . . . . . . . . .


    public void draw(PGraphics g, float x, float y, float w, float h) {

        if (!normalized && x == 0 && y == 0 && w == imageWidth && h == imageHeight) draw(g);

        g.pushMatrix();
        g.translate(x, y);

        float xm = normalized ? w : w / imageWidth;
        float ym = normalized ? h : h / imageHeight;

        g.beginShape();
        for (PVector v : getCornerVectors()) {
            g.vertex(v.x * xm, v.y * ym);
        }
        g.endShape(PConstants.CLOSE);

        g.popMatrix();

    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public void drawBounding (PGraphics g) {

        g.pushStyle();
        g.rectMode(CORNERS);
        g.rect(getMinX(), getMinY(), getMaxX(), getMaxY());
        g.popStyle();
    }



    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public void drawBounding (PGraphics g, float x, float y, float w, float h) {

        float xm = normalized ? w : w / imageWidth;
        float ym = normalized ? h : h / imageHeight;

        g.pushMatrix();
        g.translate(x, y);

        g.pushStyle();
        g.rectMode(CORNERS);
        g.rect(getMinX()*xm, getMinY()*ym, getMaxX()*xm, getMaxY()*ym);
        g.popStyle();

        g.popMatrix();
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


}
