import java.io.*;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.InputMismatchException;
import java.util.Scanner;

import static java.lang.Math.abs;



public class Shop {

    private String shopName;
    private Scanner scan;
    int numOfTrays;
    private UKTill till;
    private int maxItems;
    private ArrayList<Item> stock = new ArrayList<>();
    private int totalCostDue; // Store as pence
    private static final String SHOP_STOCK_DATA_FILE = "./stock.txt";
    private static final String SHOP_TILL_DATA_FILE = "./till.txt";
    private int numOfItems;
    private Formatter x;

    public Shop(String name) {
        shopName = name;
        till = new UKTill();
        scan = new Scanner(System.in);
    }

    /**
     * This checks if item is out of stock and removes it
     */
    public void checkAndRemove() {
        for (Item i : stock) {
            if (i.getQuanity() == 0) {
                stock.remove(i);
            }
        }
    }

    /**
     * Using stockShop an owner can add various goods to the system. We can then
     * can add an item, set the price and set the stock level.
     */
    public void stockShop() {
        do {
            System.out.println("Enter barcode for the item to be added");
            String identifier = scan.next();
            System.out.println("Enter name of the item");
            scan.nextLine();
            String name = scan.nextLine();
            System.out.println("Enter the cost of item in pennies");
            int cost = scan.nextInt();
            System.out.println("Enter the quantity of item");
            int quantity = scan.nextInt();
            Item item = new Item(identifier, name, cost, quantity);
            stock.add(item);
        }
        while (doContinue());
    }

    /**
     * Using startTill, an owner can add the various denomination floats to the till
     * specifying the name, value and quantity of each item (If she has 33
     * "10p pieces", each of which is worth 10, she enters "10p piece", 10, 33
     * (one separate lines)).
     */
    public void startTill() {
        do {
            UKDenomination ct = getDenominationType();
            int nc = getInt("Number of these coins: ");
            DenominationFloat m = new DenominationFloat(ct, nc);
            till.addFloat(m);

            System.out.println("Denomination floats enetered into till: " + m);
        } while (doContinue());
    }


    /**
     * Using runTill, an owner can sell items. Customers put in their order, the
     * system then tells her how much to charge.
     */
    public void runTill() {

        do {
            System.out.println("Enter id of item to scan");
            String identifier = scan.next();

              if(checkAndScanItem(stock, identifier)){}
                else{
                  System.out.println("Incorrect id or item out of stock");
                }


        } while (doContinue());
        String message = "Total due is:";
        displayCost(message, totalCostDue);
    }

    public boolean checkAndScanItem(ArrayList<Item> stock, String identifier) {
        for (Item i : stock) {
            if (identifier.equals(i.getIdentifier())) {
                i.setQuanity(i.getQuanity() - 1);
                totalCostDue += i.getCost();
                System.out.print("Item: ");
                System.out.print(i.getIdentifier());
                System.out.print(" ");
                System.out.println(i.getName());
                checkAndRemove();
                return true;
            }
            
        }
        return false;
    }

    /**
     * Using getChange, an owner can tell the system how much of each
     * denomination she has been given by the customer and the till tells her
     * what to giveback.
     */
    public void getChange() {
        System.out.println("Hand over the money!");
        do {
            System.out.println("Enter the denomination type, one of:");
            System.out.println("1p 2p 5p 10p 20p 50p £1 £2 £5 £10 £20 £50 ");
            String d = scan.next();
            System.out.println("Enter number of these coins/notes");
            int n = scan.nextInt();
            UKDenomination floats = UKDenomination.fromString(d);
            int amtPaid = floats.getValue()* n;
           // int amtPaid = UKDenomination.valueOf(d).getValue() * n;

            totalCostDue -= amtPaid;

            for(DenominationFloat denom: till.getContents())
            {
                if(denom.getType()== UKDenomination.fromString(d)){
                    denom.setQuantity(denom.getQuantity()+n);
                    break;
                }
            }
            String message = "Remaining Due:";
            System.out.println("Denomination floats entered into till: " + d + " * " + n);
            displayCost(message, abs(totalCostDue));

        } while (totalCostDue > 0);

        // Calculate change
        if (totalCostDue == 0) {
            System.out.println("You provided the exact amount, thank you!");
        } else {
            DenominationFloat[] change = till.getChange(abs(totalCostDue));
            System.out.println("Here is your change:");
            for (DenominationFloat m : change) {
                if (m != null) {
                    System.out.println(m);
                }
                else break;
            }
        }
    }

    /**
     * Using getBalance it tells the owner what is left in the till
     */
    public void getBalance() {
        System.out.println(till);
    }

    /**
     * runMenu provides the main menu to the shop allowing a user to select
     * their required operation
     */
    public void runMenu() {
        // This is the main menu which runs the whole shop

        String choice;
        do {
            printMenu();
            choice = scan.next();

            switch (choice) {
                case "1":
                    stockShop();
                    break;
                case "2":
                    startTill();
                    break;
                case "3":
                    runTill();
                    break;
                case "4":
                    getChange();
                    break;
                case "5":
                    getBalance();
                    break;
                case "6":
                    System.out.println("Thankyou for running " + shopName
                            + " program");
                    break;
                default:
                    System.err.println("Incorrect choice entered");
            }
        } while (!choice.equals("6"));
    }

    private boolean doContinue() {
        System.out.println("Continue? (Y/N)");
        String answer = scan.next().toUpperCase();
        scan.nextLine();
        return answer.equals("Y");
    }

