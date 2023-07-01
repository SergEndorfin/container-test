package click.itkon.container.dependency;

import click.itkon.container.annotation.Autowired;
import click.itkon.container.annotation.Component;
import click.itkon.container.annotation.PostConstructor;
import click.itkon.container.annotation.Qualifier;
import click.itkon.container.handler.DependencyHandler;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static click.itkon.container.constant.Constants.CIRCULAR_ERROR_MSG;
import static click.itkon.container.constant.Constants.POST_CONSTRUCTOR_ERROR_MSG;

public class DependencyContainer {

    private final Map<ClassLoader, Map<Class<?>, Object>> instancesContainer;
    private final Set<Class<?>> circularDependencySet;
    private final List<DependencyHandler> preAddHandlers;
    private final List<DependencyHandler> postAddHandlers;

    public DependencyContainer() {
        this.instancesContainer = new ConcurrentHashMap<>();
        this.circularDependencySet = Collections.newSetFromMap(new ConcurrentHashMap<>());
        this.preAddHandlers = new ArrayList<>();
        this.postAddHandlers = new ArrayList<>();
    }

    public <T> void register(Class<T> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        Map<Class<?>, Object> instances = instancesContainer.computeIfAbsent(classLoader, k -> new ConcurrentHashMap<>());

        if (clazz.isAnnotationPresent(Component.class) && !instances.containsKey(clazz)) {
            try {
                invokePreAddHandlers(clazz);
                createInstance(clazz, instances);
                invokePostAddHandlers(clazz);
            } catch (InstantiationException | IllegalAccessException |
                     InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
    }

    public <T> void register(Class<T> clazz, DependencyHandler preAddHandler, DependencyHandler postAddHandler) {
        preAddHandlers.add(preAddHandler);
        postAddHandlers.add(postAddHandler);
        register(clazz);
    }

    public void addPreAddHandler(DependencyHandler handler) {
        preAddHandlers.add(handler);
    }

    public void addPostAddHandler(DependencyHandler handler) {
        postAddHandlers.add(handler);
    }

    public <T> T getInstance(Class<T> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        if (classLoader == null) return null;

        Map<Class<?>, Object> instances = instancesContainer.computeIfAbsent(classLoader, k -> new ConcurrentHashMap<>());

        if (instances.containsKey(clazz)) {
            return clazz.cast(instances.get(clazz));
        } else {
            register(clazz);
            if (instances.containsKey(clazz)) {
                return clazz.cast(instances.get(clazz));
            }
        }
        return null;
    }

    //  Specific unnecessary methods:  ///////////////////////////////////////
    public void printDependencyGraph() {
        System.out.println("Dependency Graph:");
        instancesContainer.entrySet().stream()
                .flatMap(classLoaderEntry -> classLoaderEntry.getValue().entrySet().stream())
                .forEach(entry -> {
                    Class<?> clazz = entry.getKey();
                    List<Class<?>> dependencies = Arrays.stream(clazz.getDeclaredFields())
                            .map(Field::getType)
                            .collect(Collectors.toList());
                    System.out.println(clazz.getSimpleName() + " -> " + dependencies);
                });
    }

    public void printClassLoaderHierarchy() {
        System.out.println("ClassLoader Hierarchy:");
        instancesContainer.keySet().forEach(classLoader -> {
            ClassLoader currentLoader = classLoader;
            while (currentLoader != null) {
                System.out.println(currentLoader.getClass().getName());
                currentLoader = currentLoader.getParent();
            }
        });
    }

    //  Private methods:  //////////////////////////////////////
    private <T> void createInstance(Class<T> clazz, Map<Class<?>, Object> instances)
            throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (circularDependencySet.contains(clazz)) {
            printCircularDependencies();
            throw new IllegalStateException(CIRCULAR_ERROR_MSG + clazz.getName());
        }
        circularDependencySet.add(clazz);
        checkThereIsNoMoreThanOnePostConstructorAnnotation(clazz);
        Constructor<?> constructor = getConstructor(clazz);
        Object[] constructorArgs = defineDependencies(constructor);

        T instance = createInstance(clazz, constructor, constructorArgs);
        invokePostConstructor(clazz, instance);

        instances.put(clazz, instance);
        circularDependencySet.remove(clazz);
    }

    private <T> void checkThereIsNoMoreThanOnePostConstructorAnnotation(Class<T> clazz) {
        long annotationCount = Arrays.stream(clazz.getDeclaredMethods())
                .flatMap(method -> Arrays.stream(method.getAnnotations()))
                .filter(annotation -> annotation.annotationType() == PostConstructor.class)
                .count();
        if (annotationCount > 1) {
            throw new IllegalArgumentException(POST_CONSTRUCTOR_ERROR_MSG);
        }
    }

    private <T> Constructor<?> getConstructor(Class<T> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        Constructor<?> constructor = getAutowiredConstructor(constructors);
        if (constructor == null) {
            constructor = constructors[0];
        }
        return constructor;
    }

    private Object[] defineDependencies(Constructor<?> constructor) {
        Parameter[] parameters = constructor.getParameters();
        Object[] args = new Object[parameters.length];
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            if (parameter.isAnnotationPresent(Qualifier.class)) {
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                args[i] = getInstanceByQualifier(parameterType, qualifier.value());
            } else {
                args[i] = getInstance(parameterType);
            }
        }
        return args;
    }

