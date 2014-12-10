package com.github.lemmingswalker.processing;

import processing.core.PVector;

import java.util.Comparator;

/**
 * Created by doekewartena on 6/18/14.
 */
public class PVectorComparator implements Comparator<PVector> {

    public enum CompareType {
        X,
        Y,
        Z,
        XY
    }

    boolean ascending = true;

    CompareType compareType;


    public PVectorComparator() {
        // make x default
        compareType = CompareType.X;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PVectorComparator(CompareType compareType) {
        this.compareType = compareType;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PVectorComparator setAscending(boolean ascending) {
        this.ascending = ascending;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public PVectorComparator setDescending(boolean descending) {
        this.ascending = !descending;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean isAscending() {
        return ascending;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public boolean isDescending() {
        return !ascending;
    }



    // . . . . . . . . . . . . . . . . . . . . . . . .


    public PVectorComparator setCompareType(CompareType compareType) {
        this.compareType = compareType;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public CompareType getCompareType() {
        return compareType;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    @Override
    public int compare(PVector a, PVector b) {
//        switch (compareType) {
//            case X :
//                return compareX(a, b);
//            case Y :
//                return compareY(a, b);
//            case Z :
//                return compareZ(a, b);
//            default :
//                return compareX(a, b);
//        }
        if (ascending) {

            switch (compareType) {
                case X:
                    return compareXAsc(a, b);
                case Y:
                    return compareYAsc(a, b);
                case Z:
                    return compareZAsc(a, b);
                case XY:
                    return compareXYAsc(a, b);
                default:
                    return compareXAsc(a, b);
            }
        } else { // descending
            switch (compareType) {
                case X:
                    return compareXDes(a, b);
                case Y:
                    return compareYDes(a, b);
                case Z:
                    return compareZDes(a, b);
                case XY:
                    return compareXYDes(a, b);
                default:
                    return compareXDes(a, b);
            }
        }
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareXAsc(PVector a, PVector b) {
        if (a.x < b.x)
            return -1;
        else if (a.x > b.x)
            return 1;
        else
            return 0;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareYAsc(PVector a, PVector b) {
        if (a.y < b.y)
            return -1;
        else if (a.y > b.y)
            return 1;
        else
            return 0;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareZAsc(PVector a, PVector b) {
        if (a.z < b.z)
            return -1;
        else if (a.z > b.z)
            return 1;
        else
            return 0;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareXDes(PVector a, PVector b) {
        if (a.x < b.x)
            return 1;
        else if (a.x > b.x)
            return -1;
        else
            return 0;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareYDes(PVector a, PVector b) {
        if (a.y < b.y)
            return 1;
        else if (a.y > b.y)
            return -1;
        else
            return 0;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareZDes(PVector a, PVector b) {
        if (a.z < b.z)
            return 1;
        else if (a.z > b.z)
            return -1;
        else
            return 0;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareXYAsc(PVector a, PVector b) {
        int xDiff = compareXAsc(a, b);
        if (xDiff != 0) return xDiff;
        else return compareYAsc(a, b);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareXYDes(PVector a, PVector b) {
        int xDiff = compareXDes(a, b);
        if (xDiff != 0) return xDiff;
        else return compareYDes(a, b);
    }

    // . . . . . . . . . . . . . . . . . . . . . . . .






}
