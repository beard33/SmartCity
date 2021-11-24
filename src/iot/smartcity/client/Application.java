package iot.smartcity.client;

import java.util.Scanner;
import java.util.stream.IntStream;

/**
 * It is the actual implementation of the Client class for a specific
 * instance/city/matrix.
 * It gives a command line interface to interact with Client commands
 */

public class Application {

    static String options = "\n------------------------------------------\n" +
                            "Choose an action\n" +
                            "1) Get best store\n" +
                            "2) Get weather condition\n" +
                            "3) Reserve place in a store\n" +
                            "4) Subscribe for PM10/PM25 notifications\n" +
                            "0) Exit" +
                            "\n------------------------------------------";
    static int location = 10;
    static int[] storeTypes = {0,1,2};
    static int[][] adjacencyMatrix = { { 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0 },
                                       { 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0 },
                                       { 1, 1, 0, 0, 1, 1, 0, 0, 0, 0, 0 },
                                       { 1, 1, 0, 0, 1, 1, 1, 1, 1, 0, 0 },
                                       { 0, 1, 1, 1, 0, 0, 0, 0, 1, 0, 0 },
                                       { 0, 1, 1, 1, 0, 0, 1, 0, 0, 0, 0 },
                                       { 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1 },
                                       { 0, 0, 0, 1, 0, 0, 1, 0, 1, 1, 1 },
                                       { 0, 0, 0, 1, 1, 0, 1, 1, 0, 1, 0 },
                                       { 0, 0, 0, 0, 0, 0, 0, 1, 1, 0, 1 },
                                       { 0, 0, 0, 0, 0, 0, 1, 1, 0, 1, 0 }};
    static Client test = new Client(adjacencyMatrix, "TESTC");

    public static void waitInput(){
        System.out.println("Press \"ENTER\" to continue...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    public static void main(String[] args) throws Exception {
        test.setLocation(location);
        boolean subscribed = false;

        while (true) {
            System.out.println(options);
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            switch (choice) {
                case 0:
                    System.exit(0);
                case 1:
                    System.out.println("Please provide store type");
                    int type = scanner.nextInt();
                    if (IntStream.of(storeTypes).noneMatch(x -> x == type)){
                        System.err.println("Please select a valid type of store (0-2)");
                        break;
                    }
                    System.out.println(test.bestStore(type));
                    waitInput();
                    break;
                case 2:
                    System.out.println(test.airCondition());
                    waitInput();
                    break;
                case 3:
                    System.out.println("Please provide store name");
                    Scanner stringScan = new Scanner(System.in);
                    String store = stringScan.nextLine();
                    String res = test.reservePlaceInStore(store);
                    if (res != null){
                        System.out.println(res);
                    }
                    waitInput();
                    break;
                case 4:
                    if (!subscribed) {
                        test.observePMResource("pm10_3");
                        test.observePMResource("pm25_3");
                        subscribed = true;
                        break;
                    } else {
                        System.err.println("You are already subscribed to alerts");
                    }
            }
        }
    }
}
