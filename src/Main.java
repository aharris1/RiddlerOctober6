import javafx.util.Pair;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<String> playerNames = new ArrayList<>();
        playerNames.add("Jerry");
        playerNames.add("Tom");
        playerNames.add("Bill");
        playerNames.add("Jeb");
        playerNames.add("Jake");
        playerNames.add("Jeff");
        playerNames.add("Jim");
        playerNames.add("George");
        playerNames.add("Satan");
        playerNames.add("Buddha");
        System.out.println(playerNames);
        Collections.sort(playerNames);
        System.out.println(playerNames);
        int successesNaive = 0;
        int successesSmart = 0;
        int samples = 100000;
        for(long i = 0; i < samples; i ++){
            if(attemptNaive(playerNames, generateBoxes(playerNames), 5)){
                successesNaive++;
            }
        }
        for(long i = 0 ; i < samples; i++){
            if(attemptSmart(playerNames, generateBoxes(playerNames), 5)){
                successesSmart++;
            }
        }
        System.out.println("Naive Success Rate: " + (double)successesNaive / samples);
        System.out.println("Smart Success Rate: " + (double)successesSmart / samples);
    }

    public static List<Pair<Integer, String>> generateBoxes(List<String> playerNames){
        List<Pair<Integer, String>> boxes = new ArrayList<>(playerNames.size()); //Generates a list to store the
        // "boxes" represented as pairs of the box ID and the player name
        Collections.shuffle(playerNames); //Shuffles the player names so they can be randomly assigned to boxes
        for(int i = 0; i < playerNames.size(); i++){
            boxes.add(new Pair<>(i, playerNames.get(i))); //Generates a box with ID=i and one of the shuffled
            // player names, stores it in the list of boxes. The original order of the box ids in the list should be 0,
            // 1, 2, ...
        }
        return boxes;
    }

    //Simulates a naive attempt by the team, returns whether it was successful
    public static boolean attemptNaive(List<String> playerNames, List<Pair<Integer, String>> boxes, int opensAllowed){
        for(String player : playerNames){
            Collections.shuffle(boxes);
            boolean playerFoundName = false;
            for(int i = 0 ; i < opensAllowed; i++){
                if(boxes.get(i).getValue().equals(player)){
                    playerFoundName = true;
                }
            }
            if(!playerFoundName){
                return false;
            }
        }
        return true;
    }

    //Simulates a "smart" attempt by the team, returns whether it was successful
    public static boolean attemptSmart(List<String> playerNames, List<Pair<Integer, String>> boxes, int opensAllowed){
        /**
         * Description of strategy:
         * -Players enter the room in alphabetical order.
         * -The first player to enter opens the first "opensAllowed" boxes.
         * -They sort the boxes that they find in alphabetical order of the names contained inside
         * -The second player skips the first box and opens the next "opensAllowed" boxes
         * -They sort the boxes they find in alphabetical order of the names contained inside
         * -The same strategy repeats for all players on the team.
         * NOTE: In the case that any player fails, the simulation is terminated with a value of "false" to save CPU
         * cycles.
         */
        Collections.sort(playerNames); //Puts the player names in alphabetical order
        for(int playerNumber = 0 ; playerNumber < playerNames.size(); playerNumber++){
            //System.out.println("Player: " + playerNames.get(playerNumber));
            List<Pair<Integer, String>> workingSet = boxes.subList(playerNumber, Math.min(playerNumber + opensAllowed,
                    boxes.size()));
            //System.out.println("WS: " + workingSet);
            workingSet.sort(Comparator.comparing(Pair::getValue));
            boolean nameFoundInWorkingSet = false;
            for(int i = 0 ; i < workingSet.size(); i++){
                boxes.set(playerNumber + i, workingSet.get(i));
                if(workingSet.get(i).getValue().equals(playerNames.get(playerNumber))){
                    nameFoundInWorkingSet = true;
                }
            }
            //System.out.println(boxes);
            if(!nameFoundInWorkingSet){
                return false;
            }
        }
        //Only reaches this point if nameFoundInWorkingSet was true for all players, so returns true to represent all
        // players finding their name
        return true;
    }
}
