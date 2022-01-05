import play.core.PlayVersion
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-28"   % "5.18.0",
    "uk.gov.hmrc"             %% "play-frontend-hmrc"           % "1.31.0-play-28",
    "uk.gov.hmrc"             %% "internal-auth-client-play-28" % "0.13.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"     % "5.18.0"            % Test,
    "org.mockito"             %% "mockito-scala"              % "1.10.2"            % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test,
    "com.vladsch.flexmark"    %  "flexmark-all"               % "0.36.8"            % "test, it"
  )
}
