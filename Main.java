//Language: Java
//Programmers: Matthew Engrav,
//03 October 2023
//CMP SCI 4500

//This program plays a game with two "Person" entities, placing them at the southwest and northwest corners
//of a square grid and seeing how many 1-unit moves it takes for them to meet (if they ever do meet).
//Two protocols are defined: Protocol 4 and Protocol 8.
//Protocol 4 moves the Persons one unit north, south, east, or west. If the Person's move would move them out of bounds of the grid,
//it stays in place. Persons alternate turns.
//Protocol 8 moves the Persons one unit north, south, east, or west. It can also move them diagonally, northeast, northwest,
//southeast, or southwest. If the Person's move would move them out of bounds of the grid, another move is generated until
//the Person can make a valid move. Persons alternate turns.

//This program plays this game in three experiments using parameters supplied by indata.txt.
//The input file must be formatted exactly as described in the homework specification. If it has white space, non-
//digit or non-comma characters, too many or too few lines, lines with numbers out of ascending order, lines with an incorrent number of parameters,
//or a parameter greater than the limits defined, it will log these errors in the console and terminate the program.

//The program runs each experiment according to those parameters. It will take all the results, calculate the high, low,
// and average values of each experiment, then it will log those results in an output file.

//Java regex matching logic was looked up on GeeksforGeeks:
// https://www.geeksforgeeks.org/how-to-check-if-string-contains-only-digits-in-java/

//Data structures used:
//Objects, arrays, Lists, Scanners, BufferedReaders, InputStreams, Writers.

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

//This class holds each person's coordinates as they play the game.
class Person {
    int xCoordinate;
    int yCoordinate;

    public Person(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }
}


public class Main {

    // Function to move the person, protocol 4 or 8.  If it's protocol 4 it picks north, east, south, or west
    // and tries to make a move.  If it fails it goes to the next turn.  Protocol 8 gets compound directions,
    // so the random number is from 0-7 instead of 0-3 like protocol 4.  It also checks at the end if it's
    // protocol 8 and hasn't made a move, it will call the move function again until it makes a move.
    private static void move(Person person, int protocol, int Dimension) {
        Person temp = new Person(person.xCoordinate, person.yCoordinate);
        Random rand = new Random();
        int direction = 0;
        if (protocol == 4)
            direction = rand.nextInt(4);
        if (protocol == 8)
            direction = rand.nextInt(8);

        switch (direction) {
            case 0: // north
                if (person.yCoordinate < Dimension)
                    person.yCoordinate++;
                break;
            case 1: // east
                if (person.xCoordinate < Dimension)
                    person.xCoordinate++;
                break;
            case 2: // south
                if (person.yCoordinate > 0)
                    person.yCoordinate--;
                break;
            case 3: // west
                if (person.xCoordinate > 0)
                    person.xCoordinate--;
                break;
            case 4: // northeast
                if (person.yCoordinate < Dimension && person.xCoordinate < Dimension) {
                    person.yCoordinate++;
                    person.xCoordinate++;
                }
                break;
            case 5: // northwest
                if (person.yCoordinate < Dimension && person.xCoordinate > 0) {
                    person.yCoordinate++;
                    person.xCoordinate--;
                }
                break;
            case 6: // southeast
                if (person.yCoordinate > 0 && person.xCoordinate < Dimension) {
                    person.yCoordinate--;
                    person.xCoordinate++;
                }
                break;
            case 7: // southwest
                if (person.yCoordinate > 0 && person.xCoordinate > 0) {
                    person.yCoordinate--;
                    person.xCoordinate--;
                }
                break;
        }
        if (protocol == 8 && person.xCoordinate == temp.xCoordinate && person.yCoordinate == temp.yCoordinate) {
            move(person, protocol, Dimension);
        }
    }

    // This function plays the game.
    // This function actually executes the moves and gives the output.
    private static int playGame(Person person1, Person person2, int Dimension, int maxMoves, int protocol) {

        int counter = 0;

        while (counter < maxMoves) {
            if (counter % 2 == 0) {
                move(person1, protocol, Dimension);
                // It uses an if statement and take a modulus 2 of the move counter to rotate
                // between each of the players so they alternate turns.
            } else {
                move(person2, protocol, Dimension);
            }
            if (person1.xCoordinate == person2.xCoordinate && person1.yCoordinate == person2.yCoordinate)
                // Then after the player has moved it checks if there has been a meeting, if
                // there has, it breaks out of the loop.

                break;
            counter++; // If there isn't a meeting then it adds to the counter and repeats the loop.
        }

        // This section checks if the meeting occurred or not and prints the appropriate
        // message.

        if (counter < maxMoves)
            return ++counter;
        else
            return counter;
    }

    // This function runs the experiment many times depending on the repetitions and logs the outcome into a data list.
    private static List<Integer> experiment(int repetitions, int Dimension, int maxMoves, int protocol) {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < repetitions; i++) {
            Person person1 = new Person(0, 0);
            Person person2 = new Person(Dimension, Dimension);
            int result = playGame(person1, person2, Dimension, maxMoves, protocol);
            data.add(result);
        }

