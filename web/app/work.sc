import models._
import play.api.libs.json._

val info = ChessTableInfo(None, Some("player1"))

implicit val chessTableInfoWrites = new Writes[ChessTableInfo] {
  def writes(info: ChessTableInfo) = Json.obj(
    "white" -> info.whitePlayerName.getOrElse(null),
    "black" -> info.blackPlayerName.getOrElse(null)
  )
}