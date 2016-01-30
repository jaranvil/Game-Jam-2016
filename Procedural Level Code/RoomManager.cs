using UnityEngine;
using System.Collections;
using System.Collections.Generic;
using UnityEngine.SceneManagement;
using Random = UnityEngine.Random;

public class RoomManager : MonoBehaviour {

    // throughout the code, integers correspond to compass directions
    // 0 N
    // 1 E
    // 2 S
    // 3 W
    
    // the code creates a public static reference to itself so other classes can call it's methods
    public static RoomManager instance;

    // max width and height of the game world
    int width = 8;
    int height = 4;

    // max size of rooms
    int roomWidthMin = 8;
    int roomWidthMax = 18;
    int roomHeightMin = 8;
    int roomHeightMax = 18;

    // size of the individual tiles - 
    float tileOffset = 0.32f;

    // reference to player
    GameObject player;

    // direction keys correspond to vector directions
    Dictionary<int, Vector2> directions = new Dictionary<int, Vector2>()
    {
        {0, new Vector2(0, -1) },
        {1, new Vector2(1, 0) },
        {2, new Vector2(0, 1) },
        {3, new Vector2(-1, 0) }
    };

    // legend of tile ids -> game objects they represent
    public GameObject floor;
    public GameObject wall;
    public GameObject door;

    public Dictionary<int, GameObject> tileset;
    
    // the room class
    public class Room
    {
        // exists is kind of a lazy hacky way of turning off rooms that don't end up being connected to the level
        public bool exists = true;

        // the width and height of the room in tiles
        public int width = 10;
        public int height = 10;

        // each room also stores its own position in the level layout
        public int xPos;
        public int yPos;

        // whether or not a connection/door exists at each cardinal direction
        public Dictionary<int, bool> connections;

        // the position the player will spawn at when it enters the room from a certain door
        public Dictionary<int, Vector3> doorPositions;

        // an array of tile information for drawing the room
        public int[,] plan;

        // constructor
        public Room(int x, int y, int w, int h)
        {
            xPos = x;
            yPos = y;
            width = w;
            height = h;

            connections = new Dictionary<int, bool>();
            doorPositions = new Dictionary<int, Vector3>();
        }
    }

    // layout is an array of all possible rooms
    Room[,] layout;

    // the set of rooms that make up the current level
    List<Room> level;

    // the current room being drawn
    Room currentRoom;

    // a holder object for all tiles
    Transform levelHolder;

    // the position of the player
    Vector3 playerPos;

	void Awake () {

        // singleton code
        // ensures that only one instance of the RoomManager will ever exist in the Unity scene
	    if(instance == null)
        {
            instance = this;
        }
        else if(instance != this)
        {
            Destroy(gameObject);
        }

        // get reference to player
        player = GameObject.FindGameObjectWithTag("Player");

        // this object persists on reload
        DontDestroyOnLoad(gameObject);

        // populate tileset dictionary
        tileset = new Dictionary<int, GameObject>()
        {
            {0, floor },
            {1, wall },
            {2, door }
        };

        // build a new level layout
        ResetLevel();
        BuildLayout();

        // pick a random room for the player to start in
        currentRoom = level[(int)Random.Range(0, level.Count - 1)];

        // use the centre of the room as the player start position
        playerPos = new Vector3(currentRoom.width * tileOffset / 2, currentRoom.height * tileOffset / 2, 0);
        player.transform.position = playerPos;

        // BuildRoom creates the tile layout for each room in the level
        foreach(var room in level)
        {
            BuildRoom(room);
        }

        // finally we draw the current room on the screen
        DrawRoom(currentRoom);
	}

