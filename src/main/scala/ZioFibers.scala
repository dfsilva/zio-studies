import zio.{UIO, ZIO}
import zio.Duration._
import zio.Duration
import java.util.concurrent.TimeUnit
import zio.ExitCode
import zio.URIO
import zio.ZEnv

object ZioFibers extends zio.App {

//   val zmol: UIO[Int] = ZIO.succeed(42)

  val showerTime = ZIO.succeed("Taking a shower")
  val boilingWater = ZIO.succeed("Boiling water")
  val preparingCoffee = ZIO.succeed("Prepare some coffee")

  def printThread = s"${Thread.currentThread().getName()}"

  def synchronoousRoutine() = for {
    _ <- showerTime.debug(printThread)
    _ <- boilingWater.debug(printThread)
    _ <- preparingCoffee.debug(printThread)
  } yield ()

  def concurrentShowerWhileBoilingWater() = for {
    _ <- showerTime.debug(printThread).fork
    _ <- boilingWater.debug(printThread)
    _ <- preparingCoffee.debug(printThread)
  } yield ()

  def concurrentRoutine() = for {
    showerFiber <- showerTime.debug(printThread).fork
    boilingFiber <- boilingWater.debug(printThread).fork
    zippedFiber = showerFiber.zip(boilingFiber)
    result <- zippedFiber.join.debug(printThread)
    _ <- ZIO.succeed(s"$result done").debug(printThread) *> preparingCoffee
      .debug(printThread)
  } yield ()

  val callFromAlice = ZIO.succeed("Call from Alice")

  val boilingWaterWithTime = boilingWater.debug(printThread) *> ZIO.sleep(
    Duration.fromMillis(5000)
  ) *> ZIO.succeed("Boiled water ready.")

  def concurrentRoutineWithAliceCall() = for {
    _ <- showerTime.debug(printThread)
    boilingFiber <- boilingWaterWithTime.fork
    _ <- callFromAlice.debug(printThread).fork *> boilingFiber.interrupt.debug(
      printThread
    )
    _ <- ZIO.succeed("Screw my coffee, going with Alice").debug(printThread)
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    concurrentShowerWhileBoilingWater().exitCode
}
