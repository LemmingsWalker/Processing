package com.github.lemmingswalker.processing;

import com.github.lemmingswalker.ContourCreator;

import com.github.lemmingswalker.ListDivisor;
import processing.core.PGraphics;
import processing.core.PVector;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by doekewartena on 12/4/14.
 *
 * implementation for processing
 *
 *
 */
public class PContourCreator implements ContourCreator {

    protected ArrayList<PContour> blobs;
    // the array can be much bigger then the actual blobs
    // so we keep a count
    protected int nOfBlobs;

    PContour lastGivenBlob;

    PContourComparator blobComparator;


    // instead of having each blob it's own array for the
    // data, we give it a subList
    // this is better for low memory usage
    // and it avoids creating new vectors all the time
    // (it's the main reason for this class)
    // .z = pixel index!
    ListDivisor<PVector> cornerVectorsDivisor;

    ListDivisor<PVector> edgeVectorsDivisor;

    ListDivisor<PVector> cornerVectorsNormalizedDivisor;

    boolean containingBlobsComputed;
    boolean enclosedBlobsComputed;

    // =====================================
    // ========= S E T T I N G S ===========
    // =====================================

    // default
    private int initialBlobNumber = 50;

    boolean computeEdgeData = false;





    // ---------- contour specific -----------------

    private float minContourWidth = 0;
    private float minContourHeight = 0;
    private float maxContourWidth = 0;
    private float maxContourHeight = 0;




    // =====================================

    boolean didInit;


    // >>>>>>>>>>>>>>>>

    PContour currentBlob;

    // we use this to check if it's an outer blob or not
    //int[] pixels;
    //int imageWidth, imageHeight;


