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
import android.widget.Toast;

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
    private boolean canMoveRight = true;
    private boolean canMoveLeft = true;
    private boolean canMoveUp = true;
    private boolean canMoveDown = true;
    private boolean dialogOpen = false;
    protected boolean interacting = false;

    private BitmapFactory.Options options = new BitmapFactory.Options();
    Bitmap[] walking = {BitmapFactory.decodeResource(getResources(), R.drawable.walk1, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk2, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk3, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk4, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk5, options),
            BitmapFactory.decodeResource(getResources(), R.drawable.walk6, options)};

    private BitmapFactory.Options options2 = new BitmapFactory.Options();
    private Bitmap wall_top = BitmapFactory.decodeResource(getResources(), R.drawable.wall_top, options2);
    private Bitmap wall_bottom = BitmapFactory.decodeResource(getResources(), R.drawable.wall_bottom, options2);
    private Bitmap wall_right = BitmapFactory.decodeResource(getResources(), R.drawable.wall_right, options2);
    private Bitmap wall_left = BitmapFactory.decodeResource(getResources(), R.drawable.wall_left, options2);
    private Bitmap crate = BitmapFactory.decodeResource(getResources(), R.drawable.crate);
    private Bitmap jukebox = BitmapFactory.decodeResource(getResources(), R.drawable.jukebox);
    private Bitmap floor = BitmapFactory.decodeResource(getResources(), R.drawable.floor);

    // 1 - wall top
    // 2 - wall bottom
    // 3 - wall left
    // 4 - wall right
    // 5 - crate
    // 6 - jukebox
    private int room[][] = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                            {3,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,0,6,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,6,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}};

    public GameView(Context context, AttributeSet attrs)  {
        super(context, attrs);
        mContext = context;
        joystick = new Joystick();
        character = new Character(this.getWidth(), this.getHeight(), options);
    }

    protected void onDraw(Canvas c) {
        height = this.getHeight();
        width = this.getWidth();

        if (loading)
        {
            loading = false;

        }

        // TODO - remove color parsing from onDraw method
        c.drawColor(Color.parseColor("#ffffff"));

        // determine any movment restrictions\

        canMoveUp = true;
        canMoveRight = true;
        canMoveDown = true;
        canMoveLeft = true;
        interacting = false;
        for (int i=0;i<barriers.size();i++)
        {
            if (canMoveRight)
                canMoveRight = barriers.get(i).isTouchingRight(character.y, character.x, character.x + character.width, character.y + character.height);
            if (canMoveLeft)
                canMoveLeft = barriers.get(i).isTouchingLeft(character.y, character.x, character.x + character.width, character.y + character.height);
            if (canMoveUp)
                canMoveUp = barriers.get(i).isTouchingUp(character.y, character.x, character.x + character.width, character.y + character.height);
            if (canMoveDown)
                canMoveDown = barriers.get(i).isTouchingDown(character.y, character.x, character.x + character.width, character.y + character.height);

            if (!canMoveLeft || !canMoveDown || !canMoveRight || !canMoveUp)
                if (barriers.get(i).interactive)
                    this.interacting = true;
        }



        barriers.clear();
        int temp = options2.outWidth;
        for (int row=0;row < room.length;row++)
        {
            for (int col=0;col < room[row].length;col++)
            {
                int x = (col*temp) + horizontalOffset;
                int y = (row*temp) + verticalOffset;

                // TODO - switch case
                if (room[row][col] == 1)
                {
                    c.drawBitmap(wall_top, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false));
                }
                if (room[row][col] == 2)
                {
                    c.drawBitmap(wall_bottom, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false));
                }
                if (room[row][col] == 3)
                {
                    c.drawBitmap(wall_left, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false));
                }
                if (room[row][col] == 4)
                {
                    c.drawBitmap(wall_right, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false));
                }
                if (room[row][col] == 5)
                {
                    c.drawBitmap(crate, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false));
                }
                if (room[row][col] == 6)
                {
                    c.drawBitmap(jukebox, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, true));
                }
                if (room[row][col] == 0)
                    c.drawBitmap(floor, x, y, paint);
            }
        }

        if (interacting && !dialogOpen)
        {
            // show interact button
            paint.setColor(Color.parseColor("#0066ff"));
            paint.setAlpha(99);
            c.drawRect(this.width - 450, this.height - 250, this.width - 50, this.height - 50, paint);
            paint.setColor(Color.parseColor("#ffffff"));
            paint.setTextSize(86);
            c.drawText("Interact", this.width - 400, this.height - 150, paint);

        }

        joystick.draw(c, paint, height);
        character.draw(c, paint, height, width, mContext, walking);

        if (dialogOpen)
        {
            paint.setColor(Color.parseColor("#000000"));
            paint.setAlpha(90);
            c.drawRect(100, 100, this.width-100, this.height/3, paint);
            paint.setColor(Color.parseColor("#ffffff"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            c.drawRect(120, 120, this.width - 120, this.height/3 - 20, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(72);
            c.drawText("This is a jukebox.", 200, 200, paint);
        }
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

                if (xDiff > 0)
                {
                    if (canMoveRight)
                        horizontalOffset += (xDiff/5)*-1;
                } else
                {
                    if (canMoveLeft)
                        horizontalOffset += (xDiff/5)*-1;
                }

                if (yDiff < 0)
                {
                    if (canMoveUp)
                        verticalOffset += (yDiff/5)*-1;
                } else
                {
                    if (canMoveDown)
                        verticalOffset += (yDiff/5)*-1;
                }

            }

        if (dialogOpen)
            dialogOpen = false;

            // interact button
            if (interacting)
            {
                if (x >= this.width - 450 && x <= this.width - 50 && y >= this.height-250 && y <= this.height-50)
                {
                    dialogOpen = true;
                }
            }


       // }

        if (eventAction == MotionEvent.ACTION_UP)
        {
           character.moving = false;
        }
        return true;
    }



}