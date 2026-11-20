import java.awt.FlowLayout;
import java.awt.event.*;
import javax.swing.*;
public class ConverterFrameListener extends JFrame implements ActionListener {
    private final JTextField amount=new JTextField(10);
    private final JComboBox<String> target=new JComboBox<>(new String[]{"USD","EUR"});
    private final JLabel result=new JLabel(" ");
    public ConverterFrameListener() {
        super("Converter – Frame Listener"); setLayout(new FlowLayout());
        JButton button=new JButton("Convert"); add(new JLabel("INR")); add(amount); add(target); add(button); add(result);
        button.addActionListener(this); amount.addActionListener(this);
        setDefaultCloseOperation(EXIT_ON_CLOSE); pack();
    }
    public void actionPerformed(ActionEvent e) { convert(); }
    private void convert() {
        try { double n=Double.parseDouble(amount.getText().trim()); if(!Double.isFinite(n)||n<0) throw new NumberFormatException();
            String v=String.format("%.2f",n*("USD".equals(target.getSelectedItem())?0.012:0.011)); result.setText(v);
            JOptionPane.showMessageDialog(this,v+" "+target.getSelectedItem());
        } catch(NumberFormatException ex) { result.setText("Invalid amount"); JOptionPane.showMessageDialog(this,"Invalid amount"); }
    }
    public static void main(String[] a) { SwingUtilities.invokeLater(()->new ConverterFrameListener().setVisible(true)); }
}