    void ResetLevel()
    {
        // reset the level and layout data...

        level = new List<Room>();
        layout = new Room[width, height];

        // ...and generate a new set of rooms with random sizes

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                layout[x, y] = new Room(x, y, (int)Random.Range(roomWidthMin, roomWidthMax), (int)Random.Range(roomHeightMin, roomHeightMax));
            }
        }
    }

    void BuildLayout()
    {
        // create a list of all possible rooms
        // this is to keep track of which rooms remain to be connected

        List<Room> toConnect;
        toConnect = new List<Room>();

        foreach(var room in layout)
        {
            toConnect.Add(room);
        }

        // pick rooms at random until there are none left
        // for each room picked, add a new passage

        while(toConnect.Count != 0)
        {
            // we pick a room at random from the list

            Room current = toConnect[Random.Range(0, toConnect.Count - 1)];

            int dir = Random.Range(0, 3);
            if (!current.connections.ContainsKey(dir))
            {
                if(!AddConnection(current.xPos, current.yPos, dir))
                {
                    // this code is really lazy because I couldn't come up with a clever solution
                    // if the connection we attempt to make goes off the map, we just try every direction until one works
                    for(int i = 0; i < 4; i++)
                    {
                        if(AddConnection(current.xPos, current.yPos, i))
                        {
                            break;
                        }
                    }
                }
            }
            // after adding the connection, remove that room from the list of remaining rooms
            toConnect.Remove(current);
        }


        // up to this point, the code produces a flawed level, with some areas unreachable
        // I decided to embrace this flaw and use it as an opportunity to create more unique level layouts
        // the following code identifies all the isolated "domains" of rooms
        // only the largest domain is used as the level

        // "domains" is the list of all domains, each of which is a list of rooms

        List<List<Room>> domains = new List<List<Room>>();

        // put all rooms into a list again to keep track of which rooms have already been visited
        List<Room> remaining;
        remaining = new List<Room>();

        foreach (var room in layout)
        {
            remaining.Add(room);
        }

        while(remaining.Count != 0)
        {
            // we pick a room at random from the remaining rooms
            Room start = remaining[Random.Range(0, remaining.Count - 1)];

            // we do a depth first search on that room, adding every room we find to a new domain
            List<Room> newDomain = DepthFirstSearch(start, new List<Room>());

            // we remove each room in this domain from the list of remaining rooms
            foreach(var room in newDomain)
            {
                remaining.Remove(room);
            }

            // and add this domain to the list of domains
            domains.Add(newDomain);
        }

        // we sort the list of domains from largest to smallest
        domains.Sort((x, y) => y.Count - x.Count);

        // the largest domain is the level!
        level = domains[0];

        // the remaining domains are NULLIFIED
        for(int i = 1; i < domains.Count; i++)
        {
            for(int j = 0; j < domains[i].Count; j++)
            {
                domains[i][j].exists = false;
                domains[i][j].connections.Clear();
            }
        }
    }

    List<Room> DepthFirstSearch(Room start, List<Room> searched)
    {
        // this method will return a list of all rooms reachable from a starting room (including the starting room itself)
        // it works by recursively calling itself until no rooms remain to be searched
        // first we add the starting room to the list
        searched.Add(start);
        
        // for each cardinal direction we check for a connection
        for(int i = 0; i < 4; i++)
        {
            // if there is a connection...
            if (start.connections.ContainsKey(i) && start.connections[i])
            {
                // ...and if it leads to a room that hasn't been searched
                if(!searched.Contains(GetAdjacentRoom(start, i)))
                {
                    // the function calls itself on that room, and passes its searched list up the chain
                    searched = DepthFirstSearch(GetAdjacentRoom(start, i), searched);
                }
            }
        }

        // once the function has checked all possible directions it passes the updated list of searched rooms down the chain
        return searched;
    }

    Room GetAdjacentRoom(Room start, int dir)
    {
        // this helper function takes a starting room and a compass direction and returns the adjacent room in that direction
        Vector2 currentPos = new Vector2(start.xPos, start.yPos);
        Vector2 destination = currentPos + directions[dir];

        // it is smart enough to not return a non-existant room off the edge of the world
        if (destination.x >= width || destination.x < 0 || destination.y >= height || destination.y < 0)
        {
            return null;
        }

        return layout[(int)destination.x, (int)destination.y];
    }

    bool AddConnection(int x, int y, int dir)
    {
        // this function takes an x and y position and a compass direction and creates a new connection/door
        Vector2 currentPos = new Vector2(x, y);
        Vector2 destination = currentPos + directions[dir];
        // it is smart enough to not make a connection to a non-existant room
        if(destination.x >= width || destination.x < 0 || destination.y >= height || destination.y < 0)
        {
            return false;
        }
        else
        {
            // as well as adding a door to the starting room...
            layout[x, y].connections[dir] = true;
            // we also add a corresponding door to the destination room facing the opposite direction
            layout[(int)destination.x, (int)destination.y].connections[Opposite(dir)] = true;
            return true;
        }
    }

    int Opposite(int dir)
    {
        // this helper function takes a compass direction and returns the opposite direction
        if(dir + 2 < 4)
        {
            return dir + 2;
        }
        else
        {
            return dir - 2;
        }
    }

    void BuildRoom(Room room)
    {
        room.plan = new int[room.width, room.height];

        // first build an empty room with a border of walls
        for(int x = 0; x < room.width; x++)
        {
            for(int y = 0; y < room.height; y++)
            {
                if(x == 0 || x == room.width - 1 || y == 0 || y == room.height - 1)
                {
                    room.plan[x, y] = 1;
                }
                else
                {
                    room.plan[x, y] = 0;
                }
            }
        }

        // next add a door on each wall that requires one
        for(int i = 0; i < 4; i++)
        {
            if(room.connections.ContainsKey(i) && room.connections[i])
            {
                Vector2 newDoor = Vector2.zero;
                // there's probably a better way of doing this
                if (i == 0)
                {
                    newDoor = new Vector2((int)Random.Range(1, room.width - 2), 0);
                }
                if(i == 1)
                {
                    newDoor = new Vector2(room.width - 1, (int)Random.Range(1, room.height - 2));
                }
                if (i == 2)
                {
                    newDoor = new Vector2((int)Random.Range(1, room.width - 2), room.height - 1);
                }
                if (i == 3)
                {
                    newDoor = new Vector2(0, (int)Random.Range(1, room.height - 2));
                }

                // we add the door to the tile data...
                room.plan[(int)newDoor.x, (int)newDoor.y] = 2;
                // ...and record the position just in front of the door for the player to spawn when they enter the room
                room.doorPositions[i] = (newDoor + directions[Opposite(i)]) * tileOffset;
            }
        }
    }

    void DrawRoom(Room room)
    {
        // reset the object that holds all the map tiles
        Destroy(GameObject.Find("levelParent"));
        levelHolder = new GameObject("levelParent").transform;

        Vector3 tilePosition = Vector3.zero;

        // this code simply reads the array of tile information and instantiates the object corresponding to each tile
        for(int x = 0; x < room.width; x++)
        {
            for(int y = 0; y < room.height; y++)
            {
                tilePosition.Set(x * tileOffset, y * tileOffset, 0);

                GameObject newTile = Instantiate(tileset[room.plan[x,y]], tilePosition, Quaternion.identity) as GameObject;

                // this is really messy and clunky, if the object is a door it assigns a direction to the door based on it's position on the grid
                if (newTile.GetComponent<Door>() != null)
                {
                    if(x == 0)
                    {
                        newTile.GetComponent<Door>().dir = 3;
                    }
                    if (x == room.width -1)
                    {
                        newTile.GetComponent<Door>().dir = 1;
                    }
                    if (y == 0)
                    {
                        newTile.GetComponent<Door>().dir = 0;
                    }
                    if (y == room.height - 1)
                    {
                        newTile.GetComponent<Door>().dir = 2;
                    }
                }

                // parent all tiles to a holder object
                newTile.transform.SetParent(levelHolder);
            }
        }
    }

    public void TravelTo(int dir)
    {
        // this function is called when the player enters a door
        // we update the current room...
        currentRoom = GetAdjacentRoom(currentRoom, dir);

        // ...move the player to the appropriate position in the new room (and move the camera too)...
        playerPos = (Vector2)currentRoom.doorPositions[Opposite(dir)];
        player.transform.position = playerPos;
        Camera.main.transform.position = new Vector3(playerPos.x, playerPos.y, -10);

        // ...and draw the new room
        DrawRoom(currentRoom);
    }

    void TextDisplay()
    {
        // this is the code I wrote to display the level as an ASCII drawing
        // it doesn't work anymore and I don't know why
        string result = "";

        for (int y = 0; y < height * 2 + 1; y++)
        {
            string row = "";

            // if the row is a border
            if (y == 0 || y == height * 2 + 1)
            {
                row = FillRow(width * 2 + 1);
            }
            // if the row is at room level
            else if (y % 2 == 1)
            {
                for (int x = 0; x < width * 2 + 1; x++)
                {
                    // if the position is at the borders
                    if (x == 0 || x == width * 2 + 1)
                    {
                        row += "#";
                    }
                    // if the position is a room
                    else if (x % 2 == 1)
                    {
                        if (layout[(int)Mathf.Floor(x / 2), (int)Mathf.Floor(y / 2)].exists)
                        {
                            row += "O";
                        }
                        else
                        {
                            row += "#";
                        }
                    }
                    // if the position is a corridor/connection
                    else
                    {
                        if (layout[(int)Mathf.Floor((x - 1) / 2), (int)Mathf.Floor(y / 2)].connections.ContainsKey(1))
                        {
                            row += "C";
                        }
                        else
                        {
                            row += "#";
                        }
                    }
                }
            }
            // if the position is at connection level
            else
            {
                for (int x = 0; x < width * 2 + 1; x++)
                {
                    // if the position is at the borders
                    if (x == 0 || x == width * 2 + 1)
                    {
                        row += "#";
                    }
                    // if the position is at room level
                    else if (x % 2 == 1)
                    {
                        if (layout[(int)Mathf.Floor(x / 2), (int)Mathf.Floor((y - 1) / 2)].connections.ContainsKey(0))
                        {
                            row += "C";
                        }
                        else
                        {
                           row += "#";
                        }
                    }
                    // if the position is a corridor/connection
                    else
                    {
                        row += "#";
                    }
                }
            }

            result += row;
            result += "\n";
        }

        Debug.Log(result);
    }

    string FillRow(int amount)
    {
        // this is a helper function used by the ASCII display thing
        string result = "";
        for (int i = 0; i < amount; i++)
        {
            result += "#";
        }
        return result;
    }
}
