# Hospital Management System (Java Swing)

![Java](https://img.shields.io/badge/Java-1.8%2B-blue?style=flat&logo=openjdk) ![Platform](https://img.shields.io/badge/Platform-Desktop-lightgrey?style=flat-square) ![UI](https://img.shields.io/badge/UI-Swing-orange)

A simple desktop application for managing basic hospital services, developed as a course project for CSE215L (Java Programming Lab) at North South University.

## Overview

This application provides a graphical user interface (GUI) built with Java Swing for interacting with a simulated hospital management system. It features separate workflows for standard **Users** and system **Administrators**. Data is persisted locally in a custom-formatted text file (`hospital_data.txt`).

## Features

**User Features:**

*   Browse hospitals based on location (8 predefined divisions of Bangladesh).
*   View available services for a selected hospital:
    *   List available Rooms.
    *   List available Ambulances.
    *   List Doctors (Name - Department).
*   Book an available Room, Ambulance, or Doctor appointment by providing name and phone number.
*   Access the hospital's Pharmacy:
    *   View available medicines with price and quantity.
    *   Search medicines by name (live filtering).
    *   Purchase medicines by selecting, entering quantity, name, and phone number.

**Admin Features:**

*   **Secure Login:** Password protected access (`123`).
*   **Hospital Management:**
    *   Add new hospitals (specify location, name, rooms, ambulances, doctors, pharmacy stock).
    *   Remove existing hospitals.
    *   Edit details of existing hospitals (name, rooms, ambulances, doctors, medicine price/quantity).
*   **Booking Management:** View all active bookings and clear them (makes the service available again).
*   **Transaction History:** View a log of all pharmacy purchases made by users.

## Technologies Used

*   **Core Language:** Java (JDK 8 or higher recommended)
*   **GUI:** Java Swing
*   **Data Storage:** Plain Text File (`hospital_data.txt`) with custom delimiters (`#`, `:`, `,`)
*   **File I/O:** `java.io` package (`BufferedReader`, `BufferedWriter`, `FileReader`, `FileWriter`, `File`)
*   **Collections:** `java.util.List`, `ArrayList`, `Vector` (for `JComboBox`), `Optional`
*   **OOP Concepts:** Inheritance (`BasePanel`), Polymorphism (method overriding), Encapsulation
*   **Exception Handling:** `try-catch` blocks for `IOException`, `NumberFormatException`, etc.
*   **Concurrency:** `SwingUtilities.invokeLater` for EDT safety, `synchronized` methods in `DataManager`.
*   **Layout Managers:** `GridBagLayout`, `GridLayout`, `BoxLayout`
*   **Look and Feel:** Set to System's native Look and Feel for better visual integration.

## Getting Started / Running the Application

### Prerequisites

*   Java Runtime Environment (JRE) or Java Development Kit (JDK) version 8 or later installed on your system. You can download it from [Oracle](https://www.oracle.com/java/technologies/javase-downloads.html) or use distributions like OpenJDK.

### Option 1: Running the JAR File (Recommended for Users)

1.  Go to the [**Releases**](https://github.com/MahinAnowar/NSU-CSE215L-Hospital-Management/releases) page of this repository.
2.  Download the latest `Hospital-Management-System.jar` file.
3.  Place the downloaded `.jar` file in a dedicated folder on your computer.
4.  **Important:** The application needs to read and write its data file (`hospital_data.txt`). This file will be automatically created **in the same directory where you run the `.jar` file from** if it doesn't exist.
5.  Open a terminal or command prompt **in that same directory**.
6.  Run the application using the command:
    ```bash
    java -jar Hospital-Management-System.jar
    ```
    (Replace `Hospital-Management-System.jar` with the actual name of the downloaded JAR file).
7.  The application window should appear.

### Option 2: Running from Source Code

1.  Clone this repository: `git clone https://github.com/MahinAnowar/NSU-CSE215L-Hospital-Management.git`
2.  Open the project in a Java IDE (like IntelliJ IDEA, Eclipse).
3.  Ensure you have a compatible JDK configured in the IDE.
4.  Locate and run the `Main.java` file.
5.  The `hospital_data.txt` file will be created/read in the **project's root directory**.

## Data File (`hospital_data.txt`)

*   This file stores all application data.
*   It is created automatically in the directory where the JAR is run (or project root if run from source).
*   You can pre-populate it with data (like the example provided in the project development) before running the application for the first time. *Deleting this file will reset all data.*

## Usage

1.  **Launch:** Start the application using one of the methods above.
2.  **Role Selection:** Choose "User" or "Admin".
3.  **User Flow:**
    *   Click "Book a Service".
    *   Select a location.
    *   Select a hospital.
    *   Choose a service (Book Room, Book Ambulance, Appoint Doctor, Pharmacy).
    *   Follow the on-screen instructions to select items, enter details (name/phone/quantity), and book/purchase.
4.  **Admin Flow:**
    *   Enter the password (`123`).
    *   Use the buttons on the Admin Panel to Add/Remove/Edit Hospitals, Clear Bookings, or View Transaction History.
    *   Use the "Logout" button to return to the main screen.

## Project Structure

All `.java` source files are located directly within the `src` directory (default package structure). The core components include:

*   `Main.java`: Application entry point.
*   `DataManager.java`: Handles data loading, saving, and access logic.
*   Model Classes (`Hospital.java`, `Medicine.java`, `Booking.java`, `Transaction.java`): Define data structures.
*   GUI Panel Classes (`BasePanel.java`, `MainPanel.java`, `UserPanel.java`, `AdminLoginPanel.java`, etc.): Define the different screens/views.
*   `UIUtils.java`: Helper methods for consistent GUI elements.



## Author

*   **Mahin Anowar** - North South University (NSU)
    *   *Developed as a project for the CSE215L (Java Programming Lab) course.*

---
