package cats.data.diffx

import cats.data.Validated
import cats.data.Validated.Invalid
import cats.data.Validated.Valid
import com.softwaremill.diffx.Diff
import com.softwaremill.diffx.cats.DiffCatsInstances
import com.softwaremill.diffx.generic.auto._

/**
 * Workaround for  [[DiffCatsInstances]] missing instances for many of the types in [[cats.data]].
 */
object instances extends DiffCatsInstances {

  implicit def diffValid[A: Diff]: Diff[Valid[A]] = Diff.summon

  implicit def diffInvalid[E: Diff]: Diff[Invalid[E]] = Diff.summon

  implicit def diffValidated[E: Diff, A: Diff]: Diff[Validated[E, A]] = Diff.summon

}
