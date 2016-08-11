package abc.xyz;

import static java.lang.Math.E;

import java.util.concurrent.Callable;

class Imports implements Callable<Number> {

  public Number call() {
    return E * Math.PI;
  }
}
