import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;

public class ATMinterface extends JFrame implements ActionListener {
    JLabel amountLabel, transactionHistoryLabel;
    JTextField amountField;
    JTextArea ta;
    JButton withdraw, deposit, transfer, quit;
    int id, balance;
    String acno;

    public ATMinterface(int id, String acno, int balance) {
        this.id = id;
        this.acno = acno;
        this.balance = balance;

        setVisible(true);
        setLayout(null);
        setLocationRelativeTo(null); // Center the frame on the screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(630, 500);
        setTitle("ATM");

        amountLabel = new JLabel("Enter Amount:");
        amountLabel.setBounds(70, 30, 170, 25);
        amountLabel.setFont(new Font("Mongolian Baiti", 1, 20));
        add(amountLabel);

        amountField = new JTextField();
        amountField.setBounds(240, 30, 300, 25);
        amountField.setFont(new Font("Times New Roman", 1, 20));
        add(amountField);

        withdraw = new JButton("Withdraw");
        withdraw.setBounds(70, 80, 150, 25);
        withdraw.setFont(new Font("Mongolian Baiti", 1, 20));
        add(withdraw);
        withdraw.addActionListener(this);

        deposit = new JButton("Deposit");
        deposit.setBounds(240, 80, 150, 25);
        deposit.setFont(new Font("Mongolian Baiti", 1, 20));
        add(deposit);
        deposit.addActionListener(this);

        transfer = new JButton("Transfer");
        transfer.setBounds(410, 80, 150, 25);
        transfer.setFont(new Font("Mongolian Baiti", 1, 20));
        add(transfer);
        transfer.addActionListener(this);

        transactionHistoryLabel = new JLabel("Transaction History");
        transactionHistoryLabel.setBounds(70, 130, 200, 25);
        transactionHistoryLabel.setFont(new Font("Mongolian Baiti", 1, 20));
        add(transactionHistoryLabel);

        ta = new JTextArea();
        ta.setBounds(100, 180, 320, 200);
        ta.setFont(new Font("Times New Roman", 1, 20));
        add(ta);

        quit = new JButton("Quit");
        quit.setBounds(180, 395, 160, 25);
        quit.setFont(new Font("Mongolian Baiti", 1, 19));
        quit.setBackground(Color.RED);
        quit.setForeground(Color.WHITE);
        add(quit);
        quit.addActionListener(this);
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/ATM", "root", "Shrutidevansh@2701");
    }

    private void initDatabase() throws Exception {
        Class.forName("com.mysql.cj.jdbc.Driver");
    }

    private void withdraw(int amount) throws Exception {
        String qry = "UPDATE accDetails SET balance=? WHERE id=?";
        try (Connection cn = getConnection();
             PreparedStatement pstmt = cn.prepareStatement(qry)) {
            pstmt.setInt(1, balance - amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            balance -= amount;
            ta.append("Withdrawn: " + amount + "\nRemaining Balance: " + balance + "\n");

            // Insert the transaction into the transactionHistory table
            String insertQry = "INSERT INTO transactionHistory (id, operation, amount) VALUES (?, ?, ?)";
            try (PreparedStatement insertPstmt = cn.prepareStatement(insertQry)) {
                insertPstmt.setInt(1, id);
                insertPstmt.setString(2, "Withdraw");
                insertPstmt.setInt(3, amount);
                insertPstmt.executeUpdate();
            }
        }
    }

    private void deposit(int amount) throws Exception {
        String qry = "update accDetails set balance=? where id=?";
        try (Connection cn = getConnection();
             PreparedStatement pstmt = cn.prepareStatement(qry)) {
            pstmt.setInt(1, balance + amount);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
            balance += amount;
            ta.append("Deposited: " + amount + "\nRemaining Balance: " + balance + "\n");

            // Insert the transaction into the transactionHistory table
            String insertQry = "INSERT INTO transactionHistory (id, operation, amount) VALUES (?, ?, ?)";
            try (PreparedStatement insertPstmt = cn.prepareStatement(insertQry)) {
                insertPstmt.setInt(1, id);
                insertPstmt.setString(2, "Deposit");
                insertPstmt.setInt(3, amount);
                insertPstmt.executeUpdate();
            }
        }
    }

    private void transfer(int amount, String transferAccNo) throws Exception {
        String qry = "update accDetails set balance=? where acno=?";
        try (Connection cn = getConnection();
             PreparedStatement pstmt = cn.prepareStatement(qry)) {
            pstmt.setInt(1, balance - amount);
            pstmt.setString(2, acno);
            pstmt.executeUpdate();
            balance -= amount;
            ta.append("Transferred: " + amount + " to " + transferAccNo + "\nRemaining Balance: " + balance + "\n");

            // Insert the transaction into the transactionHistory table
            String insertQry = "INSERT INTO transactionHistory (id, operation, amount) VALUES (?, ?, ?)";
            try (PreparedStatement insertPstmt = cn.prepareStatement(insertQry)) {
                insertPstmt.setInt(1, id);
                insertPstmt.setString(2, "Transfer");
                insertPstmt.setInt(3, amount);
                insertPstmt.executeUpdate();
            }
        }
    }

    private String getTransactionHistory() throws Exception {
        String qry = "SELECT operation, amount FROM transactionHistory WHERE id=? ORDER BY timestamp DESC";
        StringBuilder sb = new StringBuilder();
        try (Connection cn = getConnection();
             PreparedStatement pstmt = cn.prepareStatement(qry)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sb.append(rs.getString("operation")).append(": ").append(rs.getInt("amount")).append("\n");
                }
            }
        }
        return sb.toString();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == quit) {
            setVisible(false);
        } else {
            try {
                initDatabase();
                int amount = Integer.parseInt(amountField.getText());
                if (ae.getSource() == withdraw) {
                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Insufficient Balance");
                    } else {
                        withdraw(amount);
                        String history = getTransactionHistory();
                        ta.setText(history);
                    }
                } else if (ae.getSource() == deposit) {
                    deposit(amount);
                    String history = getTransactionHistory();
                    ta.setText(history);
                } else if (ae.getSource() == transfer) {
                    if (amount > balance) {
                        JOptionPane.showMessageDialog(null, "Insufficient Balance");
                    } else {
                        String transferAccNo = JOptionPane.showInputDialog(null, "Enter Account Number to Transfer");
                        if (transferAccNo != null && !transferAccNo.isEmpty()) {
                            try {
                                transfer(amount, transferAccNo);
                                String history = getTransactionHistory();
                                ta.setText(history);
                            } catch (Exception e) {
                                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid Account Number");
                        }
                    }
                }
                amountField.setText("");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        new ATMinterface(1001, "1234567890", 10000);
    }
}