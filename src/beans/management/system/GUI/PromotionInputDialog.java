package beans.management.system.GUI;

import beans.management.system.DAO.PromotionDAO;
import beans.management.system.Model.Promotion;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class PromotionInputDialog extends JDialog {

    private JTextField promoCodeField, startDateField, endDateField, discountField;
    private JTextArea descriptionArea;
    private JButton saveButton, cancelButton;
    private boolean isEditMode;
    private Promotion currentPromotion;

    // Constructor for the dialog
    public PromotionInputDialog(JFrame parent, boolean isEditMode, Promotion promotion) {
        super(parent, "Promotion Details", true);
        this.isEditMode = isEditMode;
        this.currentPromotion = promotion;  // The promotion to edit, if available

        // Set layout for the dialog using GridBagLayout for alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Set dialog background color to white
        getContentPane().setBackground(Color.WHITE);

        // Create and align the form fields
        gbc.insets = new Insets(10, 10, 10, 10);  // Add padding around components

        // Add Promo Code label and text field
        add(new JLabel("Promo Code:"), gbc);
        promoCodeField = new JTextField(isEditMode ? promotion.getPromoCode() : "");
        promoCodeField.setHorizontalAlignment(JTextField.LEFT);  // Left-align the text field
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(promoCodeField, gbc);

        // Add Start Date label and text field
        gbc.gridx = 0;
        add(new JLabel("Start Date (yyyy-mm-dd):"), gbc);
        startDateField = new JTextField(isEditMode ? promotion.getStartDate() : "");
        startDateField.setHorizontalAlignment(JTextField.LEFT);
        gbc.gridx = 1;
        add(startDateField, gbc);

        // Add End Date label and text field
        gbc.gridx = 0;
        add(new JLabel("End Date (yyyy-mm-dd):"), gbc);
        endDateField = new JTextField(isEditMode ? promotion.getEndDate() : "");
        endDateField.setHorizontalAlignment(JTextField.LEFT);
        gbc.gridx = 1;
        add(endDateField, gbc);

        // Add Description label and text area
        gbc.gridx = 0;
        add(new JLabel("Description:"), gbc);
        descriptionArea = new JTextArea(isEditMode ? promotion.getDescription() : "");
        descriptionArea.setRows(3);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setLineWrap(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        add(descriptionScrollPane, gbc);

        // Add Discount Percentage label and text field
        gbc.gridx = 0;
        add(new JLabel("Discount Percentage:"), gbc);
        discountField = new JTextField(isEditMode ? String.valueOf(promotion.getDiscountPercentage()) : "");
        gbc.gridx = 1;
        add(discountField, gbc);

        // Create Save and Cancel buttons
        saveButton = new JButton(isEditMode ? "Update Promotion" : "Add Promotion");
        cancelButton = new JButton("Cancel");

        // Styling for the buttons
        styleButton(saveButton);
        styleButton(cancelButton);

        // Create a panel for the buttons and add them to the dialog
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add button panel to dialog using GridBagConstraints
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        add(buttonPanel, gbc);

        // Add action listener to save button
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePromotion();
            }
        });

        // Add action listener to cancel button
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();  // Close the dialog without saving
            }
        });

        // Set dialog properties
        setSize(350, 350);  // Adjusted size for better fit
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Method to handle saving or updating the promotion
    private void savePromotion() {
        String promoCode = promoCodeField.getText().trim();
        String startDateStr = startDateField.getText().trim();
        String endDateStr = endDateField.getText().trim();
        String description = descriptionArea.getText().trim();
        double discountPercentage;

        // Check for empty fields first
        if (promoCode.isEmpty() || startDateStr.isEmpty() || endDateStr.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        // Validate discount
        try {
            discountPercentage = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid discount percentage.");
            return;
        }
        
        if (discountPercentage <= 0 || discountPercentage > 100) {
            JOptionPane.showMessageDialog(this, "Discount must be greater than 0 and not exceed 100%.");
            return;
        }

        // Validate date format and logic
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(startDateStr);
            endDate = LocalDate.parse(endDateStr);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Please enter dates in yyyy-mm-dd format.");
            return;
        }

        if (startDate.isAfter(endDate)) {
            JOptionPane.showMessageDialog(this, "Start date cannot be after end date.");
            return;
        }

        // Create and save promotion
        Promotion newPromotion = new Promotion(
                isEditMode ? currentPromotion.getPromotionId() : 0,
                promoCode,
                startDateStr,
                endDateStr,
                description,
                discountPercentage
        );

        boolean success = isEditMode
                ? new PromotionDAO().updatePromotion(newPromotion)
                : new PromotionDAO().addPromotion(newPromotion);

        if (success) {
            JOptionPane.showMessageDialog(this, isEditMode ? "Promotion updated successfully." : "Promotion added successfully.");
            dispose();  // Close the dialog
        } else {
            JOptionPane.showMessageDialog(this, "Error saving promotion.");
        }
    }


    // Method to style buttons with custom background color, font, and text color
    private void styleButton(JButton button) {
        button.setBackground(new Color(8, 103, 147)); // Set the background color of the button
        button.setForeground(Color.WHITE); // Set the text color to white
        button.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Set font to Segoe UI, 14 pt, Bold
        button.setPreferredSize(new Dimension(120, 40)); // Ensure buttons are of uniform size
    }
}
