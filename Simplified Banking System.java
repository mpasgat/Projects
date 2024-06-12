import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class taskb {
    // main where all operations with the given strings will be held
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        scanner.nextLine();

        BankingFacade bankingSystem = new BankingFacade();

        for (int i = 0; i < n; i++) {
            String operation = scanner.nextLine();
            String[] details = operation.split(" ");
            String type = details[0];
            String accountName;
            switch (type) { // checking the possible cases and doing it
                case "Create":
                    String accountType = details[2];
                    accountName = details[3];
                    double initialDeposit = Double.parseDouble(details[4]);
                    bankingSystem.createAccount(accountType, accountName, initialDeposit);
                    break;
                case "Deposit":
                    accountName = details[1];
                    double depositAmount = Double.parseDouble(details[2]);
                    bankingSystem.deposit(accountName, depositAmount);
                    break;
                case "Withdraw":
                    accountName=details[1];
                    double withdrawalAmount = Double.parseDouble(details[2]);
                    bankingSystem.withdraw(accountName, withdrawalAmount);
                    break;
                case "Transfer":
                    accountName=details[1];
                    String toAccountName = details[2];
                    double transferAmount = Double.parseDouble(details[3]);
                    bankingSystem.transfer(accountName, toAccountName, transferAmount);
                    break;
                case "View":
                    accountName=details[1];
                    bankingSystem.viewAccountDetails(accountName);
                    break;
                case "Deactivate":
                    accountName=details[1];
                    bankingSystem.deactivateAccount(accountName);
                    break;
                case "Activate":
                    accountName=details[1];
                    bankingSystem.activateAccount(accountName);
                    break;
            }
        }
        scanner.close();
    }
}

// Interface for transaction fee strategy
interface TransactionFeeStrategy {
    double calculateTransactionFee(double amount);
}

// Concrete strategy for Savings Account
class SavingsTransactionFeeStrategy implements TransactionFeeStrategy {
    private static final double TRANSACTION_FEE_PERCENTAGE = 1.5;
    @Override
    public double calculateTransactionFee(double amount) {
        return amount * TRANSACTION_FEE_PERCENTAGE / 100;
    }
}

// Concrete strategy for Checking Account
class CheckingTransactionFeeStrategy implements TransactionFeeStrategy {
    private static final double TRANSACTION_FEE_PERCENTAGE = 2.0;
    @Override
    public double calculateTransactionFee(double amount) {
        return amount * TRANSACTION_FEE_PERCENTAGE / 100;
    }
}

// Concrete strategy for Business Account
class BusinessTransactionFeeStrategy implements TransactionFeeStrategy {
    private static final double TRANSACTION_FEE_PERCENTAGE = 2.5;
    @Override
    public double calculateTransactionFee(double amount) {
        return amount * TRANSACTION_FEE_PERCENTAGE / 100;
    }
}

// Context class for transaction fee strategy
class TransactionFeeContext {
    private final TransactionFeeStrategy strategy;

    public TransactionFeeContext(TransactionFeeStrategy strategy) {
        this.strategy = strategy;
    }

    public double executeStrategy(double amount) {
        return strategy.calculateTransactionFee(amount);
    }
}

// Interface for account state
interface AccountState {
    void deposit(Account account, double amount);
    String withdraw(Account account, double amount);
    String transfer(Account fromAccount, Account toAccount, double amount);
}

// Concrete state for active account
class ActiveState implements AccountState {

    // implementation of deposit for active account
    @Override
    public void deposit(Account account, double amount) {
        account.setBalance(account.getBalance() + amount); // new balance after deposit
        account.addTransaction("Deposit", amount); // adding deposit transaction to the transaction list of person
    }

    // implementation of withdraw for active account
    @Override
    public String withdraw(Account account, double amount) {
        if (account.getBalance() >= amount) {
            account.setBalance(account.getBalance() - amount); // changes the balance after withdraw
            account.addTransaction("Withdrawal", amount); // adding withdraw to the transaction list of person
            return "";
        } else {
            // error handling, if person does not have enough money to withdraw
            return "Error: Insufficient funds for " + account.getName()+".";
        }

    }

    // implementation of transfer for active account
    @Override
    public String transfer(Account fromAccount, Account toAccount, double amount) {
        if (fromAccount.getBalance() >= amount) {
            double transactionFee = fromAccount.getTransactionFeeContext().executeStrategy(amount);
            // from person1 will go certain amount to the person2, but it will have some fee in relation to
            // the account type. So the netAmount (that will receive person2) is the amount after deduction of fee
            double netAmount = amount - transactionFee;
            fromAccount.setBalance(fromAccount.getBalance() - amount); // change the balance of sender after transfer
            toAccount.setBalance(toAccount.getBalance() + netAmount); // change the balance of receiver after transfer
            fromAccount.addTransaction("Transfer", amount); // adding transfer to the transaction list of person
            return "";
        } else {
            // error handling, if sender does not have enough money to transfer
            return "Error: Insufficient funds for " + fromAccount.getName() + ".";
        }
    }
}

