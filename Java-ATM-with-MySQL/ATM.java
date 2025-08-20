package Demo_JDBC;
import java.util.*;
import java.sql.Date;
import java.sql.*;
import java.time.*;

public class Atm {
    private static double balance = 1000.00;
    private static int registeredPin;

    public static void checkBalance() {
        System.out.printf("Your current balance is: ₹%.2f%n", balance);
    }

    public static void deposit(Scanner sc, Connection con) throws SQLException {
        System.out.print("Enter amount to deposit: ₹");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Must be greater than zero.");
            return;
        }

        balance += amount;
        System.out.printf("₹%.2f deposited successfully.%n", amount);

        insertTransaction(con, amount, 0);
    }

    public static void withdraw(Scanner sc, Connection con) throws SQLException {
        System.out.print("Enter amount to withdraw: ₹");
        double amount = sc.nextDouble();

        if (amount <= 0) {
            System.out.println("Invalid amount. Must be greater than zero.");
        } else if (amount > balance) {
            System.out.println("Insufficient balance!");
        } else {
            balance -= amount;
            System.out.printf("₹%.2f withdrawn successfully.%n", amount);

            insertTransaction(con, 0, amount);
        }
    }

    public static void insertTransaction(Connection con, double credit, double debit) throws SQLException {
        LocalDate date = LocalDate.now();
        LocalTime time = LocalTime.now();

        String query = "INSERT INTO transactions (pin, credited_amount, withdrawn_amount, balance, transaction_date, transaction_time) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement pstmt = con.prepareStatement(query);
        pstmt.setInt(1, registeredPin);
        pstmt.setDouble(2, credit);
        pstmt.setDouble(3, debit);
        pstmt.setDouble(4, balance);
        pstmt.setDate(5, Date.valueOf(date));
        pstmt.setTime(6, Time.valueOf(time));
        pstmt.executeUpdate();
    }

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/atm?useSSL=false&serverTimezone=UTC", "username", "password");

            Statement stmt = con.createStatement();
            Scanner sc = new Scanner(System.in);

            System.out.println("==== ATM PIN Registration ====");
            System.out.print("Enter the Name: ");
            String name = sc.next();
            System.out.print("Create a 4-digit PIN: ");
            registeredPin = sc.nextInt();

            String str = "INSERT INTO login VALUES (" + registeredPin + ", '" + name + "')";
            int result = stmt.executeUpdate(str);
            System.out.println(result + " row inserted.");

            System.out.println("\n==== Welcome to Simple ATM ====");
            System.out.print("Enter your 4-digit PIN to login: ");
            int enteredPin = sc.nextInt();

            if (enteredPin != registeredPin) {
                System.out.println("Incorrect PIN. Access Denied.");
                con.close();
                sc.close();
                return;
            }

            int choice;
            do {
                System.out.println("\n==== ATM Menu ====");
                System.out.println("1. Check Balance");
                System.out.println("2. Deposit Money");
                System.out.println("3. Withdraw Money");
                System.out.println("4. Exit");
                System.out.print("Choose an option: ");
                choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        checkBalance();
                        break;
                    case 2:
                        deposit(sc, con);
                        break;
                    case 3:
                        withdraw(sc, con);
                        break;
                    case 4:
                        System.out.println("Thank you for using our ATM. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } while (choice != 4);

            con.close();
            sc.close();

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }
}
