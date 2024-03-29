import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class CardGame {
  public static int playerCheck(Scanner inputScanner) {
    // Method to take user input for no. of players and check validity of input
    int players;
    while (true) { // Used to force user to repeat indented code until valid input is given
      System.out.println("Please enter the number of players:");
      try {
        players = Integer.parseInt(inputScanner.nextLine());  // Read user input
        if (players < 1) { // Input must be greater than 1
          System.out.println("Invalid input. Please try again.");
          continue; // Repeat loop
        }
      } catch (NumberFormatException e) { // If input cannot be parsed to int, it is invalid
        System.out.println("Invalid input. Please try again.");
        continue; // Repeat loop
      }
      return players; // Break out of loop and return no. of players
    }
  }

  public static String fileTypeVerifier(Scanner inputScanner) {
    // Method to take user input for filepath of pack and check validity
    String fileExtension;
    while (true) { // Used to force user to repeat indented code until valid input is given
      System.out.println("Please enter location of pack to load:");
      String packFilePath = inputScanner.nextLine(); // Storing inputted file path as string
      try {
        fileExtension = packFilePath.substring(packFilePath.length() - 4); // Check filetype is valid
      } catch (StringIndexOutOfBoundsException e) { // If string given is too short to allow slicing, filetype must be invalid
        System.out.println("Incorrect file type. Please try again.");
        continue; // Repeat loop
      }
      if (!fileExtension.equals(".txt")) {
        System.out.println("Incorrect file type. Please try again.");
        continue; // Repeat loop
      }
      return packFilePath; // Break out of loop and return filepath of pack
    }
  }

  public static File packLoader(String packFilePath) {
    // Loads file
    File pack = new File(packFilePath); // If valid, attempt to load the file
    if (pack.exists() && !pack.isDirectory() && pack.isFile()) { // Pack must exist, not be a directory and be a file
      return pack;
    } else {
      System.out.println("File not found. Please try again.");
      return null; // Return null to main() method to force method to be called again
    }
  }

  public static Boolean packCounter(File pack, int players) {
    // Used to count the number of lines in the file
    Scanner counter;
    try {
      counter = new Scanner(pack);
    } catch (FileNotFoundException e) {
      System.out.println("File not found. Please try again.");
      return false; // returning false to main(), calls method to request user input again
    }
    int lines = 0;
    while (counter.hasNextLine()) { // counting lines...
      lines++;
      counter.nextLine();
    }
    if (lines != 8 * players) { // pack must have 8n lines, where n is no. of players
      System.out.println("Incorrect number of lines in file. Please try again.");
      counter.close();
      return false; // returning false to main(), calls method to request user input again
    } else {
      counter.close();
      return true; // allows user to advance to next step
    }
  }

  public static Boolean packReader(File pack) {
    // Reads contents of pack
    Scanner reader;
    try {
      reader = new Scanner(pack);
    } catch (FileNotFoundException e) {
      System.out.println("File not found. Please try again.");
      return false; // returning false to main(), calls method to request user input again
    }
    while (reader.hasNextLine()) { // repeat for all lines
      try {
        if (0 > Integer.parseInt(reader.nextLine())) { // rejects any inputs that aren't a positive integer
          System.out.println("All inputs must be positive integers. Please try again.");
          reader.close();
          return false; // returning false to main(), calls method to request user input again
        }
      } catch (NumberFormatException e) { // if a line cannot be parsed to int, it is invalid
        System.out.println("Invalid input detected. Please try again.");
        reader.close();
        return false; // returning false to main(), calls method to request user input again
      }
    }
    reader.close();
    return true; // if this stage is reached, the pack has no problems and hence is valid
  }

  public static void gameBuilder(File pack, Player[] playersArray, Deck[] decksArray, int players) {
    // Allocates cards to players and decks, creates files for players and decks
    Scanner packScanner;
    try {
      packScanner = new Scanner(pack);
      for (int i = 0; i < 4; i++) { // round-robin card dealing for players from first 4n cards in pack
        for (int j = 0; j < players; j++) {
          playersArray[j].assignCard(new Card(Integer.parseInt(packScanner.nextLine())), i);
        }
      }
      for (int i = 0; i < 4; i++) { // round-robin card dealing for decks from last 4n cards in pack
        for (int j = 0; j < players; j++) {
          decksArray[j].assignCard(new Card(Integer.parseInt(packScanner.nextLine())), i);
        }
      }
      for (Player p : playersArray) { // creates files in current directory for all players
        p.createFile();
        p.writeFileInitial(); // writes initial hand to all player files
      }
      for (Deck d : decksArray) { // creates files in current directory for all decks
        d.createFile();
      }
    } catch (FileNotFoundException e) {} // blank catch block because file has already been validated
  }

  public static void main(String[] args) {
    // Main method to be executed, game simulation is run from here
    // inputScanner is passed around to circumvent problems when opening and closing multiple scanners for System.in
    Scanner inputScanner = new Scanner(System.in);
    int players = playerCheck(inputScanner); // get player number from user
    String packFilePath = fileTypeVerifier(inputScanner); // get filepath for pack from user
    File pack = packLoader(packFilePath);
    while (pack == null) { // repeat the following methods until a valid pack is given by user
      packFilePath = fileTypeVerifier(inputScanner);
      pack = packLoader(packFilePath);
    }
    while (!packCounter(pack, players) || !packReader(pack)) { // repeat methods until a valid pack is given
      packFilePath = fileTypeVerifier(inputScanner);
      pack = packLoader(packFilePath);
    }
    System.out.println("Thank you! Proceed to program."); // final user input has been collected; proceed to simulation
    Player[] playersArray = new Player[players]; // create arrays to hold players and decks
    Deck[] decksArray = new Deck[players];
    for (int i = 0; i < players; i++) {
      // Design choice - NOT normalising player indices and player numbers (disparity of 1)
      decksArray[i] = new Deck(i+1);
    }
    for (int i = 0; i < players; i++) { // populating playersArray with player objects
      try {
        playersArray[i] = new Player(i + 1, decksArray[i], decksArray[i + 1]);
      } catch (ArrayIndexOutOfBoundsException e) { // allows connection from player n and deck 1
        playersArray[i] = new Player(i + 1, decksArray[i], decksArray[0]);
      }
    }
    gameBuilder(pack, playersArray, decksArray, players); // populates players' hands and decks, creates files
    for (Player p : playersArray) { // start all player threads
      p.start();
    }
  }
}