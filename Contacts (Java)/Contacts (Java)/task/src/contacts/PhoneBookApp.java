import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Contact {
    private String name;
    private String address;
    private String number;
    private LocalDateTime timeCreated;
    private LocalDateTime timeLastEdit;

    public Contact(String name, String address, String number) {
        this.name = name;
        this.address = address;
        this.number = number;
        this.timeCreated = LocalDateTime.now();
        this.timeLastEdit = LocalDateTime.now();
    }

    // Getters and setters for fields

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.timeLastEdit = LocalDateTime.now();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.timeLastEdit = LocalDateTime.now();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
        this.timeLastEdit = LocalDateTime.now();
    }

    public LocalDateTime getTimeCreated() {
        return timeCreated;
    }

    public LocalDateTime getTimeLastEdit() {
        return timeLastEdit;
    }
}

class PhoneBook {
    private List<Contact> contacts;

    public PhoneBook() {
        this.contacts = new ArrayList<>();
    }

    public List<String> getAllFields() {
        // Return a list of all fields that can be changed
        List<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("address");
        fields.add("number");
        return fields;
    }

    public void changeField(String field, String value, Contact contact) {
        // Change the specified field in the contact
        switch (field) {
            case "name":
                contact.setName(value);
                break;
            case "address":
                contact.setAddress(value);
                break;
            case "number":
                contact.setNumber(value);
                break;
            default:
                System.out.println("Invalid field");
        }
    }

    public String getFieldValue(String field, Contact contact) {
        // Get the value of the specified field in the contact
        switch (field) {
            case "name":
                return contact.getName();
            case "address":
                return contact.getAddress();
            case "number":
                return contact.getNumber();
            default:
                return "Invalid field";
        }
    }

    public void saveToFile(String fileName) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(contacts);
            System.out.println("Data saved to " + fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            contacts = (List<Contact>) ois.readObject();
            System.out.println("Data loaded from " + fileName);
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("File not found or error loading data.");
        }
    }

    public List<Contact> searchContacts(String query) {
        // Search for contacts containing the query (case-insensitive)
        List<Contact> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(query, Pattern.CASE_INSENSITIVE);

        for (Contact contact : contacts) {
            StringBuilder searchString = new StringBuilder();
            searchString.append(contact.getName()).append(" ");
            searchString.append(contact.getAddress()).append(" ");
            searchString.append(contact.getNumber());

            Matcher matcher = pattern.matcher(searchString.toString());
            if (matcher.find()) {
                result.add(contact);
            }
        }
        return result;
    }
}

public class PhoneBookApp {
    private static PhoneBook phoneBook;

    public static void main(String[] args) {
        phoneBook = new PhoneBook();

        if (args.length > 0) {
            phoneBook.loadFromFile(args[0]);
            System.out.println("Loaded data from " + args[0]);
        }

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("[menu] Enter action (add, list, search, count, exit):");
            String action = scanner.nextLine();

            switch (action) {
                case "add":
                    addContact(scanner);
                    break;
                case "list":
                    listContacts();
                    break;
                case "search":
                    searchContacts(scanner);
                    break;
                case "count":
                    countContacts();
                    break;
                case "exit":
                    exitProgram(args);
                    break;
                default:
                    System.out.println("Invalid action. Please try again.");
            }
        }
    }

    private static void addContact(Scanner scanner) {
        System.out.println("Enter name:");
        String name = scanner.nextLine();

        System.out.println("Enter address:");
        String address = scanner.nextLine();

        System.out.println("Enter number:");
        String number = scanner.nextLine();

        Contact contact = new Contact(name, address, number);
        phoneBook.saveToFile("phonebook.db");
        System.out.println("Contact added:");
        printContact(contact);
    }

    private static void listContacts() {
        List<Contact> contacts = phoneBook.searchContacts("");
        System.out.println("Contacts:");
        for (int i = 0; i < contacts.size(); i++) {
            System.out.println((i + 1) + ". " + contacts.get(i).getName());
        }

        System.out.println("[list] Enter action ([number], back):");
        String action = new Scanner(System.in).nextLine();

        if ("back".equals(action)) {
            return;
        }

        try {
            int index = Integer.parseInt(action) - 1;
            if (index >= 0 && index < contacts.size()) {
                printContactDetails(contacts.get(index));
                recordActions(contacts.get(index));
            } else {
                System.out.println("Invalid number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void searchContacts(Scanner scanner) {
        System.out.println("Enter search query:");
        String query = scanner.nextLine();

        List<Contact> result = phoneBook.searchContacts(query);

        System.out.println("Found " + result.size() + " results:");
        for (int i = 0; i < result.size(); i++) {
            System.out.println((i + 1) + ". " + result.get(i).getName());
        }

        System.out.println("[search] Enter action ([number], back, again):");
        String action = scanner.nextLine();

        if ("back".equals(action)) {
            return;
        }

        if ("again".equals(action)) {
            searchContacts(scanner);
            return;
        }

        try {
            int index = Integer.parseInt(action) - 1;
            if (index >= 0 && index < result.size()) {
                printContactDetails(result.get(index));
                recordActions(result.get(index));
            } else {
                System.out.println("Invalid number. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please try again.");
        }
    }

    private static void countContacts() {
        List<Contact> contacts = phoneBook.searchContacts("");
        System.out.println("The Phone Book has " + contacts.size() + " records.");
    }

    private static void exitProgram(String[] args) {
        phoneBook.saveToFile(args.length > 0 ? args[0] : "phonebook.db");
        System.out.println("Exiting the program.");
        System.exit(0);
    }

    private static void printContact(Contact contact) {
        System.out.println("Name: " + contact.getName());
        System.out.println("Address: " + contact.getAddress());
        System.out.println("Number: " + contact.getNumber());
        System.out.println("Time created: " + formatTime(contact.getTimeCreated()));
        System.out.println("Time last edit: " + formatTime(contact.getTimeLastEdit()));
    }

    private static void printContactDetails(Contact contact) {
        System.out.println("Name: " + contact.getName());
        System.out.println("Surname: " + contact.getName());  // Note: Assuming "Surname" is the last name
        System.out.println("Birth date: [no data]");
        System.out.println("Gender: [no data]");
        System.out.println("Number: " + contact.getNumber());
        System.out.println("Time created: " + formatTime(contact.getTimeCreated()));
        System.out.println("Time last edit: " + formatTime(contact.getTimeLastEdit()));
    }

    private static void recordActions(Contact contact) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("[record] Enter action (edit, delete, menu):");
            String action = scanner.nextLine();

            switch (action) {
                case "edit":
                    editContact(contact, scanner);
                    break;
                case "delete":
                    // Implement delete functionality if needed
                    System.out.println("Delete functionality not implemented.");
                    break;
                case "menu":
                    return;
                default:
                    System.out.println("Invalid action. Please try again.");
            }
        }
    }

    private static void editContact(Contact contact, Scanner scanner) {
        System.out.println("Select a field (name, address, number):");
        String field = scanner.nextLine();

        System.out.println("Enter " + field + ":");
        String value = scanner.nextLine();

        phoneBook.changeField(field, value, contact);
        phoneBook.saveToFile("phonebook.db");
        System.out.println("Saved");
        printContact(contact);
    }

    private static String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return time.format(formatter);
    }
}
