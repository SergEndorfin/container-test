package click.itkon.container.bean;

import click.itkon.container.annotation.Component;

@Component
public class QualifiedDependencyA implements QualifiedDependency {

    private final String message;

    public QualifiedDependencyA(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
