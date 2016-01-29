package com.nscc.jared.gamejam;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * Created by Jared on 1/29/2016.
 */
public class Character {
    private int horizontalMovement;
    private int verticalMovement;

    public void draw(Canvas c, Paint p, int height, int width)
    {
        p.setColor(Color.parseColor("#ffff00"));
        c.drawCircle(width/2, height/2, 20, p);
    }

    public void setDurection(int x, int y)
    {
        horizontalMovement = x;
        verticalMovement = y;
    }
}
