package bean;

public class TypeBean {

  public static TypeBean of(Class<?> type) {
    return new TypeBean(type.getCanonicalName());
  }

  public static TypeBean of(Named named) {
    return new TypeBean(named.getName());
  }

  private final String name;

  public TypeBean(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "TypeBean [name=" + name + "]";
  }
}
