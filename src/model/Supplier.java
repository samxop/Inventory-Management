package model;

public class Supplier {
    private int supplierId;
    private String name;
    private String contactNumber;
    private String email;

    public Supplier() {}

    public Supplier(int supplierId, String name, String contactNumber, String email) {
        this.supplierId = supplierId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.email = email;
    }

    // getters and setters
    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContactNumber() { return contactNumber; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return name + " (ID: " + supplierId + ")";
    }
}
