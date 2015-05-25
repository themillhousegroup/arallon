package com.themillhousegroup.arallon.util

import java.lang.reflect.Constructor
import scala.reflect.runtime.universe._
import scala.reflect.ClassTag

object ReflectionHelper {
  private val m = runtimeMirror(getClass.getClassLoader)

  private def constructor[T](c: ClassTag[T]): Constructor[_] = {
    c.runtimeClass.getConstructors.head
  }

  def construct[T](c: ClassTag[T], args: List[Object]) = {
    constructor(c).newInstance(args.toArray: _*).asInstanceOf[T]
  }
}
