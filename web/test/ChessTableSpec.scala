import akka.actor.Props
import models._
import models.User
import net.michalsitko.kloc.game.Black
import net.michalsitko.kloc.game.White
import net.michalsitko.kloc.game.{Winner, Black, White, Color}

import org.specs2.execute.Failure
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.libs.concurrent.Akka
import play.api.test._
import play.api.test.Helpers._


/**
 * Created by michal on 22/09/14.
 */
@RunWith(classOf[JUnitRunner])
class ChessTableSpec extends Specification with org.specs2.matcher.ThrownMessages{
  "ChessTable" should {

    "allows to add 2 players" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black()) should not(throwA[Throwable])
    }

    "not allow to add the same player twice" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      table.addPlayer(user1, Black()) must throwA[IllegalArgumentException]
    }

    "not allow to add 2 players with the same color" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, White()) must throwA[IllegalArgumentException]
    }

    "not allow to add 3 players" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())
      val user3 = User("bob3", "dfad3")
      table.addPlayer(user3, null) must throwA[IllegalArgumentException]
    }

    "is ready when 2 players has been added" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      table.state must equalTo(ChessTableState.WaitingForPlayers)

      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())

      table.state must equalTo(ChessTableState.WaitingForPlayers)

      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

      table.state must equalTo(ChessTableState.Ready)
    }

    "starts when both players requested start" in new WithApplication {
      val table = new ChessTable(120 * 1000)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())
      table.requestStart(user1)
      table.state must equalTo(ChessTableState.Ready)
      table.requestStart(user1)
      table.state must equalTo(ChessTableState.Ready)
      table.requestStart(user2)
      table.state must equalTo(ChessTableState.Started)
    }

  };

  "Game on ChessTable" can {
    "ends because of lack of time" in new WithApplication {
      val table = new ChessTable(300)
      val roomActor = Akka.system.actorOf(Props(classOf[RoomActor], table))
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

      table.requestStart(user1)
      table.requestStart(user2)

      table.state must equalTo(ChessTableState.Started)
      table.game.status.isFinished() must equalTo(false)

      Thread.sleep(800)

      table.state must equalTo(ChessTableState.Finished)
      table.game.status.isFinished() must equalTo(true)
      table.game.status.result.isDefined must equalTo(true)
      table.game.status.result match {
        case Some(winner: Winner) => winner.color must equalTo(Black())
        case _ => fail("game is expected to have winner")
      }

      roomActor ! NoTimeLeft(null)
    }
  }
}
