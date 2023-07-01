package click.itkon.container.bean;

import click.itkon.container.annotation.Component;

@Component
public class PostConstructor {

    private boolean postConstructorCalled = false;

    @click.itkon.container.annotation.PostConstructor
    public void postConstructor() {
        postConstructorCalled = true;
    }

    public boolean isPostConstructorCalled() {
        return postConstructorCalled;
    }
}
