package users;

public class Member extends Customer {
    public int points;

    public Member(int userID, String name, String email, String password, String dob, int points) {
        super(userID, name, email, password, dob);
        this.points = points;
    }

}
