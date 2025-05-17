package pet.project1.realtyapp.entity;

public class MainCharacteristicsTableEntity {
    private int time;
    private double price;
    private double basicAbsoluteGrowth;
    private double chainAbsoluteGrowth;
    private double chainGrowthRates;
    private double basicGrowthRates;
    private double chainGrowthRates2;
    private double basicGrowthRates2;
    private double absoluteValue;
    private double relativeAcceleration;
    private double advanceRatio;

    public MainCharacteristicsTableEntity(int time, double price) {
        this.time = time;
        this.price = price;
    }

    public MainCharacteristicsTableEntity(int time, double price, double basicAbsoluteGrowth,
                                          double chainAbsoluteGrowth, double chainGrowthRates, double basicGrowthRates,
                                          double chainGrowthRates2, double basicGrowthRates2, double absoluteValue,
                                          double relativeAcceleration, double advanceRatio) {
        this.time = time;
        this.price = price;
        this.basicAbsoluteGrowth = basicAbsoluteGrowth;
        this.chainAbsoluteGrowth = chainAbsoluteGrowth;
        this.chainGrowthRates = chainGrowthRates;
        this.basicGrowthRates = basicGrowthRates;
        this.chainGrowthRates2 = chainGrowthRates2;
        this.basicGrowthRates2 = basicGrowthRates2;
        this.absoluteValue = absoluteValue;
        this.relativeAcceleration = relativeAcceleration;
        this.advanceRatio = advanceRatio;
    }

    public MainCharacteristicsTableEntity(int time, double price, double basicAbsoluteGrowth, double chainAbsoluteGrowth,
                                          double chainGrowthRates, double basicGrowthRates, double chainGrowthRates2,
                                          double basicGrowthRates2, double absoluteValue) {
        this.time = time;
        this.price = price;
        this.basicAbsoluteGrowth = basicAbsoluteGrowth;
        this.chainAbsoluteGrowth = chainAbsoluteGrowth;
        this.chainGrowthRates = chainGrowthRates;
        this.basicGrowthRates = basicGrowthRates;
        this.chainGrowthRates2 = chainGrowthRates2;
        this.basicGrowthRates2 = basicGrowthRates2;
        this.absoluteValue = absoluteValue;
    }

    public int getTime() {

        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setBasicAbsoluteGrowth(double basicAbsoluteGrowth) {
        this.basicAbsoluteGrowth = basicAbsoluteGrowth;
    }

    public void setChainAbsoluteGrowth(double chainAbsoluteGrowth) {
        this.chainAbsoluteGrowth = chainAbsoluteGrowth;
    }

    public void setChainGrowthRates(double chainGrowthRates) {
        this.chainGrowthRates = chainGrowthRates;
    }

    public void setBasicGrowthRates(double basicGrowthRates) {
        this.basicGrowthRates = basicGrowthRates;
    }

    public void setChainGrowthRates2(double chainGrowthRates2) {
        this.chainGrowthRates2 = chainGrowthRates2;
    }

    public void setBasicGrowthRates2(double basicGrowthRates2) {
        this.basicGrowthRates2 = basicGrowthRates2;
    }

    public void setAbsoluteValue(double absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    public void setRelativeAcceleration(double relativeAcceleration) {
        this.relativeAcceleration = relativeAcceleration;
    }

    public void setAdvanceRatio(double advanceRatio) {
        this.advanceRatio = advanceRatio;
    }

    public double getPrice() {
        return price;
    }

    public double getBasicAbsoluteGrowth() {
        return basicAbsoluteGrowth;
    }

    public double getChainAbsoluteGrowth() {
        return chainAbsoluteGrowth;
    }

    public double getChainGrowthRates() {
        return chainGrowthRates;
    }

    public double getBasicGrowthRates() {
        return basicGrowthRates;
    }

    public double getChainGrowthRates2() {
        return chainGrowthRates2;
    }

    public double getBasicGrowthRates2() {
        return basicGrowthRates2;
    }

    public double getAbsoluteValue() {
        return absoluteValue;
    }

    public double getRelativeAcceleration() {
        return relativeAcceleration;
    }

    public double getAdvanceRatio() {
        return advanceRatio;
    }
}
