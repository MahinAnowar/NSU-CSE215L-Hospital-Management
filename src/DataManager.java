import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


public class DataManager {

    private static final String DATA_FILE = "hospital_data.txt";
    private List<String> locations;
    private List<Hospital> hospitals;
    private List<Booking> bookings;
    private List<Transaction> transactions;
    private final Object fileLock = new Object();

    public DataManager() {
        this.locations = new ArrayList<>();
        this.hospitals = new ArrayList<>();
        this.bookings = new ArrayList<>();
        this.transactions = new ArrayList<>();
        loadData();
    }

    // Getters
    public synchronized List<String> getLocations() {
        return new ArrayList<>(locations);
    }

    public synchronized List<Hospital> getHospitals() {
        return new ArrayList<>(hospitals);
    }

    public synchronized List<Hospital> getHospitalsByLocation(String location) {
        return hospitals.stream()
                .filter(h -> h.getLocation().equalsIgnoreCase(location))
                .collect(Collectors.toList());
    }

    public synchronized Optional<Hospital> getHospitalByNameAndLocation(String name, String location) {
        return hospitals.stream()
                .filter(h -> h.getName().equalsIgnoreCase(name) && h.getLocation().equalsIgnoreCase(location))
                .findFirst();
    }

    public synchronized List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public synchronized List<Transaction> getTransactions() {
        return new ArrayList<>(transactions);
    }


    public synchronized void addHospital(Hospital hospital) {
        if (!getHospitalByNameAndLocation(hospital.getName(), hospital.getLocation()).isPresent()) {
            this.hospitals.add(hospital);
            saveData();
        } else {
            System.err.println("Hospital already exists: " + hospital.getName() + " at " + hospital.getLocation());
        }
    }

    public synchronized void removeHospital(Hospital hospitalToRemove) {
        boolean removed = this.hospitals.removeIf(h -> h.getName().equalsIgnoreCase(hospitalToRemove.getName())
                && h.getLocation().equalsIgnoreCase(hospitalToRemove.getLocation()));
        if (removed) {
            saveData();
        } else {
            System.err.println("Could not find hospital to remove: " + hospitalToRemove.getName() + " at " + hospitalToRemove.getLocation());
        }
    }

    public synchronized void updateHospital(String originalName, String originalLocation, Hospital updatedHospital) {
        int indexToUpdate = -1;
        for (int i = 0; i < hospitals.size(); i++) {
            Hospital current = hospitals.get(i);
            if (current.getName().equalsIgnoreCase(originalName) && current.getLocation().equalsIgnoreCase(originalLocation)) {
                indexToUpdate = i;
                break;
            }
        }

        if (indexToUpdate != -1) {
            final int finalIndexToUpdate = indexToUpdate;
            boolean nameConflict = hospitals.stream()
                    .filter(h -> h.getLocation().equalsIgnoreCase(updatedHospital.getLocation()))
                    .filter(h -> h != hospitals.get(finalIndexToUpdate))
                    .anyMatch(h -> h.getName().equalsIgnoreCase(updatedHospital.getName()));

            if (nameConflict) {
                System.err.println("Update failed: Another hospital with the name '" + updatedHospital.getName() + "' already exists in " + updatedHospital.getLocation());
                return;
            }

            hospitals.set(indexToUpdate, updatedHospital);
            saveData();
        } else {
            System.err.println("Could not find hospital to update with original name/location: " + originalName + " / " + originalLocation);
        }
    }


    public synchronized void addBooking(Booking booking) {
        this.bookings.add(booking);
        saveData();
    }

