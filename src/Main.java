import javafx.util.Pair;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.*;

/**
 * Written by Andrew Harris (etotheitauequalsone@gmail.com) to solve FiveThirtyEight Classic Posted Oct. 6.
 */


public class Main {

    public static void main(String[] args) {
        testStrategy(4, 2, 1000000);
        //Note: Using a sufficient sample size to expect even 1 success in the naive case w/ 100 players & 50 opens
        // would take way to long to simulate.
        //System.out.println("Extra Credit: ");
        //testStrategy(100, 50, 100000000);
    }

    /**
     * Tests an arbitrary game show format
     * @param numPlayers the number of players on the team
     * @param numOpens the number of boxes each player may open
     */
    public static void testStrategy(int numPlayers, int numOpens, int samples){
        System.out.println("Players : " + numPlayers);
        System.out.println("Opens/Player: " + numOpens);
        List<String> playerNames = new ArrayList<>();
        while(playerNames.size() < numPlayers){
            String name = RandomStringUtils.randomAlphabetic(2);
            if(!playerNames.contains(name)){
                playerNames.add(name);
            }
        }
        Collections.sort(playerNames);
        //System.out.println(playerNames);
        int successesNaive = 0;
        int successesSmart = 0;
        for(long i = 0; i < samples; i++){
            if(i % (samples/10) == 0){
                System.out.println(i * 100.0 / samples);
            }
            if(attemptNaive(playerNames, generateBoxes(playerNames), numOpens)){
                successesNaive++;
            }
        }
        for(long i = 0 ; i < samples; i++){
            if(i % (samples/10) == 0){
                System.out.println(i * 100.0 / samples);
            }
            if(attemptSmart(playerNames, generateBoxes(playerNames), numOpens)){
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
