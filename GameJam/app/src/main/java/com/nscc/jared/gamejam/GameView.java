package com.nscc.jared.gamejam;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;

import java.util.ArrayList;

public class GameView extends View {
    private Context mContext;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Animation animation;
    private Joystick joystick;
    private Character character;

    private int horizontalOffset = 0;
    private int verticalOffset = 0;

    private ArrayList<Barrier> barriers = new ArrayList<>();

    private int height;
    private int width;

    private boolean loading = true;

    private BitmapFactory.Options options = new BitmapFactory.Options();
    Bitmap[] walking = {BitmapFactory.decodeResource(getResources(), R.drawable.walk1, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk2, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk3, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk4, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk5, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk6, options)};

    private BitmapFactory.Options options2 = new BitmapFactory.Options();
    private Bitmap wall = BitmapFactory.decodeResource(getResources(), R.drawable.wall, options2);
    private Bitmap floor = BitmapFactory.decodeResource(getResources(), R.drawable.floor);

    private int room[][] = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
                            {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}};

    public GameView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        mContext = context;
        joystick = new Joystick();
        character = new Character();
    }

    protected void onDraw(Canvas c) {
        height = this.getHeight();
        width = this.getWidth();

        if (loading)
        {
            loading = false;
            barriers.clear();
        }

        // TODO - remove color parsing from onDraw method

        c.drawColor(Color.parseColor("#ffffff"));

        int temp = options2.outWidth;
        for (int row=0;row < room.length;row++)
        {
            for (int col=0;col < room[row].length;col++)
            {
                int x = (col*temp) + horizontalOffset;
                int y = (row*temp) + verticalOffset;
                if (room[row][col] == 1)
                {
                    c.drawBitmap(wall, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp));
                }
                if (room[row][col] == 0)
                    c.drawBitmap(floor, x, y, paint);
            }
        }

        joystick.draw(c, paint, height);
        character.draw(c, paint, height, width, mContext, walking);
    }

    public boolean onTouchEvent(MotionEvent event) {

        int eventAction = event.getAction();

        int x = (int)event.getX();
        int y = (int)event.getY();

        //if (eventAction == MotionEvent.ACTION_DOWN)
        //{
            if (joystick.touchOnJoystick(x, y, this.height))
            {
                character.moving = true;
                int xDiff = x - joystick.x;
                int yDiff = y - joystick.y;

                //character.setDirection(xDiff, yDiff);
                horizontalOffset += (xDiff/5)*-1;
                verticalOffset += (yDiff/5)*-1;
            }
       // }

        if (eventAction == MotionEvent.ACTION_UP)
        {
           character.moving = false;
        }
        return true;
    }



}