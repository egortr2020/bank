package bank;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class    Transaction {
    private final long id;
    private final TransactionType type;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String note;

    public Transaction(long id, TransactionType type, double amount, LocalDateTime timestamp, String note) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.timestamp = timestamp;
        this.note = note == null ? "" : note;
    }

    public TransactionType getType() { return type; }
    public double getAmount() { return amount; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return String.format("#%d | %s | %,.2f | %s | %s",
                id, type, amount, fmt.format(timestamp), note);
    }
}
