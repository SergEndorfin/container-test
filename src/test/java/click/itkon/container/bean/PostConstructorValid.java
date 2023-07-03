package click.itkon.container.bean;

import click.itkon.container.annotation.Component;
import click.itkon.container.annotation.PostConstructor;

@Component
public class PostConstructorValid {

    private boolean postConstructorCalled = false;

    @PostConstructor
    public void postConstructor() {
        postConstructorCalled = true;
    }

    public boolean isPostConstructorCalled() {
        return postConstructorCalled;
    }
}
