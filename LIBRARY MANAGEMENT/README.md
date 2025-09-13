# Library Management System

## Features
- Book and user management (add, edit, remove, search, issue, return, reserve)
- Admin/user roles, reporting, import/export, overdue tracking, GUI with dark mode

## Requirements
- Java 8 or higher

## Compile
```
javac -d bin src/*.java
```

## Package as JAR
```
jar cfm LibraryApp.jar manifest.txt -C bin .
```

## Run
```
java -jar LibraryApp.jar
```

## Data Files
- Books, users, and other data are stored as CSV files in the project directory.
- You can import/export books and users via the GUI (admin only).

## Notes
- For best experience, run with Java 8+.
- All features are accessible via the GUI main menu. 