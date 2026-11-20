# Task 1 – GUI Design Review

## 1.1 Deficiencies and user impact
1. The frame mixes UI, validation, payment and JDBC work; changes are difficult to test and maintain.
2. `==` compares String references; use `"Pay".equals(e.getActionCommand())`. The Pay branch may not run reliably.
3. Empty or non-numeric amount causes NumberFormatException; negative and zero amounts are not rejected.
4. JDBC and `Thread.sleep(3000)` run on the event-dispatch thread (EDT); the window freezes.
5. No success/failure dialog or status label informs the user.
6. Receipt button is treated like Clear; no receipt is generated.
7. SQL is concatenated with user input, allowing SQL injection and malformed values.
8. Connection and Statement are not closed; resources can leak.
9. No window-closing handler or default close operation is defined.
10. Checked SQL/Interrupted exceptions are not handled correctly.

## 1.2 Repaired structure
- `FeeWindow`: collects registration number and amount, calls service, displays result.
- `FeeService`: validates inputs and performs payment through a repository.
- `FeeRepository`: uses JDBC PreparedStatement and try-with-resources.
- Run database work in SwingWorker; never block the EDT.

### Event handler sketch
```java
payButton.addActionListener(e -> {
    String reg = regField.getText().trim();
    String text = amountField.getText().trim();
    try {
        double amount = Double.parseDouble(text);
        payButton.setEnabled(false);
        new SwingWorker<String, Void>() {
            protected String doInBackground() throws Exception {
                feeService.pay(reg, amount);
                return "Payment successful";
            }
            protected void done() {
                payButton.setEnabled(true);
                try { JOptionPane.showMessageDialog(frame, get()); }
                catch (Exception ex) { JOptionPane.showMessageDialog(frame, ex.getMessage()); }
            }
        }.execute();
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(frame, "Enter a valid amount");
    }
});
clearButton.addActionListener(e -> { regField.setText(""); amountField.setText(""); });
receiptButton.addActionListener(e -> showLatestReceipt());
```

Use PreparedStatement: `INSERT INTO fees(reg_no, amount) VALUES (?, ?)`. Validate non-empty registration number and amount > 0. Close the connection with try-with-resources.

## 1.3 Design defence (under 100 words)
Separating the window from FeeService keeps presentation code independent of payment rules. The service can be unit-tested without opening a GUI, while the window can be changed without rewriting database logic. Validation and SQL are centralized, prepared statements reduce injection risk, and SwingWorker keeps the interface responsive. Small event handlers are easier to read, debug and extend.

## Alternative and complexity
An if–else listener may perform up to k command comparisons: O(k) dispatch; one shared listener needs O(1) listener space. Separate listener objects or Swing Actions allow direct dispatch in O(1) expected time but require O(k) listener/action objects. In practice k is small, so separation and maintainability matter more than the tiny dispatch cost.
