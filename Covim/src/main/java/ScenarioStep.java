import lombok.RequiredArgsConstructor;

import java.util.function.Consumer;
import java.util.function.Predicate;

@RequiredArgsConstructor
class ScenarioStep implements Consumer<Epid> {

    public static Predicate<Epid> conditionDay(int day) {
        return epid -> epid.getDay() == day;
    }

    public static Predicate<Epid> conditionInfectionsHigherThan(int infections) {
        return epid -> epid.getCurrentInfectionsPerDay() >= infections;
    }

    public static Predicate<Epid> conditionInfectionsLowerThan(int infections) {
        return epid -> epid.getCurrentInfectionsPerDay() < infections;
    }

    public static Predicate<Epid> conditionPercentageHigherThan(double infectionsPercent) {
        return epid -> epid.getCurrentInfectionsPerDay() * 100.0 / epid.getCity().getCityPopulation()
                >= infectionsPercent;
    }

    public static Predicate<Epid> conditionPercentageLowerThan(double infectionsPercent) {
        return epid -> epid.getCurrentInfectionsPerDay() * 100.0 / epid.getCity().getCityPopulation()
                < infectionsPercent;
    }

    public static Consumer<Epid> changeR(double r) {
        return epid -> epid.setR(r);
    }

    public ScenarioStep(Predicate<Epid> condition, Consumer<Epid> action, int delay) {
        this(condition, action);
        this.counter = delay;
    }

    private final Predicate<Epid> condition;
    private final Consumer<Epid> action;
    private int counter;
    private boolean triggered;

    public void accept(Epid epid) {
        triggered = triggered || condition.test(epid);
        if (triggered && (counter-- == 0)) {
            action.accept(epid);
        }
    }
}
