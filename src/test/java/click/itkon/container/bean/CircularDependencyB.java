package click.itkon.container.bean;

import click.itkon.container.annotation.Autowired;
import click.itkon.container.annotation.Component;

@Component
public class CircularDependencyB {

    private final CircularDependencyA dependencyA;

    @Autowired
    public CircularDependencyB(CircularDependencyA dependencyA) {
        this.dependencyA = dependencyA;
    }

    public CircularDependencyA getDependencyA() {
        return dependencyA;
    }
}
