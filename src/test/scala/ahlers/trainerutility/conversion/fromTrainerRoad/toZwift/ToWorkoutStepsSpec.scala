//package ahlers.trainerutility.conversion.fromTrainerRoad.toZwift
//
//import ahlers.trainerutility.conversion.fromTrainerRoad.toZwift.ToWorkoutStepSpec.toWorkoutData
//import cats.data.NonEmptyList
//import cats.data.Validated.Valid
//import org.scalacheck.{Arbitrary, Gen}
//import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks.{forAll, sizeRange}
//import zwift.schema.desktop.WorkoutStep
//import zwift.schema.desktop.scalacheck.instances.{genCooldown, genRamp, genSteadyState, genWarmup}
//
//class ToWorkoutStepsSpec {
//
//  "Warmup" when {
//
//    "first interval" in {
//      implicit val arbSteps: Arbitrary[NonEmptyList[WorkoutStep]] = Arbitrary(for {
//        head <- genWarmup
//        internal <- Gen.listOf(Gen.oneOf(
//          genSteadyState,
//          genRamp,
//        ))
//        last <- genCooldown
//      } yield NonEmptyList(head, internal) :+ last)
//
//      forAll(sizeRange(3)) { steps: NonEmptyList[WorkoutStep] =>
//        val workouts = toWorkoutData(steps).toList
//
//        ToWorkoutStep
//          .from(
//            workouts = workouts,
//          )
//          .shouldMatchTo(Valid((
//            steps.head,
//            Nil,
//          )))
//      }
//    }
//
//  }
//
//}
//
