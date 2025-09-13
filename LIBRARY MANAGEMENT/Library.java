import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Library {
	private ArrayList<Book> books;
	private List<User> users = new ArrayList<>();
	private static final String FILE_NAME = "library_books.csv";
	private static final String USERS_FILE = "users.csv";
	private Map<Integer, Integer> bookIssueCounts = new HashMap<>();
	private Map<Integer, Integer> userIssueCounts = new HashMap<>();

	public Library() {
		books = new ArrayList<>();
		loadUsers();
		loadFromFile();
		loadBookIssueCounts();
	}

	public void addBook(Scanner scanner) {
		System.out.print("Enter Book ID: ");
		int id = scanner.nextInt();
		scanner.nextLine(); // consume newline
		System.out.print("Enter Book Name: ");
		String name = scanner.nextLine();
		System.out.print("Enter Author Name: ");
		String author = scanner.nextLine();
		System.out.print("Enter Publication Year: ");
		int year = scanner.nextInt();
		scanner.nextLine(); // consume newline
		System.out.print("Enter Genre: ");
		String genre = scanner.nextLine();
		books.add(new Book(id, name, author, year, genre));
		System.out.println("Book added successfully!\n");
	}

	public void addUser(User user) {
		users.add(user);
		saveUsers();
	}

	public User findUserById(int userId) {
		for (User user : users) {
			if (user.getUserId() == userId) {
				return user;
			}
		}
		return null;
	}

	public List<User> getUsers() {
		return users;
	}

	public java.util.List<Book> getBooks() {
		return books;
	}

	public void saveUsers() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(USERS_FILE))) {
			for (User user : users) {
				// Format: id,name,role,password (password may be empty)
				writer.printf("%d,%s,%s,%s\n", user.getUserId(), user.getUserName(), user.getRole(), escapeCsv(user.getPassword()));
			}
		} catch (IOException e) {
			System.out.println("Error saving users: " + e.getMessage());
		}
	}

	public void loadUsers() {
		users.clear();
		File file = new File(USERS_FILE);
		if (!file.exists()) return; // Just return if file is missing, do not exit or show dialog
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = parseCsvLine(line);
				// Support legacy formats:
				// 2 columns: id,name -> role=user, password=""
				// 3 columns: id,name,role -> password=""
				// 4+ columns: id,name,role,password
				if (parts.length >= 2) {
					int id = Integer.parseInt(parts[0]);
					String name = parts[1];
					String role = (parts.length >= 3 && parts[2] != null && !parts[2].isEmpty()) ? parts[2] : "user";
					String password = (parts.length >= 4) ? parts[3] : "";
					users.add(new User(id, name, role, password));
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading users: " + e.getMessage());
		}
	}

	public void displayBooks() {
		System.out.println("\n---------------------------------------------------------------------------------------------");
		System.out.println("ID\tName\t\tAuthor\t\tYear\tGenre\t\tStatus");
		System.out.println("---------------------------------------------------------------------------------------------");
		for (Book book : books) {
			System.out.printf("%d\t%-15s\t%-15s\t%d\t%-10s\t%s\n", book.getBookId(), book.getBookName(), book.getAuthor(), book.getPublicationYear(), book.getGenre(), book.getStatus());
		}
		System.out.println("---------------------------------------------------------------------------------------------\n");
	}

	public void issueBook(Scanner scanner) {
		System.out.print("Enter Book ID to issue: ");
		int id = scanner.nextInt();
		scanner.nextLine();
		boolean found = false;
		for (Book book : books) {
			if (book.getBookId() == id) {
				found = true;
				if (book.getStatus().equals("Available")) {
					System.out.print("Enter User ID: ");
					int userId = scanner.nextInt();
					scanner.nextLine();
					System.out.print("Enter User Name: ");
					String userName = scanner.nextLine();
					User user = new User(userId, userName);
					book.setStatus("Issued");
					book.setIssuedTo(user);
					book.setDueDate(LocalDate.now().plusDays(14));
					// Track issue count
					bookIssueCounts.put(book.getBookId(), bookIssueCounts.getOrDefault(book.getBookId(), 0) + 1);
					userIssueCounts.put(user.getUserId(), userIssueCounts.getOrDefault(user.getUserId(), 0) + 1);
					System.out.println("Book issued successfully to " + userName + "! Due date: " + book.getDueDate() + "\n");
				} else {
					System.out.println("Book is already issued!\n");
				}
				break;
			}
		}
		if (!found) {
			System.out.println("Book not found!\n");
		}
	}

	public void reserveBook(Scanner scanner) {
		System.out.print("Enter Book ID to reserve: ");
		int id = scanner.nextInt();
		scanner.nextLine();
		boolean found = false;
		for (Book book : books) {
			if (book.getBookId() == id) {
				found = true;
				if (book.getStatus().equals("Available")) {
					System.out.println("Book is available. You can issue it directly.");
				} else {
					System.out.print("Enter User ID: ");
					int userId = scanner.nextInt();
					scanner.nextLine();
					System.out.print("Enter User Name: ");
					String userName = scanner.nextLine();
					User user = new User(userId, userName);
					if (book.getReservationQueue().stream().anyMatch(u -> u.getUserId() == userId)) {
						System.out.println("You have already reserved this book.");
					} else {
						book.addReservation(user);
						System.out.println("Book reserved successfully!");
					}
				}
				break;
			}
		}
		if (!found) {
			System.out.println("Book not found!\n");
		}
	}

	public void returnBook(Scanner scanner) {
		System.out.print("Enter Book ID to return: ");
		int id = scanner.nextInt();
		boolean found = false;
		for (Book book : books) {
			if (book.getBookId() == id) {
				found = true;
				if (book.getStatus().equals("Issued")) {
					int fine = book.calculateFine();
					if (fine > 0) {
						System.out.println("Book is overdue! Fine: " + fine + " units.");
					}
					if (!book.getReservationQueue().isEmpty()) {
						User nextUser = book.pollNextReservation();
						book.setIssuedTo(nextUser);
						book.setDueDate(LocalDate.now().plusDays(14));
						System.out.println("Book returned and automatically issued to next user in reservation queue: " + nextUser.getUserName() + " (ID: " + nextUser.getUserId() + ")");
					} else {
						book.setStatus("Available");
						book.setIssuedTo(null);
						book.setDueDate(null);
						System.out.println("Book returned successfully!\n");
					}
				} else {
					System.out.println("Book was not issued!\n");
				}
				break;
			}
		}
		if (!found) {
			System.out.println("Book not found!\n");
		}
	}

	public Book searchBookById(int id) {
		for (Book book : books) {
			if (book.getBookId() == id) {
				return book;
			}
		}
		return null;
	}

	public ArrayList<Book> searchBooksByName(String name) {
		ArrayList<Book> result = new ArrayList<>();
		for (Book book : books) {
			if (book.getBookName().equalsIgnoreCase(name)) {
				result.add(book);
			}
		}
		return result;
	}

	public ArrayList<Book> searchBooksByAuthor(String author) {
		ArrayList<Book> result = new ArrayList<>();
		for (Book book : books) {
			if (book.getAuthor().equalsIgnoreCase(author)) {
				result.add(book);
			}
		}
		return result;
	}

	public ArrayList<Book> searchBooksByGenre(String genre) {
		ArrayList<Book> result = new ArrayList<>();
		for (Book book : books) {
			if (book.getGenre().equalsIgnoreCase(genre)) {
				result.add(book);
			}
		}
		return result;
	}

	public boolean removeBookById(int id) {
		for (Book book : books) {
			if (book.getBookId() == id) {
				books.remove(book);
				return true;
			}
		}
		return false;
	}

	public void listBooksByStatus(String status) {
		System.out.println("\nBooks with status: " + status);
		for (Book book : books) {
			if (book.getStatus().equalsIgnoreCase(status)) {
				System.out.println(book);
			}
		}
	}

	public void listBooksByAuthor(String author) {
		System.out.println("\nBooks by author: " + author);
		for (Book book : books) {
			if (book.getAuthor().equalsIgnoreCase(author)) {
				System.out.println(book);
			}
		}
	}

	public void listBooksByGenre(String genre) {
		System.out.println("\nBooks in genre: " + genre);
		for (Book book : books) {
			if (book.getGenre().equalsIgnoreCase(genre)) {
				System.out.println(book);
			}
		}
	}

	public void saveToFile() {
		try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
			DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
			for (Book book : books) {
				String userId = (book.getIssuedTo() != null) ? String.valueOf(book.getIssuedTo().getUserId()) : "";
				String userName = (book.getIssuedTo() != null) ? escapeCsv(book.getIssuedTo().getUserName()) : "";
				String dueDate = (book.getDueDate() != null) ? book.getDueDate().format(fmt) : "";
				// Reservation queue: userId1:userName1;userId2:userName2;...
				StringBuilder reservations = new StringBuilder();
				for (User u : book.getReservationQueue()) {
					if (reservations.length() > 0) reservations.append(";");
					reservations.append(u.getUserId()).append(":").append(escapeCsv(u.getUserName()));
				}
				writer.printf("%d,%s,%s,%d,%s,%s,%s,%s,%s,%s\n",
					book.getBookId(),
					escapeCsv(book.getBookName()),
					escapeCsv(book.getAuthor()),
					book.getPublicationYear(),
					escapeCsv(book.getGenre()),
					book.getStatus(),
					userId,
					userName,
					dueDate,
					reservations.toString()
				);
			}
		} catch (IOException e) {
			System.out.println("Error saving books: " + e.getMessage());
		}
		// Save issue counts
		try (PrintWriter writer = new PrintWriter(new FileWriter("book_issue_counts.csv"))) {
			for (Map.Entry<Integer, Integer> entry : bookIssueCounts.entrySet()) {
				writer.printf("%d,%d\n", entry.getKey(), entry.getValue());
			}
		} catch (IOException e) {
			System.out.println("Error saving book issue counts: " + e.getMessage());
		}
		// Save user issue counts
		try (PrintWriter writer = new PrintWriter(new FileWriter("user_issue_counts.csv"))) {
			for (Map.Entry<Integer, Integer> entry : userIssueCounts.entrySet()) {
				writer.printf("%d,%d\n", entry.getKey(), entry.getValue());
			}
		} catch (IOException e) {
			System.out.println("Error saving user issue counts: " + e.getMessage());
		}
	}

	public void loadFromFile() {
		books.clear();
		bookIssueCounts.clear();
		userIssueCounts.clear();
		File file = new File(FILE_NAME);
		if (!file.exists()) return; // Just return if file is missing, do not exit or show dialog
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			DateTimeFormatter fmt = DateTimeFormatter.ISO_LOCAL_DATE;
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = parseCsvLine(line);
				if (parts.length >= 6) {
					int id = Integer.parseInt(parts[0]);
					String name = parts[1];
					String author = parts[2];
					int year = Integer.parseInt(parts[3]);
					String genre = parts[4];
					String status = parts[5];
					Book book = new Book(id, name, author, year, genre);
					book.setStatus(status);
					if (parts.length >= 8 && !parts[6].isEmpty() && !parts[7].isEmpty()) {
						int userId = Integer.parseInt(parts[6]);
						String userName = parts[7];
						book.setIssuedTo(new User(userId, userName));
					}
					if (parts.length >= 9 && !parts[8].isEmpty()) {
						book.setDueDate(LocalDate.parse(parts[8], fmt));
					}
					if (parts.length >= 10 && !parts[9].isEmpty()) {
						String[] reservations = parts[9].split(";");
						for (String res : reservations) {
							if (!res.isEmpty()) {
								String[] userParts = res.split(":", 2);
								if (userParts.length == 2) {
									int resUserId = Integer.parseInt(userParts[0]);
									String resUserName = userParts[1];
									book.addReservation(new User(resUserId, resUserName));
								}
							}
						}
					}
					books.add(book);
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading books: " + e.getMessage());
		}
		loadBookIssueCounts();
		loadUserIssueCounts();
	}

	public void listOverdueBooks() {
		System.out.println("\nOverdue Books:");
		LocalDate today = LocalDate.now();
		boolean any = false;
		for (Book book : books) {
			if (book.getStatus().equals("Issued") && book.getDueDate() != null && book.getDueDate().isBefore(today)) {
				int fine = book.calculateFine();
				System.out.println(book + ", Fine: " + fine + " units");
				any = true;
			}
		}
		if (!any) {
			System.out.println("No overdue books.");
		}
	}

	public Map<Book, Integer> getBookIssueCounts() {
		Map<Book, Integer> result = new HashMap<>();
		for (Book book : books) {
			int count = bookIssueCounts.getOrDefault(book.getBookId(), 0);
			result.put(book, count);
		}
		return result;
	}

	public Map<User, Integer> getUserIssueCounts() {
		Map<User, Integer> result = new HashMap<>();
		for (User user : users) {
			int count = userIssueCounts.getOrDefault(user.getUserId(), 0);
			result.put(user, count);
		}
		return result;
	}

	// Helper to escape commas in CSV fields
	private String escapeCsv(String field) {
		if (field == null) return "";
		if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
			field = field.replace("\"", "\"\"");
			return '"' + field + '"';
		}
		return field;
	}

	// Helper to parse a CSV line (handles quoted fields)
	private String[] parseCsvLine(String line) {
		ArrayList<String> fields = new ArrayList<>();
		boolean inQuotes = false;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '"') {
				inQuotes = !inQuotes;
				if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					sb.append('"');
					i++;
				}
			} else if (c == ',' && !inQuotes) {
				fields.add(sb.toString());
				sb.setLength(0);
			} else {
				sb.append(c);
			}
		}
		fields.add(sb.toString());
		return fields.toArray(new String[0]);
	}

	public void loadBookIssueCounts() {
		bookIssueCounts.clear();
		File file = new File("book_issue_counts.csv");
		if (!file.exists()) return;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2) {
					int bookId = Integer.parseInt(parts[0]);
					int count = Integer.parseInt(parts[1]);
					bookIssueCounts.put(bookId, count);
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading book issue counts: " + e.getMessage());
		}
	}

	public void loadUserIssueCounts() {
		userIssueCounts.clear();
		File file = new File("user_issue_counts.csv");
		if (!file.exists()) return;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2) {
					int userId = Integer.parseInt(parts[0]);
					int count = Integer.parseInt(parts[1]);
					userIssueCounts.put(userId, count);
				}
			}
		} catch (IOException e) {
			System.out.println("Error loading user issue counts: " + e.getMessage());
		}
	}
} 