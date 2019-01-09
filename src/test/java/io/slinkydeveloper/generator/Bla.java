package io.slinkydeveloper.generator;

import io.slinkydeveloper.vertx.generators.Futurify;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Date;

@Futurify
public interface Bla {

  static void someStatic() {}

  void methodA(Integer a);

  //Date methodB(Integer b);

  void methodC(Handler<AsyncResult<Integer>> asyncResultHandler);

  void methodD(Integer a, Handler<AsyncResult<Integer>> asyncResultHandler);

  Future<Integer> methodE();

}
