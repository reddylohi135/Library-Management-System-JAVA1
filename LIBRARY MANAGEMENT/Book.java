import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

public class Book {
    private int bookId;
    private String bookName;
    private String author;
    private String status; // "Available" or "Issued"
    private int publicationYear;
    private String genre;
    private User issuedTo;
    private LocalDate dueDate;
    private List<User> reservationQueue = new ArrayList<>();

    public Book(int bookId, String bookName, String author, int publicationYear, String genre) {
        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.status = "Available";
        this.publicationYear = publicationYear;
        this.genre = genre;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public User getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(User user) {
        this.issuedTo = user;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public List<User> getReservationQueue() {
        return reservationQueue;
    }

    public void addReservation(User user) {
        reservationQueue.add(user);
    }

    public void removeReservation(User user) {
        reservationQueue.remove(user);
    }

    public User pollNextReservation() {
        if (!reservationQueue.isEmpty()) {
            return reservationQueue.remove(0);
        }
        return null;
    }

    public int calculateFine() {
        if (status.equals("Issued") && dueDate != null && dueDate.isBefore(LocalDate.now())) {
            return 10 * (int) java.time.temporal.ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        }
        return 0;
    }

    @Override
    public String toString() {
        String userInfo = (status.equals("Issued") && issuedTo != null) ? (", Issued to: " + issuedTo.getUserName() + " (ID: " + issuedTo.getUserId() + ")") : "";
        String dueInfo = (status.equals("Issued") && dueDate != null) ? (", Due: " + dueDate.toString()) : "";
        String reservationInfo = reservationQueue.isEmpty() ? "" : (", Reservations: " + reservationQueue.size());
        return "Book ID: " + bookId + ", Name: " + bookName + ", Author: " + author + ", Year: " + publicationYear + ", Genre: " + genre + ", Status: " + status + userInfo + dueInfo + reservationInfo;
    }
} 