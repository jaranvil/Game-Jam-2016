package com.nscc.jared.gamejam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * Created by Jared on 1/29/2016.
 */
public class Barrier {
    protected int left;
    protected int top;
    protected int bottom;
    protected int right;

    public Barrier(int x, int y, int width, int height)
    {
        this.right = x + width;
        this.bottom = y + height;
        this.left = x;
        this.top = y;
    }

    public String isTouchingHorizontal(int objX, int objY, int objWidth, int objHeight)
    {
        int rightSide = objX + objWidth;
        int leftSide = objX;
        int topSide = objY;
        int bottomSide = objY + objHeight;

        // check right
        if (rightSide > left && rightSide < (right))
            if (topSide < bottom || bottomSide > top)
                return "right";

        return "";
    }

}
