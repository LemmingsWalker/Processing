package com.github.lemmingswalker.processing;

import java.util.Comparator;

/**
 * Created by doekewartena on 6/14/14.
 */
public class PContourComparator implements Comparator<PContour> {


//    public enum SortType {
//        ASCENDING,
//        DESCENDING
//    }

    boolean ascending = true;

    public enum CompareType {
        BOUNDING_AREA,
        DEPTH,
    }

    //SortType sortType;
    CompareType compareType;


    public PContourComparator() {
       // make ascending default
       //sortType = SortType.ASCENDING;
       // make bounding default
       compareType = CompareType.BOUNDING_AREA;
    }


    public PContourComparator(CompareType compareType) {
        this.compareType = compareType;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourComparator setSortTypeAscending(boolean b) {
        ascending = b;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . .


    public PContourComparator setSortTypeDescending(boolean b) {
        ascending = !b;
        return this;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public PContourComparator setCompareType(CompareType compareType) {
        this.compareType = compareType;
        return this;
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public CompareType getCompareType() {
        return compareType;
    }


    // . . . . . . . . . . . . . . . . . . . . . . . . .




    @Override
    public int compare(PContour blob, PContour blob2) {

        if (ascending) {

            switch (compareType) {
                case BOUNDING_AREA:
                    return compareBoundingAreaAsc(blob, blob2);
                case DEPTH:
                    return compareDepthAsc(blob, blob2);
                default:
                    return compareBoundingAreaAsc(blob, blob2);
            }
        } else { // descending
            switch (compareType) {
                case BOUNDING_AREA:
                    return compareBoundingAreaDes(blob, blob2);
                case DEPTH:
                    return compareDepthDes(blob, blob2);
                default:
                    return compareBoundingAreaDes(blob, blob2);
            }
        }

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareBoundingAreaAsc(PContour blob, PContour blob2) {

      final float bAreaB1 = blob.getWidth()*blob.getHeight();
      final float bAreaB2 = blob2.getWidth()*blob2.getHeight();

      if (bAreaB1 < bAreaB2) {
          return -1;
      }
      else if (bAreaB1 > bAreaB2) {
          return 1;
      }
      else {
          return 0;
      }

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareBoundingAreaDes(PContour blob, PContour blob2) {

        final float bAreaB1 = blob.getWidth()*blob.getHeight();
        final float bAreaB2 = blob2.getWidth()*blob2.getHeight();

        if (bAreaB1 < bAreaB2) {
            return 1;
        }
        else if (bAreaB1 > bAreaB2) {
            return -1;
        }
        else {
            return 0;
        }
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareDepthAsc(PContour blob, PContour blob2) {

        if (blob.depth < blob2.depth) {
            return -1;
        }
        else if (blob.depth > blob2.depth) {
            return 1;
        }
        else {
            return 0;
        }

    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .

    public int compareDepthDes(PContour blob, PContour blob2) {

        if (blob.depth < blob2.depth) {
            return 1;
        }
        else if (blob.depth > blob2.depth) {
            return -1;
        }
        else {
            return 0;
        }
    }

    // . . . . . . . . . . . . . . . . . . . . . . . . .
}
