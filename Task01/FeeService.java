import java.sql.*;
public class FeeService {
    private final String url, user, password;
    public FeeService(String url, String user, String password) {
        this.url=url; this.user=user; this.password=password;
    }
    public void pay(String regNo, double amount) throws SQLException {
        if (regNo == null || !regNo.trim().matches("[A-Za-z0-9-]{4,20}"))
            throw new IllegalArgumentException("Enter a valid registration number");
        if (!Double.isFinite(amount) || amount <= 0)
            throw new IllegalArgumentException("Amount must be greater than zero");
        String sql="INSERT INTO fees(reg_no, amount) VALUES (?, ?)";
        try(Connection c=DriverManager.getConnection(url,user,password);
            PreparedStatement p=c.prepareStatement(sql)) {
            p.setString(1,regNo.trim()); p.setDouble(2,amount); p.executeUpdate();
        }
    }
}
