import akka.actor.ActorSystem
import models.{User, _}
import net.michalsitko.kloc.game.{Black, White, Winner}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.PlaySpec

class ChessTableSpec extends PlaySpec with BeforeAndAfterAll {
  val actorSystem = ActorSystem("ChessTableSpecSystem")

  override def afterAll(): Unit = {
    actorSystem.terminate()
  }

  "ChessTable" should {

    "allows to add 2 players" in {
      val table = new ChessTable(actorSystem, 120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())
    }

    "not allow to add the same player twice" in {
      val table = new ChessTable(actorSystem, 120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      an[IllegalArgumentException] should be thrownBy(table.addPlayer(user1, Black()))
    }

    "not allow to add 2 players with the same color" in {
      val table = new ChessTable(actorSystem, 120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      an[IllegalArgumentException] should be thrownBy(table.addPlayer(user2, White()))
    }

    "not allow to add 3 players" in {
      val table = new ChessTable(actorSystem, 120 * 1000)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())
      val user3 = User("bob3", "dfad3")
      an[IllegalArgumentException] should be thrownBy(table.addPlayer(user3, null))
    }

    "starts after 2 players has been added" in {
      val table = new ChessTable(actorSystem, 120 * 1000)
      table.state mustBe(ChessTableState.WaitingForPlayers)

      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())

      table.state mustBe(ChessTableState.WaitingForPlayers)

      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

      table.state mustBe(ChessTableState.Started)
    }

  }

  "Game on ChessTable" can {
    "ends because of lack of time" in {
      val table = new ChessTable(actorSystem, 300)
      val user1 = User("bob", "dfad")
      table.addPlayer(user1, White())
      val user2 = User("bob2", "dfad2")
      table.addPlayer(user2, Black())

      table.state mustBe(ChessTableState.Started)
      table.game.status.isFinished() mustBe(false)

      Thread.sleep(800)

      table.state mustBe(ChessTableState.Finished)
      table.game.status.isFinished() mustBe(true)
      table.game.status.result.isDefined mustBe(true)
      table.game.status.result match {
        case Some(winner: Winner) => winner.color mustBe(Black())
        case _ => fail("game is expected to have winner")
      }
    }
  }
}
