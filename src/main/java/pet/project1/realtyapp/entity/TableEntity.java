package pet.project1.realtyapp.entity;

public class TableEntity {
    double time;
    double price;

    public TableEntity(double time, double price) {
        this.time = time;
        this.price = price;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
