# Task 5 – Four event-handling styles

The completed runnable lambda implementation is in [Converter.java](Converter.java).

## Equivalent Convert handler styles
**(a) Frame implements ActionListener**
```java
public class ConverterFrame extends JFrame implements ActionListener {
    // In constructor: convertButton.addActionListener(this);
    // and amount.addActionListener(this);
    public void actionPerformed(ActionEvent e) { convert(); }
}
```

**(b) Named inner class**
```java
class ConvertHandler implements ActionListener {
    public void actionPerformed(ActionEvent e) { convert(); }
}
// Register one ConvertHandler with button and amount field.
```

**(c) Anonymous inner class**
```java
ActionListener handler = new ActionListener() {
    public void actionPerformed(ActionEvent e) { convert(); }
};
convertButton.addActionListener(handler);
amount.addActionListener(handler);
```

**(d) Lambda**
```java
ActionListener handler = e -> convert();
convertButton.addActionListener(handler);
amount.addActionListener(handler);
```

Use a WindowAdapter for AWT Frame closing; for JFrame use setDefaultCloseOperation(EXIT_ON_CLOSE).

## Requirement change
The amount field's ActionListener is also registered, so Enter triggers conversion. The convert() method displays a JOptionPane dialog. The same shared handler can be reused in all four styles; the frame itself needs the least extra object code but couples the frame to ActionListener. A named inner class is reusable and testable; anonymous class and lambda are concise.

## Comparison
| Style | Readability | Access to fields | Reuse | Change effort |
|---|---|---|---|---|
| Frame implements listener | Simple for tiny form; mixes roles | Direct | Low | More coupling |
| Named inner class | Clear for complex handler | Direct | High | Low |
| Anonymous inner class | Local but verbose | Direct | Moderate | Low |
| Lambda | Concise for short action | Lexical access | Moderate | Lowest for simple actions |

For a large application, prefer a separate Action class or named handler for reuse, testing and centralized enable/disable behaviour.

## Alternative and complexity
A method reference `this::convert` is concise when the handler method matches the listener signature (use a wrapper lambda if ActionEvent is not accepted). One shared Action object can be attached to a button, menu item and keyboard shortcut. It avoids duplicating handler logic; adding a trigger is one registration line. The number of listener/action objects can remain O(1) per shared action, while separate per-trigger listeners may use O(k) objects.
