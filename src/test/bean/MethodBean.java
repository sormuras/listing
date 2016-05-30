package bean;

public interface MethodBean extends Annotated, Modified, Named {

  void setBody(String implementation);

  default void setReturnType(Class<?> type) {
    setReturnType(TypeBean.of(type));
  }

  default void setReturnType(Named type) {
    setReturnType(TypeBean.of(type));
  }

  void setReturnType(TypeBean type);
}
