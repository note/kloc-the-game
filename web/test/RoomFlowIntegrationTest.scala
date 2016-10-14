import akka.actor.ActorSystem
import akka.util.Timeout
import infrastructure.{TestMyApplicationLoader, TestMyComponents}
import org.scalatest.BeforeAndAfterAll
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.libs.json._
import play.api.test.Helpers._
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Application, ApplicationLoader, Environment, Mode}
import utils.WebsocketProbe

import scala.concurrent.duration._

class RoomFlowIntegrationTest extends PlaySpec with OneServerPerSuite with FutureAwaits with DefaultAwaitTimeout with BeforeAndAfterAll {

  var components: TestMyComponents = null
  override implicit lazy val app: Application = {
    val testContext =
      ApplicationLoader.createContext(Environment(new java.io.File("."), getClass.getClassLoader, Mode.Test))
    components = new TestMyComponents(testContext)
    new TestMyApplicationLoader(components).load(testContext)
  }



  implicit val system = ActorSystem("RoomFlowIntegrationTest")

  override def afterAll(): Unit = {
    system.terminate()
  }

  override implicit def defaultAwaitTimeout: Timeout = 3.seconds
  val defaultDuration = 3.seconds

  val baseUrl = s"http://localhost:$port"

  "should works as expected for happy path" in new TestContext {
    val userId1 = createUser(user1)
    val userId2 = createUser(user2)
    val userId3 = createUser(user3)

    val listRoomsProbe = new WebsocketProbe(s"localhost:$port/listRooms")
    listRoomsProbe.expectMsg(JsObject(List("rooms" -> JsObject(List.empty))))

    val roomId = createRoom()

    // user1 and user2 joins room
    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
    val roomOneWhite = new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)
    val roomsJson = JsObject(List(
      // TODO: contract-change: get rid of JsArray - it's completely unneccessary
      "0" -> JsArray(List(JsObject(List(
        ("white" -> JsString(user1))))))
    ))
    listRoomsProbe.expectMsg(JsObject(List("rooms" -> roomsJson)))

    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
    val roomOneBlack = new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)
    val roomsJson2 = JsObject(List(
      "0" -> JsArray(List(JsObject(List(
        ("white" -> JsString(user1)), ("black" -> JsString(user2))))))
    ))
    listRoomsProbe.expectMsg(JsObject(List("rooms" -> roomsJson2)))

    roomOneWhite.expectMsg(startGameMsg)
    roomOneBlack.expectMsg(startGameMsg)

    // Make the first move. In that case we don't use applyMoveAndVerify since we know user2's time up front
    roomOneWhite.sendMsg(moveMsg("g2", "g4"))

    val move1RecvByWhite = roomOneWhite.fetchMsg(defaultDuration)
    val move1RecvByBlack = roomOneBlack.fetchMsg(defaultDuration)

    move1RecvByWhite must equal(move1RecvByBlack)

    val (JsNumber(whiteTime)) = (move1RecvByWhite \ "times" \ user1).get

    val times = Map[String, Long](user1 -> whiteTime.toLong, user2 -> timeLimitInSeconds * 1000)
    val expectedMoveMsg = receivedMoveMsg("g2", "g4", "", times, userId1)
    move1RecvByWhite must equal(expectedMoveMsg)

    // Make next moves. Ends with checkmate - black wins
    applyMoveAndVerify(roomOneBlack, roomOneWhite, "e7", "e5", userId2)
    applyMoveAndVerify(roomOneWhite, roomOneBlack, "f2", "f4", userId1)
    applyMoveAndVerify(roomOneBlack, roomOneWhite, "d8", "h4", userId2, "b")
  }

  "cannot join non existing room" in new TestContext {
    val userId1 = createUser(user1)
    val userId2 = createUser(user2)
    val nonExistingRoomId = 456

    // room with id 0 has not been created
    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$nonExistingRoomId&color=w"
    an [AssertionError] should be thrownBy (new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1))
  }

  "cannot join one room with already taken color" in new TestContext {
    val userId1 = createUser(user1)
    val userId2 = createUser(user2)

    val roomId = createRoom()

    // user1 joins room
    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
     new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)

    // we try to join as white which is already taken
    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
    an [AssertionError] should be thrownBy (new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2))
  }

  "only 2 players can join" in new TestContext {
    val userId1 = createUser(user1)
    val userId2 = createUser(user2)
    val userId3 = createUser(user3)

    val roomId = createRoom()

    // user1 and user2 joins room
    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
    new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)

    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
    new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)

    an [AssertionError] should be thrownBy (new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId3))
  }

  "should not allow for illegal moves" in new TestContext {
    val userId1 = createUser(user1)
    val userId2 = createUser(user2)

    val roomId = createRoom()

    // user1 and user2 joins room
    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
    val roomOneWhite = new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)

    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
    val roomOneBlack = new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)

    roomOneWhite.expectMsg(startGameMsg)
    roomOneBlack.expectMsg(startGameMsg)

    an [AssertionError] should be thrownBy applyMoveAndVerify(roomOneWhite, roomOneBlack, "e2", "e5", userId1)
  }

