package pet.project1.realtyapp.entity;

public class MovingAverageTableEntity {

    private int time;
    private double price;
    private double movingAverage;

    public MovingAverageTableEntity(int time, double price) {
        this.time = time;
        this.price = price;
    }

    public MovingAverageTableEntity(int time, double price, double movingAverage) {
        this.time = time;
        this.price = price;
        this.movingAverage = movingAverage;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getMovingAverage() {
        return movingAverage;
    }

    public void setMovingAverage(double movingAverage) {
        this.movingAverage = movingAverage;
    }
}
