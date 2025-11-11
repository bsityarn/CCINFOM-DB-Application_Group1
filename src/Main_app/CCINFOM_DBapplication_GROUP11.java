/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package Main_app;

import javax.swing.JFrame;
import Model.*;
import View.*;
import Controller.*;

/**
 *
 * @author marcquizon
 */
public class CCINFOM_DBapplication_GROUP11 {

    /**
     * @param args the command line arguments
     */
    private final int number = 0;

    public static void main(String[] args) {
        // TODO code application logic here
        System.out.println("Program start");

        Technician technician = new Technician();
        Tester tester = new Tester();

        AppController.setTechnicianSession(technician);
        AppController.setTesterSession(tester);
        LoginFrame frame1 = new LoginFrame();
        frame1.setVisible(true);

    }

}
