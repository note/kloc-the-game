name := """web"""

version := "1.0-SNAPSHOT"

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws
)

appJsDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" }

appJsLibDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "libs" }

jasmineTestDir <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" }

jasmineConfFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" / "test.dependencies.js" }

jasmineRequireJsFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "libs" / "require-2.1.15.js" }

jasmineRequireConfFile <+= Def.setting { baseDirectory.value / "public" / "javascripts" / "tests" / "require.conf.js" }

(test in Test) <<= (test in Test) dependsOn (jasmine)
