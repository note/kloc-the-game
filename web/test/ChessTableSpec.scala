import models.{User, _}
import net.michalsitko.kloc.game.{Black, White, Winner}
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test._


@RunWith(classOf[JUnitRunner])
class ChessTableSpec extends Specification with org.specs2.matcher.ThrownMessages{
  "ChessTable" should {

    "allows to add 2 players" in {
      val table = new ChessTable(120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black()) should not(throwA[Throwable])
    }

    "not allow to add the same player twice" in {
      val table = new ChessTable(120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      table.addPlayer(user1, Black()) must throwA[IllegalArgumentException]
    }

    "not allow to add 2 players with the same color" in {
      val table = new ChessTable(120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, White()) must throwA[IllegalArgumentException]
    }

    "not allow to add 3 players" in {
      val table = new ChessTable(120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())
      val user3 = User("bob3", "dfad3")
      table.addPlayer(user3, null) must throwA[IllegalArgumentException]
    }

    "starts after 2 players has been added" in {
      val table = new ChessTable(120 * 1000)
      table.state must equalTo(ChessTableState.WaitingForPlayers)

      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())

      table.state must equalTo(ChessTableState.WaitingForPlayers)

      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

      table.state must equalTo(ChessTableState.Started)
    }

  }

  "Game on ChessTable" can {
    "ends because of lack of time" in {
      val table = new ChessTable(300)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

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
    }
  }
}
