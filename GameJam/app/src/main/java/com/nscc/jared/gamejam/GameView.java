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
    protected int interactingWith = 0;

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
    private Bitmap pot = BitmapFactory.decodeResource(getResources(), R.drawable.pot);
    private Bitmap skelly = BitmapFactory.decodeResource(getResources(), R.drawable.skelly);
    private Bitmap floor = BitmapFactory.decodeResource(getResources(), R.drawable.floor);

    private BitmapFactory.Options options3 = new BitmapFactory.Options();
    private Bitmap rubble = BitmapFactory.decodeResource(getResources(), R.drawable.rubble, options3);

    // 1 - wall top
    // 2 - wall bottom
    // 3 - wall left
    // 4 - wall right

    private int room[][] = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2}};

    // 6 - puddle
    // 7 - jukebox
    // 8 - crate
    // 9 - pot
    // 10 - skelly
    private int objectsInRoom[][] = {{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
                            {3,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,9,0,0,0,0,0,0,8,0,0,0,0,6,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,8,8,8,8,8,8,8,8,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,7,0,0,4},
                            {3,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
                            {3,0,10,0,0,0,0,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,4},
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
            boolean movmentChangeThisIteration = false;
            if (canMoveRight)
            {
                canMoveRight = barriers.get(i).isTouchingRight(character.y, character.x, character.x + character.width, character.y + character.height);
                if (!canMoveRight)
                    movmentChangeThisIteration = true;
            }
            if (canMoveLeft)
            {
                canMoveLeft = barriers.get(i).isTouchingLeft(character.y, character.x, character.x + character.width, character.y + character.height);
                if (!canMoveLeft)
                    movmentChangeThisIteration = true;
            }
            if (canMoveUp)
            {
                canMoveUp = barriers.get(i).isTouchingUp(character.y, character.x, character.x + character.width, character.y + character.height);
                if (!canMoveUp)
                    movmentChangeThisIteration = true;
            }
            if (canMoveDown)
            {
                canMoveDown = barriers.get(i).isTouchingDown(character.y, character.x, character.x + character.width, character.y + character.height);
                if (!canMoveDown)
                    movmentChangeThisIteration = true;
            }

            if (movmentChangeThisIteration)
            {
                if (barriers.get(i).interactive)
                    this.interacting = true;
                    this.interactingWith = barriers.get(i).type;
            }
        }

        // messy way to prevent the character being fully stuck
        if (!canMoveLeft && !canMoveUp && !canMoveRight && !canMoveDown)
        {
            canMoveRight = true;
            canMoveDown = true;
            canMoveUp = true;
            canMoveLeft = true;
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
                    barriers.add(new Barrier(x, y, temp, temp, false, 0));
                }
                if (room[row][col] == 2)
                {
                    c.drawBitmap(wall_bottom, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false, 0));
                }
                if (room[row][col] == 3)
                {
                    c.drawBitmap(wall_left, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false, 0));
                }
                if (room[row][col] == 4)
                {
                    c.drawBitmap(wall_right, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false, 0));
                }

                if (room[row][col] == 0)
                    c.drawBitmap(floor, x, y, paint);
            }
        }

        int objWidth = options3.outWidth;
        int objHeight = options3.outHeight;
        for (int row=0;row < objectsInRoom.length;row++) {
            for (int col = 0; col < objectsInRoom[row].length; col++) {
                int x = (col*temp) + horizontalOffset;
                int y = (row*temp) + verticalOffset;

                // TODO - switch case
                if (objectsInRoom[row][col] == 6)
                {
                    c.drawBitmap(rubble, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, true, 1));
                    c.drawRect(x, y, x + temp, y + temp, paint);
                }
                if (objectsInRoom[row][col] == 7)
                {
                    c.drawBitmap(jukebox, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, true, 2));
                    c.drawRect(x, y, x + temp, y + temp, paint);
                }
                if (objectsInRoom[row][col] == 8)
                {
                    c.drawBitmap(crate, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, false, 0));
                }
                if (objectsInRoom[row][col] == 9)
                {
                    c.drawBitmap(pot, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, true, 3));
                }
                if (objectsInRoom[row][col] == 10)
                {
                    c.drawBitmap(skelly, x, y, paint);
                    barriers.add(new Barrier(x, y, temp, temp, true, 4));
                }

            }
        }

        if (interacting && !dialogOpen)
        {
            // show interact button
            paint.setColor(Color.parseColor("#0066ff"));
            //paint.setAlpha(99);
            //c.drawRect(this.width - 450, this.height - 250, this.width - 50, this.height - 50, paint);
            c.drawCircle(this.width - 150, this.height-150, 150, paint);

            paint.setColor(Color.parseColor("#ffffff"));
            paint.setTextSize(160);
            c.drawText("?", this.width - 175, this.height - 100, paint);
        }

        joystick.draw(c, paint, height);
        character.draw(c, paint, height, width, mContext, walking);

        if (dialogOpen)
        {
            paint.setColor(Color.parseColor("#000000"));
            paint.setAlpha(90);
            c.drawRect(100, 100, this.width - 100, this.height / 3, paint);
            paint.setColor(Color.parseColor("#ffffff"));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(10);
            c.drawRect(120, 120, this.width - 120, this.height / 3 - 20, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(72);

            if (this.interactingWith == 1)
                c.drawText("I think this is a puddle?", 200, 200, paint);
            if (this.interactingWith == 2)
                c.drawText("This is a jukebox. Play a song", 200, 200, paint);
            if (this.interactingWith == 3)
                c.drawText("Maybe I should plant a flower?", 200, 200, paint);
            if (this.interactingWith == 4)
                c.drawText("Poor human", 200, 200, paint);
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