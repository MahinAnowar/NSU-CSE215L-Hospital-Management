import java.io.Serializable;
import java.util.Objects;


public class Medicine implements Serializable {
    private String name;
    private double price;
    private int quantity;

    public Medicine(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    @Override
    public String toString() {

        return name + " - Price: " + String.format("%.2f", price) + " (Qty: " + quantity + ")";
    }


    public String toFileString() {
        return name + ":" + price + ":" + quantity;
    }


    public static Medicine fromFileString(String fileString) {
        if (fileString == null || fileString.trim().isEmpty()) return null;
        try {
            String[] parts = fileString.trim().split(":");
            if (parts.length == 3) {
                String name = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                int quantity = Integer.parseInt(parts[2].trim());
                if (!name.isEmpty() && price >= 0 && quantity >= 0) { // Basic validation
                    return new Medicine(name, price, quantity);
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing medicine from string: '" + fileString + "' - " + e.getMessage());
        }
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Medicine medicine = (Medicine) o;
        return Objects.equals(name, medicine.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}