    // we store here for every edge index the contourExistCheckValue
    // this we we can check fast if a blob exists or not
    // it's not used atm but we might could give access
    // to the user
    // does it make sense? For selecting clicking a edge is kind of shit
    // if we will ise this, we could create another array that holds the index of the blob
    // that way we can return the blob with 2 checks
    int[] contourExistCheckData;
    int contourExistCheckValue;




    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public PContourCreator() {

        ListDivisor.InstanceHelper<PVector> ihVector = new ListDivisor.InstanceHelper<PVector>() {
            @Override
            public PVector createInstance() {
                return new PVector();
            }
            //@Override
            public boolean doResetInstances() {
                // we don't reset to save speed
                return false;
            }

            //@Override
            public void resetInstance(PVector v) {
                v.set(0,0,0);
            }
        };


        cornerVectorsDivisor = new ListDivisor<PVector>(new ArrayList<PVector>(), ihVector);
        edgeVectorsDivisor = new ListDivisor<PVector>(new ArrayList<PVector>(), ihVector);

        cornerVectorsNormalizedDivisor = new ListDivisor<PVector>(new ArrayList<PVector>(), ihVector);


    }



    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * The higher the number the more memory it will take but the less has to be created on runtime.
     *
     *
     * @param initialBlobNumber  Amount of blobs that will be created on initialisation of the class.
     * @return
     */
    public PContourCreator setInitialBlobNumber(int initialBlobNumber) {

        if (didInit) {
            // todo, throw error?
            System.err.println("ERROR: setInitialBlobNumber(int) not allowed after init.");
            return this;
        }
        this.initialBlobNumber = initialBlobNumber;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourCreator setMinContourWidth(int i) {
        minContourWidth = i;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourCreator setMinContourHeight(int i) {
        minContourHeight = i;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourCreator setMaxContourWidth(int i) {
        maxContourWidth = i;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourCreator setMaxContourHeight(int i) {
        maxContourHeight = i;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMinContourWidth() {
        return minContourWidth;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMinContourHeight() {
        return minContourHeight;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMaxContourWidth() {
        return maxContourWidth;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public float getMaxContourHeight() {
        return maxContourHeight;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .



    protected void init() {

        if (didInit) return;

        blobs = new ArrayList<PContour>(initialBlobNumber);

        PContour b;
        for (int i = 0; i < initialBlobNumber; i++) {
            b = new PContour(this);
            blobs.add(b);
        }

        blobComparator = new PContourComparator();

        didInit = true;

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public void reset() {
        cornerVectorsDivisor.reset();
        edgeVectorsDivisor.reset();
        nOfBlobs = 0;

        lastGivenBlob = null;

        containingBlobsComputed = false;
        enclosedBlobsComputed = false;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public void clear() {
        cornerVectorsDivisor.clear();
        edgeVectorsDivisor.clear();
        blobs.clear();
        reset();
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public int nOfBlobs() {
        return nOfBlobs;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public ArrayList<PContour> getBlobs () {
        init();
        return new ArrayList<PContour>(blobs.subList(0, nOfBlobs));
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    /*
   This will set up links to the arrays.
    */
    protected PContour getOrCreateBlob () {

        init();

        if (nOfBlobs >= blobs.size()) {
            blobs.add(new PContour(this));
        }

        lastGivenBlob = blobs.get(nOfBlobs++);

        return lastGivenBlob;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    protected void returnLastGivenBlob () {
        nOfBlobs--;
        lastGivenBlob = null;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean pointOnEdgeBlob(int x, int y, float edgeHitEpsilon) {

        init();

        for (int i = 0; i < nOfBlobs; i++) {
            PContour b = blobs.get(i);
            if (b.edgeHit(x, y, edgeHitEpsilon)) return true;
        }
        return false;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    // is there any reason to make this public?
    protected void computeContainingBlobs() {
        if (containingBlobsComputed) return;
        computeContainingBlobs(blobs, 0, nOfBlobs);

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    // is there any reason to make this public?
    // i prefer to keep the library simple and clean
    // the user can just call setComputeContainingBlobs(true)
    // and the method doesn't take long
    // maybe an advanced user wants to do this only with a certain range
    // open for discussion

    protected void computeContainingBlobs(ArrayList<PContour> blobs, int startIndex, int endIndex) {

        if (containingBlobsComputed) return;

        PContour b1, b2;

        Rectangle2D r1 = new Rectangle.Float();
        Rectangle2D r2 = new Rectangle.Float();

        for (int i = startIndex; i < endIndex; i++) {
            b1 = blobs.get(i);

            r1.setRect(b1.getMinX(), b1.getMinY(), b1.getWidth(), b1.getHeight());

            for (int j = startIndex; j < endIndex; j++) {

                // don't compare to it's self
                if (i == j) continue;

                b2 = blobs.get(j);

                r2.setRect(b2.getMinX(), b2.getMinY(), b2.getWidth(), b2.getHeight());

                if (r1.contains(r2)) {
                    // this works but it does not create a hierarchy
                    //b1.containingBlobs.add(b2);
                    //PApplet.println(r1.getMinX(), r1.getMinY(), r1.getWidth(), r1.getHeight());
                    //PApplet.println(r2.getMinX(), r2.getMinY(), r2.getWidth(), r2.getHeight());

                    // this takes care of the hierarchy
                    b1.addContainingBlob(b2, 0);
                }

            }

            // we never need this
            //b1.containingBlobsComputed = true;

        }

        containingBlobsComputed = true;

    }


    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    protected void computeEnclosedBlobs() {
        if (enclosedBlobsComputed) return;
        computeEnclosedBlobs(blobs, 0, nOfBlobs);

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .

    // same thing about public as by computeContainingBlobs
    // also, should the method be in this class?
    protected void computeEnclosedBlobs(ArrayList<PContour> blobs, int startIndex, int endIndex) {

        if (enclosedBlobsComputed) return;

        // sort the blobs so we can start with the smallest
        blobComparator.setSortTypeAscending(true);
        blobComparator.setCompareType(PContourComparator.CompareType.BOUNDING_AREA);

        Collections.sort(blobs.subList(startIndex, endIndex), blobComparator);


        for (int i = startIndex; i < endIndex; i++) {
            PContour b = blobs.get(i);

            for (PContour containedBlob : b.containingBlobs) {

                if (containedBlob.enclosedBlobsComputed) continue;

                if (b.isOuterContour != containedBlob.isOuterContour) {
                    containedBlob.enclosingParent = b;
                    containedBlob.enclosedBlobsComputed = true;
                    b.enclosedBlobs.add(containedBlob);
                }
            }
        }

        // not all blobs will have enclosedBlobsComputed
        // set to true, do that now
        // also set the depth
        for (int i = startIndex; i < endIndex; i++) {
            PContour b = blobs.get(i);
            b.enclosedBlobsComputed = true;

            if (b.enclosingParent == null) {  // we can't call b.hasParent() cause that will call this method..
                b.computeDepth(0);
            }
        }

        enclosedBlobsComputed = true;

    }




    // ==========================================================
    // =================== D R A W   M E T H O D S ==============
    // ==========================================================


    public void drawBlobs(PGraphics g) {
        for (int i = 0; i < nOfBlobs; i++) {
            blobs.get(i).draw(g);
        }
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     * Use this when setNormalize is set to true.
     *
     *
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawBlobs(PGraphics g, float x, float y, float w, float h) {
        for (int i = 0; i < nOfBlobs; i++) {
            blobs.get(i).draw(g, x, y, w, h);
        }
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public void drawBlobBoundings(PGraphics g) {
        for (int i = 0; i < nOfBlobs; i++) {
            blobs.get(i).drawBounding(g);
        }
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    /**
     *
     * Use this when setNormalize is set to true.
     *
     * @param g
     * @param x
     * @param y
     * @param w
     * @param h
     */
    public void drawBlobBoundings(PGraphics g, float x, float y, float w, float h) {
        for (int i = 0; i < nOfBlobs; i++) {
            blobs.get(i).drawBounding(g, x, y, w, h);
        }
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .



    public boolean checkForExistingBlob(int index, int x, int y) {
        //return pointOnEdgeBlob(x, y, 0.5f) || pointOnEdgeRejectedBlob(x, y, 0.5f);
        return contourExistCheckData[index] == contourExistCheckValue;
    }




    // =========================================================


    /**
     *
     * @param computeEdgeData
     * @return
     */
    public PContourCreator setComputeEdgeData(boolean computeEdgeData) {
        this.computeEdgeData = computeEdgeData;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . . .


    public boolean isComputeEdgeData() {
        return computeEdgeData;
    }



    // =========================================================

    @Override
    public void startOfScan(int[] pixels, int imageWidth, int imageHeight) {
        init();
        reset();

        if (contourExistCheckData == null || contourExistCheckData.length < pixels.length) {
            contourExistCheckData = new int[pixels.length];
        }
        contourExistCheckValue++;

        //this.pixels = pixels;
        //this.imageWidth = imageWidth;
        //this.imageHeight = imageHeight;
    }

    @Override
    public void startContour(int startIndex, int[] pixels, int imageWidth, int imageHeight) {
       // screw startIndex
       currentBlob = getOrCreateBlob();
       currentBlob.reset();
       currentBlob.setImageSize(imageWidth, imageHeight);
    }

    @Override
    public void contourCreationFail() {
       returnLastGivenBlob();
    }

    @Override
    public void finishContour(int[] pixels, int imageWidth, int imageHeight) {

        currentBlob.prepareForUse();

        // check if the blob is valid
        if (currentBlob.getWidth() >= minContourWidth && currentBlob.getHeight() >= minContourHeight) {

            boolean widthOk = (maxContourWidth <= 0 || currentBlob.getWidth() <= maxContourWidth);
            boolean heightOk = (maxContourHeight <= 0 || currentBlob.getHeight() <= maxContourHeight);

            if (!widthOk || !heightOk) {
                returnLastGivenBlob();
            }

        }


    }


    @Override
    public void addCorner(int index, int x, int y) {
        //System.out.println(x+" "+y);
        currentBlob.addCorner(index, x, y);
    }

    @Override
    public void addEdge(int index, int x, int y) {
        contourExistCheckData[index] = contourExistCheckValue;
        if (computeEdgeData) {
            currentBlob.addEdge(index, x, y);
        }
    }

    @Override
    public void setMinAndMaxCornerValues(int minXIndex, int minX, int minYIndex, int minY, int maxXIndex, int maxX, int maxYIndex, int maxY) {
       currentBlob.setMinAndMaxCornerValues(minXIndex, minX, minYIndex, minY, maxXIndex, maxX, maxYIndex, maxY);
    }

    @Override
    public void isOuterContour(boolean isOuterContour) {
         currentBlob.isOuterContour = isOuterContour;
    }

    @Override
    public void finishOfScan() {
        // we do nothing...
    }

}

