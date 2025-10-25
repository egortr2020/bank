package bank;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;


public class Account {
    private final String owner;
    private final String number;
    private double balance;
    private final List<Transaction> transactions = new ArrayList<>();
    private final AtomicLong seq = new AtomicLong(1);

    public Account(String owner, String number) {
        this.owner = owner;
        this.number = number;
        this.balance = 0.0;
    }

    public String getOwner() {
        return owner;
    }

    public String getNumber() {
        return number;
    }

    public double getBalance() {
        return balance;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void deposit(double amount, String note) {
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");
        balance += amount;
        add(TransactionType.DEPOSIT, amount, note);
    }

    public boolean withdraw(double amount, String note) {
        if (amount <= 0) throw new IllegalArgumentException("Сумма должна быть > 0");
        if (amount > balance) return false;
        balance -= amount;
        add(TransactionType.WITHDRAW, amount, note);
        return true;
    }

    private void add(TransactionType type, double amount, String note) {
        transactions.add(new Transaction(
                seq.getAndIncrement(),
                type,
                amount,
                LocalDateTime.now(),
                note
        ));
    }
    public java.util.List<Transaction> search(
            TransactionType type,
            java.lang.Double minAmount,
            java.lang.Double maxAmount,
            java.time.LocalDate fromDate,
            java.time.LocalDate toDate,
            java.lang.String noteSubstring) {

        return transactions.stream()
                .filter(t -> type == null || t.getType() == type)
                .filter(t -> minAmount == null || t.getAmount() >= minAmount)
                .filter(t -> maxAmount == null || t.getAmount() <= maxAmount)
                .filter(t -> {
                    if (fromDate == null && toDate == null) return true;
                    java.time.LocalDate d = t.getTimestamp().toLocalDate();
                    boolean ge = fromDate == null || !d.isBefore(fromDate);
                    boolean le = toDate == null || !d.isAfter(toDate);
                    return ge && le;
                })
                .filter(t -> noteSubstring == null
                        || t.getNote().toLowerCase().contains(noteSubstring.toLowerCase()))
                .collect(java.util.stream.Collectors.toList());
    }
}