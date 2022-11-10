package TicTacToeRemote;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Move {
    String playerID;
    int x,y;

    @Override
    public String toString() {
        return playerID + ": " + x + "," + y;
    }

    private void SetCoords(String cords){
        int[] coords = Arrays.stream(
                        cords
                                .replace(" ", "")
                                .split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
        x = coords[0];
        y = coords[1];
    }
    public Move(String from){
        String[] split = from.split(":");
        if(split.length > 1) {
            playerID = split[0];
            SetCoords(split[1]);
        }
        else {
            playerID = null;
            SetCoords(split[0]);
        }
        System.out.println(from);

    }

    public Move(String playerID, int x, int y){
        this.playerID = playerID;
        this.x = x;
        this.y = y;
    }
}
