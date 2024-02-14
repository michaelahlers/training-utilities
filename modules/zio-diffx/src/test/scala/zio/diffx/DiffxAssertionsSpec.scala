package zio.diffx

import com.softwaremill.diffx._
import com.softwaremill.diffx.generic.auto._
import zio.diffx.DiffxAssertions._
import zio.test.Assertion._
import zio.test._

object DiffxAssertionsSpec extends ZIOSpecDefault {
  import Animal._

  sealed trait Animal
  object Animal {
    case class Dog(name: String) extends Animal
    case class Cat(name: String) extends Animal

    implicit val diff: Diff[Animal] = Diff.summon
  }

  val isMatching = test("is matching") {
    assert(Dog("Croix"))(matchesTo(Dog("Croix")))
  }

  val notMatching = test("not matching") {
    assert(Dog("Croix"))(not(matchesTo(Dog("Kona")))) &&
    assert[Animal](Dog("Croix"))(not(matchesTo(Cat("Croix"))))
  }

  override val spec = suite("DiffxAssertions") {
    isMatching ::
      notMatching ::
      Nil
  }

}
