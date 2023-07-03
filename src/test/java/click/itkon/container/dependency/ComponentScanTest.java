package click.itkon.container.dependency;

import click.itkon.container.bean.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static click.itkon.container.constant.Constants.*;
import static org.junit.jupiter.api.Assertions.*;

class ComponentScanTest {

    static DependencyContainer container;

    @BeforeAll
    static void setUp() {
        container = new DependencyContainer();
        container.scanAndInitializeContainer(BASE_PACKAGE);
    }

    @AfterAll
    static void tearDown() {
        container = null;
    }

    @Test
    void testDependencyInjection() {
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
    void testPostConstructors() {
        var instance = container.getInstance(PostConstructorValid.class);
        assertNotNull(instance);
        assertTrue(instance.isPostConstructorCalled());
    }

    @Test
    void testQualifierAnnotationWithOneDependency() {
        var dependencyHolder = container.getInstance(QualifiedDependencyHolder.class);
        assertNotNull(dependencyHolder);
        var qualifiedB = dependencyHolder.getQualifiedDependency();
        assertNotNull(qualifiedB);
        assertEquals(QUALIFIER_DEPENDENCY_B_NAME, qualifiedB.getClass().getSimpleName());
    }

    @Test
    void given2DependenciesWithSameType_whenInjected_thenDefinedByQualifiers() {
        var dependencyHolder = container.getInstance(QualifiedDependencyHolderAdvanced.class);
        assertNotNull(dependencyHolder);

        var qualifiedA = dependencyHolder.getDependencyA();
        assertNotNull(qualifiedA);
        assertEquals(QUALIFIER_DEPENDENCY_A_NAME, qualifiedA.getClass().getSimpleName());

        var qualifiedB = dependencyHolder.getDependencyB();
        assertNotNull(qualifiedB);
        assertEquals(QUALIFIER_DEPENDENCY_B_NAME, qualifiedB.getClass().getSimpleName());
    }
}
