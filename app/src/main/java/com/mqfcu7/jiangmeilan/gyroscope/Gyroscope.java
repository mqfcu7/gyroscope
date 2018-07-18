package com.mqfcu7.jiangmeilan.gyroscope;

import android.graphics.PointF;

public class Gyroscope {
    public static final float EXP = 0.000001f;
    public static final int INVALID_SELECTED_SECTION = -1;
    private static final int ARROW_START_ANGLE = 290;
    private static final float ARROW_LINE_WIDTH_RATIO = 1.0f / 15;
    private static final float ARROW_FLAG_MARGIN_RATIO = 1.0f / 15;
    private static final float INNER_RADIUS_RATIO = 1.0f / 2;

    public static class Line {
        public PointF s = new PointF();
        public PointF e = new PointF();
    }

    public static class Circle {
        public PointF c = new PointF();
        public float r;
    }

    private PointF mCenter;
    private int mRadius;
    private String mTitle;
    private int mSectionsNum;
    private Line[] mSectionsLine;
    private float[] mSectionsAngle;
    private String[] mSectionsName;

    private Line mArrowLine;
    private Line mArrowSubLine;
    private Line mArrowFlagLine;

    private int mArrowLineWidth;
    private int mArrowFlagMargin;
    private float mArrowCurrentAngle;
    private float mArrowStartAngle;
    private int mSelectedSection = INVALID_SELECTED_SECTION;

    public Gyroscope() {}

    public void init(PointF center, int radius, int sectionsNum) {
        float angle = 360.0f / sectionsNum;
        float[] sectionsAngle = new float[sectionsNum];
        for (int i = 0; i < sectionsNum; ++ i) {
            sectionsAngle[i] = angle;
        }
        init(center, radius, "", sectionsNum, sectionsAngle, null, ARROW_START_ANGLE);
    }

    public void init(PointF center,
                     int radius,
                     String title,
                     int sectionsNum,
                     float[] sectionsAngle,
                     String[] sectionsName,
                     float arrowAngle) {
        mCenter = center;
        mRadius = radius;

        mTitle = title;
        mSectionsNum = sectionsNum;
        mSectionsLine = new Line[mSectionsNum];
        mSectionsAngle = new float[mSectionsNum];
        mSectionsName = new String[mSectionsNum];
        for (int i = 0; i < mSectionsNum; ++ i) {
            mSectionsLine[i] = new Line();
            mSectionsAngle[i] = sectionsAngle[i];
            if (sectionsName != null && sectionsName.length == mSectionsNum) {
                mSectionsName[i] = sectionsName[i];
            }
        }
        calcSectionsPosition();

        mArrowStartAngle = arrowAngle;
        mArrowCurrentAngle = mArrowStartAngle;
        mArrowLineWidth = (int)(radius * ARROW_LINE_WIDTH_RATIO);
        mArrowFlagMargin = (int)(radius * ARROW_FLAG_MARGIN_RATIO);
        mArrowLine = new Line();
        mArrowSubLine = new Line();
        mArrowFlagLine = new Line();
        calcArrowPosition();
    }

    private void calcSectionsPosition() {
        float angle = 90 - mSectionsAngle[0] / 2;
        for (int i = 0; i < mSectionsNum; ++ i) {
            mSectionsLine[i].s.set(mCenter);
            double radian = Math.toRadians(angle);
            mSectionsLine[i].e.set(
                    (float)(Math.cos(radian) * mRadius + mCenter.x),
                    (float)(Math.sin(radian) * mRadius + mCenter.y));
            angle += mSectionsAngle[i];
        }
    }

    private void calcArrowPosition() {
        float radian = (float) Math.toRadians(mArrowCurrentAngle);

        mArrowLine.s.set(mCenter);
        mArrowLine.e.set(
                (float)(Math.cos(radian) * (mRadius - mArrowFlagMargin) + mCenter.x),
                (float)(Math.sin(radian) * (mRadius - mArrowFlagMargin) + mCenter.y));

        mArrowSubLine.s.set(mCenter);
        mArrowSubLine.e.set(
                (float)(Math.cos(radian) * mRadius * INNER_RADIUS_RATIO + mCenter.x),
                (float)(Math.sin(radian) * mRadius * INNER_RADIUS_RATIO + mCenter.y));

        mArrowFlagLine.s.set(
                (float)(Math.cos(radian) * mRadius * INNER_RADIUS_RATIO + mCenter.x),
                (float)(Math.sin(radian) * mRadius * INNER_RADIUS_RATIO + mCenter.y));
        mArrowFlagLine.e.set(
                (float)(Math.cos(radian) * (mRadius - mArrowFlagMargin) + mCenter.x),
                (float)(Math.sin(radian) * (mRadius - mArrowFlagMargin) + mCenter.y));
    }

