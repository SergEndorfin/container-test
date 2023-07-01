package click.itkon.container.bean;

import click.itkon.container.annotation.Autowired;
import click.itkon.container.annotation.Component;

@Component
public class CircularDependencyA {
    private final CircularDependencyB dependencyB;

    @Autowired
    public CircularDependencyA(CircularDependencyB dependencyB) {
        this.dependencyB = dependencyB;
    }

    public CircularDependencyB getDependencyB() {
        return dependencyB;
    }
}
