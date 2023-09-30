//Language: Java
//Programmers: Matthew Engrave, Gene Choi, Marielle Koffi
//22 Sep 2023
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

//This class holds each person's coordinates as they play the game.
class Person {
    int xCoordinate;
    int yCoordinate;

    public Person(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
    }
}

//This is a class that creates an object that holds each repetition's of each experiment's data.
class ExperimentData {
    int Dimension;
    int maxMoves;
    int repetitions;
    int protocol;
    double highMoves;
    double lowMoves;
    double averageMoves;

    public ExperimentData(int dimension, int maxMoves, int repetitions, int protocol, double lowMoves, double highMoves,
                          double averageMoves) {
        Dimension = dimension;
        this.maxMoves = maxMoves;
        this.repetitions = repetitions;
        this.protocol = protocol;
        this.highMoves = highMoves;
        this.lowMoves = lowMoves;
        this.averageMoves = averageMoves;
    }
}

public class Main {

    //Here we define the data structures that are integral to the experiments.  The first 3 hold the results of the experiments,
    //Then the next 6 are what hold the parameters that we get from the input file.
    private static List<Double> resultsExp1 = new ArrayList<>();
    private static List<Double> resultsExp2 = new ArrayList<>();
    private static List<Double> resultsExp3 = new ArrayList<>();
    private static int[] experiment1Dimensions = new int[5];
    private static int[] experiment1PMR = new int[3];
    private static int[] experiment2Reps = new int[5];
    private static int[] experiment2DPM = new int[3];
    private static int[] experiment3Protocols = { 4, 4, 8, 8 };
    private static int[] experiment3DMR = new int[3];