    public synchronized void clearBooking(Booking bookingToClear) {
        Optional<Hospital> hospitalOpt = getHospitalByNameAndLocation(bookingToClear.getHospitalName(), bookingToClear.getLocation());

        if (hospitalOpt.isPresent()) {
            Hospital hospital = hospitalOpt.get();
            switch (bookingToClear.getServiceType()) {
                case "Room":
                    hospital.returnRoom(bookingToClear.getServiceIdentifier());
                    break;
                case "Ambulance":
                    hospital.returnAmbulance(bookingToClear.getServiceIdentifier());
                    break;
                case "Doctor":
                    hospital.returnDoctor(bookingToClear.getServiceIdentifier());
                    break;
                default:
                    System.err.println("Unknown service type in booking to clear: " + bookingToClear.getServiceType());
            }

            int hospitalIndex = -1;
            for(int i=0; i<this.hospitals.size(); i++) {
                if(this.hospitals.get(i).getName().equals(hospital.getName()) && this.hospitals.get(i).getLocation().equals(hospital.getLocation())) {
                    hospitalIndex = i;
                    break;
                }
            }


        } else {
            System.err.println("Cannot restore service for booking: Hospital not found - " + bookingToClear.getHospitalName() + " (" + bookingToClear.getLocation() + ")");
        }


        boolean removed = this.bookings.removeIf(b ->
                b.getHospitalName().equals(bookingToClear.getHospitalName()) &&
                        b.getLocation().equals(bookingToClear.getLocation()) &&
                        b.getServiceType().equals(bookingToClear.getServiceType()) &&
                        b.getServiceIdentifier().equals(bookingToClear.getServiceIdentifier()) &&
                        b.getUserName().equals(bookingToClear.getUserName()) &&
                        b.getUserPhone().equals(bookingToClear.getUserPhone())
        );

        if (removed) {
            System.out.println("Booking cleared successfully.");
            saveData();
        } else {
            System.err.println("Could not find the exact booking to remove from the list.");
        }
    }

