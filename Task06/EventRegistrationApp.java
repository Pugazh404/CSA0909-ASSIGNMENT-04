import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class EventRegistrationApp extends JFrame {
    // Set these environment variables before running, or edit the defaults for your local MySQL.
    private static final String URL = System.getenv().getOrDefault("DB_URL",
            "jdbc:mysql://localhost:3306/campus_events");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "");

    private final JTextField studentId = new JTextField(12);
    private final JTextField studentName = new JTextField(14);
    private final JComboBox<EventItem> event = new JComboBox<>();
    private final JComboBox<EventItem> filter = new JComboBox<>();
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"ID", "Student ID", "Student Name", "Event", "Registered At"}, 0) {
        public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable table = new JTable(model);
    private final JLabel status = new JLabel("Ready");

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    static class EventItem {
        final int id; final String name;
        EventItem(int id, String name) { this.id=id; this.name=name; }
        public String toString() { return name; }
    }

    public EventRegistrationApp() {
        super("Campus Event Registration");
        setLayout(new BorderLayout(8,8));
        JMenuBar bar = new JMenuBar();
        JMenu file = new JMenu("File");
        JMenuItem refresh = new JMenuItem("Refresh");
        JMenuItem export = new JMenuItem("Export CSV");
        JMenuItem exit = new JMenuItem("Exit");
        file.add(refresh); file.add(export); file.addSeparator(); file.add(exit);
        bar.add(file); setJMenuBar(bar);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT));
        form.add(new JLabel("Student ID")); form.add(studentId);
        form.add(new JLabel("Name")); form.add(studentName);
        form.add(new JLabel("Event")); form.add(event);
        JButton register = new JButton("Register");
        form.add(register);
        add(form, BorderLayout.NORTH);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Filter event")); filterPanel.add(filter);
        JButton applyFilter = new JButton("Apply");
        filterPanel.add(applyFilter);
        JPanel center = new JPanel(new BorderLayout());
        center.add(filterPanel, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);
        add(status, BorderLayout.SOUTH);

        register.addActionListener(e -> registerStudent());
        applyFilter.addActionListener(e -> loadRegistrations());
        refresh.addActionListener(e -> loadEvents());
        export.addActionListener(e -> exportCsv());
        exit.addActionListener(e -> dispose());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 520); setLocationRelativeTo(null);
        loadEvents();
        loadRegistrations();
    }

    private void loadEvents() {
        new SwingWorker<Vector<EventItem>, Void>() {
            protected Vector<EventItem> doInBackground() throws Exception {
                Vector<EventItem> list = new Vector<>();
                try (Connection c=connect(); Statement s=c.createStatement();
                     ResultSet rs=s.executeQuery("SELECT event_id,event_name FROM events ORDER BY event_name")) {
                    while(rs.next()) list.add(new EventItem(rs.getInt(1),rs.getString(2)));
                }
                return list;
            }
            protected void done() {
                try {
                    Vector<EventItem> list=get();
                    event.setModel(new DefaultComboBoxModel<>(list));
                    Vector<EventItem> all=new Vector<>(); all.add(new EventItem(0,"All events")); all.addAll(list);
                    filter.setModel(new DefaultComboBoxModel<>(all));
                    status.setText("Events loaded");
                } catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private void registerStudent() {
        String sid=studentId.getText().trim(), name=studentName.getText().trim();
        EventItem selected=(EventItem)event.getSelectedItem();
        if(sid.isEmpty() || name.isEmpty() || selected==null) {
            JOptionPane.showMessageDialog(this,"Enter student ID, name and select an event.");
            return;
        }
        setBusy(true);
        new SwingWorker<Void,Void>() {
            protected Void doInBackground() throws Exception {
                String sql="INSERT INTO registrations(student_id,student_name,event_id) VALUES(?,?,?)";
                try(Connection c=connect(); PreparedStatement p=c.prepareStatement(sql)) {
                    p.setString(1,sid); p.setString(2,name); p.setInt(3,selected.id); p.executeUpdate();
                }
                return null;
            }
            protected void done() {
                setBusy(false);
                try { get(); status.setText("Registration successful"); loadRegistrations(); }
                catch(Exception ex) {
                    Throwable cause=ex.getCause();
                    if(cause instanceof SQLException && ((SQLException)cause).getErrorCode()==1062)
                        JOptionPane.showMessageDialog(EventRegistrationApp.this,
                                "This student is already registered for this event.");
                    else showError(ex);
                }
            }
        }.execute();
    }

    private void loadRegistrations() {
        EventItem selected=(EventItem)filter.getSelectedItem();
        int eventId=selected==null ? 0 : selected.id;
        new SwingWorker<Vector<Object[]>,Void>() {
            protected Vector<Object[]> doInBackground() throws Exception {
                Vector<Object[]> rows=new Vector<>();
                String sql="SELECT r.registration_id,r.student_id,r.student_name,e.event_name,r.registered_at " +
                        "FROM registrations r JOIN events e ON r.event_id=e.event_id " +
                        (eventId==0 ? "" : "WHERE r.event_id=? ") + "ORDER BY r.registered_at DESC";
                try(Connection c=connect(); PreparedStatement p=c.prepareStatement(sql)) {
                    if(eventId!=0) p.setInt(1,eventId);
                    try(ResultSet rs=p.executeQuery()) {
                        while(rs.next()) rows.add(new Object[]{rs.getInt(1),rs.getString(2),
                                rs.getString(3),rs.getString(4),rs.getTimestamp(5)});
                    }
                }
                return rows;
            }
            protected void done() {
                try { model.setRowCount(0); for(Object[] row:get()) model.addRow(row); status.setText(model.getRowCount()+" registration(s)"); }
                catch(Exception ex) { showError(ex); }
            }
        }.execute();
    }

    private void exportCsv() {
        JFileChooser chooser=new JFileChooser();
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION) return;
        try(java.io.PrintWriter out=new java.io.PrintWriter(chooser.getSelectedFile())) {
            for(int c=0;c<model.getColumnCount();c++) out.print(csv(model.getColumnName(c))+(c+1==model.getColumnCount()?"":","));
            out.println();
            for(int r=0;r<model.getRowCount();r++) {
                for(int c=0;c<model.getColumnCount();c++)
                    out.print(csv(String.valueOf(model.getValueAt(r,c)))+(c+1==model.getColumnCount()?"":","));
                out.println();
            }
            status.setText("CSV exported");
        } catch(Exception ex) { showError(ex); }
    }
    private String csv(String s) { return "\"" + s.replace("\"","\"\"") + "\""; }
    private void setBusy(boolean busy) { studentId.setEnabled(!busy); studentName.setEnabled(!busy); event.setEnabled(!busy); }
    private void showError(Exception ex) {
        Throwable t=ex.getCause()==null?ex:ex.getCause();
        JOptionPane.showMessageDialog(this,t.getMessage(),"Database error",JOptionPane.ERROR_MESSAGE);
        status.setText("Operation failed");
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EventRegistrationApp().setVisible(true));
    }
}
