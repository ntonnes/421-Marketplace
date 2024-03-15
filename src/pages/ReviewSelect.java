package pages;

import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import database.Model;
import database.users.*;
import main.Main;

public class ReviewSelect extends ColumnPage{
    private JTable table;
    private static int modelID;

    public ReviewSelect() {
        super("Select a model to review");
    }

    @Override
    protected void populateContent() {
        Customer customer = (Customer)(Main.user);
        String[] columnNames = {"Model ID", "Price", "Brand", "Stars"};

        Model[] models = customer.getModelsOrdered();
        Object[][] data = new String[models.length][4];
        for (int i = 0; i < models.length; i++) {
            Model model = models[i];
            data[i][0] = model.getModelID();
            data[i][1] = model.getPrice();
            data[i][2] = model.getBrand();
            data[i][3] = model.getStars();
        }

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        addComponent(scrollPane, 0.8);

        addBuffer(0.05);
        JButton selectButton = createButton("Select", BUTTON_GREEN, e -> select());
        addComponent(selectButton, 0.1);
        addBuffer();
    }

    private void select() {
        int row = table.getSelectedRow();
        if (row == -1) {
            UIUtils.showErr("Please select a model to review");
        } else {
            modelID = (int) table.getValueAt(row, 0);
            Main.goNew(new ReviewForm(), "Review Form");
        }
    }

   public static int getSelected() {
        return modelID;
    }

}