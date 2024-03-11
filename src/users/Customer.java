package users;

public class Customer extends User {
    public String name;
    public String email;
    public String password;
    public String dob;

    public Customer(int userID, String name, String email, String password, String dob) {
        super(userID);
        this.name = name;
        this.email = email;
        this.password = password;
        this.dob = dob;
    }

    public Member makeMember() {
        return new Member(this.userID, this.name, this.email, this.password, this.dob, 0);
    }
}