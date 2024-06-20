import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.sql.*;
public class signUp extends JFrame implements ActionListener
{
	JLabel accno,acName,ifsc,branchname,actype;
	JTextField accField,acNameFiled,ifscField,bnameField;
	JButton signUpBtn;
	JRadioButton saving,current,fd;
	ButtonGroup groupBtn;
	
	public signUp()
	{
		setVisible(true);
		setLayout(null);
		setLocation(300,70);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(630,500);
		setTitle("SignUp Page");

		accno=new JLabel("Account Number:");
		accno.setBounds(70,30,170,25);
		accno.setFont(new Font("Mongolian Baiti",1,20));
		add(accno);

		accField=new JTextField();
		accField.setBounds(240,30,300,25);
		accField.setFont(new Font("Times New Roman",1,20));
		add(accField);

		acName=new JLabel("Holder Name:");
		acName.setBounds(70,80,170,25);
		acName.setFont(new Font("Mongolian Baiti",1,20));
		add(acName);

		acNameFiled=new JTextField();
		acNameFiled.setBounds(240,80,300,25);
		acNameFiled.setFont(new Font("Times New Roman",1,20));
		add(acNameFiled);

		ifsc=new JLabel("IFSC code:");
		ifsc.setBounds(70,130,170,25);
		ifsc.setFont(new Font("Mongolian Baiti",1,20));
		add(ifsc);

		ifscField=new JTextField();
		ifscField.setBounds(240,130,300,25);
		ifscField.setFont(new Font("Times New Roman",1,20));
		add(ifscField);

		branchname=new JLabel("Branch Name:");
		branchname.setBounds(70,180,170,25);
		branchname.setFont(new Font("Mongolian Baiti",1,20));
		add(branchname);

		bnameField=new JTextField();
		bnameField.setBounds(240,180,300,25);
		bnameField.setFont(new Font("Times New Roman",1,20));
		add(bnameField);

		actype=new JLabel("Account Type:");
		actype.setBounds(70,230,170,25);
		actype.setFont(new Font("Mongolian Baiti",1,20));
		add(actype);

		saving=new JRadioButton("Saving");
		saving.setBounds(240,230,110,25);
		saving.setFont(new Font("Times New Roman",1,20));
		add(saving);

		current=new JRadioButton("Current");
		current.setBounds(350,230,110,25);
		current.setFont(new Font("Times New Roman",1,20));
		add(current);

		fd=new JRadioButton("Fixed Deposit");
		fd.setBounds(460,230,150,25);
		fd.setFont(new Font("Times New Roman",1,20));
		add(fd);

		groupBtn=new ButtonGroup();
		groupBtn.add(saving);
		groupBtn.add(current);
		groupBtn.add(fd);		

		signUpBtn=new JButton("Generate Pin");
		signUpBtn.setBounds(180,280,160,25);
		signUpBtn.setFont(new Font("Mongolian Baiti",1,19));
		signUpBtn.setBackground(Color.RED);
		signUpBtn.setForeground(Color.WHITE);
		add(signUpBtn);
		signUpBtn.addActionListener(this);
	}
	public void actionPerformed(ActionEvent ae)
	{
		String acno=accField.getText();
		String hname=acNameFiled.getText();
		String ifsc=ifscField.getText();
		String bname=bnameField.getText();	
		String acctype="";
		if(ae.getSource()==signUpBtn)
		{
			if(acno.isEmpty()||hname.isEmpty()||ifsc.isEmpty()||bname.isEmpty())
        	{
            	JOptionPane.showMessageDialog(null,"Please Fill The Details");
        	}
        	else if(acno.length()<10 || acno.length()>16 || !isNumeric(acno))
        	{
            	JOptionPane.showMessageDialog(null,"Please Enter Valid Account Number!");
        	}
	        else if(ifsc.length()>11 || ifsc.length()<11)
	        {
	            JOptionPane.showMessageDialog(null,"Please Enter Valid IFSC Code!");
	        }
	        else if(!saving.isSelected() && !current.isSelected() && !fd.isSelected())
	        {
	            JOptionPane.showMessageDialog(null,"Please Select an Account Type");
	        }
	        else if(saving.isSelected())
	        {
	            acctype="Saving";
	            new generatePin(acno, hname, ifsc, bname, acctype);
                setVisible(false);
	        }
	        else if(current.isSelected())
	        {
	            acctype="Current";
	            new generatePin(acno, hname, ifsc, bname, acctype);
                setVisible(false);
	        }
	        else if(fd.isSelected())
	        {
	            acctype="Fixed Deposit";
	            new generatePin(acno, hname, ifsc, bname, acctype);
                setVisible(false);
	        }
		}
	}
	private boolean isNumeric(String str)
	{
		try
		{
			Long.parseLong(str);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	public static void main(String[] args) {
		new signUp();
	}
}