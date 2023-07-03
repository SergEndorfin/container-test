package click.itkon.container.constant;

public final class Constants {

    public static final String OBJECT_A_MSG = ">>> NormalDependencyA";
    public static final String CIRCULAR_ERROR_MSG = "Circular dependency detected for class: ";
    public static final String QUALIFIER_DEPENDENCY_A_NAME = "QualifiedDependencyA";
    public static final String QUALIFIER_DEPENDENCY_B_NAME = "QualifiedDependencyB";
    public static final String POST_CONSTRUCTOR_ERROR_MSG = "Only one instance of PostConstructor is allowed.";
    public static final String BASE_PACKAGE = "click.itkon.container";

    private Constants() {
    }
}
