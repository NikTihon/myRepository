package pet.project1.realtyapp.entity;

public class MovingAverageTableEntity extends TableEntity {

    private double movingAverage;

    public MovingAverageTableEntity(double time, double price, double movingAverage) {
        super(time, price);
        this.movingAverage = movingAverage;
    }

    public MovingAverageTableEntity(double time, double price) {
        super(time, price);
    }

    public double getMovingAverage() {
        return movingAverage;
    }

    public void setMovingAverage(double movingAverage) {
        this.movingAverage = movingAverage;
    }
}
