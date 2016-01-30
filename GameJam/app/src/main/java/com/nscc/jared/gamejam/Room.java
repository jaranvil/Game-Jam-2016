package com.nscc.jared.gamejam;

import java.util.Random;

/**
 * Created by Jared on 1/30/2016.
 */
public class Room {
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
    private int[] optionalItems = {6,7,8,9,10};
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

    protected int[][] layer1;
    protected int[][] layer2;

    public void generationRoom()
    {
        Random random = new Random();
        int roomWidth = 10 + random.nextInt(30);
        int roomHeight = 10 + random.nextInt(30);

        createLayer1(roomWidth, roomHeight);
        createLayer2(roomWidth, roomHeight);
    }

    // background tiles
    public void createLayer1(int roomWidth, int roomHeight)
    {
        layer1 = new int[roomWidth][roomHeight];

        for (int row = 0;row < layer1.length;row++)
            for (int col = 0;col < layer1[row].length;col++)
            {
                if (row == 0)
                    layer1[row][col] = 1;
                if (col == 0)
                    layer1[row][col] = 3;
                if (row == roomWidth -1)
                    layer1[row][col] = 2;
                if (col == roomHeight -1)
                    layer1[row][col] = 4;
            }
    }

    public void createLayer2(int roomWidth, int roomHeight)
    {
        layer2 = new int[roomWidth][roomHeight];
        Random random = new Random();
        int numberOfItems = 2 + random.nextInt(7);

        int counter = 0;
        boolean stop = false;
        for (int row = 0;row < layer2.length;row++)
            for (int col = 0;col < layer2[row].length;col++)
            {
                if (!stop)
                {
                    // add item
                    int colPos = 0 + random.nextInt(roomHeight);
                    int rowPos = 0 + random.nextInt(roomWidth);

                    layer2[rowPos][colPos] = optionalItems[random.nextInt(optionalItems.length)];

                    counter++;

                    if (counter == numberOfItems)
                        stop = true;
                }

            }
    }


}
