package database;

import java.sql.Date;

public class Card {
    private Date expDate;
    private int cardNumber;

    Card(int cardNumber, Date expDate) {
        this.cardNumber = cardNumber;
        this.expDate = expDate;

    }
}
