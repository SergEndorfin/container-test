package click.itkon.container.bean;

import click.itkon.container.annotation.Component;
import click.itkon.container.annotation.Qualifier;

import static click.itkon.container.constant.Constants.QUALIFIER_DEPENDENCY_A_NAME;
import static click.itkon.container.constant.Constants.QUALIFIER_DEPENDENCY_B_NAME;

@Component
public class QualifiedDependencyHolderAdvanced {

    private final QualifiedDependency dependencyA;
    private final QualifiedDependency dependencyB;

    public QualifiedDependencyHolderAdvanced(@Qualifier(QUALIFIER_DEPENDENCY_A_NAME) QualifiedDependency dependencyA,
                                             @Qualifier(QUALIFIER_DEPENDENCY_B_NAME)QualifiedDependency dependencyB) {
        this.dependencyA = dependencyA;
        this.dependencyB = dependencyB;
    }

    public QualifiedDependency getDependencyA() {
        return dependencyA;
    }

    public QualifiedDependency getDependencyB() {
        return dependencyB;
    }
}
