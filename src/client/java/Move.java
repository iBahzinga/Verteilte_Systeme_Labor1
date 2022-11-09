import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for the moves in the game
 */
public class Move
{
    String playerID;
    int x;
    int y;
    /**
     * to String method to get a normal String
     * @return
     */
    @Override
    public String toString() {
        return playerID + ": " + x + "," + y;
    }

    /**
     * First constructor
     * A String can be resolved and get coordinates from a string
     * @param from
     */
    public Move(String from)
    {
        String[] split = from.split(":");
        if(split.length > 1)
        {
            playerID = split[0];
            SetCoords(split[1]);
        }
        else
        {
            playerID = null;
            SetCoords(split[0]);
        }
        System.out.println(from);
    }

    /**
     * Second cnstructor
     * set the coordinates and the player id
     * @param playerID ID of the player
     * @param x x coordinate
     * @param y y coordinate
     */
    public Move(String playerID, int x, int y)
    {
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }

    /**
     * String operation to get the x and y coordinates
     * @param cords String that contains the coordinates
     */
    private void SetCoords(String cords)
    {
        int[] coords = Arrays.stream(
                        cords
                                .replace(" ", "")
                                .split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        x = coords[0];
        y = coords[1];
    }
}