package com.themillhousegroup.arallon.util

import java.lang.reflect.Constructor
import scala.reflect.runtime.universe._

object ReflectionHelper {
  private val m = runtimeMirror(getClass.getClassLoader)

  private def constructor[_](m: RuntimeMirror, t: Type): Constructor[_] = {
    val c = m.runtimeClass(t.typeSymbol.asClass)
    c.getConstructors()(0)
  }

  def construct[T](t: Type, args: List[Object]) = {
    constructor(m, t).newInstance(args.toArray: _*).asInstanceOf[T]
  }

}
