import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class Hospital implements Serializable {
    private String location;
    private String name;
    private List<String> availableRooms;
    private List<String> availableAmbulances;
    private List<String> doctors;
    private List<Medicine> pharmacyStock;

    public Hospital(String location, String name) {
        this.location = location;
        this.name = name;
        this.availableRooms = new ArrayList<>();
        this.availableAmbulances = new ArrayList<>();
        this.doctors = new ArrayList<>();
        this.pharmacyStock = new ArrayList<>();
    }

    // Getters
    public String getLocation() { return location; }
    public String getName() { return name; }
    public List<String> getAvailableRooms() { return new ArrayList<>(availableRooms); } // Return copy
    public List<String> getAvailableAmbulances() { return new ArrayList<>(availableAmbulances); } // Return copy
    public List<String> getDoctors() { return new ArrayList<>(doctors); } // Return copy
    public List<Medicine> getPharmacyStock() { return new ArrayList<>(pharmacyStock); } // Return copy

    // Setters/Modifiers
    public void setName(String name) { this.name = name; }
    public void setAvailableRooms(List<String> availableRooms) { this.availableRooms = new ArrayList<>(availableRooms); }
    public void setAvailableAmbulances(List<String> availableAmbulances) { this.availableAmbulances = new ArrayList<>(availableAmbulances); }
    public void setDoctors(List<String> doctors) { this.doctors = new ArrayList<>(doctors); }
    public void setPharmacyStock(List<Medicine> pharmacyStock) { this.pharmacyStock = new ArrayList<>(pharmacyStock); }


    // Business Logic
    public boolean bookRoom(String room) { return availableRooms.remove(room); }
    public void returnRoom(String room) { if (room != null && !availableRooms.contains(room)) availableRooms.add(room); }

    public boolean bookAmbulance(String ambulance) { return availableAmbulances.remove(ambulance); }
    public void returnAmbulance(String ambulance) { if (ambulance != null && !availableAmbulances.contains(ambulance)) availableAmbulances.add(ambulance); }

    public boolean bookDoctor(String doctor) { return doctors.remove(doctor); }
    public void returnDoctor(String doctor) { if (doctor != null && !doctors.contains(doctor)) doctors.add(doctor); }

    public Medicine findMedicine(String name) {
        for (Medicine med : pharmacyStock) {
            if (med.getName().equalsIgnoreCase(name)) {
                return med;
            }
        }
        return null;
    }

    public boolean purchaseMedicine(String name, int quantity) {
        Medicine med = findMedicine(name);
        if (med != null && med.getQuantity() >= quantity) {
            med.setQuantity(med.getQuantity() - quantity);

            return true;
        }
        return false;
    }

    // Admin modifications
    public void addRoom(String room) { if (room != null && !room.trim().isEmpty() && !availableRooms.contains(room)) availableRooms.add(room); }
    public void removeRoom(String room) { availableRooms.remove(room); }

    public void addAmbulance(String ambulance) { if (ambulance != null && !ambulance.trim().isEmpty() && !availableAmbulances.contains(ambulance)) availableAmbulances.add(ambulance); }
    public void removeAmbulance(String ambulance) { availableAmbulances.remove(ambulance); }

    public void addDoctor(String doctor) { if (doctor != null && !doctor.trim().isEmpty() && !doctors.contains(doctor)) doctors.add(doctor); }
    public void removeDoctor(String doctor) { doctors.remove(doctor); }

    public void addOrUpdateMedicine(Medicine medicine) {
        Medicine existing = findMedicine(medicine.getName());
        if (existing != null) {
            existing.setPrice(medicine.getPrice());
            existing.setQuantity(medicine.getQuantity());
        } else {
            pharmacyStock.add(medicine);
        }
    }

    public void removeMedicine(String medicineName) {
        pharmacyStock.removeIf(med -> med.getName().equalsIgnoreCase(medicineName));
    }


    @Override
    public String toString() {

        return name + " (" + location + ")";
    }

    // file representation
    public List<String> toFileLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#HOSPITAL");
        lines.add("Location:" + location);
        lines.add("Name:" + name);
        lines.add("Rooms:" + (availableRooms.isEmpty() ? "" : String.join(",", availableRooms)));
        lines.add("Ambulances:" + (availableAmbulances.isEmpty() ? "" : String.join(",", availableAmbulances)));
        lines.add("Doctors:" + (doctors.isEmpty() ? "" : String.join(",", doctors)));
        lines.add("Pharmacy:" + (pharmacyStock.isEmpty() ? "" : pharmacyStock.stream().map(Medicine::toFileString).collect(Collectors.joining(","))));
        lines.add("#END_HOSPITAL");
        return lines;
    }


    public void addService(String serviceId, String serviceType) {
        switch(serviceType.toLowerCase()) {
            case "room": addRoom(serviceId); break;
            case "ambulance": addAmbulance(serviceId); break;
            case "doctor": addDoctor(serviceId); break;
            default: System.err.println("Unknown service type to add: " + serviceType);
        }
    }

    public void removeService(String serviceId, String serviceType) {
        switch(serviceType.toLowerCase()) {
            case "room": removeRoom(serviceId); break;
            case "ambulance": removeAmbulance(serviceId); break;
            case "doctor": removeDoctor(serviceId); break;
            default: System.err.println("Unknown service type to remove: " + serviceType);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Hospital hospital = (Hospital) o;
        return Objects.equals(location, hospital.location) &&
                Objects.equals(name, hospital.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location, name);
    }
}