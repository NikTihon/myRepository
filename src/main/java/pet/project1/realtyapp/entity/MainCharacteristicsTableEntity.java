package pet.project1.realtyapp.entity;

public class MainCharacteristicsTableEntity extends TableEntity{

    private double basicAbsoluteGrowth;
    private double chainAbsoluteGrowth;
    private double chainGrowthRates;
    private double basicGrowthRates;
    private double chainGrowthRates2;
    private double basicGrowthRates2;
    private double absoluteValue;
    private double relativeAcceleration;
    private double advanceRatio;

    public MainCharacteristicsTableEntity(double time, double price, double basicAbsoluteGrowth,
                                          double chainAbsoluteGrowth, double chainGrowthRates, double basicGrowthRates,
                                          double chainGrowthRates2, double basicGrowthRates2, double absoluteValue,
                                          double relativeAcceleration, double advanceRatio) {
        super(time, price);
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

    public MainCharacteristicsTableEntity(double time, double price, double basicAbsoluteGrowth,
                                          double chainAbsoluteGrowth, double chainGrowthRates, double basicGrowthRates,
                                          double chainGrowthRates2, double basicGrowthRates2, double absoluteValue) {
        super(time, price);
        this.basicAbsoluteGrowth = basicAbsoluteGrowth;
        this.chainAbsoluteGrowth = chainAbsoluteGrowth;
        this.chainGrowthRates = chainGrowthRates;
        this.basicGrowthRates = basicGrowthRates;
        this.chainGrowthRates2 = chainGrowthRates2;
        this.basicGrowthRates2 = basicGrowthRates2;
        this.absoluteValue = absoluteValue;
    }

    public MainCharacteristicsTableEntity(double time, double price) {
        super(time, price);
    }

    public double getBasicAbsoluteGrowth() {
        return basicAbsoluteGrowth;
    }

    public void setBasicAbsoluteGrowth(double basicAbsoluteGrowth) {
        this.basicAbsoluteGrowth = basicAbsoluteGrowth;
    }

    public double getChainAbsoluteGrowth() {
        return chainAbsoluteGrowth;
    }

    public void setChainAbsoluteGrowth(double chainAbsoluteGrowth) {
        this.chainAbsoluteGrowth = chainAbsoluteGrowth;
    }

    public double getChainGrowthRates() {
        return chainGrowthRates;
    }

    public void setChainGrowthRates(double chainGrowthRates) {
        this.chainGrowthRates = chainGrowthRates;
    }

    public double getBasicGrowthRates() {
        return basicGrowthRates;
    }

    public void setBasicGrowthRates(double basicGrowthRates) {
        this.basicGrowthRates = basicGrowthRates;
    }

    public double getChainGrowthRates2() {
        return chainGrowthRates2;
    }

    public void setChainGrowthRates2(double chainGrowthRates2) {
        this.chainGrowthRates2 = chainGrowthRates2;
    }

    public double getBasicGrowthRates2() {
        return basicGrowthRates2;
    }

    public void setBasicGrowthRates2(double basicGrowthRates2) {
        this.basicGrowthRates2 = basicGrowthRates2;
    }

    public double getAbsoluteValue() {
        return absoluteValue;
    }

    public void setAbsoluteValue(double absoluteValue) {
        this.absoluteValue = absoluteValue;
    }

    public double getRelativeAcceleration() {
        return relativeAcceleration;
    }

    public void setRelativeAcceleration(double relativeAcceleration) {
        this.relativeAcceleration = relativeAcceleration;
    }

    public double getAdvanceRatio() {
        return advanceRatio;
    }

    public void setAdvanceRatio(double advanceRatio) {
        this.advanceRatio = advanceRatio;
    }
}
