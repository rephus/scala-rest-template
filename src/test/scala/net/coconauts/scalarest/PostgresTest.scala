package net.coconauts.scalarest

import net.coconauts.scalarest.FlywaySettings._
import org.flywaydb.core.Flyway

trait PostgresTest {

  //Run migrations once, triggered by this interface
  migrate()

}

object FlywaySettings {

  val conf = Global.conf

  var triggered = false

  def migrate() {

    if (triggered) return
    triggered = true

    val flyway = new Flyway()

    flyway.setDataSource(conf.getString("postgres.url"), conf.getString("postgres.user"), conf.getString("postgres.password"))

    //This runs on class initialization (aka when the test starts, not between tests)
    println("Running Flyway migration on scalarest_test")
    Utils.retry(5) {
      println("Trying to clean existing DB")
      flyway.clean()
    }
    flyway.migrate()

  }

}