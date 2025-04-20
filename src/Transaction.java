import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transaction implements Serializable {
    private String hospitalName;
    private String location;
    private String userName;
    private String userPhone;
    private String medicineName;
    private int quantity;
    private double totalPrice;

    public Transaction(String hospitalName, String location, String userName, String userPhone, String medicineName, int quantity, double totalPrice) {
        this.hospitalName = hospitalName;
        this.location = location;
        this.userName = userName;
        this.userPhone = userPhone;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters
    public String getHospitalName() { return hospitalName; }
    public String getLocation() { return location; }
    public String getUserName() { return userName; }
    public String getUserPhone() { return userPhone; }
    public String getMedicineName() { return medicineName; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        // Format: name - phone - medicine - quantity - price (Hospital, Location)
        return String.format("%s - %s - %s - %d - %.2f (Hospital: %s, %s)",
                userName, userPhone, medicineName, quantity, totalPrice, hospitalName, location);
    }

    public List<String> toFileLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#TRANSACTION");
        lines.add("HospitalName:" + hospitalName);
        lines.add("Location:" + location);
        lines.add("UserName:" + userName);
        lines.add("UserPhone:" + userPhone);
        lines.add("MedicineName:" + medicineName);
        lines.add("Quantity:" + quantity);
        lines.add("TotalPrice:" + totalPrice);
        lines.add("#END_TRANSACTION");
        return lines;
    }
}