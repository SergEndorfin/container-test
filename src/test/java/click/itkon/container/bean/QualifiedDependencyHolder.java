package click.itkon.container.bean;

import click.itkon.container.annotation.Component;
import click.itkon.container.annotation.Qualifier;

import static click.itkon.container.constant.Constants.QUALIFIER_DEPENDENCY_B_NAME;

@Component
public class QualifiedDependencyHolder {

    private final QualifiedDependency qualifiedDependency;

    public QualifiedDependencyHolder(@Qualifier(QUALIFIER_DEPENDENCY_B_NAME) QualifiedDependency qualifiedDependency) {
        this.qualifiedDependency = qualifiedDependency;
    }

    public QualifiedDependency getQualifiedDependency() {
        return qualifiedDependency;
    }
}
