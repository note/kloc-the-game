import org.scalatest.FlatSpec
import org.scalatest.matchers.ShouldMatchers

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 5/25/13
 * Time: 12:08 AM
 * To change this template use File | Settings | File Templates.
 */
class ElementSpec extends FlatSpec with ShouldMatchers {
  behavior of "two plus two"
  it should "be four" in {
    2 + 2 should be (4)
  }
}
