package trainerroad.workout

import cats.data.NonEmptyList

case class Workout(
  Details: Details,
  intervalData: NonEmptyList[IntervalData],
)
