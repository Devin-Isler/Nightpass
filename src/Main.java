/**
 * CMPE 250 Project 1 - Nightpass Survivor Card Game
 * 
 * This skeleton provides file I/O infrastructure. Implement your game logic
 * as you wish. There are some import that is suggested to use written below. 
 * You can use them freely and create as manys classes as you want. However, 
 * you cannot import any other java.util packages with data structures, you
 * need to implement them yourself. For other imports, ask through Moodle before 
 * using.
 * 
 * TESTING YOUR SOLUTION:
 * ======================
 * 
 * Use the Python test runner for automated testing:
 * 
 * python test_runner.py              # Test all cases
 * python test_runner.py --type type1 # Test only type1  
 * python test_runner.py --type type2 # Test only type2
 * python test_runner.py --verbose    # Show detailed diffs
 * python test_runner.py --benchmark  # Performance testing (no comparison)
 * 
 * Flags can be combined, e.g.:
 * python test_runner.py -bv              # benchmark + verbose
 * python test_runner.py -bv --type type1 # benchmark + verbose + type1
 * python test_runner.py -b --type type2  # benchmark + type2
 * 
 * MANUAL TESTING (For Individual Runs):
 * ======================================
 * 
 * 1. Compile: cd src/ && javac *.java
 * 2. Run: java Main ../testcase_inputs/test.txt ../output/test.txt
 * 3. Compare output with expected results
 * 
 * PROJECT STRUCTURE:
 * ==================
 * 
 * project_root/
 * ├── src/                     # Your Java files (Main.java, etc.)
 * ├── testcase_inputs/         # Input test files  
 * ├── testcase_outputs/        # Expected output files
 * ├── output/                  # Generated outputs (auto-created)
 * └── test_runner.py           # Automated test runner
 * 
 * REQUIREMENTS:
 * =============
 * - Java SDK 8+ (javac, java commands)
 * - Python 3.6+ (for test runner)
 * 
 * @author Devin İşler
 */

import java.io.*;

public class Main {
    // Initializing the deck and discard pile 
    static AttackTree deck = new AttackTree();
    static HealthTree discardPile = new HealthTree(true);
    static int survivorPoint = 0;
    static int strangerPoint = 0;

    public static void main(String[] args) {
        // Check command line arguments
        if (args.length != 2) {
            System.out.println("Usage: java Main <input_file> <output_file>");
            System.out.println("Example: java Main ../testcase_inputs/test.txt ../output/test.txt");
            return;
        }

        String inFile = args[0];
        String outFile = args[1];

        // Initialize file reader and writer - Used Buffered, since it is a much faster alternative for Scanner
        try (BufferedReader br = new BufferedReader(new FileReader(inFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outFile))) {

            String line = br.readLine();
            while (line != null) {
                if (line.trim().isEmpty()) 
                {
                    line = br.readLine();
                    continue;
                }
                String[] parts = line.trim().split("\\s+");
                String command = parts[0];
                String out = "";

                switch (command) {
                    case "draw_card": {
                        String name = parts[1];
                        int att = Integer.parseInt(parts[2]);
                        int hp = Integer.parseInt(parts[3]);
                        out = draw_card(name, att, hp);
                        break;
                    }
                    case "battle": {
                        int att = Integer.parseInt(parts[1]);
                        int hp = Integer.parseInt(parts[2]);
                        int heal = Integer.parseInt(parts[3]);
                        out = battle(att, hp, heal);
                        break;
                    }
                    case "find_winning": {
                        out = findWinning();
                        break;
                    }
                    case "deck_count": {
                        out = deckCount();
                        break;
                    }
                    case "discard_pile_count": {
                        out = discardPileCount();
                        break;
                    }
                    case "steal_card": {
                        int att = Integer.parseInt(parts[1]);
                        int hp = Integer.parseInt(parts[2]);
                        out = steal_card(att, hp);
                        break;
                    }
                    default: {
                        System.out.println("Invalid command: " + command);
                        return;
                    }
                }

                bw.write(out);
                bw.newLine();
                line = br.readLine();
            }

        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + inFile);
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("I/O error occurred.");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Error processing commands: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("end");
    }

