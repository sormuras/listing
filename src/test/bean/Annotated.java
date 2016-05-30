package bean;

import java.util.List;

public interface Annotated {

  default void addAnnotation(Class<?> type) {
    addAnnotation(TypeBean.of(type));
  }

  default void addAnnotation(Named type) {
    addAnnotation(TypeBean.of(type));
  }

  void addAnnotation(TypeBean type);

  List<?> getAnnotations();
}
