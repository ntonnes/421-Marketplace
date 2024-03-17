package pages.review;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import database.Database;
import database.Model;
import database.users.*;
import java.awt.*;
import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

public class ReviewSelect extends ColumnPage{
    private JTable table;
    private static int modelID;

    public ReviewSelect() {
        super("Select a model to review");
    }

    @Override
    protected void populateContent() {
        setWeights(.8,.1);
        Customer customer = (Customer)(Main.user);
        String[] columnNames = {"Model ID", "Price", "Brand", "Stars"};

        Model[] models = customer.getModelsOrdered();

        try (Connection conn = DriverManager.getConnection(Database.DB_URL, Database.USER, Database.PASS)) {
            models = filterReviewed(models, conn);
        } catch (SQLException e) {
            System.out.println("Error while accessing the database");
            e.printStackTrace();
        }

        Object[][] data = new String[models.length][4];
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            data[i][0] = String.valueOf(model.getModelID());
            data[i][1] = "$" + String.valueOf(model.getPrice());
            data[i][2] = model.getBrand();
            data[i][3] = String.valueOf(model.getStars());
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = table.getTableHeader();
        header.setBackground(Color.DARK_GRAY);
        header.setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false); 
        scrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        addComponent(scrollPane, 0.7);

        addBuffer(0.05);
        JButton selectButton = createButton("Select", BUTTON_GREEN, e -> select());
        addComponent(selectButton, 0.1);
        addBuffer();

        resetWeights();
    }

    private void select() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Popup.showErr("Please select a model to review");
        } else {
            String modelIDStr = (String) table.getValueAt(row, 0);
            try {
                modelID = Integer.parseInt(modelIDStr);
                Main.goNew(new ReviewForm(), "Review Form");
            } catch (NumberFormatException e) {
                Popup.showErr("Invalid model ID: " + modelIDStr);
            }
        }
    }

   public static int getSelected() {
        return modelID;
    }

    private Model[] filterReviewed(Model[] models, Connection conn) throws SQLException {
        List<Model> unreviewedModels = new ArrayList<>();
        for (Model model : models) {
            try (PreparedStatement stmt = conn.prepareStatement(
                "SELECT COUNT(*) FROM Review WHERE userID = ? AND modelID = ?")) {
                stmt.setInt(1, Main.user.getUserID());
                stmt.setInt(2, model.getModelID());
                ResultSet rs = stmt.executeQuery();
                if (rs.next() && rs.getInt(1) == 0) {
                    unreviewedModels.add(model);
                }
            }
        }
        return unreviewedModels.toArray(new Model[0]);
    }

}