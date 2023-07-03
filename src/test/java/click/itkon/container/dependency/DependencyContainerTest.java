package click.itkon.container.dependency;

import click.itkon.container.annotation.PostConstructor;
import click.itkon.container.bean.*;
import click.itkon.container.handler.MockHandler;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static click.itkon.container.constant.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class DependencyContainerTest {

    DependencyContainer container;

    @BeforeEach
    void setUp() {
        container = new DependencyContainer();
    }

    @AfterEach
    void tearDown() {
        container = null;
    }

    @Test
    void testDependencyInjection() {
        container.register(NormalDependencyA.class);
        container.register(NormalDependencyB.class);

        var dependencyA = container.getInstance(NormalDependencyA.class);
        assertNotNull(dependencyA);

        var dependencyB = container.getInstance(NormalDependencyB.class);
        assertNotNull(dependencyB);

        var dependencyAFromB = dependencyB.getDependencyA();
        assertNotNull(dependencyAFromB);

        assertEquals(dependencyA, dependencyAFromB);
        assertEquals(OBJECT_A_MSG, dependencyB.getMessage());
    }

    @Test
    void givenUnregisteredClassComponent_whenGet_thenRegisterAndGet() {
        assertNotNull(container.getInstance(NormalDependencyA.class));
    }

    @Test
    void testPostConstructors() {
        container.register(PostConstructor.class);
        var instance = container.getInstance(PostConstructorValid.class);
        assertTrue(instance.isPostConstructorCalled());
    }

    @Test
    void testQualifierAnnotationWithOneDependency() {
        container.register(QualifiedDependencyA.class);
        container.register(QualifiedDependencyB.class);
        container.register(QualifiedDependencyHolder.class);

        var dependencyHolder = container.getInstance(QualifiedDependencyHolder.class);
        assertNotNull(dependencyHolder);

        var qualifiedB = dependencyHolder.getQualifiedDependency();
        assertNotNull(qualifiedB);
        assertEquals(QUALIFIER_DEPENDENCY_B_NAME, qualifiedB.getClass().getSimpleName());
    }

    @Test
    void givenUnregisteredClassWithQualifierAnnotation_whenTryInject_thenThrowException() {
        var exception = assertThrows(
                IllegalStateException.class,
                () -> container.register(QualifiedDependencyHolder.class)
        );
        var expectedMessage = "No instance found for class: " + QualifiedDependency.class.getName() +
                " with qualifier: " + QUALIFIER_DEPENDENCY_B_NAME;
        var actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void given2DependenciesWithSameType_whenInjected_thenDefinedByQualifiers() {
        container.register(QualifiedDependencyA.class);
        container.register(QualifiedDependencyB.class);
        container.register(QualifiedDependencyHolderAdvanced.class);

        var dependencyHolder = container.getInstance(QualifiedDependencyHolderAdvanced.class);
        assertNotNull(dependencyHolder);

        var qualifiedA = dependencyHolder.getDependencyA();
        assertNotNull(qualifiedA);
        assertEquals(QUALIFIER_DEPENDENCY_A_NAME, qualifiedA.getClass().getSimpleName());

        var qualifiedB = dependencyHolder.getDependencyB();
        assertNotNull(qualifiedB);
        assertEquals(QUALIFIER_DEPENDENCY_B_NAME, qualifiedB.getClass().getSimpleName());
    }

    @Test
    void testRegisterWithHandlers() {
        var preAddHandler = new MockHandler();
        var postAddHandler = new MockHandler();
        container.register(NormalDependencyA.class, preAddHandler, postAddHandler);
        assertEquals(1, preAddHandler.getHandledClasses().size());
        assertEquals(NormalDependencyA.class, preAddHandler.getHandledClasses().get(0));
        assertEquals(1, postAddHandler.getHandledClasses().size());
        assertEquals(NormalDependencyA.class, postAddHandler.getHandledClasses().get(0));
    }

    @Test
    void testDependencyHandlersArbitrarily() {
        container.addPreAddHandler(clazz -> System.out.println("Pre-Add class: " + clazz.getSimpleName()));
        container.addPostAddHandler(clazz -> System.out.println("Post-Add added: " + clazz.getSimpleName()));
        container.register(NormalDependencyA.class);
        container.register(NormalDependencyB.class);
        var normalDependencyB = container.getInstance(NormalDependencyB.class);
        assertNotNull(normalDependencyB);
    }

    @Test
    void testPrintClassLoaderHierarchy() {
        container.register(QualifiedDependencyA.class);
        container.register(QualifiedDependencyB.class);
        container.register(QualifiedDependencyHolderAdvanced.class);
        container.printClassLoaderHierarchy();
    }

    @Test
    void testPrintDependencyGraph() {
        container.register(QualifiedDependencyA.class);
        container.register(QualifiedDependencyB.class);
        container.register(QualifiedDependencyHolderAdvanced.class);
        container.printDependencyGraph();
    }

    @Test
    void givenPrimitive_whenGet_thenReturnNull() {
        assertNull(container.getInstance(String.class));
    }
}