import zio.Console._
import zio.ExitCode
import zio.ZEnv
import zio.URIO

object ZioGetStarted extends zio.App {

  val myAppLogic = for {
    _ <- printLine("Hello! What is your name?")
    name <- readLine
    _ <- printLine(s"Hello, $name!")
  } yield ()

  override def run(args: List[String]): URIO[ZEnv, ExitCode] =
    myAppLogic.exitCode
}
