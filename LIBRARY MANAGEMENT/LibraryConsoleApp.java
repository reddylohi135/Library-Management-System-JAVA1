import java.util.Scanner;

public class LibraryConsoleApp {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		Library library = new Library();
		User currentUser = null;
		int choice;
		
		// Check if running in non-interactive mode
		if (System.console() == null) {
			System.out.println("Console application requires interactive input.");
			System.out.println("Please run this application in a terminal/command prompt.");
			System.out.println("Alternatively, you can run the GUI version with: java LibraryGUIApp");
			return;
		}
		
		do {
			System.out.println("===== Library Management System =====");
			System.out.println("Current user: " + (currentUser != null ? currentUser.getUserName() + " (ID: " + currentUser.getUserId() + ")" : "None"));
			System.out.println("1. Register");
			System.out.println("2. Login");
			System.out.println("3. Add Book");
			System.out.println("4. Display Books");
			System.out.println("5. Issue Book");
			System.out.println("6. Return Book");
			System.out.println("7. Search Book");
			System.out.println("8. Remove Book");
			System.out.println("9. List Books by Status");
			System.out.println("10. List Books by Author");
			System.out.println("11. List Books by Genre");
			System.out.println("12. Exit");
			System.out.println("13. List Overdue Books");
			System.out.println("14. Reserve Book");
			System.out.print("Enter your choice: ");
			
			try {
				choice = scanner.nextInt();
				scanner.nextLine(); // consume newline
			} catch (java.util.InputMismatchException e) {
				System.out.println("Invalid input. Please enter a number.");
				scanner.nextLine(); // clear invalid input
				choice = 0; // continue loop
				continue;
			} catch (java.util.NoSuchElementException e) {
				System.out.println("No input available. Exiting...");
				break;
			}
			
			switch (choice) {
				case 1: // Register
					System.out.print("Enter new user ID: ");
					int regId = scanner.nextInt();
					scanner.nextLine();
					if (library.findUserById(regId) != null) {
						System.out.println("User ID already exists!");
						break;
					}
					System.out.print("Enter new user name: ");
					String regName = scanner.nextLine();
					System.out.print("Create password: ");
					String regPassword = scanner.nextLine();
					User newUser = new User(regId, regName, "user", regPassword);
					library.addUser(newUser);
					System.out.println("Registration successful! You can now log in.");
					break;
				case 2: // Login
					System.out.print("Enter user ID: ");
					int loginId = scanner.nextInt();
					scanner.nextLine();
					User foundUser = library.findUserById(loginId);
					if (foundUser == null) {
						System.out.println("User not found. Please register first.");
						break;
					}
					System.out.print("Enter password: ");
					String loginPassword = scanner.nextLine();
					if (foundUser.getPassword() != null && foundUser.getPassword().equals(loginPassword)) {
						currentUser = foundUser;
						System.out.println("Login successful! Welcome, " + currentUser.getUserName());
					} else {
						System.out.println("Invalid password.");
					}
					break;
				case 3:
					library.addBook(scanner);
					break;
				case 4:
					library.displayBooks();
					break;
				case 5:
					if (currentUser == null) {
						System.out.println("Please log in to issue a book.");
						break;
					}
					library.issueBook(scanner);
					break;
				case 6:
					if (currentUser == null) {
						System.out.println("Please log in to return a book.");
						break;
					}
					library.returnBook(scanner);
					break;
				case 7:
					System.out.println("Search by: 1. ID  2. Name  3. Author  4. Genre");
					int searchChoice = scanner.nextInt();
					scanner.nextLine();
					switch (searchChoice) {
						case 1:
							System.out.print("Enter Book ID: ");
							int id = scanner.nextInt();
							scanner.nextLine();
							Book bookById = library.searchBookById(id);
							if (bookById != null) System.out.println(bookById);
							else System.out.println("Book not found!");
							break;
						case 2:
							System.out.print("Enter Book Name: ");
							String name = scanner.nextLine();
							var booksByName = library.searchBooksByName(name);
							if (!booksByName.isEmpty()) booksByName.forEach(System.out::println);
							else System.out.println("No books found!");
							break;
						case 3:
							System.out.print("Enter Author Name: ");
							String author = scanner.nextLine();
							var booksByAuthor = library.searchBooksByAuthor(author);
							if (!booksByAuthor.isEmpty()) booksByAuthor.forEach(System.out::println);
							else System.out.println("No books found!");
							break;
						case 4:
							System.out.print("Enter Genre: ");
							String genre = scanner.nextLine();
							var booksByGenre = library.searchBooksByGenre(genre);
							if (!booksByGenre.isEmpty()) booksByGenre.forEach(System.out::println);
							else System.out.println("No books found!");
							break;
						default:
							System.out.println("Invalid search option!");
					}
					break;
				case 8:
					System.out.print("Enter Book ID to remove: ");
					int removeId = scanner.nextInt();
					scanner.nextLine();
					boolean removed = library.removeBookById(removeId);
					if (removed) System.out.println("Book removed successfully!");
					else System.out.println("Book not found!");
					break;
				case 9:
					System.out.print("Enter status (Available/Issued): ");
					String status = scanner.nextLine();
					library.listBooksByStatus(status);
					break;
				case 10:
					System.out.print("Enter author name: ");
					String listAuthor = scanner.nextLine();
					library.listBooksByAuthor(listAuthor);
					break;
				case 11:
					System.out.print("Enter genre: ");
					String listGenre = scanner.nextLine();
					library.listBooksByGenre(listGenre);
					break;
				case 12:
					System.out.println("Exiting the system. Goodbye!");
					break;
				case 13:
					library.listOverdueBooks();
					break;
				case 14:
					if (currentUser == null) {
						System.out.println("Please log in to reserve a book.");
						break;
					}
					library.reserveBook(scanner);
					break;
				default:
					System.out.println("Invalid choice! Please try again.\n");
			}
		} while (choice != 12);
		library.saveToFile();
		scanner.close();
	}
} 