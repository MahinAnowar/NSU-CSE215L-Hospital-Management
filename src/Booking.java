import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class Booking implements Serializable {
    private String hospitalName;
    private String location;
    private String serviceType;
    private String serviceIdentifier;
    private String userName;
    private String userPhone;

    public Booking(String hospitalName, String location, String serviceType, String serviceIdentifier, String userName, String userPhone) {
        this.hospitalName = hospitalName;
        this.location = location;
        this.serviceType = serviceType;
        this.serviceIdentifier = serviceIdentifier;
        this.userName = userName;
        this.userPhone = userPhone;
    }


    public String getHospitalName() { return hospitalName; }
    public String getLocation() { return location; }
    public String getServiceType() { return serviceType; }
    public String getServiceIdentifier() { return serviceIdentifier; }
    public String getUserName() { return userName; }
    public String getUserPhone() { return userPhone; }

    @Override
    public String toString() {
        return String.format("User: %s (%s), Hospital: %s (%s), Service: %s (%s)",
                userName, userPhone, hospitalName, location, serviceType, serviceIdentifier);
    }


    public List<String> toFileLines() {
        List<String> lines = new ArrayList<>();
        lines.add("#BOOKING");
        lines.add("HospitalName:" + hospitalName);
        lines.add("Location:" + location);
        lines.add("ServiceType:" + serviceType);
        lines.add("ServiceIdentifier:" + serviceIdentifier);
        lines.add("UserName:" + userName);
        lines.add("UserPhone:" + userPhone);
        lines.add("#END_BOOKING");
        return lines;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(hospitalName, booking.hospitalName) &&
                Objects.equals(location, booking.location) &&
                Objects.equals(serviceType, booking.serviceType) &&
                Objects.equals(serviceIdentifier, booking.serviceIdentifier) &&
                Objects.equals(userName, booking.userName) &&
                Objects.equals(userPhone, booking.userPhone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hospitalName, location, serviceType, serviceIdentifier, userName, userPhone);
    }
}