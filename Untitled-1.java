import java.io.*;
import java.util.*;

class StockMarket {
    private Map<String, Double> prices;

    public StockMarket() {
        prices = new HashMap<>();
        prices.put("AAPL", 150.0);
        prices.put("GOOGL", 2800.0);
        prices.put("TSLA", 700.0);
        prices.put("AMZN", 3300.0);
    }

    public void displayMarket() {
        System.out.println("\n--- Stock Market Prices ---");
        for (Map.Entry<String, Double> entry : prices.entrySet()) {
            System.out.printf("%s: $%.2f\n", entry.getKey(), entry.getValue());
        }
    }

    public void updatePrices() {
        Random rand = new Random();
        for (String stock : prices.keySet()) {
            double change = -5 + rand.nextDouble() * 10;
            prices.put(stock, Math.max(1.0, prices.get(stock) + change));
        }
    }

    public double getPrice(String symbol) {
        return prices.getOrDefault(symbol, -1.0);
    }

    public boolean isStockAvailable(String symbol) {
        return prices.containsKey(symbol);
    }
}

class User {
    private String name;
    private double balance;
    private Map<String, Integer> portfolio;

    public User(String name) {
        this.name = name;
        this.balance = 10000.0;
        this.portfolio = new HashMap<>();
    }

    public void buyStock(String symbol, int qty, double price) {
        double total = qty * price;
        if (total > balance) {
            System.out.println("Not enough balance.");
            return;
        }
        balance -= total;
        portfolio.put(symbol, portfolio.getOrDefault(symbol, 0) + qty);
        System.out.println("Bought " + qty + " shares of " + symbol);
    }

    public void sellStock(String symbol, int qty, double price) {
        int owned = portfolio.getOrDefault(symbol, 0);
        if (qty > owned) {
            System.out.println("Not enough shares to sell.");
            return;
        }
        balance += qty * price;
        if (qty == owned) {
            portfolio.remove(symbol);
        } else {
            portfolio.put(symbol, owned - qty);
        }
        System.out.println("Sold " + qty + " shares of " + symbol);
    }

    public void viewPortfolio(StockMarket market) {
        System.out.println("\n--- Portfolio ---");
        System.out.printf("Balance: $%.2f\n", balance);
        double totalValue = balance;
        for (String symbol : portfolio.keySet()) {
            int qty = portfolio.get(symbol);
            double price = market.getPrice(symbol);
            double value = qty * price;
            System.out.printf("%s: %d shares = $%.2f\n", symbol, qty, value);
            totalValue += value;
        }
        System.out.printf("Total Portfolio Value: $%.2f\n", totalValue);
    }

    public void saveToFile() {
        try {
            FileWriter fw = new FileWriter("portfolio.txt");
            fw.write(name + "\n");
            fw.write(balance + "\n");
            for (String symbol : portfolio.keySet()) {
                fw.write(symbol + " " + portfolio.get(symbol) + "\n");
            }
            fw.close();
            System.out.println("Portfolio saved.");
        } catch (IOException e) {
            System.out.println("Error saving file.");
        }
    }

    public void loadFromFile() {
        try {
            File file = new File("portfolio.txt");
            Scanner sc = new Scanner(file);
            name = sc.nextLine();
            balance = Double.parseDouble(sc.nextLine());
            portfolio.clear();
            while (sc.hasNextLine()) {
                String[] line = sc.nextLine().split(" ");
                portfolio.put(line[0], Integer.parseInt(line[1]));
            }
            sc.close();
            System.out.println("Portfolio loaded.");
        } catch (IOException e) {
            System.out.println("No saved portfolio found.");
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        StockMarket market = new StockMarket();
        User user = new User("Trader");

        while (true) {
            market.updatePrices();
            System.out.println("\n====== STOCK TRADING SIMULATOR ======");
            System.out.println("1. Show Market Prices");
            System.out.println("2. Buy Stocks");
            System.out.println("3. Sell Stocks");
            System.out.println("4. View Portfolio");
            System.out.println("5. Save Portfolio");
            System.out.println("6. Load Portfolio");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    market.displayMarket();
                    break;
                case "2":
                    System.out.print("Enter stock symbol: ");
                    String buySymbol = scanner.nextLine().toUpperCase();
                    if (!market.isStockAvailable(buySymbol)) {
                        System.out.println("Stock not found.");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int buyQty = scanner.nextInt(); scanner.nextLine();
                    user.buyStock(buySymbol, buyQty, market.getPrice(buySymbol));
                    break;
                case "3":
                    System.out.print("Enter stock symbol: ");
                    String sellSymbol = scanner.nextLine().toUpperCase();
                    if (!market.isStockAvailable(sellSymbol)) {
                        System.out.println("Stock not found.");
                        break;
                    }
                    System.out.print("Enter quantity: ");
                    int sellQty = scanner.nextInt(); scanner.nextLine();
                    user.sellStock(sellSymbol, sellQty, market.getPrice(sellSymbol));
                    break;
                case "4":
                    user.viewPortfolio(market);
                    break;
                case "5":
                    user.saveToFile();
                    break;
                case "6":
                    user.loadFromFile();
                    break;
                case "7":
                    System.out.println("Exiting. Bye!");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
