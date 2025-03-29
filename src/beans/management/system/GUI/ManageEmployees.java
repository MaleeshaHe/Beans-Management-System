/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package beans.management.system.GUI;

import javax.swing.*;
import java.awt.*;

public class ManageEmployees extends JPanel {

    public ManageEmployees() {
        setLayout(new BorderLayout());
        JLabel label = new JLabel("Manage Employees Form", JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 18));
        add(label, BorderLayout.CENTER);
    }
}

