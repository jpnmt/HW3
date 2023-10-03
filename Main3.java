import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try (Scanner in = new Scanner(new File("indata.txt"));
             PrintWriter out = new PrintWriter("outdata.txt")) {

            // Read independent variable line
            String independentLine = in.nextLine().trim();
            String[] independentParts = independentLine.split(",");
            if (independentParts.length < 2 || !independentParts[0].equals("independent"))
                throw new IllegalArgumentException("Invalid independent variable line format");

            char independentVar = independentParts[1].charAt(0);
            ArrayList<Integer> xCoordinates = new ArrayList<>();
            for (int i = 2; i < independentParts.length; i++) {
                try {
                    xCoordinates.add(Integer.parseInt(independentParts[i]));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer in independent variable line");
                }
            }

            // Process three 'fixed' lines
            int M = 0, R = 0, P = 0;
            for (int i = 0; i < 3; i++) {
                String fixedLine = in.nextLine().trim();
                String[] fixedParts = fixedLine.split(",");
                if (fixedParts.length != 3 || !fixedParts[0].equals("fixed"))
                    throw new IllegalArgumentException("Invalid fixed variable line format");

                char W = fixedParts[1].charAt(0);
                int value;
                try {
                    value = Integer.parseInt(fixedParts[2]);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid integer in fixed variable line");
                }

                switch (W) {
                    case 'M':
                        M = value;
                        break;
                    case 'R':
                        R = value;
                        break;
                    case 'P':
                        P = value;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Variable in fixed line");
                }
            }

            // Read dependent variable line
            String dependentLine = in.nextLine().trim();
            String[] dependentParts = dependentLine.split(",");
            if (dependentParts.length != 2 || !dependentParts[0].equals("dependent"))
                throw new IllegalArgumentException("Invalid dependent variable line format");

            char dependentVar = dependentParts[1].charAt(0);

            // Assuming a dummy simulation to populate yCoordinates
            ArrayList<Integer> yCoordinates = new ArrayList<>();
            for (int x : xCoordinates) {
                yCoordinates.add(x * 10); // This is just a dummy calculation for y
            }

            // Validate and print Table and BarGraph
            if (xCoordinates.size() != yCoordinates.size())
                throw new IllegalArgumentException("Mismatch in size of xCoordinates and yCoordinates");

            printTable(out, xCoordinates, yCoordinates, M, R, P, independentVar, dependentVar);
            printBarGraph(out, xCoordinates, yCoordinates);
        } catch (FileNotFoundException | IllegalArgumentException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void printTable(PrintWriter out, ArrayList<Integer> xCoordinates, ArrayList<Integer> yCoordinates, int M, int R, int P, char independentVar, char dependentVar) {
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
            int y = yCoordinates.get(i);

            if (independentVar == 'D') {
                out.printf("%-12d| %-10d| %-10d| %-10d| %-10d\n", x, P, M, R, y);
            } else {
                out.printf("%-12d| %-10d| %-10d| %-10d| %-10d\n", y, P, M, R, x);
            }
        }
    }

    private static void printBarGraph(PrintWriter out, ArrayList<Integer> xCoordinates, ArrayList<Integer> yCoordinates) {
        out.println("\nTypewriter Graph");
        for (int i = 0; i < xCoordinates.size(); i++) {
            out.print(xCoordinates.get(i) + " | ");
            int length = yCoordinates.get(i);
            for (int j = 0; j < length; j++) out.print("*");
            out.println();
        }
    }
}