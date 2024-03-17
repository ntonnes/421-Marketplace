package pages.search;

import java.util.Arrays;
import java.util.Comparator;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import main.Main;
import pages.utils.ColumnPage;
import pages.utils.Popup;

public class SearchSelect extends ColumnPage {
    private JTable table;
    private JComboBox<String> sortByBox;

    private Integer minStars;
    private Integer maxStars;
    private String brand;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer modelID;
    private String[][] data;

    public SearchSelect() {
        super("Search and Select");
    }

    @Override
    protected void populateContent() {

        // Create the "sort by" dropdown menu
        String[] sortByOptions = {
            "Best Selling",
            "Price Low to High",
            "Price High to Low",
            "Rating High to Low",
            "Rating Low to High",
            "Model ID",
            "Brand"
        };
        sortByBox = new JComboBox<>(sortByOptions);
        sortByBox.addActionListener(e -> sortData());
        JPanel sortByPanel = new JPanel();
        sortByPanel.add(new JLabel("Sort by:"));
        sortByPanel.add(sortByBox);
        addComponent(sortByPanel, 0.1);

                
        data = SearchForm.getData();
        minStars = SearchForm.getMinStars();
        maxStars = SearchForm.getMaxStars();
        brand = SearchForm.getBrand();
        minPrice = SearchForm.getMinPrice();
        maxPrice = SearchForm.getMaxPrice();
        modelID = SearchForm.getModelID();

        setWeights(.8,.1);
        String[] columnNames = {"Model ID", "Price", "Brand", "Rating"};

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        addComponent(scrollPane, 0.7);

        // Display the search parameters
        StringBuilder searchParameters = new StringBuilder();
        if (minStars != null) searchParameters.append("Min Stars: ").append(minStars).append(", ");
        if (maxStars != null) searchParameters.append("Max Stars: ").append(maxStars).append(", ");
        if (brand != null) searchParameters.append("Brand: ").append(brand).append(", ");
        if (minPrice != null) searchParameters.append("Min Price: ").append(minPrice).append(", ");
        if (maxPrice != null) searchParameters.append("Max Price: ").append(maxPrice).append(", ");
        if (modelID != null) searchParameters.append("Model ID: ").append(modelID);
    
        JLabel searchParametersLabel = new JLabel(searchParameters.toString());
        addComponent(searchParametersLabel, 0.1);

        addBuffer(0.05);
        JButton selectButton = createButton("Select", BUTTON_GREEN, e -> select());
        addComponent(selectButton, 0.1);
        addBuffer();

        resetWeights();
    }

    private void select() {
        int row = table.getSelectedRow();
        if (row == -1) {
            Popup.showErr("Please select a model");
        } else {
            String modelIDStr = (String) table.getValueAt(row, 0);
            try {
                modelID = Integer.parseInt(modelIDStr);
                Main.goNew(new ModelPage(modelID), "Model Page");
            } catch (NumberFormatException e) {
                Popup.showErr("Invalid model ID: " + modelIDStr);
            }
        }
    }

    private void sortData() {
        String sortBy = (String) sortByBox.getSelectedItem();
        Comparator<String[]> comparator;
        switch (sortBy) {
            case "Best Selling":
                // Assuming the best selling models have the highest model IDs
                comparator = Comparator.comparingInt(arr -> Integer.parseInt(arr[0]));
                break;
            case "Price Low to High":
                comparator = Comparator.comparingDouble(arr -> Double.parseDouble(arr[1]));
                break;
            case "Price High to Low":
                comparator = Comparator.comparingDouble((String[] arr) -> arr[1] == null || arr[1].isEmpty() ? 0 : Double.parseDouble(arr[1])).reversed();
                break;
            case "Rating High to Low":
                comparator = Comparator.comparingDouble((String[] arr) -> arr[3] == null || arr[3].isEmpty() ? 0 : Double.parseDouble(arr[3])).reversed();
                break;
            case "Rating Low to High":
                comparator = Comparator.comparingDouble(arr -> Integer.parseInt(arr[3]));
                break;
            case "Model ID":
                comparator = Comparator.comparingInt(arr -> Integer.parseInt(arr[0]));
                break;
            case "Brand":
                comparator = Comparator.comparing(arr -> arr[2]);
                break;
            default:
                throw new IllegalArgumentException("Invalid sort by option: " + sortBy);
        }
        Arrays.sort(data, comparator);

        // Update the table with the sorted data
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, new String[] {"Model ID", "Price", "Brand", "Rating"});
    }
}