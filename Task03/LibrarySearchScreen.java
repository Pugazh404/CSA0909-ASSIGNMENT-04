import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class LibrarySearchScreen extends JFrame {
    private final JTextField search = new JTextField(22);
    private final JComboBox<String> category =
            new JComboBox<>(new String[]{"All", "Book", "Journal", "Thesis"});
    private final DefaultListModel<String> results = new DefaultListModel<>();
    private final JList<String> resultList = new JList<>(results);
    private final JLabel status = new JLabel("Enter a title and click Search");

    public LibrarySearchScreen() {
        super("Campus Library Search");
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Campus Library Search", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 20f));
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchRow.add(new JLabel("Search:"));
        searchRow.add(search);
        searchRow.add(category);
        JButton find = new JButton("Search");
        searchRow.add(find);
        top.add(title, BorderLayout.NORTH);
        top.add(searchRow, BorderLayout.CENTER);
        add(top, BorderLayout.NORTH);

        resultList.setBorder(BorderFactory.createTitledBorder("Results"));
        add(new JScrollPane(resultList), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout());
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton prev = new JButton("Prev"), next = new JButton("Next"), reserve = new JButton("Reserve");
        buttons.add(prev); buttons.add(next); buttons.add(reserve);
        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(status, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        find.addActionListener(e -> {
            results.clear();
            String q = search.getText().trim();
            if (q.isEmpty()) { status.setText("Enter a search term"); return; }
            results.addElement(q + " – Sample library result 1");
            results.addElement(q + " – Sample library result 2");
            status.setText("2 result(s) found in " + category.getSelectedItem());
        });
        prev.addActionListener(e -> status.setText("Previous page selected"));
        next.addActionListener(e -> status.setText("Next page selected"));
        reserve.addActionListener(e -> status.setText(
                resultList.getSelectedValue() == null ? "Select a result first" : "Reservation requested"));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(720, 480);
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibrarySearchScreen().setVisible(true));
    }
}
