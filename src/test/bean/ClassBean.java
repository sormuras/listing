package bean;

public interface ClassBean extends Annotated, Modified, Named {

  MethodBean declareMethod();
}
