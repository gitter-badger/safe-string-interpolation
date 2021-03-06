package test.com.thaj.safe.string.interpolator

import com.thaj.safe.string.interpolator.{Safe, Secret}
import org.specs2.{ScalaCheck, Specification}

object SafeSpec extends Specification with ScalaCheck {
  def is =
    s2"""
       Safe instances are successfully generated by macros for any / nested case classes $testClasses
       Safe instances are successfully generated by macros for Integer type  $testInteger
       Safe instances are successfully generated by macros for Long type  $testInteger
       Safe instances are successfully generated by macros for Seq of any type type  $testSeq
       Safe instances for Secrets always produce hidden strings $testInductive
      """

  final case class Another(another: String)

  final case class Hello(hellov: Another)

  final case class Dummy(name: String, age: Hello, age2: Hello, age3: Hello)

  private def testClasses = {
    prop { (name: String, x1: String, x2: String, x3: String) =>
      val r = implicitly[Safe[Dummy]]

      r.value(Dummy(name, Hello(Another(x1)), Hello(Another(x2)), Hello(Another(x3)))) must_===
        s"{name : ${name}, age : {hellov : {another : $x1}}, age2 : {hellov : {another : $x2}}, age3 : {hellov : {another : $x3}}}"

    }
  }

  private def testInteger = {
    prop { value: Int =>
      Safe[Int].value(value) must_=== value.toString
    }
  }

  private def testLong = {
    prop { value: Long =>
      Safe[Long].value(value) must_=== value.toString
    }
  }

  private def testSeq = {
    prop { value: List[Long] => {
      Safe[List[Long]].value(value) must_=== value.mkString(",")
    }}
  }

  final case class Inductive(name: Secret)

  private def testInductive = {
    prop { a: String =>
      Safe[Inductive].value(Inductive(Secret(a))) must_=== s"{name : ${List.fill(a.length)("*").mkString}}"
    }
  }
}
