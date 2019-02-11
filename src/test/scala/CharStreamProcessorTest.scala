import org.scalatest.{FlatSpec, Matchers}

class CharStreamTest extends FlatSpec with Matchers {
  behavior of "CharStream"

  it should "create charstream from string" in {
    val stream = new CharStreamProcessorImpl("12345")
    assert(stream.processString("1"))
    assert(stream.processString("23"))
    assert(stream.processString("4"))
    assert(!stream.isFinished)
    assert(stream.processString("5"))
    assert(stream.isFinished)
  }

  it should "print charstream with position pointer" in {
    val stream = new CharStreamProcessorImpl("12345")
    stream.processString("12")
    stream.toString shouldBe "12^345"
  }


}



