package com.nscc.jared.gamejam;
import java.util.Random;

/**
 * Created by Devon on 1/30/2016.
 */
public class RoomGeneration
{
    //properties of room corners
    int cornerAx = 0;
    int cornerAy = 0;
    int cornerBx;
    int cornerBy = 0;
    int cornerCx = 0;
    int cornerCy;
    int cornerDx = cornerBx;
    int cornerDy = cornerCy;
    private int[][] room;

    Random rn = new Random();

    public int[][] getRoom()
    {
        return room;
    }

    public void createRoom()
    {
        cornerBx = rn.nextInt(11)+4;
        cornerCy = rn.nextInt(11)+4;

        room = new int[cornerBx][cornerCy];
        //create room array
        for(int c=0; c<cornerBx; c++)
        {
            for(int r=0; r<cornerCy-1; r++)
            {
                if(r==0 && c == 0)
                {
                    room[c][r] = 10;//top-left corner
                } else if( r==0 && c==cornerBx) {
                    room[c][r] = 11;//top-right corner
                } else if(r==cornerCy && c==0){
                    room[c][r] = 12;//bottom-left corner
                } else if(r==cornerCy && c==cornerBx) {
                    room[c][r] = 13;//bottom-right corner
                } else if(r==0 && c!=cornerBx && c!=0){
                    room[c][r] = 20;// top wall
                } else if(c==0 && r!=0 && r!=cornerCy){
                    room[c][r] = 21; //left wall
                } else if(c==cornerBx && r!=0 && r!=cornerCy){
                    room[c][r] = 22; //right wall
                } else if(r==cornerCy && c!=0 && c!=cornerBx){
                    room[c][r] = 23; //bottom wall
                } else {
                    room[c][r] = 0; //floors
                }
            }//end for
        }

        //create a door?
        if(cornerCy%2 !=0)
        {
            int doorNum = (cornerCy+1)/2;
            room[0][doorNum] = 30;//door
            room[cornerBx][doorNum] = 30;//door
        } else if(cornerCy%2 == 0){
            int doorNum = (cornerCy)/2;
            room[0][doorNum] = 30;//door
            room[cornerBx][doorNum] = 30;//door
        }

    }//end createRoom
}//end RoomGeneration