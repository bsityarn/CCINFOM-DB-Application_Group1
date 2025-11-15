/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controller;

import Model.*;
import View.*;

/**
 *
 * @author marcquizon
 */
public class AppController {

    private static Technician technician = new Technician();
    private static Tester tester = new Tester();

    public static void setTechnicianSession(Technician technicianModel) {
        technician = technicianModel;
    }

    public static void setTesterSession(Tester userModel) {
        tester = userModel;
    }

    public static boolean loginTechnician(String idnumber, char[] password) {
        if (false) {//TODO create function under MODEL to check if a Technician exists
            MenuTechnicianFrame menuTechnician = new MenuTechnicianFrame(idnumber);
            menuTechnician.setVisible(true);
            System.out.println("Technician ID " + idnumber + " Logged in");

            return true;
        }
        return false;
    }

    public static boolean loginTester(String idnumber, char[] password) {
        if (true) {//TODO create function under MODEL to check if a Tester exists
            MenuTesterFrame menuUser = new MenuTesterFrame(idnumber);
            menuUser.setVisible(true);
            System.out.println("Tester ID " + idnumber + " Logged in");

            return true;
        }
        return false;
    }
    
//    public static Technician editTechnician(Technician technician) {
//        
//        return techni
//    }

}
