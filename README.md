# Vert.x Custom Generators prototypes

My experiments with `vertx-codegen`

## `@Futurify`

With `@Futurify` you can generate a delegate for you vertx interface that transforms a callbacks style interface to a future style interface. For example:

```java
@Futurify
public interface Bla {

  void methodD(Integer a, Handler<AsyncResult<Integer>> asyncResultHandler);

}
```

Generates:

```java
public class BlaFuturized {

  private final Bla delegate;

  public BlaFuturized(Bla delegate){
    this.delegate = delegate;
  }

  public Bla getDelegate(){
    return this.delegate;
  }
  public Future<java.lang.Integer> methodD(java.lang.Integer a){
    Future<java.lang.Integer> fut = Future.future();
    this.delegate.methodD(a, fut.completer());
    return fut;
  }
}
```