package click.itkon.container.handler;

import java.util.ArrayList;
import java.util.List;

public class MockHandler implements DependencyHandler {

    private final List<Class<?>> handledClasses = new ArrayList<>();

    @Override
    public void handle(Class<?> clazz) {
        handledClasses.add(clazz);
    }

    public List<Class<?>> getHandledClasses() {
        return handledClasses;
    }
}
