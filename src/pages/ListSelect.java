package pages;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public abstract class ListSelect extends Page {
    protected JTable table;

    public ListSelect(Page lastPage, String name) {
        super(lastPage, name);
        table = new JTable();
    }

    protected void populateTable(Object[][] data, String[] columnNames) {
        table.setModel(new DefaultTableModel(data, columnNames));
    }

    protected void displayTable() {

        JScrollPane scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected void populateContent() {
        populateTable(getData(), getColumnNames());
        displayTable();
        JButton selectButton = UIUtils.createButton("Select", e -> select(), BUTTON_BLUE, new Dimension(0, 35));
        UIUtils.addToGrid(content, selectButton, null);
    }

    protected void select(){
        int row = table.getSelectedRow();
        if (row == -1) {
            UIUtils.showErr("Please select a row.");
            return;
        }
        Object[] rowData = new Object[table.getColumnCount()];
        for (int i = 0; i < rowData.length; i++) {
            rowData[i] = table.getValueAt(row, i);
        }
        onSelection(rowData);
    };

    // Abstract methods to be implemented by subclasses
    protected abstract Object[][] getData();
    protected abstract String[] getColumnNames();
    protected abstract void onSelection(Object[] rowData);
}