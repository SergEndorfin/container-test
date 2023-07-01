package click.itkon.container.bean;

import click.itkon.container.annotation.Component;
import click.itkon.container.annotation.PostConstructor;

@Component
public class PostConstructorInvalid {

    private boolean postConstructorCalled = false;

    @PostConstructor
    public void postConstructor() {
        postConstructorCalled = true;
    }

    @PostConstructor
    public void invalid() {
        postConstructorCalled = true;
    }

    public boolean isPostConstructorCalled() {
        return postConstructorCalled;
    }
}
