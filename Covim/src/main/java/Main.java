import java.util.*;

public class Main {

    public static void main(String[] args) {
        City chisinau = new City(Constants.P);

        List<ScenarioStep> governmentActions = new LinkedList<>();
        governmentActions.add(new ScenarioStep(
                ScenarioStep.conditionPercentageHigherThan(0.2),
                ScenarioStep.changeR(Constants.R_PLATO)
        ));
        governmentActions.add(new ScenarioStep(
                ScenarioStep.conditionPercentageHigherThan(0.2),
                ScenarioStep.changeR(Constants.R_LOCKDOWN),
                7
        ));

        Epid covid = new Epid(governmentActions, chisinau);
        covid.run();

        covid.printStats();
    }
}

