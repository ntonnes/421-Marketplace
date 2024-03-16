package database;

// A class to represent a product key
public class ProductKey {
    private final int modelID;
    private final int serialNo;

    public ProductKey(int modelID, int serialNo) {
        this.modelID = modelID;
        this.serialNo = serialNo;
    }

    public int getModelID() {
        return modelID;
    }

    public int getSerialNo() {
        return serialNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductKey that = (ProductKey) o;
        return modelID == that.modelID && serialNo == that.serialNo;
    }

    @Override
    public int hashCode() {
        return 31 * modelID + serialNo;
    }
}