    private int getInt(String message) {
        boolean correct = false;
        int result = 0;
        do {
            System.out.println(message);
            try {
                result = scan.nextInt();
                scan.nextLine();
                correct = true;
            } catch (InputMismatchException ime) {
                System.err.println("Please enter an number");
                scan.nextLine();
            }
        } while (!correct);
        return result;
    }

    private void displayCost(String message, int amountInPence) {
        System.out.format("%s %d.%02d\n", message, amountInPence / 100, amountInPence % 100);
    }

    private UKDenomination getDenominationType() {
        UKDenomination result;
        do {
            System.out.println("Enter the denomination type. One of: ");
            for (UKDenomination denom : UKDenomination.values()) {
                System.out.print(denom + " ");
            }
            String choice = scan.nextLine();
            result = UKDenomination.fromString(choice);
            if (result == null) {
                System.err.println("Incorrect denomination entered. Try again!");
            }
        } while (result == null);
        return result;
    }

    private void printMenu() {
        System.out.println("Welcome to " + shopName + ". Please enter choice:");
        System.out.println("1 - Stock the shop");
        System.out.println("2 - Add coins to the till");
        System.out.println("3 - Process customer order");
        System.out.println("4 - Process customer payment");
        System.out.println("5 - Display till balance");
        System.out.println("6 - Exit shop program");
    }

    /**
     * Saves data to the shop database (stock and till)
     *
     * @throws IOException thrown when file problems occur
     */
    public void save(String fileName) throws IOException {
        if (fileName.equals(SHOP_STOCK_DATA_FILE)) {
            writeStock(fileName);
        } else {
            writeTill(fileName);
        }
    }

    /**
     * @param fileName
     * @throws IOException writes Till data to shop's database
     */
    public void writeTill(String fileName) throws IOException {
        FileWriter fileOutput = new FileWriter(fileName);
        PrintWriter outfile = new PrintWriter(fileOutput);
        outfile.println(numOfTrays);
        for (DenominationFloat d : till.getContents()) {
            outfile.print(d.getType());
            outfile.print(" ");
            outfile.println(d.getQuantity());
        }
        outfile.close();
    }

    /**
     * @param fileName
     * @throws IOException writes stock data to shop's database
     */
    public void writeStock(String fileName) throws IOException {
        FileWriter fileOutput = new FileWriter(fileName);
        PrintWriter outfile = new PrintWriter(fileOutput);
        outfile.println(stock.size());
        for (int i = 0; i < stock.size(); i++) {
            outfile.println(stock.get(i).getIdentifier());
            outfile.println(stock.get(i).getCost());
            outfile.println(stock.get(i).getName());
            outfile.println(stock.get(i).getQuanity());
        }
        outfile.close();
    }

    /**
     * @param fileName
     * @throws IOException reads stock data from file
     */
    public void readStock(String fileName) throws IOException {
        FileReader fileInput = new FileReader(fileName);
        Scanner infile = new Scanner(fileInput);
        infile.useDelimiter(" :|\r?\n|\r");
        maxItems = infile.nextInt();
        while (infile.hasNext()) {
            String identifier = infile.next();
            int cost = infile.nextInt();
            String name = infile.next();
            int quantity = infile.nextInt();
            Item item = new Item(identifier, name, cost, quantity);
            this.addItem(item);
        }
        infile.close();
    }

    /**
     * @param fileName
     * @throws IOException reads Till data from file
     */

    public void readTill(String fileName) throws IOException {
        FileReader fileInput = new FileReader(fileName);
        Scanner infile = new Scanner(fileInput);
        infile.useDelimiter(" :|\r?\n|\r");
        numOfTrays = infile.nextInt();
                    for (UKDenomination d : UKDenomination.values()) {
                        String input = infile.next();
                Scanner s = new Scanner(input);
                while ((s.hasNext()))
                {
                    String denom = s.next();
                    if (denom.equals(d.toString())) {
                        int quantity = s.nextInt();
                        DenominationFloat f = new DenominationFloat(d, quantity);
                        till.addFloat(f);
                    }
                }
            }
                    infile.close();
    }

    /**
     * @param item method to add item to arraylist stock
     */

    public void addItem(Item item) {
        if (numOfItems < maxItems) {
            stock.add(item);
            numOfItems++;
        }
    }
    

    /**
     * Loads data from the shop database (stock and till)
     *
     * @throws IOException thrown when file problems occur
     */
    public void load() throws IOException {
        //openFile2Read(SHOP_STOCK_DATA_FILE);

        readStock(SHOP_STOCK_DATA_FILE);
        //openFile2Read(SHOP_TILL_DATA_FILE);
        readTill(SHOP_TILL_DATA_FILE);

    }

    public static void main(String[] args) {
        // Don't touch any of this code
        Shop migginsPieShop = new Shop("Mrs Miggins Pie Shop");
        try {
            migginsPieShop.load();
        } catch (IOException e) {
            // Something went wrong so start a new shop
            System.err.println("Sorry but we were unable to load shop data: "
                    + e.getMessage());
        }

        migginsPieShop.runMenu();

        try {
            migginsPieShop.save(SHOP_STOCK_DATA_FILE);
            migginsPieShop.save(SHOP_TILL_DATA_FILE);
        } catch (IOException e) {
            System.err
                    .println("Sorry but we just lost everything. Unable to save shop data: "
                            + e.getMessage());
        }
    }

}