        return data;
    }

    // This function takes the results of the experiments run in main() and
    // writes them to outdata.txt.
    private static void printTable(PrintWriter out, ArrayList<Integer> xCoordinates, ArrayList<Double> yCoordinates, int M, int R, int P, char independentVar, char dependentVar) {
        String[] headers = new String[] {
                String.valueOf(independentVar),
                "Protocol",
                "Max Moves",
                "Repeats",
                String.valueOf(dependentVar)
        };

        out.println(String.format("%-12s| %-10s| %-10s| %-10s| %-10s", (Object[]) headers));
        out.println("-------------------------------------------------------------");

        for (int i = 0; i < xCoordinates.size(); i++) {
            int x = xCoordinates.get(i);
            double y = yCoordinates.get(i);

            if (independentVar == 'D') {
                out.printf("%-12d| %-10d| %-10d| %-10d| %-10f\n", x, P, M, R, y);
            } else {
                out.printf("%-12f| %-10d| %-10d| %-10d| %-10d\n", y, P, M, R, x);
            }
        }
    }

    private static void printBarGraph(PrintWriter out, ArrayList<Integer> xCoordinates, ArrayList<Double> yCoordinates) {
        out.println("\nTypewriter Graph");
        for (int i = 0; i < xCoordinates.size(); i++) {
            out.print(xCoordinates.get(i) + " | ");
            double length = yCoordinates.get(i);
            for (int j = 0; j < (int) length; j++) out.print("*");
            out.println();
        }
    }

    public static void main(String[] args) throws IOException {
        System.out
                .println("The program takes an input file which describes the parameters of 3 different experiments.\n"
                        +
                        "The program takes the input file and parses each of the parameters for the experiments, then it runs\n"
                        +
                        "each experiment according to those parameters.  It will take all the results, calculate the high, low,\n"
                        +
                        " and average values of each experiment, then it will log those results in an output file.\n");
        double low;
        double high;
        double average;

        //                            Parsing Input
        //----------------------------------------------------------------------------------------//
        ArrayList<Integer> xCoordinates = new ArrayList<>();
        ArrayList<Double> yCoordinates = new ArrayList<>();
        int M = 0, R = 0, P = 0, D = 0;
        char independentVar = 'z';
        char dependentVar;

        try (Scanner in = new Scanner(new File("indata.txt"));
             PrintWriter out = new PrintWriter("outdata.txt")) {

            // Read independent variable line
            String independentLine = in.nextLine().trim();
            String[] independentParts = independentLine.split(",");
            if (independentParts.length < 2 || !independentParts[0].equals("independent"))
                throw new IllegalArgumentException("Invalid independent variable line format");

            independentVar = independentParts[1].charAt(0);
            for (int i = 2; i < independentParts.length; i++) {
                try {
                    xCoordinates.add(Integer.parseInt(independentParts[i]));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer in independent variable line");
                }
            }

            // Process three 'fixed' lines
            for (int i = 0; i < 3; i++) {
                String fixedLine = in.nextLine().trim();
                String[] fixedParts = fixedLine.split(",");
                if (fixedParts.length != 3 || !fixedParts[0].equals("fixed"))
                    throw new IllegalArgumentException("Invalid fixed variable line format");

                char switchIdentifier = fixedParts[1].charAt(0);
                int value;
                try {
                    value = Integer.parseInt(fixedParts[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer in fixed variable line");
                }
                //Matt--I added case D in case dimension is a fixed variable
                switch (switchIdentifier) {
                    case 'M':
                        M = value;
                        break;
                    case 'R':
                        R = value;
                        break;
                    case 'P':
                        P = value;
                        break;
                    case 'D':
                        D = value;
                    default:
                        throw new IllegalArgumentException("Invalid Variable in fixed line");
                }
            }

            // Read dependent variable line
            String dependentLine = in.nextLine().trim();
            String[] dependentParts = dependentLine.split(",");
            if (dependentParts.length != 2 || !dependentParts[0].equals("dependent"))
                throw new IllegalArgumentException("Invalid dependent variable line format");

            dependentVar = dependentParts[1].charAt(0);

            //                                   Run Simulation
            //------------------------------------------------------------------------------------------//
            List<Integer> data = new ArrayList<>();
            if(independentVar != 'z') {

                for (int i = 0; i < xCoordinates.size(); i++) {
                    switch (independentVar) {
                        case 'M':
                            data = experiment(R, D, xCoordinates.get(i), P);
                            break;
                        case 'D':
                            data = experiment(R, xCoordinates.get(i), M, P);
                            break;
                        case 'R':
                            data = experiment(xCoordinates.get(i), D, M, P);
                            break;
                        case 'P':
                            data = experiment(R, D, M, xCoordinates.get(i));
                            break;

                    }
                    switch (dependentVar) {
                        case 'L':
                            low = Collections.min(data);
                            yCoordinates.add(low);
                            break;
                        case 'H':
                            high = Collections.max(data);
                            yCoordinates.add(high);
                            break;
                        case 'A':
                            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
                            yCoordinates.add(average);
                            break;
                    }
                }
                System.out.println("X and Y coordinate sizes: " + xCoordinates.size() + " " + yCoordinates.size());
                System.out.println("Independent and dependent vars: " + independentVar + dependentVar);
            }


        //                                  Printing Output
        //--------------------------------------------------------------------------------------------------//
        // Validate and print Table and BarGraph
        if (xCoordinates.size() != yCoordinates.size())
            throw new IllegalArgumentException("Mismatch in size of xCoordinates and yCoordinates");

        printTable(out, xCoordinates, yCoordinates, M, R, P, independentVar, dependentVar);
        printBarGraph(out, xCoordinates, yCoordinates);
    } catch (FileNotFoundException | IllegalArgumentException e) {
        System.err.println("Error: " + e.getMessage());
    }










    }
}