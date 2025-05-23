package pet.project1.realtyapp.entity;

public class FunctionTableEntity extends TableEntity {

    private double lvl;
    private double error;

    public FunctionTableEntity(double time, double price, double lvl, double error) {
        super(time, price);
        this.lvl = lvl;
        this.error = error;
    }

    public FunctionTableEntity(double time, double price) {
        super(time, price);
    }

    public double getLvl() {
        return lvl;
    }

    public void setLvl(double lvl) {
        this.lvl = lvl;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }

}
