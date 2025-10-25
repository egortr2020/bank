package bank;

import java.time.LocalDate;
import java.util.*;

public class BankApp {
    private static final Scanner SC = new Scanner(System.in);

    private static final Map<String, Account> accounts = new LinkedHashMap<>();
    private static Account current;

    public static void main(String[] args) {
        while (true) {
            printHeader();
            printMenu();
            System.out.print("Выбор: ");
            String cmd = SC.nextLine().trim();
            switch (cmd) {
                case "1": openAccount(); break;
                case "2": selectAccount(); break;
                case "3": deposit(); break;
                case "4": withdraw(); break;
                case "5": showBalance(); break;
                case "6": listTransactions(); break;
                case "7": searchTransactions(); break;
                case "0": System.out.println("До свидания!"); return;
                default: System.out.println("Неизвестная команда.");
            }
            System.out.println();
        }
    }

    private static void printHeader() {
        System.out.println(" Аккаунтов: " + accounts.size() + (current == null ? "" : ", активен: " + current.getNumber() + " (" + current.getOwner() + ")"));
        System.out.println("==============================");
    }
    private static void printMenu() {
        System.out.println("1) Открыть счет");
        System.out.println("2) Выбрать активный счет");
        System.out.println("3) Положить деньги");
        System.out.println("4) Снять деньги");
        System.out.println("5) Показать баланс");
        System.out.println("6) Список транзакций");
        System.out.println("7) Искать по атрибутам");
        System.out.println("0) Выход");
    }

    private static void openAccount() {
        String owner = InputUtils.readNonEmpty(SC, "ФИО:");
        String number;
        while (true) {
            number = InputUtils.readNonEmpty(SC, "Номер счета:");
            if (!accounts.containsKey(number)) break;
            System.out.println("Номер уже существует.");
        }
        Account acc = new Account(owner, number);
        accounts.put(number, acc);
        current = acc;
        System.out.println("Счет открыт и активен");
    }

    private static void selectAccount() {
        if (accounts.isEmpty()) {
            System.out.println("Нет счетов.");
            return;
        }
        System.out.println("Доступные счета:");
        accounts.values().forEach(a -> System.out.printf("- %s (%s), баланс: %,.2f%n", a.getNumber(), a.getOwner(), a.getBalance()));
        String number = InputUtils.readNonEmpty(SC, "Введите номер счета:");
        Account acc = accounts.get(number);
        if (acc == null) {
            System.out.println("Счет не найден");
        } else {
            current = acc;
            System.out.println("Выбрали активный счет: " + number);
        }
    }
    private static void deposit() {
        if (ensureActive()) return;
        double amount = InputUtils.readPositiveDouble(SC, "Сумма пополнения: ");
        System.out.print("Примечание (необязательно): ");
        String note = SC.nextLine();
        current.deposit(amount, note); // пополнение всегда успешно при amount > 0
        System.out.println("Готово. Баланс: " + String.format("%,.2f", current.getBalance()));
    }

    private static void withdraw() {
        if (ensureActive()) return;
        double amount = InputUtils.readPositiveDouble(SC, "Сумма снятия: ");
        System.out.print("Примечание (необязательно): ");
        String note = SC.nextLine();
        boolean ok = current.withdraw(amount, note);
        if (!ok) {
            System.out.println("Недостаточно средств. Баланс: " + String.format("%,.2f", current.getBalance()));
        } else {
            System.out.println("Готово. Баланс: " + String.format("%,.2f", current.getBalance()));
        }
    }


    private static void showBalance() {
        if (ensureActive()) return;
        System.out.printf("Баланс счета %s (%s): %,.2f%n", current.getNumber(), current.getOwner(), current.getBalance());
    }

    private static void listTransactions() {
        if (ensureActive()) return;
        if (current.getTransactions().isEmpty()) {
            System.out.println("Транзакций пока нет.");
            return;
        }
        System.out.println("ID | TYPE | AMOUNT | DATE | NOTE");
        current.getTransactions().forEach(System.out::println);
    }

    private static void searchTransactions() {
        if (ensureActive()) return;
        System.out.println("Фильтры (Enter — пропустить)");
        System.out.print("Тип (DEPOSIT/WITHDRAW): ");
        String typeStr = SC.nextLine().trim();
        TransactionType type = null;
        if (!typeStr.isEmpty()) {
            try { type = TransactionType.valueOf(typeStr.toUpperCase()); } catch (IllegalArgumentException ignored) {}
        }

        System.out.print("Мин. сумма: ");
        String minStr = SC.nextLine().trim().replace(',', '.');
        Double min = minStr.isEmpty() ? null : parseDoubleOrNull(minStr);

        System.out.print("Макс. сумма: ");
        String maxStr = SC.nextLine().trim().replace(',', '.');
        Double max = maxStr.isEmpty() ? null : parseDoubleOrNull(maxStr);

        LocalDate from = InputUtils.readDateOrEmpty(SC, "С даты");
        LocalDate to = InputUtils.readDateOrEmpty(SC, "По дату");

        System.out.print("Текст в примечании: ");
        String note = SC.nextLine().trim();
        if (note.isEmpty()) note = null;

        List<Transaction> result = current.search(type, min, max, from, to, note);
        if (result.isEmpty()) {
            System.out.println("Ничего не найдено.");
            return;
        }
        System.out.println("Найдено: " + result.size());
        System.out.println("ID | TYPE | AMOUNT | DATE | NOTE");
        result.forEach(System.out::println);
    }

    private static boolean ensureActive() {
        if (current == null) {
            System.out.println("Активный счет не выбран");
            return true;
        }
        return false;
    }

    private static Double parseDoubleOrNull(String s) {
        try { return Double.parseDouble(s); } catch (NumberFormatException e) { return null; }
    }
}