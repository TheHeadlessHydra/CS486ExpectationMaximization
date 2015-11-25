/**
 * Created by Serj on 23/11/2015.
 */
public class Probability {
    private final double nonNormalizedProbability;
    private final double normalizedProbability;

    public Probability(double nonNormalizedProbability, double normalizedProbability) {
        this.nonNormalizedProbability = nonNormalizedProbability;
        this.normalizedProbability = normalizedProbability;
    }

    public double getNonNormalizedProbability() {
        return nonNormalizedProbability;
    }

    public double getNormalizedProbability() {
        return normalizedProbability;
    }

    @Override
    public String toString() {
        return "Probability{" +
                "nonNormalizedProbability=" + nonNormalizedProbability +
                ", normalizedProbability=" + normalizedProbability +
                '}';
    }
}
