import Data.Database;
import GUIs.GUI;

import java.sql.Connection;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        Connection con = null;
        try {

            if (db.getConnection() != null) {
                db.initialize();
                con = db.getConnection();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (con == null) {
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            GUI gui = new GUI(db);
            gui.setVisible(true);

            gui.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    if (gui.clockTimer != null) {
                        gui.clockTimer.stop();
                    }
                    if (gui.refreshTimer != null) {
                        gui.refreshTimer.stop();
                    }
                }
            });
        });
    }
}