// Concrete state for inactive account
class InactiveState implements AccountState {
    // implementation of deposit for inactive account
    @Override
    public void deposit(Account account, double amount) {
        account.setBalance(account.getBalance() + amount);
        account.addTransaction("Deposit", amount);

    }

    // implementation of withdraw for inactive account
    @Override
    public String withdraw(Account account, double amount) {
        return "Error: Account " + account.getName() + " is inactive.";

    }

    // implementation of transfer for active account
    @Override
    public String transfer(Account fromAccount, Account toAccount, double amount) {
        return "Error: Account " + fromAccount.getName() + " is inactive.";
    }
}

// Account class
class Account {
    private final String type;
    private final String name;
    private double balance;
    private AccountState state;
    private final List<String> transactions;
    private final TransactionFeeContext transactionFeeContext;

    public Account(String type, String name, double initialDeposit) {
        this.type = type;
        this.name = name;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.transactionFeeContext = new TransactionFeeContext(getTransactionFeeStrategy());
        this.state = new ActiveState();
        addTransaction("Initial Deposit", initialDeposit);
    }

    // process of account activation
    public String activate() {
        // if account is activated, it will give already activated error
        if (state instanceof ActiveState) {
            return "Error: Account " + name + " is already activated.";
        } else {
            // if account not activated it will activate it
            this.state = new ActiveState();
            return "";
        }
    }

    // process of account deactivation
    public String deactivate() {
        // if account is deactivated, it will give already deactivated error
        if (state instanceof InactiveState) {
            return "Error: Account " + name + " is already deactivated.";
        } else {
            // if account not deactivated it will deactivate it
            this.state = new InactiveState();
            return "";
        }
    }

    // deposit of an account
    public void deposit(double amount) {
        state.deposit(this, amount);
    }

    // withdraw from an account
    public String withdraw(double amount) {
        return state.withdraw(this, amount);
    }

    // transfer from one person to another
    public String transfer(Account toAccount, double amount) {
        return state.transfer(this, toAccount, amount);
    }

    // adding any possible type of transaction to transaction list
    public void addTransaction(String type, double amount) {
        transactions.add(type + " $" + String.format("%.3f", amount));
    }

    // implementation of showing details of account(type, balance, active or inactive, transactions without fees)
    public void viewDetails() {
        System.out.print(name + "'s Account: Type: " + type + ", Balance: $" + String.format("%.3f", balance)
                + ", State: " + (state instanceof ActiveState ? "Active" : "Inactive") + ", Transactions: [");
        for (int g=0; g<transactions.size()-1; g++) {
            System.out.print(transactions.get(g) + ", ");
        }
        System.out.println(transactions.get(transactions.size()-1)+"].");

    }


    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    // make appropriate changes in balance
    public void setBalance(double balance) {
        this.balance = balance;
    }

    // gives the percentage of fee in relation to the account type
    public double percent() {
        return switch (type) {
            case "Savings" -> 1.5;
            case "Checking" -> 2.0;
            case "Business" -> 2.5;
            default -> 0.00;
        };
    }

    // calculating fee of certain account type
    public double calculateTransactionFee(double amount) {
        return switch (type) {
            case "Savings" -> amount * 0.015;
            case "Checking" -> amount * 0.02;
            case "Business" -> amount * 0.025;
            default -> 0.00;
        };
    }

    public TransactionFeeContext getTransactionFeeContext() {
        return transactionFeeContext;
    }

    // which fee strategy to apply according to the account type
    public TransactionFeeStrategy getTransactionFeeStrategy() {
        return switch (type) {
            case "Savings" -> new SavingsTransactionFeeStrategy();
            case "Checking" -> new CheckingTransactionFeeStrategy();
            case "Business" -> new BusinessTransactionFeeStrategy();
            default -> null;
        };
    }
}

// Singleton class for Banking System
class BankingSystem {
    private static BankingSystem instance;
    private final HashMap<String, Account> accounts;

    private BankingSystem() {
        accounts = new HashMap<>();
    }

    // Ensuring that only a single instance of the banking system exists.
    public static BankingSystem getInstance() {
        if (instance == null) {
            instance = new BankingSystem();
        }
        return instance;
    }

    // implementation of account creation
    public void createAccount(String accountType, String accountName, double initialDeposit) {
        Account account = new Account(accountType, accountName, initialDeposit);
        accounts.put(accountName, account);
        System.out.println("A new " + accountType + " account created for " + accountName
                + " with an initial balance of $" + String.format("%.3f", initialDeposit) + ".");
    }