    public void setStartAngle(float angle) {
        mSelectedSection = INVALID_SELECTED_SECTION;
        mArrowStartAngle = angle;
        mArrowCurrentAngle = angle;
        calcArrowPosition();
    }

    public boolean updateArrowAngle(float angle) {
        float sign = angle / Math.abs(angle);
        if (sign * mArrowCurrentAngle >= sign * (mArrowStartAngle + angle)) {
            mArrowCurrentAngle %= 360;
            mArrowStartAngle = mArrowCurrentAngle;
            mSelectedSection = calcSelectedSection(mArrowCurrentAngle);
            return false;
        }

        mSelectedSection = INVALID_SELECTED_SECTION;
        mArrowCurrentAngle = mArrowStartAngle + angle;
        calcArrowPosition();
        return true;
    }

    public void setSectionsNum(int num) {
        mSectionsNum = num;
        mSectionsLine = new Line[mSectionsNum];
        mSectionsAngle = new float[mSectionsNum];
        float angle = 360.0f / mSectionsNum;
        mSectionsName = new String[mSectionsNum];
        for (int i = 0; i < mSectionsNum; ++ i) {
            mSectionsLine[i] = new Line();
            mSectionsAngle[i] = angle;
            mSectionsName[i] = String.valueOf(i + 1);
        }
        calcSectionsPosition();
    }

    public void setNewTitle(String title, int sectionsNum, float[] sectionsAngle, String[] sectionsName) {
        mTitle = title;
        setSectionsNum(sectionsNum);
        mSectionsName = new String[sectionsNum];
        if (sectionsName != null && sectionsName.length == sectionsNum) {
            for (int i = 0; i < sectionsNum; ++ i) {
                mSectionsName[i] = sectionsName[i];
            }
        }
        mSelectedSection = INVALID_SELECTED_SECTION;
    }

    public String getTitle() {
        return mTitle;
    }

    public int getSectionsNum() {
        return mSectionsNum;
    }

    public float[] getSectionsAngle() { return mSectionsAngle; }

    public String[] getSectionsName() { return mSectionsName; }

    public int getArrowLineWidth() {
        return mArrowLineWidth;
    }

    public Line getArrowSubLine() {
        return mArrowSubLine;
    }

    public Line getArrowFlagLine() {
        return mArrowFlagLine;
    }

    public Line[] getSectionsLine() {
        return mSectionsLine;
    }

    public float getArrowCurrentAngle() { return mArrowCurrentAngle; }

    public int getSelectedSection() { return mSelectedSection; }

    public void setSelectedSection(int selectedSection) { mSelectedSection = selectedSection; }

    public Circle getOutCircle() {
        Circle circle = new Circle();
        circle.c.set(mCenter);
        circle.r = mRadius;
        return circle;
    }

    public Circle getInnerCircle() {
        Circle circle = new Circle();
        circle.c.set(mCenter);
        circle.r = mRadius * INNER_RADIUS_RATIO;
        return circle;
    }

    public Circle getInnermostCircle() {
        Circle circle = new Circle();
        circle.c.set(mCenter);
        circle.r = mRadius * 1.0f / 9;
        return circle;
    }

    public boolean isTouchArrow(PointF point) {
        return calcPointToLineDistance(point, mArrowLine) < mArrowLineWidth;
    }

