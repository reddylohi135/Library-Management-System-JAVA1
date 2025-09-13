import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LibraryGUIApp {
    private JFrame frame;
    private JTextField userIdField;
    private JTextField userNameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel messageLabel;
    private Library library;
    private User loggedInUser;
    private boolean darkMode = false;

    public LibraryGUIApp() {
        System.out.println("LibraryGUIApp constructor called");
        library = new Library();
        frame = new JFrame("Library Login/Register");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

		JLabel userIdLabel = new JLabel("User ID:");
		gbc.gridx = 0; gbc.gridy = 0;
		frame.add(userIdLabel, gbc);
		userIdField = new JTextField();
		gbc.gridx = 1; gbc.gridy = 0;
		frame.add(userIdField, gbc);

		JLabel passwordLabel = new JLabel("Password:");
		gbc.gridx = 0; gbc.gridy = 1;
		frame.add(passwordLabel, gbc);
		passwordField = new JPasswordField();
		gbc.gridx = 1; gbc.gridy = 1;
		frame.add(passwordField, gbc);

		loginButton = new JButton("Login");
		registerButton = new JButton("Register");
		gbc.gridx = 0; gbc.gridy = 2;
		frame.add(loginButton, gbc);
		gbc.gridx = 1; gbc.gridy = 2;
		frame.add(registerButton, gbc);

		messageLabel = new JLabel("");
		gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
		frame.add(messageLabel, gbc);

        loginButton.addActionListener(e -> handleLogin());
        registerButton.addActionListener(e -> handleRegister());

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void handleLogin() {
        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                messageLabel.setText("Password cannot be empty.");
                return;
            }
            User user = library.findUserById(userId);
            if (user != null && user.getPassword() != null && user.getPassword().equals(password)) {
                messageLabel.setText("Login successful! Welcome, " + user.getUserName());
                loggedInUser = user;
                SwingUtilities.invokeLater(() -> {
                    showMainMenu();
                    showOverdueNotificationForUser(loggedInUser);
                });
                frame.setVisible(false);
            } else {
                messageLabel.setText("Invalid ID or password.");
            }
        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid User ID.");
        }
    }

    private void handleRegister() {
        try {
            String idInput = JOptionPane.showInputDialog(frame, "Enter new User ID:");
            if (idInput == null) return;
            int userId = Integer.parseInt(idInput.trim());
            if (library.findUserById(userId) != null) {
                messageLabel.setText("User ID already exists.");
                return;
            }
            String userName = JOptionPane.showInputDialog(frame, "Enter user name:");
            if (userName == null || userName.trim().isEmpty()) {
                messageLabel.setText("User name cannot be empty.");
                return;
            }
            String password = new String(passwordField.getPassword());
            if (password.isEmpty()) {
                messageLabel.setText("Password cannot be empty.");
                return;
            }
            String role = "user";
            int adminOption = JOptionPane.showConfirmDialog(frame, "Register as admin? (Requires code)", "Admin Registration", JOptionPane.YES_NO_OPTION);
            if (adminOption == JOptionPane.YES_OPTION) {
                String code = JOptionPane.showInputDialog(frame, "Enter admin code:");
                if (code != null && code.equals("ADMIN2024")) {
                    role = "admin";
                } else {
                    messageLabel.setText("Invalid admin code. Registering as regular user.");
                }
            }
            User newUser = new User(userId, userName.trim(), role, password);
            library.addUser(newUser);
            messageLabel.setText("Registration successful as " + role + "! You can now log in.");
        } catch (NumberFormatException ex) {
            messageLabel.setText("Invalid User ID.");
        }
    }

    private void showMainMenu() {
        JFrame menuFrame = new JFrame("Library Main Menu");
        menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        menuFrame.setSize(400, 400);
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
            menuFrame.setExtendedState(JFrame.NORMAL);
            menuFrame.setSize(900, 600);
        }
        menuFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel welcomeLabel = new JLabel("Welcome, " + loggedInUser.getUserName() + " (ID: " + loggedInUser.getUserId() + ", Role: " + loggedInUser.getRole() + ")");
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        menuFrame.add(welcomeLabel, gbc);

        JButton listBooksBtn = new JButton("List All Books");
        JButton addBookBtn = new JButton("Add Book");
        JButton issueBookBtn = new JButton("Issue Book");
        JButton returnBookBtn = new JButton("Return Book");
        JButton reserveBookBtn = new JButton("Reserve Book");
        JButton overdueBtn = new JButton("List Overdue Books/Fines");
        JButton logoutBtn = new JButton("Logout");
        JButton removeBookBtn = new JButton("Remove Book");
        JButton viewUsersBtn = new JButton("View All Users");
        JButton reportsBtn = new JButton("Reports");
        JButton themeBtn = new JButton(darkMode ? "Light Mode" : "Dark Mode");
        JButton profileBtn = new JButton("Profile");
        JButton importExportBtn = new JButton("Import/Export");
        JButton advSearchBtn = new JButton("Advanced Search");
		JButton adjustLayoutBtn = new JButton("Adjust Layout");

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        menuFrame.add(listBooksBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        menuFrame.add(addBookBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        menuFrame.add(issueBookBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        menuFrame.add(returnBookBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        menuFrame.add(reserveBookBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        menuFrame.add(overdueBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        menuFrame.add(logoutBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 5;
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) menuFrame.add(removeBookBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) menuFrame.add(viewUsersBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 6;
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) menuFrame.add(reportsBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        menuFrame.add(themeBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 7;
        menuFrame.add(profileBtn, gbc);
        gbc.gridx = 1; gbc.gridy = 7;
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) menuFrame.add(importExportBtn, gbc);
        gbc.gridx = 0; gbc.gridy = 8;
        menuFrame.add(advSearchBtn, gbc);
		gbc.gridx = 0; gbc.gridy = 9;
		if ("admin".equalsIgnoreCase(loggedInUser.getRole())) menuFrame.add(adjustLayoutBtn, gbc);

        // Restrict Add Book to admins only
        if (!"admin".equalsIgnoreCase(loggedInUser.getRole())) {
            addBookBtn.setEnabled(false);
            addBookBtn.setToolTipText("Only admins can add books.");
        }

        themeBtn.addActionListener(e -> {
            darkMode = !darkMode;
            try {
                if (darkMode) {
                    for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                        if ("Nimbus".equals(info.getName())) {
                            UIManager.setLookAndFeel(info.getClassName());
                            break;
                        }
                    }
                } else {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                }
                SwingUtilities.updateComponentTreeUI(menuFrame);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(menuFrame, "Failed to change theme: " + ex.getMessage(), "Theme Error", JOptionPane.ERROR_MESSAGE);
            }
            menuFrame.dispose();
            showMainMenu();
        });

        profileBtn.addActionListener(e -> showProfileDialog(menuFrame));

        logoutBtn.addActionListener(e -> {
            menuFrame.dispose();
            frame.setVisible(true);
            loggedInUser = null;
        });

        listBooksBtn.addActionListener(e -> showListBooksWindow());
        addBookBtn.addActionListener(e -> {
            if (!"admin".equalsIgnoreCase(loggedInUser.getRole())) {
                JOptionPane.showMessageDialog(menuFrame, "Only admins can add books.", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
            showAddBookDialog();
        });
        issueBookBtn.addActionListener(e -> showIssueBookDialog());
        returnBookBtn.addActionListener(e -> showReturnBookDialog());
        reserveBookBtn.addActionListener(e -> showReserveBookDialog());
        overdueBtn.addActionListener(e -> showOverdueBooksDialog());
        removeBookBtn.addActionListener(e -> showRemoveBookDialog());
        viewUsersBtn.addActionListener(e -> showViewUsersDialog());
        reportsBtn.addActionListener(e -> showReportsDialog());
        importExportBtn.addActionListener(e -> showImportExportDialog());
        advSearchBtn.addActionListener(e -> showAdvancedSearchDialog());
		adjustLayoutBtn.addActionListener(e -> showAdjustLayoutDialog(menuFrame));

        listBooksBtn.setToolTipText("View all books in the library");
        addBookBtn.setToolTipText("Add a new book (admin only)");
        issueBookBtn.setToolTipText("Issue a book to yourself");
        returnBookBtn.setToolTipText("Return a book you have issued");
        reserveBookBtn.setToolTipText("Reserve a book that is currently issued");
        overdueBtn.setToolTipText("View all overdue books and fines");
        logoutBtn.setToolTipText("Log out of your account");
        removeBookBtn.setToolTipText("Remove a book from the library (admin only)");
        viewUsersBtn.setToolTipText("View all registered users (admin only)");
        reportsBtn.setToolTipText("View and export library reports (admin only)");
        themeBtn.setToolTipText("Toggle dark/light mode");
        profileBtn.setToolTipText("Edit your user profile");
        importExportBtn.setToolTipText("Import or export books and users (admin only)");
        advSearchBtn.setToolTipText("Search books by multiple fields");
        adjustLayoutBtn.setToolTipText("Adjust window size and position (admin only)");

        menuFrame.setLocationRelativeTo(null);
        menuFrame.setVisible(true);
    }

    private void showListBooksWindow() {
        JFrame booksFrame = new JFrame("All Books");
        booksFrame.setSize(900, 500);
        String[] columns = {"ID", "Name", "Author", "Year", "Genre", "Status", "Issued To", "Due Date", "Reservations"};
        java.util.List<Book> books = library.getBooks();
        String[][] data = new String[books.size()][columns.length + ("admin".equalsIgnoreCase(loggedInUser.getRole()) ? 1 : 0)];
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            data[i][0] = String.valueOf(b.getBookId());
            data[i][1] = b.getBookName();
            data[i][2] = b.getAuthor();
            data[i][3] = String.valueOf(b.getPublicationYear());
            data[i][4] = b.getGenre();
            data[i][5] = b.getStatus();
            data[i][6] = (b.getIssuedTo() != null) ? b.getIssuedTo().getUserName() + " (" + b.getIssuedTo().getUserId() + ")" : "";
            data[i][7] = (b.getDueDate() != null) ? b.getDueDate().toString() : "";
            data[i][8] = String.valueOf(b.getReservationQueue().size());
            if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
                data[i][9] = "Edit";
            }
        }
        String[] tableColumns = columns;
        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
            tableColumns = new String[columns.length + 1];
            System.arraycopy(columns, 0, tableColumns, 0, columns.length);
            tableColumns[columns.length] = "Action";
        }
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(data, tableColumns) {
            public boolean isCellEditable(int row, int column) {
                return "admin".equalsIgnoreCase(loggedInUser.getRole()) && column == getColumnCount() - 1;
            }
        };
        JTable table = new JTable(model);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);

        if ("admin".equalsIgnoreCase(loggedInUser.getRole())) {
            table.getColumn("Action").setCellRenderer(new javax.swing.table.DefaultTableCellRenderer() {
                public java.awt.Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    JButton button = new JButton("Edit");
                    return button;
                }
            });
            table.getColumn("Action").setCellEditor(new ButtonEditor(table, model, library, this));
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel searchLabel = new JLabel("Search: ");
        JTextField searchField = new JTextField();
        topPanel.add(searchLabel, BorderLayout.WEST);
        topPanel.add(searchField, BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            private void filter() {
                String text = searchField.getText().trim().toLowerCase();
                javax.swing.table.TableRowSorter<javax.swing.table.DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(model);
                table.setRowSorter(sorter);
                if (text.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("(?i)" + java.util.regex.Pattern.quote(text)));
                }
            }
        });

        booksFrame.add(topPanel, BorderLayout.NORTH);
        booksFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> booksFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        booksFrame.add(bottomPanel, BorderLayout.SOUTH);
        booksFrame.setLocationRelativeTo(null);
        booksFrame.setVisible(true);
    }

    public void showEditBookDialog(Book book, javax.swing.table.DefaultTableModel model, int row) {
        JFrame editFrame = new JFrame("Edit Book");
        editFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        editFrame.setSize(350, 300);
        editFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        editFrame.add(idLabel, gbc);
        JTextField idField = new JTextField(String.valueOf(book.getBookId()));
        idField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 0;
        editFrame.add(idField, gbc);

        JLabel nameLabel = new JLabel("Book Name:");
        gbc.gridx = 0; gbc.gridy = 1;
        editFrame.add(nameLabel, gbc);
        JTextField nameField = new JTextField(book.getBookName());
        gbc.gridx = 1; gbc.gridy = 1;
        editFrame.add(nameField, gbc);

        JLabel authorLabel = new JLabel("Author:");
        gbc.gridx = 0; gbc.gridy = 2;
        editFrame.add(authorLabel, gbc);
        JTextField authorField = new JTextField(book.getAuthor());
        gbc.gridx = 1; gbc.gridy = 2;
        editFrame.add(authorField, gbc);

        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 3;
        editFrame.add(yearLabel, gbc);
        JTextField yearField = new JTextField(String.valueOf(book.getPublicationYear()));
        gbc.gridx = 1; gbc.gridy = 3;
        editFrame.add(yearField, gbc);

        JLabel genreLabel = new JLabel("Genre:");
        gbc.gridx = 0; gbc.gridy = 4;
        editFrame.add(genreLabel, gbc);
        JTextField genreField = new JTextField(book.getGenre());
        gbc.gridx = 1; gbc.gridy = 4;
        editFrame.add(genreField, gbc);

        JButton saveBtn = new JButton("Save Changes");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        editFrame.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String author = authorField.getText().trim();
            String yearText = yearField.getText().trim();
            String genre = genreField.getText().trim();
            if (name.isEmpty() || author.isEmpty() || yearText.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(editFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int year = Integer.parseInt(yearText);
                book.setBookName(name);
                book.setAuthor(author);
                book.setPublicationYear(year);
                book.setGenre(genre);
                library.saveToFile();
                model.setValueAt(name, row, 1);
                model.setValueAt(author, row, 2);
                model.setValueAt(yearText, row, 3);
                model.setValueAt(genre, row, 4);
                JOptionPane.showMessageDialog(editFrame, "Book details updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
                editFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(editFrame, "Invalid year.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        editFrame.setLocationRelativeTo(null);
        editFrame.setVisible(true);
    }

    private void showAddBookDialog() {
        JFrame addBookFrame = new JFrame("Add New Book");
        addBookFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addBookFrame.setSize(350, 300);
        addBookFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        addBookFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addBookFrame.add(idField, gbc);

        JLabel nameLabel = new JLabel("Book Name:");
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        addBookFrame.add(nameLabel, gbc);
        JTextField nameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addBookFrame.add(nameField, gbc);

        JLabel authorLabel = new JLabel("Author:");
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        addBookFrame.add(authorLabel, gbc);
        JTextField authorField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addBookFrame.add(authorField, gbc);

        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        addBookFrame.add(yearLabel, gbc);
        JTextField yearField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addBookFrame.add(yearField, gbc);

        JLabel genreLabel = new JLabel("Genre:");
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        addBookFrame.add(genreLabel, gbc);
        JTextField genreField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        addBookFrame.add(genreField, gbc);

        JButton addBtn = new JButton("Add Book");
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        addBookFrame.add(addBtn, gbc);

        addBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            String bookName = nameField.getText().trim();
            String author = authorField.getText().trim();
            String yearText = yearField.getText().trim();
            String genre = genreField.getText().trim();
            if (idText.isEmpty() || bookName.isEmpty() || author.isEmpty() || yearText.isEmpty() || genre.isEmpty()) {
                JOptionPane.showMessageDialog(addBookFrame, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int bookId = Integer.parseInt(idText);
                int year = Integer.parseInt(yearText);
                if (library.searchBookById(bookId) != null) {
                    JOptionPane.showMessageDialog(addBookFrame, "Book with this ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Book newBook = new Book(bookId, bookName, author, year, genre);
                library.getBooks().add(newBook);
                library.saveToFile();
                JOptionPane.showMessageDialog(addBookFrame, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                addBookFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addBookFrame, "Invalid year or ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        addBookFrame.setLocationRelativeTo(null);
        addBookFrame.setVisible(true);
    }

    private void showIssueBookDialog() {
        JFrame issueFrame = new JFrame("Issue Book");
        issueFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        issueFrame.setSize(300, 180);
        issueFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        issueFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        issueFrame.add(idField, gbc);

        JButton issueBtn = new JButton("Issue Book");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        issueFrame.add(issueBtn, gbc);

        issueBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(issueFrame, "Book ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int bookId = Integer.parseInt(idText);
                Book book = library.searchBookById(bookId);
                if (book == null) {
                    JOptionPane.showMessageDialog(issueFrame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!book.getStatus().equals("Available")) {
                    JOptionPane.showMessageDialog(issueFrame, "Book is not available.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                book.setStatus("Issued");
                book.setIssuedTo(loggedInUser);
                book.setDueDate(java.time.LocalDate.now().plusDays(14));
                library.saveToFile();
                JOptionPane.showMessageDialog(issueFrame, "Book issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                issueFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(issueFrame, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        issueFrame.setLocationRelativeTo(null);
        issueFrame.setVisible(true);
    }

    private void showReturnBookDialog() {
        JFrame returnFrame = new JFrame("Return Book");
        returnFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        returnFrame.setSize(300, 180);
        returnFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        returnFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        returnFrame.add(idField, gbc);

        JButton returnBtn = new JButton("Return Book");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        returnFrame.add(returnBtn, gbc);

        returnBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(returnFrame, "Book ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int bookId = Integer.parseInt(idText);
                Book book = library.searchBookById(bookId);
                if (book == null) {
                    JOptionPane.showMessageDialog(returnFrame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (!book.getStatus().equals("Issued")) {
                    JOptionPane.showMessageDialog(returnFrame, "Book is not issued.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(returnFrame, "Are you sure you want to return this book?", "Confirm Return", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                int fine = book.calculateFine();
                book.setStatus("Available");
                book.setIssuedTo(null);
                book.setDueDate(null);
                library.saveToFile();
                String msg = "Book returned successfully!";
                if (fine > 0) msg += " Fine: " + fine + " units.";
                JOptionPane.showMessageDialog(returnFrame, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                returnFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(returnFrame, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        returnFrame.setLocationRelativeTo(null);
        returnFrame.setVisible(true);
    }

    private void showReserveBookDialog() {
        JFrame reserveFrame = new JFrame("Reserve Book");
        reserveFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reserveFrame.setSize(300, 180);
        reserveFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        reserveFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        reserveFrame.add(idField, gbc);

        JButton reserveBtn = new JButton("Reserve Book");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        reserveFrame.add(reserveBtn, gbc);

        reserveBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(reserveFrame, "Book ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int bookId = Integer.parseInt(idText);
                Book book = library.searchBookById(bookId);
                if (book == null) {
                    JOptionPane.showMessageDialog(reserveFrame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (book.getStatus().equals("Available")) {
                    JOptionPane.showMessageDialog(reserveFrame, "Book is available. You can issue it directly.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                if (book.getReservationQueue().stream().anyMatch(u -> u.getUserId() == loggedInUser.getUserId())) {
                    JOptionPane.showMessageDialog(reserveFrame, "You have already reserved this book.", "Info", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                book.addReservation(loggedInUser);
                library.saveToFile();
                JOptionPane.showMessageDialog(reserveFrame, "Book reserved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                reserveFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(reserveFrame, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        reserveFrame.setLocationRelativeTo(null);
        reserveFrame.setVisible(true);
    }

    private void showOverdueBooksDialog() {
        JFrame overdueFrame = new JFrame("Overdue Books/Fines");
        overdueFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        overdueFrame.setSize(700, 400);
        String[] columns = {"ID", "Name", "Issued To", "Due Date", "Fine"};
        java.util.List<Book> books = library.getBooks();
        java.util.List<Book> overdueBooks = new java.util.ArrayList<>();
        for (Book b : books) {
            if (b.getStatus().equals("Issued") && b.getDueDate() != null && b.getDueDate().isBefore(java.time.LocalDate.now())) {
                overdueBooks.add(b);
            }
        }
        String[][] data = new String[overdueBooks.size()][columns.length];
        for (int i = 0; i < overdueBooks.size(); i++) {
            Book b = overdueBooks.get(i);
            data[i][0] = String.valueOf(b.getBookId());
            data[i][1] = b.getBookName();
            data[i][2] = (b.getIssuedTo() != null) ? b.getIssuedTo().getUserName() + " (" + b.getIssuedTo().getUserId() + ")" : "";
            data[i][3] = (b.getDueDate() != null) ? b.getDueDate().toString() : "";
            data[i][4] = String.valueOf(b.calculateFine());
        }
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        overdueFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> overdueFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        overdueFrame.add(bottomPanel, BorderLayout.SOUTH);
        overdueFrame.setLocationRelativeTo(null);
        overdueFrame.setVisible(true);
    }

    private void showRemoveBookDialog() {
        JFrame removeFrame = new JFrame("Remove Book");
        removeFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        removeFrame.setSize(300, 150);
        removeFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        removeFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        removeFrame.add(idField, gbc);

        JButton removeBtn = new JButton("Remove Book");
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        removeFrame.add(removeBtn, gbc);

        removeBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(removeFrame, "Book ID cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int bookId = Integer.parseInt(idText);
                Book book = library.searchBookById(bookId);
                if (book == null) {
                    JOptionPane.showMessageDialog(removeFrame, "Book not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                int confirm = JOptionPane.showConfirmDialog(removeFrame, "Are you sure you want to remove this book?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) return;
                library.getBooks().remove(book);
                library.saveToFile();
                JOptionPane.showMessageDialog(removeFrame, "Book removed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                removeFrame.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(removeFrame, "Invalid ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeFrame.setLocationRelativeTo(null);
        removeFrame.setVisible(true);
    }

    private void showViewUsersDialog() {
        JFrame usersFrame = new JFrame("All Users");
        usersFrame.setSize(500, 300);
        String[] columns = {"User ID", "Name", "Role"};
        java.util.List<User> users = library.getUsers();
        String[][] data = new String[users.size()][columns.length];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i][0] = String.valueOf(u.getUserId());
            data[i][1] = u.getUserName();
            data[i][2] = u.getRole();
        }
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        usersFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> usersFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        usersFrame.add(bottomPanel, BorderLayout.SOUTH);
        usersFrame.setLocationRelativeTo(null);
        usersFrame.setVisible(true);
    }

    private void showReportsDialog() {
        String[] options = {"Most Issued Books", "Most Active Users", "Books Never Issued", "Export Reports to CSV"};
        String choice = (String) JOptionPane.showInputDialog(null, "Select a report:", "Reports", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;
        switch (choice) {
            case "Most Issued Books":
                showMostIssuedBooksReport();
                break;
            case "Most Active Users":
                showMostActiveUsersReport();
                break;
            case "Books Never Issued":
                showBooksNeverIssuedReport();
                break;
            case "Export Reports to CSV":
                exportReportsToCSV();
                break;
        }
    }

    private void showMostIssuedBooksReport() {
        java.util.Map<Book, Integer> issueCounts = library.getBookIssueCounts();
        java.util.List<java.util.Map.Entry<Book, Integer>> sorted = new java.util.ArrayList<>(issueCounts.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        String[] columns = {"ID", "Name", "Author", "Issue Count"};
        String[][] data = new String[sorted.size()][columns.length];
        for (int i = 0; i < sorted.size(); i++) {
            Book b = sorted.get(i).getKey();
            int count = sorted.get(i).getValue();
            data[i][0] = String.valueOf(b.getBookId());
            data[i][1] = b.getBookName();
            data[i][2] = b.getAuthor();
            data[i][3] = String.valueOf(count);
        }
        JFrame reportFrame = new JFrame("Most Issued Books");
        reportFrame.setSize(600, 400);
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        reportFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> reportFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        reportFrame.add(bottomPanel, BorderLayout.SOUTH);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setVisible(true);
    }

    private void showMostActiveUsersReport() {
        java.util.Map<User, Integer> userCounts = library.getUserIssueCounts();
        java.util.List<java.util.Map.Entry<User, Integer>> sorted = new java.util.ArrayList<>(userCounts.entrySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        String[] columns = {"User ID", "Name", "Role", "Issue Count"};
        String[][] data = new String[sorted.size()][columns.length];
        for (int i = 0; i < sorted.size(); i++) {
            User u = sorted.get(i).getKey();
            int count = sorted.get(i).getValue();
            data[i][0] = String.valueOf(u.getUserId());
            data[i][1] = u.getUserName();
            data[i][2] = u.getRole();
            data[i][3] = String.valueOf(count);
        }
        JFrame reportFrame = new JFrame("Most Active Users");
        reportFrame.setSize(600, 400);
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        reportFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> reportFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        reportFrame.add(bottomPanel, BorderLayout.SOUTH);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setVisible(true);
    }

    private void showBooksNeverIssuedReport() {
        java.util.Map<Book, Integer> issueCounts = library.getBookIssueCounts();
        java.util.List<Book> neverIssued = new java.util.ArrayList<>();
        for (java.util.Map.Entry<Book, Integer> entry : issueCounts.entrySet()) {
            if (entry.getValue() == 0) {
                neverIssued.add(entry.getKey());
            }
        }
        String[] columns = {"ID", "Name", "Author", "Year", "Genre"};
        String[][] data = new String[neverIssued.size()][columns.length];
        for (int i = 0; i < neverIssued.size(); i++) {
            Book b = neverIssued.get(i);
            data[i][0] = String.valueOf(b.getBookId());
            data[i][1] = b.getBookName();
            data[i][2] = b.getAuthor();
            data[i][3] = String.valueOf(b.getPublicationYear());
            data[i][4] = b.getGenre();
        }
        JFrame reportFrame = new JFrame("Books Never Issued");
        reportFrame.setSize(600, 400);
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        reportFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> reportFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        reportFrame.add(bottomPanel, BorderLayout.SOUTH);
        reportFrame.setLocationRelativeTo(null);
        reportFrame.setVisible(true);
    }

    private void exportReportsToCSV() {
        String[] options = {"Most Issued Books", "Most Active Users", "Books Never Issued"};
        String choice = (String) JOptionPane.showInputDialog(null, "Select a report to export:", "Export to CSV", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV File");
        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = fileChooser.getSelectedFile();
        try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
            if (choice.equals("Most Issued Books")) {
                writer.println("ID,Name,Author,Issue Count");
                java.util.Map<Book, Integer> issueCounts = library.getBookIssueCounts();
                java.util.List<java.util.Map.Entry<Book, Integer>> sorted = new java.util.ArrayList<>(issueCounts.entrySet());
                sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
                for (var entry : sorted) {
                    Book b = entry.getKey();
                    int count = entry.getValue();
                    writer.printf("%d,%s,%s,%d\n", b.getBookId(), b.getBookName(), b.getAuthor(), count);
                }
            } else if (choice.equals("Most Active Users")) {
                writer.println("User ID,Name,Role,Issue Count");
                java.util.Map<User, Integer> userCounts = library.getUserIssueCounts();
                java.util.List<java.util.Map.Entry<User, Integer>> sorted = new java.util.ArrayList<>(userCounts.entrySet());
                sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
                for (var entry : sorted) {
                    User u = entry.getKey();
                    int count = entry.getValue();
                    writer.printf("%d,%s,%s,%d\n", u.getUserId(), u.getUserName(), u.getRole(), count);
                }
            } else if (choice.equals("Books Never Issued")) {
                writer.println("ID,Name,Author,Year,Genre");
                java.util.Map<Book, Integer> issueCounts = library.getBookIssueCounts();
                for (var entry : issueCounts.entrySet()) {
                    if (entry.getValue() == 0) {
                        Book b = entry.getKey();
                        writer.printf("%d,%s,%s,%d,%s\n", b.getBookId(), b.getBookName(), b.getAuthor(), b.getPublicationYear(), b.getGenre());
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Report exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed to export report: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showProfileDialog(JFrame parentFrame) {
        JFrame profileFrame = new JFrame("Edit Profile");
        profileFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        profileFrame.setSize(300, 180);
        profileFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("User ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        profileFrame.add(idLabel, gbc);
        JTextField idField = new JTextField(String.valueOf(loggedInUser.getUserId()));
        idField.setEditable(false);
        gbc.gridx = 1; gbc.gridy = 0;
        profileFrame.add(idField, gbc);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0; gbc.gridy = 1;
        profileFrame.add(nameLabel, gbc);
        JTextField nameField = new JTextField(loggedInUser.getUserName());
        gbc.gridx = 1; gbc.gridy = 1;
        profileFrame.add(nameField, gbc);

        JButton saveBtn = new JButton("Save");
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        profileFrame.add(saveBtn, gbc);

        saveBtn.addActionListener(e -> {
            String newName = nameField.getText().trim();
            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(profileFrame, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loggedInUser.setUserName(newName);
            library.saveUsers();
            JOptionPane.showMessageDialog(profileFrame, "Profile updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
            profileFrame.dispose();
            parentFrame.dispose();
            showMainMenu();
        });

        profileFrame.setLocationRelativeTo(null);
        profileFrame.setVisible(true);
    }

    private void showOverdueNotificationForUser(User user) {
        java.util.List<Book> books = library.getBooks();
        java.util.List<Book> overdue = new java.util.ArrayList<>();
        for (Book b : books) {
            if (b.getStatus().equals("Issued") && b.getIssuedTo() != null && b.getIssuedTo().getUserId() == user.getUserId() && b.getDueDate() != null && b.getDueDate().isBefore(java.time.LocalDate.now())) {
                overdue.add(b);
            }
        }
        if (!overdue.isEmpty()) {
            StringBuilder msg = new StringBuilder("You have overdue books:\n\n");
            for (Book b : overdue) {
                msg.append("- ").append(b.getBookName()).append(" (Fine: ").append(b.calculateFine()).append(" units)\n");
            }
            JOptionPane.showMessageDialog(null, msg.toString(), "Overdue Books", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showImportExportDialog() {
        String[] options = {"Import Books", "Export Books", "Import Users", "Export Users"};
        String choice = (String) JOptionPane.showInputDialog(null, "Select an action:", "Import/Export", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (choice == null) return;
        JFileChooser fileChooser = new JFileChooser();
        if (choice.startsWith("Import")) fileChooser.setDialogTitle("Select CSV File to Import");
        else fileChooser.setDialogTitle("Save CSV File");
        int userSelection = choice.startsWith("Import") ? fileChooser.showOpenDialog(null) : fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) return;
        java.io.File file = fileChooser.getSelectedFile();
        try {
            if (choice.equals("Import Books")) {
                int imported = 0, skipped = 0;
                try (java.util.Scanner sc = new java.util.Scanner(file)) {
                    if (sc.hasNextLine()) sc.nextLine(); // skip header
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] parts = line.split(",");
                        if (parts.length >= 5) {
                            int id = Integer.parseInt(parts[0]);
                            String name = parts[1];
                            String author = parts[2];
                            int year = Integer.parseInt(parts[3]);
                            String genre = parts[4];
                            if (library.searchBookById(id) == null) {
                                Book b = new Book(id, name, author, year, genre);
                                library.getBooks().add(b);
                                imported++;
                            } else {
                                skipped++;
                            }
                        }
                    }
                }
                library.saveToFile();
                JOptionPane.showMessageDialog(null, "Books imported: " + imported + ", Skipped (duplicate ID): " + skipped, "Import Books", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Export Books")) {
                // Export all books to CSV
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println("ID,Name,Author,Year,Genre,Status,IssuedTo,DueDate,Reservations");
                    for (Book b : library.getBooks()) {
                        writer.printf("%d,%s,%s,%d,%s,%s,%s,%s,%d\n",
                            b.getBookId(), b.getBookName(), b.getAuthor(), b.getPublicationYear(), b.getGenre(), b.getStatus(),
                            (b.getIssuedTo() != null ? b.getIssuedTo().getUserName() : ""),
                            (b.getDueDate() != null ? b.getDueDate().toString() : ""),
                            b.getReservationQueue().size()
                        );
                    }
                }
                JOptionPane.showMessageDialog(null, "Books exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Import Users")) {
                int imported = 0, skipped = 0;
                try (java.util.Scanner sc = new java.util.Scanner(file)) {
                    if (sc.hasNextLine()) sc.nextLine(); // skip header
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine();
                        String[] parts = line.split(",");
                        if (parts.length >= 2) {
                            int id = Integer.parseInt(parts[0]);
                            String name = parts[1];
                            String role = (parts.length >= 3) ? parts[2] : "user";
                            if (library.findUserById(id) == null) {
                                User u = new User(id, name, role);
                                library.getUsers().add(u);
                                imported++;
                            } else {
                                skipped++;
                            }
                        }
                    }
                }
                library.saveUsers();
                JOptionPane.showMessageDialog(null, "Users imported: " + imported + ", Skipped (duplicate ID): " + skipped, "Import Users", JOptionPane.INFORMATION_MESSAGE);
            } else if (choice.equals("Export Users")) {
                // Export all users to CSV
                try (java.io.PrintWriter writer = new java.io.PrintWriter(file)) {
                    writer.println("UserID,Name,Role");
                    for (User u : library.getUsers()) {
                        writer.printf("%d,%s,%s\n", u.getUserId(), u.getUserName(), u.getRole());
                    }
                }
                JOptionPane.showMessageDialog(null, "Users exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAdvancedSearchDialog() {
        JFrame searchFrame = new JFrame("Advanced Book Search");
        searchFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        searchFrame.setSize(600, 300);
        searchFrame.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel idLabel = new JLabel("Book ID:");
        gbc.gridx = 0; gbc.gridy = 0;
        searchFrame.add(idLabel, gbc);
        JTextField idField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 0;
        searchFrame.add(idField, gbc);

        JLabel nameLabel = new JLabel("Name:");
        gbc.gridx = 0; gbc.gridy = 1;
        searchFrame.add(nameLabel, gbc);
        JTextField nameField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 1;
        searchFrame.add(nameField, gbc);

        JLabel authorLabel = new JLabel("Author:");
        gbc.gridx = 0; gbc.gridy = 2;
        searchFrame.add(authorLabel, gbc);
        JTextField authorField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 2;
        searchFrame.add(authorField, gbc);

        JLabel genreLabel = new JLabel("Genre:");
        gbc.gridx = 0; gbc.gridy = 3;
        searchFrame.add(genreLabel, gbc);
        JTextField genreField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 3;
        searchFrame.add(genreField, gbc);

        JLabel yearLabel = new JLabel("Year:");
        gbc.gridx = 0; gbc.gridy = 4;
        searchFrame.add(yearLabel, gbc);
        JTextField yearField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 4;
        searchFrame.add(yearField, gbc);

        JLabel statusLabel = new JLabel("Status:");
        gbc.gridx = 0; gbc.gridy = 5;
        searchFrame.add(statusLabel, gbc);
        JTextField statusField = new JTextField();
        gbc.gridx = 1; gbc.gridy = 5;
        searchFrame.add(statusField, gbc);

        JButton searchBtn = new JButton("Search");
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        searchFrame.add(searchBtn, gbc);

        searchBtn.addActionListener(e -> {
            String idText = idField.getText().trim();
            String name = nameField.getText().trim().toLowerCase();
            String author = authorField.getText().trim().toLowerCase();
            String genre = genreField.getText().trim().toLowerCase();
            String yearText = yearField.getText().trim();
            String status = statusField.getText().trim().toLowerCase();
            java.util.List<Book> results = new java.util.ArrayList<>();
            for (Book b : library.getBooks()) {
                boolean match = true;
                if (!idText.isEmpty()) {
                    try {
                        if (b.getBookId() != Integer.parseInt(idText)) match = false;
                    } catch (NumberFormatException ex) { match = false; }
                }
                if (!name.isEmpty() && !b.getBookName().toLowerCase().contains(name)) match = false;
                if (!author.isEmpty() && !b.getAuthor().toLowerCase().contains(author)) match = false;
                if (!genre.isEmpty() && !b.getGenre().toLowerCase().contains(genre)) match = false;
                if (!yearText.isEmpty()) {
                    try {
                        if (b.getPublicationYear() != Integer.parseInt(yearText)) match = false;
                    } catch (NumberFormatException ex) { match = false; }
                }
                if (!status.isEmpty() && !b.getStatus().toLowerCase().contains(status)) match = false;
                if (match) results.add(b);
            }
            showSearchResultsTable(results);
        });

        searchFrame.setLocationRelativeTo(null);
        searchFrame.setVisible(true);
    }

    private void showSearchResultsTable(java.util.List<Book> books) {
        JFrame resultsFrame = new JFrame("Search Results");
        resultsFrame.setSize(900, 400);
        String[] columns = {"ID", "Name", "Author", "Year", "Genre", "Status", "Issued To", "Due Date", "Reservations"};
        String[][] data = new String[books.size()][columns.length];
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            data[i][0] = String.valueOf(b.getBookId());
            data[i][1] = b.getBookName();
            data[i][2] = b.getAuthor();
            data[i][3] = String.valueOf(b.getPublicationYear());
            data[i][4] = b.getGenre();
            data[i][5] = b.getStatus();
            data[i][6] = (b.getIssuedTo() != null) ? b.getIssuedTo().getUserName() + " (" + b.getIssuedTo().getUserId() + ")" : "";
            data[i][7] = (b.getDueDate() != null) ? b.getDueDate().toString() : "";
            data[i][8] = String.valueOf(b.getReservationQueue().size());
        }
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
        resultsFrame.add(scrollPane, BorderLayout.CENTER);
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> resultsFrame.dispose());
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(closeBtn);
        resultsFrame.add(bottomPanel, BorderLayout.SOUTH);
        resultsFrame.setLocationRelativeTo(null);
        resultsFrame.setVisible(true);
    }

    private void showAdjustLayoutDialog(JFrame menuFrame) {
		String[] options = {"Maximize", "Restore", "Custom Size"};
		String choice = (String) JOptionPane.showInputDialog(menuFrame, "Adjust window layout:", "Adjust Layout", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (choice == null) return;
		switch (choice) {
			case "Maximize":
				menuFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				break;
			case "Restore":
				menuFrame.setExtendedState(JFrame.NORMAL);
				menuFrame.setSize(900, 600);
				menuFrame.setLocationRelativeTo(null);
				break;
			case "Custom Size":
				String widthStr = JOptionPane.showInputDialog(menuFrame, "Enter width (px):", String.valueOf(menuFrame.getWidth()));
				if (widthStr == null) return;
				String heightStr = JOptionPane.showInputDialog(menuFrame, "Enter height (px):", String.valueOf(menuFrame.getHeight()));
				if (heightStr == null) return;
				try {
					int w = Integer.parseInt(widthStr.trim());
					int h = Integer.parseInt(heightStr.trim());
					if (w < 400 || h < 300) {
						JOptionPane.showMessageDialog(menuFrame, "Minimum size is 400x300.", "Invalid Size", JOptionPane.WARNING_MESSAGE);
						return;
					}
					menuFrame.setExtendedState(JFrame.NORMAL);
					menuFrame.setSize(w, h);
					menuFrame.setLocationRelativeTo(null);
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(menuFrame, "Invalid width/height.", "Error", JOptionPane.ERROR_MESSAGE);
				}
				break;
		}
	}

    public static void main(String[] args) {
        System.out.println("Starting Library GUI App...");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            System.out.println("Uncaught exception in thread " + t + ": " + e);
            e.printStackTrace();
        });
        SwingUtilities.invokeLater(LibraryGUIApp::new);
    }
}

class ButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {
    private JButton button;
    private int editingRow;
    private JTable table;
    private javax.swing.table.DefaultTableModel model;
    private Library library;
    private LibraryGUIApp app;

    public ButtonEditor(JTable table, javax.swing.table.DefaultTableModel model, Library library, LibraryGUIApp app) {
        this.table = table;
        this.model = model;
        this.library = library;
        this.app = app;
        button = new JButton("Edit");
        button.addActionListener(e -> {
            editingRow = table.getEditingRow();
            if (editingRow >= 0) {
                int bookId = Integer.parseInt((String) model.getValueAt(editingRow, 0));
                Book book = library.searchBookById(bookId);
                if (book != null) {
                    app.showEditBookDialog(book, model, editingRow);
                }
            }
            fireEditingStopped();
        });
    }
    @Override
    public Object getCellEditorValue() { return "Edit"; }
    @Override
    public java.awt.Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        editingRow = row;
        return button;
    }
} 