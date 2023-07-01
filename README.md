## Dependency container with implementation based on annotations (Something like Spring).
___
### List of annotations and their description.
1. Component - applied to the class. The class marked with this annotation must be registered with the container.
2. Autowired - applied to the constructor. If there are several constructors in the class, it should be possible to mark which constructor should be used with a given annotation. Otherwise, the first available constructor is used.
3. Qualifier - applied to constructor parameters, in case you need to add another class instance. Identifies the parameter by string.
4. PostConstructor - applies to a method that is called immediately after an instance has been created.

### Requirements:
1. The container must be insulated.
2. Access to instances must be carried out by the container method, by class.
3. The dependency of the component should be automatically included in it.
4. Ability to include handlers before and after adding to the container.
5. Detailed and understandable information about the circular loop spawn point is included.
6. Detailed work with ClassLoader so that different loaders do not break the implementation.

### Note:
JUnit 5 is the only library is used.
The code is compiled for educational purposes only. There is no goal to implement additional requirements other than those specified. Like design patterns or something...

---
#### How to use:
Create instance of a container:
> DependencyContainer container = new DependencyContainer();

Register dependency in it:
> container.register(SomeClass.class);
> 
> or with handlers:
> 
> container.register(SomeClass.class, preAddHandler, postAddHandler);

Handlers could be added at any time by using methods:

> container.addPreAddHandler(clazz -> System.out.println("Pre-" + clazz.getSimpleName()));
> 
> container.addPostAddHandler(clazz -> System.out.println("Post-" + clazz.getSimpleName()));

Getting object from container:  
> var someClass = container.getInstance(SomeClass.class);

If the Component-class is not previously registered when trying to get it from the container, the Component-class will be registered automatically.

See more samples in the **test** folder.
