import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class userAuthenticate extends JFrame implements ActionListener {
	JLabel username, password, signUplbl;
	JTextField nameTextField;
	JPasswordField passwordField;
	JButton signIn, view1;
	int id;
	String pinStr, acno;

	public userAuthenticate() {
		// Initialize any necessary components or variables
		initComponents();
	}

	public userAuthenticate(String acno) {
		this.acno = acno;
		initComponents();
	}

	private void initComponents() {
		this.acno = acno;
		getContentPane().setBackground(Color.WHITE);
		setTitle("User Authentication");
		setVisible(true);
		setLayout(null);
		setLocation(300,95);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500,350);

		username=new JLabel("Enter User Id:");
		username.setBounds(40,30,120,25);
		username.setFont(new Font("Mongolian Baiti",1,18));
		add(username);

		nameTextField=new JTextField();
		nameTextField.setBounds(165,30,200,25);
		nameTextField.setFont(new Font("Times New Roman",1,15));
		add(nameTextField);

		password=new JLabel("Enter User Pin:");
		password.setBounds(40,70,120,25);
		password.setFont(new Font("Mongolian Baiti",1,18));
		add(password);

		passwordField=new JPasswordField();
		passwordField.setBounds(165,70,200,25);
		passwordField.setEchoChar('X');
		passwordField.setFont(new Font("Times New Roman",1,15));
		add(passwordField);

		view1=new JButton("view");
		view1.setBounds(370,70,70,25);
		view1.setBackground(Color.GREEN);
		view1.setForeground(Color.WHITE);
		add(view1);
		view1.addActionListener(this);

		signIn=new JButton("Sign In");
		signIn.setBounds(185,110,120,25);
		signIn.setFont(new Font("Mongolian Baiti",1,19));
		signIn.setBackground(Color.RED);
		signIn.setForeground(Color.WHITE);
		add(signIn);
		signIn.addActionListener(this);

		signUplbl=new JLabel("<html><u><font color='blue'>Have you been Sign Up?</font></u></html>");
		signUplbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		signUplbl.setBounds(60,140,190,25);
		signUplbl.setFont(new Font("Times New Roman",Font.PLAIN,14));
		add(signUplbl);
		signUplbl.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				try {
					setVisible(false);
					new signUp();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	private Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM", "root", "Shrutidevansh@2701");
	}

	private void initDatabase() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
	}

	private boolean autheticateUser(int userid, String pin) throws Exception {
		String qry = "SELECT * FROM accDetails WHERE id=? AND pin=?";
		try (Connection cn = getConnection();
			 PreparedStatement pstmt = cn.prepareStatement(qry)) {
			pstmt.setInt(1, userid);
			pstmt.setString(2, pin);
			try (ResultSet rs = pstmt.executeQuery()) {
				return rs.next();
			}
		}
	}

	private int getBalance(int id) throws Exception {
		String qry = "SELECT balance FROM accDetails WHERE id=?";
		try (Connection cn = getConnection();
			 PreparedStatement pstmt = cn.prepareStatement(qry)) {
			pstmt.setInt(1, id);
			try (ResultSet rs = pstmt.executeQuery()) {
				if (rs.next()) {
					return rs.getInt("balance");
				}
			}
		}
		return 0; // or throw an exception if the user is not found
	}

	public void actionPerformed(ActionEvent ae) {
		String uid = nameTextField.getText();
		char[] passwordChars = passwordField.getPassword();
		pinStr = new String(passwordChars);
		Arrays.fill(passwordChars, '\0');

		if(ae.getSource()==view1)
		{
			if(passwordField.getEchoChar()==0)
			{
				passwordField.setEchoChar('X');
				view1.setText("View");
			}
			else
			{
				passwordField.setEchoChar((char) 0);
				view1.setText("Hide");
			}
		}

        else if (ae.getSource() == signIn) {
			if (pinStr.isEmpty()) {
				JOptionPane.showMessageDialog(null, "Please Fill The Pin!");
			} else if (pinStr.length() != 6) {
				JOptionPane.showMessageDialog(null, "Please Enter Valid Pin!");
			} else {
				int id = Integer.parseInt(uid);
				int finalId = id - 1000;
				try {
					initDatabase();
					if (autheticateUser(finalId, pinStr)) {
						JOptionPane.showMessageDialog(null, "Sign In Successful!");
						int balance = getBalance(finalId);
						new ATMinterface(finalId, acno, balance);
						clearFields();
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, "Invalid credentials.");
					}
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
				}
			}
		}
	}

	private void clearFields() {
		nameTextField.setText("");
		passwordField.setText("");
	}

	public static void main(String[] args) {
		new userAuthenticate("1234567890");
	}
}