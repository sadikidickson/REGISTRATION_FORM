import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

class RegistrationForm extends JFrame {
    private JTextField nameField, idField, contactField, addressField;
    private JRadioButton maleRadio, femaleRadio;
    private JComboBox<String> dayBox, monthBox, yearBox;
    private Connection conn;

    public RegistrationForm() {
        // Initialize components
        JPanel mainPanel = new JPanel(new GridLayout(0, 2));
        nameField = new JTextField(20);
        idField = new JTextField(10);
        contactField = new JTextField(15);
        addressField = new JTextField(30);

        mainPanel.add(new JLabel("Name:"));
        mainPanel.add(nameField);
        mainPanel.add(new JLabel("ID:"));
        mainPanel.add(idField);

        // Add radio buttons for gender
        maleRadio = new JRadioButton("Male");
        femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);
        mainPanel.add(new JLabel("Gender:"));
        mainPanel.add(genderPanel);

        // Add date of birth selection using JComboBox
        dayBox = new JComboBox<>();
        for (int day = 1; day <= 31; day++) {
            dayBox.addItem(String.valueOf(day));
        }
        monthBox = new JComboBox<>();
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        for (String month : months) {
            monthBox.addItem(month);
        }
        yearBox = new JComboBox<>();
        for (int year = 2024; year >= 1960; year--) {
            yearBox.addItem(String.valueOf(year));
        }
        mainPanel.add(new JLabel("Date of Birth:"));
        JPanel dobPanel = new JPanel();
        dobPanel.add(dayBox);
        dobPanel.add(monthBox);
        dobPanel.add(yearBox);
        mainPanel.add(dobPanel);

        mainPanel.add(new JLabel("Contact:"));
        mainPanel.add(contactField);
        mainPanel.add(new JLabel("Address:"));
        mainPanel.add(addressField);

        // Add submit and reset buttons
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> {
            // Save data to database upon submission
            saveToDatabase();
            // Display second GUI for registration confirmation
            showRegistrationConfirmation();
        });
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            // Clear all fields
            clearFields();
        });
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(resetButton);
        mainPanel.add(buttonPanel);

        // Set up JFrame
        add(mainPanel);
        setTitle("Registration Form");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize database connection
        initializeDatabase();
    }

    private void initializeDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/form_registration", "root", "mbuva__17");
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void saveToDatabase() {
        try {
            String sql = "INSERT INTO users (name, user_id, dob, gender, contact, address) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, nameField.getText());
            statement.setString(2, idField.getText());
            String dob = yearBox.getSelectedItem() + "-" + (monthBox.getSelectedIndex() + 1) + "-" + dayBox.getSelectedItem();
            statement.setDate(3, Date.valueOf(dob));
            statement.setString(4, maleRadio.isSelected() ? "Male" : "Female");
            statement.setString(5, contactField.getText());
            statement.setString(6, addressField.getText());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void showRegistrationConfirmation() {
        JFrame confirmationFrame = new JFrame("Registration Confirmation");
        JPanel confirmationPanel = new JPanel(new GridLayout(0, 2));
        confirmationPanel.add(new JLabel("Name:"));
        confirmationPanel.add(new JLabel(nameField.getText()));
        confirmationPanel.add(new JLabel("ID:"));
        confirmationPanel.add(new JLabel(idField.getText()));
        confirmationPanel.add(new JLabel("Date of Birth:"));
        confirmationPanel.add(new JLabel(yearBox.getSelectedItem() + "-" + (monthBox.getSelectedIndex() + 1) + "-" + dayBox.getSelectedItem()));
        confirmationPanel.add(new JLabel("Gender:"));
        confirmationPanel.add(new JLabel(maleRadio.isSelected() ? "Male" : "Female"));
        confirmationPanel.add(new JLabel("Contact:"));
        confirmationPanel.add(new JLabel(contactField.getText()));
        confirmationPanel.add(new JLabel("Address:"));
        confirmationPanel.add(new JLabel(addressField.getText()));

        JCheckBox termsCheckBox = new JCheckBox("Accept Terms and Conditions");
        confirmationPanel.add(termsCheckBox);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            if (termsCheckBox.isSelected()) {
                JOptionPane.showMessageDialog(confirmationFrame, "Registration Successful!");
                confirmationFrame.dispose();
            } else {
                JOptionPane.showMessageDialog(confirmationFrame, "You must accept the terms and conditions to register.");
            }
        });

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> confirmationFrame.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);
        confirmationPanel.add(buttonPanel);

        confirmationFrame.add(confirmationPanel);
        confirmationFrame.pack();
        confirmationFrame.setLocationRelativeTo(null);
        confirmationFrame.setVisible(true);
    }

    private void clearFields() {
        nameField.setText("");
        idField.setText("");
        contactField.setText("");
        addressField.setText("");
        maleRadio.setSelected(false);
        femaleRadio.setSelected(false);
        dayBox.setSelectedIndex(0);
        monthBox.setSelectedIndex(0);
        yearBox.setSelectedIndex(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(RegistrationForm::new);
    }
}