    // Creating cards, and adding them to the deck
    public static String draw_card(String name, int att, int hp) {
        Card card = new Card(name, att, hp);
        deck.insert(card);
        return String.format("Added %s to the deck", name);
    }

    // Get the winner by points
    public static String findWinning() {
        String winner = "Stranger";
        if (survivorPoint >= strangerPoint) {
            winner = "Survivor";
        }
        return String.format("The %s, Score: %d", winner, Math.max(strangerPoint, survivorPoint));
    }

    // Return the size of the deck
    public static String deckCount() {
        return String.format("Number of cards in the deck: %d", deck.size());
    }

    // Return the size of the discard pile
    public static String discardPileCount() {
        return String.format("Number of cards in the discard pile: %d", discardPile.size());
    }

    // Battle and Heal phase
    public static String battle(int att, int hp, int heal) {
        Card suitable = null;
        int priority;

        // Checking for priorities to find a suitable card - if found, do not search others
        suitable = deck.firstPriority(att, hp); // Surive and kill
        priority = 1;
        if (suitable == null){
            suitable = deck.secondPriority(att, hp); // Survive and don't kill
            priority = 2;
            if (suitable == null){
                suitable = deck.thirdPriority(att, hp); // Kill and don't survive
                priority = 3;
                if(suitable == null){
                    suitable = deck.fourthPriority(att, hp); // Don't kill and don't survive
                    priority = 4;
                }
            }
        }

        if (suitable == null){
            priority = 0;
        }

        // Update points based on priority
        switch (priority) {
            // Stranger automatically kills you(+2)
            case 0:
                strangerPoint += 2;
                break;
            // Survivor kills(+2), Stranger just damages(+1)
            case 1:
                strangerPoint += 1;
                survivorPoint += 2;
                break;

            case 2:
            // Survivor just damages(+1), Stranger just damages(+1)
                strangerPoint += 1;
                survivorPoint += 1;
                break;
            // Survivor kills(+2), Stranger kills(+2)
            case 3:
                strangerPoint += 2;
                survivorPoint += 2;
                break;
            // Survivor just damages(+1), Stranger kills(+2)
            case 4:
                strangerPoint += 2;
                survivorPoint += 1;
                break;
        }

        String text = "is discarded";
        // If there is any suitable card, take damage
        if (suitable != null) {
            suitable.takeDamage(att);
            // If the card survives
            if (suitable.curHp > 0) {
                suitable.changeAtt();
                text = "returned to deck";
                deck.insert(suitable);
            } 
            // If the card gets killed
            else {
                discardPile.insert(suitable);
            }
        }
        
        // Healing phase
        int reviveCounter = 0;
        Card revive;
        // Continue until there are no more heal(if there are suitable cards)
        while (heal > 0) {
            if (discardPile.size() == 0) 
            {
                break;
            }
            // Find the cards that can be healed fully
            revive = discardPile.getMaxHpSmaller(heal);
            if (revive == null) {
                // If not, try to find the minHp possible, and partially revive it
                revive = discardPile.getMinHp();
                if (revive != null) {
                    discardPile.delete(revive);
                    revive.partialRevive(heal);
                    discardPile.insert(revive);
                    break;
                }
            }
            // If it is fully healable, heal it, and add it to the deck
            else {
                discardPile.delete(revive);
                heal -= revive.missingHp;
                revive.fullyRevive();
                reviveCounter++;
                deck.insert(revive);
            }
        }

        if (suitable == null) {
            return String.format("No card to play, %d cards revived", reviveCounter);
        }

        return String.format(
            "Found with priority %d, Survivor plays %s, the played card %s, %d cards revived",
            priority, suitable.name, text, reviveCounter
        );
    }

    // Stealing the suitable card
    public static String steal_card(int attackLimit, int healthLimit) {
        Card stolenCard = deck.stealCard(attackLimit, healthLimit);
        if (stolenCard == null) {
            return "No card to steal";
        }
        return String.format("The Stranger stole the card: %s", stolenCard.name);
    }
}
