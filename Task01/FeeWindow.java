import java.awt.*;
import javax.swing.*;
public class FeeWindow extends JFrame {
    private final JTextField regNo=new JTextField(14), amount=new JTextField(10);
    private final FeeService service=new FeeService(
        System.getenv().getOrDefault("DB_URL","jdbc:mysql://localhost:3306/campus_events"),
        System.getenv().getOrDefault("DB_USER","root"),
        System.getenv().getOrDefault("DB_PASSWORD",""));
    public FeeWindow() {
        super("Student Fee Payment");
        setLayout(new GridLayout(4,2,8,8));
        add(new JLabel("Registration No")); add(regNo);
        add(new JLabel("Amount")); add(amount);
        JButton pay=new JButton("Pay"), clear=new JButton("Clear"), receipt=new JButton("Receipt");
        add(pay); add(clear); add(receipt);
        pay.addActionListener(e -> {
            final double value;
            try { value=Double.parseDouble(amount.getText().trim()); }
            catch(NumberFormatException ex) { JOptionPane.showMessageDialog(this,"Enter a valid amount"); return; }
            pay.setEnabled(false);
            new SwingWorker<Void,Void>() {
                protected Void doInBackground() throws Exception { service.pay(regNo.getText(),value); return null; }
                protected void done() {
                    pay.setEnabled(true);
                    try { get(); JOptionPane.showMessageDialog(FeeWindow.this,"Payment successful"); }
                    catch(Exception ex) {
                        Throwable cause=ex.getCause();
                        JOptionPane.showMessageDialog(FeeWindow.this,
                            cause==null?ex.getMessage():cause.getMessage(),"Payment failed",JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });
        clear.addActionListener(e -> { regNo.setText(""); amount.setText(""); });
        receipt.addActionListener(e -> JOptionPane.showMessageDialog(this,
            "Use the database payment record as the receipt. A printable receipt feature is not implemented."));
        setDefaultCloseOperation(EXIT_ON_CLOSE); pack(); setLocationRelativeTo(null);
    }
    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new FeeWindow().setVisible(true)); }
}