    //This function takes the input file and makes sure it's valid, and assigns each of the values
    //to its appropriate place in our data structure.
    private static void parseInput() throws IOException {
        BufferedReader br = new BufferedReader(new FileReader("indata.txt"));

        String line;
        List<String> errors = new ArrayList<>();

        try (Stream<String> fileStream = Files.lines(Paths.get("indata.txt"))) {
            int noOfLines = (int) fileStream.count();
            if (noOfLines > 6) {
                errors.add("Input file contains too many lines.");
            }
        }

        // Check and parse each line of input file
        for (int i = 1; i <= 6; i++) {
            line = br.readLine();
            String[] values;

            // regex matching logic credit:
            // https://www.geeksforgeeks.org/how-to-check-if-string-contains-only-digits-in-java/
            String regex = "[0-9]+";
            Pattern p = Pattern.compile(regex);

            // check every line to see if it is null. if true, this indicates a file with <6
            // lines. if false, split line between commas.
            if (line == null) {
                errors.add("Incomplete input file.");
                break;
            } else {
                // if line contains whitespace, add that to errors. then, eliminate all
                // whitespace and keep analyzing file for errors.
                if (line.contains(" ")) {
                    errors.add("Line " + i + " contains whitespace.");
                    line = line.replaceAll(" ", "");
                }
                values = line.split(",");
            }

            if (i == 1 && values.length == 5) {
                // Check and store experiment 1 dimensions
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if ((!m.matches())) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {
                        if (Integer.parseInt(values[j]) > 100) {
                            errors.add("Line " + i + " contains a dimension greater than 100.");
                        } else {
                            if (j == 0) {
                                experiment1Dimensions[j] = Integer.parseInt(values[j]);
                            } else if ((Integer.parseInt(values[j]) < Integer.parseInt(values[j - 1]))) {
                                errors.add("Values in line " + i + " not ordered in ascending order.");
                            } else {
                                experiment1Dimensions[j] = Integer.parseInt(values[j]);
                            }
                        }
                    }
                }

            } else if (i == 2 && values.length == 3) {
                // Check and store experiment 1 PMR
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if (!m.matches()) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {
                        //I tried using switch statements here but for some reason they would repeatedly be tripped every loop.
                        if (j == 0) {
                            if ((Integer.parseInt(values[0]) != 4) && (Integer.parseInt(values[0]) != 8)) {
                                errors.add("Line " + i + " contains an incorrect protocol code.");
                                break;
                            } else {
                                experiment1PMR[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 1) {
                            if (Integer.parseInt(values[1]) > 1000000) {
                                errors.add("Line " + i + " contains a number of moves greater than 1000000.");
                                break;
                            } else {
                                experiment1PMR[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 2) {
                            if (Integer.parseInt(values[2]) > 100000) {
                                errors.add("Line " + i + " contains a number of repetitions greater than 100000.");
                                break;
                            } else {
                                experiment1PMR[j] = Integer.parseInt(values[j]);
                            }
                        }
                    }
                }

            } else if (i == 3 && values.length == 5) {
                // Check and store experiment 2 reps
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if ((!m.matches())) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {
                        if (Integer.parseInt(values[j]) > 100000) {
                            errors.add("Line " + i + " contains a number of repetitions greater than 100000.");
                        } else {
                            if (j == 0) {
                                experiment2Reps[j] = Integer.parseInt(values[j]);
                            } else if ((Integer.parseInt(values[j]) < Integer.parseInt(values[j - 1]))) {
                                errors.add("Values in line " + i + " not ordered in ascending order.");
                            } else {
                                experiment2Reps[j] = Integer.parseInt(values[j]);
                            }
                        }
                    }
                }

            } else if (i == 4 && values.length == 3) {
                // Check and store experiment 2 DPM
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if (!m.matches()) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {
                        if (j == 0) {
                            if (Integer.parseInt(values[0]) > 100) {

                                errors.add("Line " + i + " contains a dimension greater than 100.");
                                break;
                            } else {
                                experiment2DPM[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 1) {
                            if ((Integer.parseInt(values[1]) != 4) && (Integer.parseInt(values[1]) != 8)) {
                                errors.add("Line " + i + " contains an incorrect protocol code.");
                                break;
                            } else {
                                experiment2DPM[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 2) {
                            if (Integer.parseInt(values[2]) > 1000000) {
                                errors.add("Line " + i + " contains a number of moves greater than 1000000.");
                                break;
                            } else {
                                experiment2DPM[j] = Integer.parseInt(values[j]);
                            }
                        }
                    }
                }

            } else if (i == 5 && values.length == 4) {
                // check if exp 3 protocols are correctly specified
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if ((!m.matches())) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {
                        if (!line.equals("4,4,8,8")) {
                            errors.add("Line " + i + " contains an illegal character.");
                        }
                    }
                }

            } else if (i == 6 && values.length == 3) {
                // Check and store experiment 3 DMR
                for (int j = 0; j < values.length; j++) {
                    Matcher m = p.matcher(values[j]);
                    if (!m.matches()) {
                        errors.add("Line " + i + " contains an illegal character.");
                        break;
                    } else {

                        if (j == 0) {
                            if (Integer.parseInt(values[0]) > 100) {
                                errors.add("Line " + i + " contains a dimension greater than 100.");
                                break;
                            } else {
                                experiment3DMR[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 1) {
                            if (Integer.parseInt(values[1]) > 1000000) {
                                errors.add("Line " + i + " contains a number of moves greater than 1000000.");
                                break;
                            } else {
                                experiment3DMR[j] = Integer.parseInt(values[j]);
                            }
                        } else if (j == 2) {
                            if (Integer.parseInt(values[2]) > 100000) {
                                errors.add("Line " + i + " contains a number of repetitions greater than 100000.");
                                break;
                            } else {
                                experiment3DMR[j] = Integer.parseInt(values[j]);
                            }
                        }
                    }
                }

            } else {
                errors.add("Line " + i + " is the wrong length.");
            }
        }

        br.close();

        // print any problematic lines, if there are any.
        if (!errors.isEmpty())

        {
            System.out.println(errors.size() + " error(s) found:");
            for (String error : errors) {
                System.out.println(error);
            }
            System.exit(1);
        } else {
            System.out.println("No errors found in input file.");
        }
    }

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
    private static void outputGenerator() throws IOException {
        // Packaging the results of the experiments into objects
        ExperimentData[] experiment1 = new ExperimentData[5];
        for (int i = 0; i < 5; i++)
            experiment1[i] = new ExperimentData(experiment1Dimensions[i], experiment1PMR[1], experiment1PMR[2],
                    experiment1PMR[0], resultsExp1.get((3 * i)), resultsExp1.get((3 * i) + 1),
                    resultsExp1.get((3 * i) + 2));

        ExperimentData[] experiment2 = new ExperimentData[5];
        for (int i = 0; i < 5; i++)
            experiment2[i] = new ExperimentData(experiment2DPM[0], experiment2DPM[2], experiment2Reps[i],
                    experiment2DPM[1], resultsExp2.get((3 * i)), resultsExp2.get((3 * i) + 1),
                    resultsExp2.get((3 * i) + 2));

        ExperimentData[] experiment3 = new ExperimentData[4];
        for (int i = 0; i < 4; i++)
            experiment3[i] = new ExperimentData(experiment3DMR[0], experiment3DMR[1], experiment3DMR[2],
                    experiment3Protocols[i], resultsExp3.get((3 * i)), resultsExp3.get((3 * i) + 1),
                    resultsExp3.get((3 * i) + 2));

        // write gets results of experiments and writes them to a file
        PrintWriter writer = new PrintWriter(new FileWriter("outdata.txt"));
        System.out.println("Generating results...");
        writer.println(
                "Experiment #1 \nChanges the dimensions of the grid. Other variables are held constant.");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        writer.println(
                "|              |  Max Number  |               |            |    Lowest    |    Highest   |    Average  |");
        writer.println(
                "|  Dimensions  |   of Moves   |  Repetitions  |  Protocol  |  # of moves  |  # of moves  |  # of moves |");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        for (int i = 0; i < 5; i++) {
            writer.printf("| %-12d | %-12d | %-13d | %-10d | %-12.0f | %-12.0f | %-11.2f |\n", experiment1[i].Dimension,
                    experiment1[i].maxMoves, experiment1[i].repetitions, experiment1[i].protocol,
                    experiment1[i].lowMoves, experiment1[i].highMoves, experiment1[i].averageMoves);
            writer.println(
                    "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        }
        writer.println(
                "Experiment #2 \nChanges the number of wanderings (repeats) on each row. Other variables are held\r\n" + //
                        "constant.");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        writer.println(
                "|              |  Max Number  |               |            |    Lowest    |    Highest   |    Average  |");
        writer.println(
                "|  Repetitions |   of Moves   |  Dimensions   |  Protocol  |  # of moves  |  # of moves  |  # of moves |");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");

        for (int i = 0; i < 5; i++) {
            writer.printf("| %-12d | %-12d | %-13d | %-10d | %-12.0f | %-12.0f | %-11.2f |\n", experiment2[i].repetitions,
                    experiment2[i].maxMoves, experiment2[i].Dimension, experiment2[i].protocol,
                    experiment2[i].lowMoves, experiment2[i].highMoves, experiment2[i].averageMoves);
            writer.println(
                    "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        }
        writer.println(
                "Experiment #3 \nChanges the protocols. Other variables are held constant.");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        writer.println(
                "|              |  Max Number  |               |            |    Lowest    |    Highest   |    Average  |");
        writer.println(
                "|  Protocol    |   of Moves   |  Repetitions  | Dimensions |  # of moves  |  # of moves  |  # of moves |");
        writer.println(
                "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        for (int i = 0; i < 4; i++) {
            writer.printf("| %-12d | %-12d | %-13d | %-10d | %-12.0f | %-12.0f | %-11.2f |\n", experiment3[i].protocol,
                    experiment3[i].maxMoves, experiment3[i].repetitions, experiment3[i].Dimension,
                    experiment3[i].lowMoves, experiment3[i].highMoves, experiment3[i].averageMoves);
            writer.println(
                    "*--------------*--------------*---------------*------------*--------------*--------------*-------------*");
        }
        System.out.println("Results written to outdata.txt.");
        writer.close();
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
        parseInput();
        List<Integer> data = new ArrayList<>();

        // Running experiment 1 and logging the calculations to a new results data structure.
        for (int i = 0; i < 5; i++) {
            data = experiment(experiment1PMR[2], experiment1Dimensions[i], experiment1PMR[1], experiment1PMR[0]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp1.add(low);
            resultsExp1.add(high);
            resultsExp1.add(average);
        }

        // Running Experiment 2 and logging the calculations to a new results data structure.
        for (int i = 0; i < 5; i++) {
            data = experiment(experiment2Reps[i], experiment2DPM[0], experiment2DPM[2], experiment2DPM[1]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp2.add(low);
            resultsExp2.add(high);
            resultsExp2.add(average);
        }

        // Running Experiment 3 and logging the calculations to a new results data structure.
        for (int i = 0; i < 4; i++) {
            data = experiment(experiment3DMR[2], experiment3DMR[0], experiment3DMR[1], experiment3Protocols[i]);
            low = Collections.min(data);
            high = Collections.max(data);
            average = data.stream().mapToDouble(Integer::doubleValue).average().orElse(0.0);
            resultsExp3.add(low);
            resultsExp3.add(high);
            resultsExp3.add(average);
        }

        outputGenerator();
    }
}