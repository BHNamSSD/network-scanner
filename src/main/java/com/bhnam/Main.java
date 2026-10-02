package com.bhnam;

import javax.swing.SwingUtilities;

public class Main {

//    public static void main(String[] args) {
//
//        System.out.println("================================");
//        System.out.println("      Java Network Scanner");
//        System.out.println("================================");
//
//        System.out.println("Java version : "
//                + System.getProperty("java.version"));
//
//        System.out.println("OS           : "
//                + System.getProperty("os.name"));
//
//        System.out.println("Architecture : "
//                + System.getProperty("os.arch"));
//    }

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            ScannerWindow window = new ScannerWindow();
            window.setVisible(true);
        });
    }
}