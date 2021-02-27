import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
class Isolation {
    List<DayStats> isolationDays;

    public double getStupidityConsequences() {
        double cumulativeRiskToContract = isolationDays.stream()
                .mapToDouble(DayStats::getRiskToContract)
                .sum();

        int avgInfections = (int) isolationDays.stream()
                .mapToInt(DayStats::getInfections)
                .average()
                .getAsDouble();

        int avgFutureInfections = (int) isolationDays.stream()
                .mapToInt(DayStats::getCumulativeFutureInfections)
                .average()
                .getAsDouble();

        return cumulativeRiskToContract * avgFutureInfections
                * Constants.IFR / avgInfections;
    }
}