    private static <T> T createInstance(Class<T> clazz, Constructor<?> constructor, Object[] constructorArgs)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return clazz.getDeclaredConstructor(constructor.getParameterTypes()).newInstance(constructorArgs);
    }

    private <T> void invokePostConstructor(Class<T> clazz, T instance) {
        Optional<Method> postConstructMethod = getPostConstructMethod(clazz);
        if (postConstructMethod.isPresent()) {
            try {
                postConstructMethod.get().invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private Constructor<?> getAutowiredConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }
        return null;
    }

    private <T> Optional<Method> getPostConstructMethod(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(PostConstructor.class))
                .findFirst();
    }

    private <T> T getInstanceByQualifier(Class<T> clazz, String qualifierValue) {
        return getInstanceByQualifierIfRegistered(clazz, qualifierValue)
                .orElseThrow(
                        () -> new IllegalStateException("No instance found for class: " + clazz.getName() +
                                " with qualifier: " + qualifierValue)
                );
    }

    private <T> Optional<T> getInstanceByQualifierIfRegistered(Class<T> clazz, String qualifierValue) {
        return instancesContainer.get(clazz.getClassLoader())
                .entrySet().stream()
                .filter(entry -> isObjectOrImplementation(clazz, entry))
                .map(entry -> clazz.cast(entry.getValue()))
                .filter(instance -> hasMatchingQualifier(instance, qualifierValue))
                .findFirst();
    }

    private boolean hasMatchingQualifier(Object instance, String qualifierValue) {
        return (instance.getClass().getSimpleName().equals(qualifierValue)) ||
                Arrays.stream(instance.getClass().getInterfaces())
                        .map(Class::getSimpleName)
                        .anyMatch(className -> className.equals(qualifierValue));
    }

    private <T> boolean isObjectOrImplementation(Class<T> clazz, Map.Entry<Class<?>, Object> entry) {
        boolean isObjectItself = entry.getKey().equals(clazz);
        boolean isImplementation = entry.getKey().getInterfaces().length > 0;
        return isObjectItself || isImplementation;
    }

    private void invokePreAddHandlers(Class<?> clazz) {
        preAddHandlers.forEach(handler -> handler.handle(clazz));
    }

    private void invokePostAddHandlers(Class<?> clazz) {
        postAddHandlers.forEach(handler -> handler.handle(clazz));
    }

    private void printCircularDependencies() {
        System.err.println("Circular Dependencies:");
        circularDependencySet.forEach(clazz -> System.err.println(clazz.getSimpleName()));
    }
}