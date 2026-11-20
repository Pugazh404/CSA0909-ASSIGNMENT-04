import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;
public class ConverterAnonymous extends JFrame {
    private final JTextField amount=new JTextField(10);
    private final JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"});
    private final JLabel result=new JLabel(" ");
    public ConverterAnonymous() {
        super("Converter – Anonymous Class"); setLayout(new FlowLayout());
        JButton button=new JButton("Convert"); add(new JLabel("INR")); add(amount); add(target); add(button); add(result);
        ActionListener h=new ActionListener() { public void actionPerformed(ActionEvent e) { convert(); } };
        button.addActionListener(h); amount.addActionListener(h);
        setDefaultCloseOperation(EXIT_ON_CLOSE); pack();
    }
    private void convert() {
        try { double n=Double.parseDouble(amount.getText().trim()); if(!Double.isFinite(n)||n<0) throw new NumberFormatException();
            String v=String.format("%.2f",n*("USD".equals(target.getSelectedItem())?0.012:0.011)); result.setText(v);
            JOptionPane.showMessageDialog(this,v+" "+target.getSelectedItem());
        } catch(NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(this,"Invalid amount"); }
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(()->new ConverterAnonymous().setVisible(true)); }
}