    // processing account name and the amount of deposit and printing corresponding result
    public void deposit(String accountName, double amount) {
        Account account = accounts.get(accountName);
        if (account != null) {
            account.deposit(amount);
            // printing the result of deposit
            System.out.println(accountName + " successfully deposited $" + String.format("%.3f", amount)
                    + ". New Balance: $" + String.format("%.3f", account.getBalance())+".");
        } else {
            // error handling if account does not exist
            System.out.println("Error: Account " + accountName + " does not exist.");
        }
    }

    // processing account name and the amount of withdraw and printing corresponding result
    public void withdraw(String accountName, double amount) {
        Account account = accounts.get(accountName);
        if (account != null) {
            String InsufficientFundsError = account.withdraw(amount);
            // error handling if person does not have enough money to deposit
            if (!InsufficientFundsError.isEmpty()) {
                System.out.println(InsufficientFundsError);
                return;
            }
            double transactionFee = account.calculateTransactionFee(amount);
            amount -= transactionFee;
            // printing the result of withdraw
            System.out.println(accountName + " successfully withdrew $" + String.format("%.3f", amount)
                    + ". New Balance: $" + String.format("%.3f", account.getBalance()) + ". Transaction Fee: $"
                    + String.format("%.3f", transactionFee) + " (" + account.percent()+ "%) in the system.");
        } else {
            // error handling if account does not exist
            System.out.println("Error: Account " + accountName + " does not exist.");
        }
    }


    // processing account name of sender and receiver and the amount of transfer then printing corresponding result
    public void transfer(String fromAccountName, String toAccountName, double amount) {
        Account fromAccount = accounts.get(fromAccountName);
        Account toAccount = accounts.get(toAccountName);
        if (fromAccount != null && toAccount != null) {
            String InsufficientFundsError = fromAccount.transfer(toAccount, amount);
            if (!InsufficientFundsError.isEmpty()) {
                // error handling if sender does not have enough money to transfer
                System.out.println(InsufficientFundsError);
                return;
            }
            double transactionFee = fromAccount.calculateTransactionFee(amount);
            amount -= transactionFee;
            // printing the result of transfer
            System.out.println(fromAccountName + " successfully transferred $" + String.format("%.3f", amount) + " to "
                    + toAccountName + ". New Balance: $" + String.format("%.3f", fromAccount.getBalance())
                    + ". Transaction Fee: $" + String.format("%.3f", transactionFee) + " (" + fromAccount.percent()+
                    "%) in the system.");
        }
        // error handling if sender account does not exist
        else if (fromAccount==null) System.out.println("Error: Account " + fromAccountName + " does not exist.");
        // error handling if receiver account does not exist
        else System.out.println("Error: Account " + toAccountName + " does not exist.");
    }

    // showing the account details
    public void viewAccountDetails(String accountName) {
        Account account = accounts.get(accountName);
        if (account != null) {
            account.viewDetails();
        } else {
            // error handling if account does not exist
            System.out.println("Error: Account " + accountName + " does not exist.");
        }
    }

    // process the deactivating account and printing corresponding result
    public void deactivateAccount(String accountName) {
        Account account = accounts.get(accountName);
        if (account != null) {
            if (account.deactivate().equals("Error: Account " + accountName + " is already deactivated.")){
                System.out.println("Error: Account " + accountName + " is already deactivated.");
                return;
            }
            System.out.println(accountName + "'s account is now deactivated.");
        } else {
            System.out.println("Error: Account " + accountName + " does not exist.");
        }
    }

    // process the activating account and printing corresponding result
    public void activateAccount(String accountName) {
        Account account = accounts.get(accountName);

        if (account != null) {
            if (account.activate().equals("Error: Account " + accountName + " is already activated.")){
                System.out.println("Error: Account " + accountName + " is already activated.");
                return;
            }
            System.out.println(accountName + "'s account is now activated.");
        } else {
            System.out.println("Error: Account " + accountName + " does not exist.");
        }
    }
}

// Facade class for simplified interaction with BankingSystem
class BankingFacade {
    private final BankingSystem bankingSystem;

    public BankingFacade() {
        this.bankingSystem = BankingSystem.getInstance();
    }

    public void createAccount(String accountType, String accountName, double initialDeposit) {
        bankingSystem.createAccount(accountType, accountName, initialDeposit);
    }

    public void deposit(String accountName, double amount) {
        bankingSystem.deposit(accountName, amount);
    }

    public void withdraw(String accountName, double amount) {
        bankingSystem.withdraw(accountName, amount);
    }

    public void transfer(String fromAccountName, String toAccountName, double amount) {
        bankingSystem.transfer(fromAccountName, toAccountName, amount);
    }

    public void viewAccountDetails(String accountName) {
        bankingSystem.viewAccountDetails(accountName);
    }

    public void deactivateAccount(String accountName) {
        bankingSystem.deactivateAccount(accountName);
    }

    public void activateAccount(String accountName) {
        bankingSystem.activateAccount(accountName);
    }
}