    public float calcForceValue(PointF accelerate) {
        if (Math.abs(accelerate.x) < EXP || Math.abs(accelerate.y) < EXP) {
            return 0;
        }

        PointF lineVector = new PointF(mArrowFlagLine.e.x - mArrowFlagLine.s.x,
                mArrowFlagLine.e.y - mArrowFlagLine.s.y);
        float cosTheta = getVectorDot(lineVector, accelerate) / (getVectorNorm(lineVector) * getVectorNorm(accelerate));
        float sinTheta = (float)Math.sqrt(1 - cosTheta * cosTheta);
        return getVectorNorm(accelerate) * sinTheta;
    }

    public int calcForceOrientation(PointF accelerate) {
        PointF lineVector = new PointF(mArrowFlagLine.e.x - mArrowFlagLine.s.x,
                mArrowFlagLine.e.y - mArrowFlagLine.s.y);
        return lineVector.x * accelerate.y - lineVector.y * accelerate.x > 0 ? 1 : -1;
    }

    private float calcPointDistance(PointF p1, PointF p2) {
        return (float) Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    private float calcPointToLineDistance(PointF point, Line line) {
        float sDist = calcPointDistance(point, line.s);
        float eDist = calcPointDistance(point, line.e);
        float length = calcPointDistance(line.s, line.e);
        if (sDist < EXP || eDist < EXP) {
            return 0f;
        }

        // 点在直线上
        if (sDist * sDist >= length * length + eDist * eDist) {
            return eDist;
        }
        if (eDist * eDist >= length * length + sDist * sDist) {
            return sDist;
        }

        float p = (sDist + eDist + length) / 2;
        float s = (float) Math.sqrt(p * (p - sDist) * (p - eDist) * (p - length));  // 海伦公式求面积
        return 2 * s / length;  // 三角形面积公式求高
    }

    private boolean onSegment(PointF p, PointF q, PointF r) {
        if (q.x <= Math.max(p.x, r.x) && q.x >= Math.min(p.x, r.x) &&
                q.y <= Math.max(p.y, r.y) && q.y >= Math.min(p.y, r.y))
            return true;
        return false;
    }

    private int orientation(PointF p, PointF q, PointF r) {
        float val = (q.y - p.y) * (r.x - q.x) - (q.x - p.x) * (r.y - q.y);
        if (val == 0) return 0;
        return (val > 0) ? 1 : 2;
    }

    public boolean lineIntersect(Line line) {
        Line l1 = mArrowLine;
        Line l2 = line;
        int o1 = orientation(l1.s, l1.e, l2.s);
        int o2 = orientation(l1.s, l1.e, l2.e);
        int o3 = orientation(l2.s, l2.e, l1.s);
        int o4 = orientation(l2.s, l2.e, l1.e);

        if (o1 != o2 && o3 != o4) {
            return true;
        }

        if (o1 == 0 && onSegment(l1.s, l2.s, l1.e)) return true;
        if (o2 == 0 && onSegment(l1.s, l2.e, l1.e)) return true;
        if (o3 == 0 && onSegment(l2.s, l1.s, l2.e)) return true;
        if (o4 == 0 && onSegment(l2.s, l1.e, l2.e)) return true;

        return false;
    }

    public int calcPointAngle(PointF point) {
        PointF center = mCenter;
        if (center.x == point.x) {
            if (point.y > center.y) {
                return 90;
            } else {
                return 180;
            }
        }

        int angle = (int)Math.toDegrees(Math.atan((point.y - center.y) / (point.x - center.x)));
        float dx = point.x - center.x;
        float dy = point.y - center.y;
        if (dx > 0 && dy > 0) {
            return angle;
        } else if (dx > 0 && dy < 0) {
            return 360 + angle;
        } else if (dx < 0 && dy < 0) {
            return 180 + angle;
        } else {
            return 180 + angle;
        }
    }

    private float getVectorDot(PointF v1, PointF v2) {
        return v1.x * v2.x + v1.y * v2.y;
    }

    private float getVectorNorm(PointF v) {
        return (float)Math.sqrt(v.x * v.x + v.y * v.y);
    }

    private int calcSelectedSection(float angle) {
        angle = (angle - (90 - mSectionsAngle[0] / 2) + 360) % 360;
        float acc = 0;
        for (int i = 0; i < mSectionsNum; ++ i) {
            acc += mSectionsAngle[i];
            if (acc > angle) {
                return i;
            }
        }
        return mSectionsNum - 1;
    }
}