    public synchronized void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        saveData();
    }

    // File Handling
    private void initializeDefaultLocations() {
        locations.clear();
        locations.add("Barishal");
        locations.add("Chattogram");
        locations.add("Dhaka");
        locations.add("Khulna");
        locations.add("Mymensingh");
        locations.add("Rajshahi");
        locations.add("Rangpur");
        locations.add("Sylhet");
    }

    private String getValue(String line, String key) {
        if (line.startsWith(key + ":")) {
            return line.substring(key.length() + 1).trim();
        }
        return null;
    }


    private List<String> parseCommaSeparatedList(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>(); // Return empty list, not null
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }


    private synchronized void loadData() {
        synchronized (fileLock) {
            File file = new File(DATA_FILE);
            if (!file.exists()) {
                System.out.println("Data file not found (" + DATA_FILE + "). Initializing default locations and creating file.");
                initializeDefaultLocations();
                saveData();
                return;
            }

            this.locations.clear();
            this.hospitals.clear();
            this.bookings.clear();
            this.transactions.clear();

            String currentSection = "";
            Hospital currentHospital = null;
            String bkHospitalName = null, bkLocation = null, bkServiceType = null, bkServiceId = null, bkUserName = null, bkUserPhone = null;
            String trHospitalName = null, trLocation = null, trUserName = null, trUserPhone = null, trMedName = null;
            int trQuantity = 0;
            double trTotalPrice = 0.0;

            try (BufferedReader reader = new BufferedReader(new FileReader(DATA_FILE))) {
                String line;
                int lineNumber = 0;

                while ((line = reader.readLine()) != null) {
                    lineNumber++;
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("//")) continue;



                    if(line.equals("#LOCATIONS_START")) { currentSection = "LOCATIONS"; continue; }
                    if(line.equals("#LOCATIONS_END")) { currentSection = ""; continue; }
                    if(line.equals("#HOSPITALS_START")) { currentSection = "HOSPITALS"; continue; }
                    if(line.equals("#HOSPITALS_END")) { currentSection = ""; currentHospital = null; continue; }
                    if(line.equals("#HOSPITAL")) { currentHospital = null; continue; } // Reset for new hospital block
                    if(line.equals("#END_HOSPITAL")) {
                        if (currentHospital != null && currentHospital.getName() != null && currentHospital.getLocation() != null) {
                            hospitals.add(currentHospital);
                            System.out.println("Loaded Hospital: " + currentHospital.getName());
                        } else System.err.println("Skipping incomplete hospital near line " + lineNumber);
                        currentHospital = null; continue;
                    }
                    if(line.equals("#BOOKINGS_START")) { currentSection = "BOOKINGS"; continue; }
                    if(line.equals("#BOOKINGS_END")) { currentSection = ""; continue; }
                    if(line.equals("#BOOKING")) {
                        bkHospitalName = bkLocation = bkServiceType = bkServiceId = bkUserName = bkUserPhone = null;
                        continue;
                    }
                    if(line.equals("#END_BOOKING")) {
                        if (bkHospitalName != null && bkLocation != null && bkServiceType != null &&
                                bkServiceId != null && bkUserName != null && bkUserPhone != null) {
                            bookings.add(new Booking(bkHospitalName, bkLocation, bkServiceType, bkServiceId, bkUserName, bkUserPhone));
                            System.out.println("Loaded Booking for: " + bkUserName);
                        } else System.err.println("Skipping incomplete booking near line " + lineNumber);
                        bkHospitalName = bkLocation = bkServiceType = bkServiceId = bkUserName = bkUserPhone = null;
                        continue;
                    }
                    if(line.equals("#TRANSACTIONS_START")) { currentSection = "TRANSACTIONS"; continue; }
                    if(line.equals("#TRANSACTIONS_END")) { currentSection = ""; continue; }
                    if(line.equals("#TRANSACTION")) {
                        trHospitalName = trLocation = trUserName = trUserPhone = trMedName = null;
                        trQuantity = 0; trTotalPrice = 0.0;
                        continue;
                    }
                    if(line.equals("#END_TRANSACTION")) {
                        if (trHospitalName != null && trLocation != null && trUserName != null && trUserPhone != null &&
                                trMedName != null && trQuantity > 0 && trTotalPrice >= 0) {
                            transactions.add(new Transaction(trHospitalName, trLocation, trUserName, trUserPhone, trMedName, trQuantity, trTotalPrice));
                            System.out.println("Loaded Transaction for: " + trUserName);
                        } else System.err.println("Skipping incomplete transaction near line " + lineNumber);
                        trHospitalName = trLocation = trUserName = trUserPhone = trMedName = null;
                        trQuantity = 0; trTotalPrice = 0.0;
                        continue;
                    }


                    try {
                        switch (currentSection) {
                            case "LOCATIONS":
                                locations.add(line);
                                break;

                            case "HOSPITALS":
                                if (currentHospital == null) {
                                    String loc = getValue(line, "Location");
                                    if (loc != null) {
                                        String nameLine = reader.readLine();
                                        lineNumber++;
                                        if (nameLine != null) {
                                            String name = getValue(nameLine.trim(), "Name");
                                            if(name != null) currentHospital = new Hospital(loc, name);
                                            else System.err.println("HOSPITALS: Missing Name after Location near line " + lineNumber);
                                        } else System.err.println("HOSPITALS: Unexpected EOF after Location near line " + lineNumber);
                                    } else {
                                        System.err.println("HOSPITALS: Expecting 'Location:' line for new hospital near line " + lineNumber);
                                    }
                                } else {
                                    String val;
                                    if((val = getValue(line, "Rooms")) != null) currentHospital.setAvailableRooms(parseCommaSeparatedList(val));
                                    else if((val = getValue(line, "Ambulances")) != null) currentHospital.setAvailableAmbulances(parseCommaSeparatedList(val));
                                    else if((val = getValue(line, "Doctors")) != null) currentHospital.setDoctors(parseCommaSeparatedList(val));
                                    else if((val = getValue(line, "Pharmacy")) != null) {
                                        List<Medicine> meds = new ArrayList<>();
                                        for (String medStr : val.split(",")) {
                                            if (medStr.trim().isEmpty()) continue;
                                            Medicine med = Medicine.fromFileString(medStr.trim());
                                            if (med != null) meds.add(med);
                                            else System.err.println("HOSPITALS: Skipping invalid medicine string: " + medStr);
                                        }
                                        currentHospital.setPharmacyStock(meds);
                                    }

                                }
                                break;

                            case "BOOKINGS":
                                if (bkHospitalName == null) bkHospitalName = getValue(line, "HospitalName");
                                else if (bkLocation == null) bkLocation = getValue(line, "Location");
                                else if (bkServiceType == null) bkServiceType = getValue(line, "ServiceType");
                                else if (bkServiceId == null) bkServiceId = getValue(line, "ServiceIdentifier");
                                else if (bkUserName == null) bkUserName = getValue(line, "UserName");
                                else if (bkUserPhone == null) bkUserPhone = getValue(line, "UserPhone");
                                break;

                            case "TRANSACTIONS":
                                String val;
                                if (trHospitalName == null) trHospitalName = getValue(line, "HospitalName");
                                else if (trLocation == null) trLocation = getValue(line, "Location");
                                else if (trUserName == null) trUserName = getValue(line, "UserName");
                                else if (trUserPhone == null) trUserPhone = getValue(line, "UserPhone");
                                else if (trMedName == null) trMedName = getValue(line, "MedicineName");
                                else if ((val = getValue(line, "Quantity")) != null) try {trQuantity = Integer.parseInt(val);} catch(NumberFormatException e) { System.err.println("Invalid Quantity format near line " + lineNumber); }
                                else if ((val = getValue(line, "TotalPrice")) != null) try {trTotalPrice = Double.parseDouble(val);} catch(NumberFormatException e) { System.err.println("Invalid TotalPrice format near line " + lineNumber); }
                                break;
                        }
                    } catch (Exception e) {
                        System.err.println("Error processing line " + lineNumber + ": [" + line + "] - " + e.getMessage());
                    }
                }

            } catch (IOException e) {
                System.err.println("Error reading data file (" + DATA_FILE + "): " + e.getMessage());
                initializeDefaultLocations();
            } catch (Exception e) {
                System.err.println("An unexpected error occurred during data loading: " + e.getMessage());
                e.printStackTrace();
                initializeDefaultLocations();
            }

            if (this.locations.isEmpty()) {
                System.out.println("Locations list empty after load, initializing defaults.");
                initializeDefaultLocations();
            } else {
                System.out.println("Data loaded. Locations: " + locations.size() + ", Hospitals: " + hospitals.size() + ", Bookings: " + bookings.size() + ", Transactions: " + transactions.size());
            }
        }
    }


    private synchronized void saveData() {
        synchronized (fileLock) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {

                writer.write("#LOCATIONS_START"); writer.newLine();
                for (String location : locations) { writer.write(location); writer.newLine(); }
                writer.write("#LOCATIONS_END"); writer.newLine(); writer.newLine();

                writer.write("#HOSPITALS_START"); writer.newLine();
                for (Hospital hospital : hospitals) {
                    for(String line : hospital.toFileLines()) { writer.write(line); writer.newLine(); }
                }
                writer.write("#HOSPITALS_END"); writer.newLine(); writer.newLine();

                writer.write("#BOOKINGS_START"); writer.newLine();
                for (Booking booking : bookings) {
                    for(String line : booking.toFileLines()) { writer.write(line); writer.newLine(); }
                }
                writer.write("#BOOKINGS_END"); writer.newLine(); writer.newLine();

                writer.write("#TRANSACTIONS_START"); writer.newLine();
                for (Transaction transaction : transactions) {
                    for(String line : transaction.toFileLines()) { writer.write(line); writer.newLine(); }
                }
                writer.write("#TRANSACTIONS_END"); writer.newLine();

                System.out.println("Data saved successfully to " + DATA_FILE);

            } catch (IOException e) {
                System.err.println("Error writing data to file (" + DATA_FILE + "): " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e){
                System.err.println("An unexpected error occurred during data saving: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}