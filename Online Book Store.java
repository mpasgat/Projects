import java.util.*;

public class yes {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        HashMap<String, Book> books = new HashMap<>(); // HashMap to store books
        HashMap<String, User> users = new HashMap<>(); // HashMap to store users
        Subscribers subs = new Subscribers(); // Subscribers object to manage observers

        while (true) {
            String command = scanner.nextLine().trim();
            if (command.equals("end")) {
                break;
            }

            String[] parts = command.split(" ");

            // managing all possible operations
            switch (parts[0]) {
                case "createBook":
                    String title = parts[1];
                    if (books.containsKey(title)) {
                        System.out.println("Book already exists"); // error if book exists
                    } else {
                        String author = parts[2];
                        String price = parts[3];
                        Book newBook = new Book(title, author, price);
                        books.put(title, newBook);
                    }
                    break;

                case "createUser":
                    String userType = parts[1];
                    String username = parts[2];
                    if (users.containsKey(username)) {
                        System.out.println("User already exists"); // error if user exists
                    } else {
                        User newUser = UserFactory.createUser(userType, username);
                        users.put(username, new BookProxy(newUser));
                    }
                    break;

                case "subscribe":
                    String subscribeUser = parts[1];
                    User subscriber = users.get(subscribeUser);
                    if (subscriber != null) {
                        if (subscriber.isSubscribed()) {
                            System.out.println("User already subscribed");// error is subscribed user wants to subscribe
                        } else {
                            subscriber.subscribe();
                            subs.sub(subscriber);
                        }
                    }
                    break;

                case "unsubscribe":
                    String unsubscribeUser = parts[1];
                    User unsubscribedUser = users.get(unsubscribeUser);
                    if (unsubscribedUser != null) {
                        if (!unsubscribedUser.isSubscribed()) {
                            System.out.println("User is not subscribed");
                            // error if not subscribed person wants to unsubscribe
                        } else {
                            unsubscribedUser.unsubscribe();
                            subs.unsub(unsubscribedUser);
                        }
                    }
                    break;

                case "updatePrice":
                    String bookTitle = parts[1];
                    String newPrice = parts[2];
                    if (books.containsKey(bookTitle)) {
                        Book updatedBook = books.get(bookTitle);
                        updatedBook.updatePrice(newPrice);
                        subs.notifyObservers("price update for " + bookTitle + " to " + updatedBook.getPrice());
                    }
                    break;

                case "readBook":
                    String readerUsername = parts[1];
                    String bookToRead = parts[2];
                    User reader = users.get(readerUsername);
                    Book bookRead = books.get(bookToRead);
                    reader.readBook(bookRead);
                    break;

                case "listenBook":
                    String listenerUsername = parts[1];
                    String bookToListen = parts[2];
                    User listener = users.get(listenerUsername);
                    Book bookListen = books.get(bookToListen);
                    if (listener.isPremium()) listener.listenBook(bookListen);
                    else System.out.println("No access"); // no access to the user with no premium
                    break;

            }
        }
    }
}


// Book class representing a book entity
class Book {
    private final String title;
    private final String author;
    private String price;

    public Book(String title, String author, String price) { // setting the title, author, and price of the book
        this.title = title;
        this.author = author;
        this.price = price;
    }

    public String getTitle() {
        return title;
    }
    public String getAuthor() {
        return author;
    }
    public String getPrice() {
        return price;
    }
    public void updatePrice(String newPrice) { // giving nwe price to the book
        this.price = newPrice;
    }
}

// User interface representing a bookstore user
interface User {
    void subscribe(); // method to subscribe the user
    void unsubscribe(); // method to unsubscribe the user
    void notify(String message); // method to notify the user
    boolean isPremium(); // method to check if the user is premium
    void readBook(Book book); // method for the user to read a book
    boolean isSubscribed(); // method to check if the user is subscribed
    void listenBook(Book book); // method for the user to listen to a book
}

// Concrete implementation of User for Standard users
class StandardUser implements User {
    private final String username;
    private boolean subscribed;

    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    public StandardUser(String username) { // setting name of user and initially marking him as unsubscribed
        this.username = username;
        this.subscribed = false;
    }

    @Override
    public void subscribe() {
        this.subscribed = true;
    }
    @Override
    public void unsubscribe() {
        this.subscribed = false;
    }

    @Override
    public void notify(String message) { // notifying about price update
        System.out.println(username + " notified about " + message);
    }

    @Override
    public boolean isPremium() { // mentioning that standard user is not premium
        return false;
    }

    public void readBook(Book book) { // printing result of reading
        System.out.println(username + " reading " + book.getTitle() + " by " + book.getAuthor());
    }

    public void listenBook(Book book) { // standard user can not listen
        System.out.println("You should update to premium to listen books");
    }
}

// Concrete implementation of User for Premium users
class PremiumUser implements User {
    private final String username;
    private boolean subscribed;

    public PremiumUser(String username) { // setting name of user and initially marking him as unsubscribed
        this.username = username;
        this.subscribed = false;
    }

    @Override
    public boolean isSubscribed() {
        return subscribed;
    }

    @Override
    public void subscribe() {
        this.subscribed = true;
    }
    @Override
    public void unsubscribe() {
        this.subscribed = false;
    }

    @Override
    public void notify(String message) { // notifying about price update
        System.out.println(username + " notified about " + message);
    }

    @Override
    public boolean isPremium() {  // mentioning that premium user has status premium
        return true;
    }

    public void readBook(Book book) { // printing result of reading
        System.out.println(username + " reading " + book.getTitle() + " by " + book.getAuthor());
    }

    public void listenBook(Book book) { // printing result of listening
        System.out.println(username + " listening " + book.getTitle() + " by " + book.getAuthor());
    }
}

// Factory class for creating different types of users
class UserFactory {
    public static User createUser(String userType, String username) {
        if (userType.equals("standard")) {
            return new StandardUser(username);
        } else if (userType.equals("premium")) {
            return new PremiumUser(username);
        }
        return null;
    }
}

// Proxy implementation of User for controlling book access
class BookProxy implements User {
    private final User user;
    public BookProxy(User user) {
        this.user = user;
    }
    @Override
    public void subscribe() {
        user.subscribe();
    }
    @Override
    public void unsubscribe() {
        user.unsubscribe();
    }
    @Override
    public void notify(String message) {
        user.notify(message);
    }

    @Override
    public boolean isPremium() {
        return user.isPremium();
    }
    @Override
    public void readBook(Book book) {
        user.readBook(book);
    }

    @Override
    public boolean isSubscribed() {
        return user.isSubscribed();
    }

    @Override
    public void listenBook(Book book) {
        if (user.isPremium()) {
            user.listenBook(book);
        } else {
            System.out.println("No access");
        }
    }
}


// Subscribers class to manage observers/subscribers and notify them of updates
class Subscribers {
    private final List<User> observers = new ArrayList<>();

    public void sub(User user) { // adding subscribed user to the subscribers list
        observers.add(user);
    }

    public void unsub(User user) { // removing subscribed user from subscribers list
        observers.remove(user);
    }

    public void notifyObservers(String message) { // notifying subscribers about price update
        for (User observer : observers) {
            observer.notify(message);
        }
    }
}
