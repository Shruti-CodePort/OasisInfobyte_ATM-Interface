import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class generatePin extends JFrame implements ActionListener {
	JLabel pin;
	JTextField pinField;
	JButton done;
	String acno, hname, ifsc, bname, actype;
	int id;

	public generatePin(String acno, String hname, String ifsc, String bname, String actype) {
		this.acno = acno;
		this.hname = hname;
		this.ifsc = ifsc;
		this.bname = bname;
		this.actype = actype;
		getContentPane().setBackground(Color.WHITE);
		setTitle("Pin Generate");
		setVisible(true);
		setLayout(null);
		setLocationRelativeTo(null); // Center the frame on the screen
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 250);

		pin = new JLabel("Generate Your 6 Digit Pin:");
		pin.setBounds(70, 30, 250, 25);
		pin.setFont(new Font("Mongolian Baiti", Font.BOLD, 20));
		pin.setForeground(Color.GREEN);
		add(pin);

		pinField = new JTextField();
		pinField.setBounds(100, 80, 150, 25);
		pinField.setFont(new Font("Times New Roman", Font.BOLD, 20));
		add(pinField);

		done = new JButton("Done");
		done.setBounds(120, 130, 100, 25);
		done.setFont(new Font("Mongolian Baiti", Font.BOLD, 19));
		done.setBackground(Color.RED);
		done.setForeground(Color.WHITE);
		add(done);
		done.addActionListener(this);
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM", "root", "Shrutidevansh@2701");
	}

	private void initDatabase() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
	}

	private void registerUser(String acno, String hname, String ifsc, String bname, String actype, String pin) throws Exception {
		String sql = "INSERT INTO accDetails (acno, hname, ifsc, bname, actype, pin) VALUES (?, ?, ?, ?, ?, ?)";
		try (Connection cn = getConnection();
			 PreparedStatement pstmt = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			pstmt.setString(1, acno);
			pstmt.setString(2, hname);
			pstmt.setString(3, ifsc);
			pstmt.setString(4, bname);
			pstmt.setString(5, actype);
			pstmt.setString(6, pin);
			pstmt.executeUpdate();
			ResultSet rs = pstmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
				JOptionPane.showMessageDialog(null, "Your ID is: " + (id + 1000) + " Please remember your ID!");
			}
		}
	}

	public void actionPerformed(ActionEvent ae) {
		String pinStr = pinField.getText();
		if (ae.getSource() == done) {
			if (pinStr.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please Fill The Pin!");
			} else if (pinStr.length() != 6 || !isNumeric(pinStr)) {
				JOptionPane.showMessageDialog(null, "Please Enter Valid Pin!");
			} else {
				try {
					initDatabase();
					registerUser(acno, hname, ifsc, bname, actype, pinStr);
					JOptionPane.showMessageDialog(null, "Successfully Signed Up");
					setVisible(false);
					dispose(); // Dispose the current frame
					new userAuthenticate(acno);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
				}
			}
		}
	}

	private boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void main(String[] args) {
		new generatePin("accno", "hname", "ifsc", "bname", "actype");
	}
}