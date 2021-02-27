import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Data
@RequiredArgsConstructor
class Epid {
    final List<ScenarioStep> scenario;
    final City city;

    private Map<Integer, DayStats> dayStats = new HashMap<>();
    private double r = Constants.R_0;
    private int currentInfectionsPerDay = Constants.INIT_INFECTIONS;

    public void printStats() {
        if (!isEnded()) {
            throw new IllegalStateException("Run the epid first!");
        }

        System.out.println("Abbreviations: " +
                "\nFDGs (Fat Diabetic Grannies saved) - estimated amount of lives saved due to isolation." +
                "\nDLs (Days of Life saved) - FDG * YLL * 365" +
                "\nWC (Average Worst Case Scenario) - average amount of future victims per infection");

        System.out.println("The wave lasted " + getDay() + " days");

        System.out.println("1 day of isolation:");
        int step = dayStats.size() / 10;
        for (int i = 1; i <= dayStats.size(); i+= step) {
            System.out.println("Day " + i + " (infections = " + dayStats.get(i).getInfections() + "):"
                    + "\n FDGs = " + formatDouble(dayStats.get(i).getStupidityConsequences())
                    + "\n DLSs = " + formatDouble(dgsToDls(dayStats.get(i).getStupidityConsequences()))
                    + "\n WC = " + formatDouble(dayStats.get(i).getPossibleDeaths())
            );
        }

        System.out.println("\nIsolating through the first week:");
        Isolation isolation = new Isolation(getFirstDays(7));
        System.out.println("FDGs = " + formatDouble(isolation.getStupidityConsequences()));
        System.out.println("DLs = " + formatDouble(dgsToDls(isolation.getStupidityConsequences())));

        System.out.println("\nIsolating through the worst week:");
        isolation = new Isolation(getWorstDays(7));
        System.out.println("FDGs = " + formatDouble(isolation.getStupidityConsequences()));
        System.out.println("DLs = " + formatDouble(dgsToDls(isolation.getStupidityConsequences())));

        System.out.println("\nIsolating through the whole wave:");
        isolation = new Isolation(new LinkedList<>(dayStats.values()));
        System.out.println("FDGs = " + formatDouble(isolation.getStupidityConsequences()));
        System.out.println("DLs = " + formatDouble(dgsToDls(isolation.getStupidityConsequences())));
    }

    public int getDay() {
        return dayStats.size() + 1;
    }

    public void run() {
        while (!isEnded()) {
            runDay();
        }
    }

    private void runDay() {
        if (isEnded()) {
            throw new IllegalStateException("Attempt to continue a finished epid");
        }

        int realInfections = city.infect(currentInfectionsPerDay, Constants.IFR);
        if (realInfections > 0) {
            dayStats.values().forEach(dayStat ->
                    dayStat.addCumulativeFutureInfections(realInfections));
            dayStats.put(getDay(), new DayStats(getRiskToContract(), realInfections));
        }
        scenario.forEach(scenarioStep -> scenarioStep.accept(this));
        currentInfectionsPerDay *= r;
    }

    private boolean isEnded() {
        return currentInfectionsPerDay == 0 || city.getHealthyCount() == 0;
    }

    private double getRiskToContract() {
        return currentInfectionsPerDay * 1f / city.getCityPopulation();
    }

    private List<DayStats> getWorstDays(int days) {
        List<DayStats> result = new LinkedList<>(dayStats.values());

        result.sort(Comparator.comparingDouble(DayStats::getRiskToContract));
        if (result.size() <= days) {
            return result;
        }
        return result.subList(result.size() - days, result.size());
    }

    private List<DayStats> getFirstDays(int days) {
        List<DayStats> result = new LinkedList<>();

        for (int i = 1; i <= Math.min(days, dayStats.size()); i++) {
            result.add(dayStats.get(i));
        }
        return result;
    }

    private double dgsToDls (double deaths) {
        return deaths * Constants.YLL * Constants.DAYS_IN_A_YEAR;
    }

    private String formatDouble (double val) {
        return BigDecimal.valueOf(val)
                .setScale(Constants.PRECISION, RoundingMode.HALF_EVEN)
                .toPlainString();
    }
}
