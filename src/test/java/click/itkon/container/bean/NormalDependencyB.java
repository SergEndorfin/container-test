package click.itkon.container.bean;

import click.itkon.container.annotation.Autowired;
import click.itkon.container.annotation.Component;

@Component
public class NormalDependencyB {

    private final NormalDependencyA normalDependencyA;

    @Autowired
    public NormalDependencyB(NormalDependencyA normalDependencyA) {
        this.normalDependencyA = normalDependencyA;
    }

    public String getMessage() {
        return normalDependencyA.getMessage();
    }

    public NormalDependencyA getDependencyA() {
        return normalDependencyA;
    }
}
