package click.itkon.container.bean;

import click.itkon.container.annotation.Component;

import static click.itkon.container.constant.Constants.OBJECT_A_MSG;

@Component
public class NormalDependencyA {

    public String getMessage() {
        return OBJECT_A_MSG;
    }
}