//  "should not allow for making two moves in row by the same player" in new TestContext {
//    val userId1 = createUser(user1)
//    val userId2 = createUser(user2)
//
//    val roomId = createRoom()
//
//    // user1 and user2 joins room
//    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
//    val roomOneWhite = new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)
//
//    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
//    val roomOneBlack = new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)
//
//    roomOneWhite.expectMsg(startGameMsg)
//    roomOneBlack.expectMsg(startGameMsg)
//
//    applyMoveAndVerify(roomOneWhite, roomOneBlack, "e2", "e4", userId1)
//    an [AssertionError] should be thrownBy applyMoveAndVerify(roomOneWhite, roomOneBlack, "e4", "e5", userId1)
//  }
//
//  "should not allow for making move after checkmate" in new TestContext {
//    val userId1 = createUser(user1)
//    val userId2 = createUser(user2)
//
//    val roomId = createRoom()
//
//    // user1 and user2 joins room
//    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
//    val roomOneWhite = new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)
//
//    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
//    val roomOneBlack = new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)
//
//    roomOneWhite.expectMsg(startGameMsg)
//    roomOneBlack.expectMsg(startGameMsg)
//
//    applyMoveAndVerify(roomOneWhite, roomOneBlack, "g2", "g4", userId1)
//    applyMoveAndVerify(roomOneBlack, roomOneWhite, "e7", "e5", userId2)
//    applyMoveAndVerify(roomOneWhite, roomOneBlack, "f2", "f4", userId1)
//    applyMoveAndVerify(roomOneBlack, roomOneWhite, "d8", "h4", userId2, "b")
//
//    an [AssertionError] should be thrownBy applyMoveAndVerify(roomOneWhite, roomOneBlack, "e1", "f2", userId1, "b")
//    an [AssertionError] should be thrownBy applyMoveAndVerify(roomOneWhite, roomOneBlack, "e1", "f2", userId1)
//  }
//
//  "should stop malicious user who tries to make move for his opponent" in new TestContext {
//    val userId1 = createUser(user1)
//    val userId2 = createUser(user2)
//
//    val roomId = createRoom()
//
//    // user1 and user2 joins room
//    val joinRoomOneWhiteUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=w"
//    val roomOneWhite = new WebsocketProbe(joinRoomOneWhiteUrl, "userId" -> userId1)
//
//    val joinRoomOneBlackUrl = s"localhost:$port/joinRoom?roomId=$roomId&color=b"
//    val roomOneBlack = new WebsocketProbe(joinRoomOneBlackUrl, "userId" -> userId2)
//
//    roomOneWhite.expectMsg(startGameMsg)
//    roomOneBlack.expectMsg(startGameMsg)
//
//    applyMoveAndVerify(roomOneWhite, roomOneBlack, "g2", "g4", userId1)
//    an [AssertionError] should be thrownBy applyMoveAndVerify(roomOneWhite, roomOneBlack, "e7", "e5", userId1)
//  }


  trait TestContext extends Fixtures {
    val wsClient = components.wsClient

    def createUser(userName: String): String = {
      val createUserUrl = s"$baseUrl/logInUser"
      val response = await(wsClient.url(createUserUrl).withQueryString("userName" -> userName).get())
      response.status must be (OK)
      (response.json \ "userId") match {
        case JsDefined(JsString(userId)) => userId
        case _ => fail("Unexpected JSON returned when creating user")
      }
    }

    def createRoom(): Int = {
      val createUserUrl = s"$baseUrl/createRoom"
      val response = await(wsClient.url(createUserUrl).withQueryString("timeLimitInSeconds" -> timeLimitInSeconds.toString).get())
      response.status must be (OK)
      (response.json \ "roomId") match {
        case JsDefined(JsNumber(roomId)) => roomId.toInt
        case _ => fail("Unexpected JSON returned when creating room")
      }
    }

    def moveMsg(from: String, to: String): JsObject =
      JsObject(List(
        "type" -> JsString("move"),
        "from" -> JsString(from),
        "to" -> JsString(to)
      ))

    def receivedMoveMsg(from: String, to: String, res: String, times: Map[String, Long], userId: String): JsObject =
      JsObject(List(
        "type" -> JsString("move"),
        "from" -> JsString(from),
        "to" -> JsString(to),
        "result" -> JsString(res),
        "times" -> JsObject(times.mapValues(JsNumber(_))),
        "userId" -> JsString(userId)
      ))

    def applyMoveAndVerify(activeWs: WebsocketProbe, passiveWs: WebsocketProbe,
                           from: String, to: String, activeUserId: String, result: String = ""): Unit = {

      activeWs.sendMsg(moveMsg(from, to))

      val move1RecvByActive = activeWs.fetchMsg(defaultDuration)
      val move1RecvByPassive = passiveWs.fetchMsg(defaultDuration)

      move1RecvByActive must equal(move1RecvByPassive)

      val (JsNumber(user1Time)) = (move1RecvByActive \ "times" \ user1).get
      val (JsNumber(user2Time)) = (move1RecvByActive \ "times" \ user2).get

      val times = Map[String, Long](user1 -> user1Time.toLong, user2 -> user2Time.toLong)
      val expectedMoveMsg = receivedMoveMsg(from, to, result, times, activeUserId)
      move1RecvByActive must equal(expectedMoveMsg)
    }
  }

  trait Fixtures {
    val user1 = "user1"
    val user2 = "user2"
    val user3 = "user3"

    val timeLimitInSeconds = 300

    val startGameMsg = JsObject(List(
      ("type" -> JsString("start")),
      // TODO: contract-change: timeLimit should be consistent - use ms or secs everywhere
      ("times" -> JsObject(List((user1 -> JsNumber(timeLimitInSeconds * 1000)), (user2 -> JsNumber(timeLimitInSeconds * 1000))))),
      ("colors" -> JsObject(List((user1 -> JsString("w")), (user2 -> JsString("b")))))
    ))
  }
}
