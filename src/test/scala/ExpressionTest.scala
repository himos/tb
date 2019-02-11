import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class Question1CalculatorTest extends FlatSpec with Matchers {
  behavior of "calculator"

  it should "parse string into expression" in {
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-+5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("+-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-+-+-+-5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("+ + + + + +-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5 + +5")), State()).result == 10)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-5+ +5")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+ +50")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+-50")), State()).result == -100)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+ +- -+ + + + + + +- -51")), State()).result == 1)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5+ + + + +5")), State()).result == 10)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-10+5+ + + + +5")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4")), State()).result == 12)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4/3")), State()).result == 4)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4")), State()).result == 12)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("1+2*3")), State()).result == 7)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("(1+2)*3")), State()).result == 9)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("((1+2)*3)")), State()).result == 9)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("((1+2)*3)+((3+2)*2)")), State()).result == 19)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5+3")), State()).result == 8)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5+3*4")), State()).result == 17)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=(5+3)*4")), State()).result == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=x=(5+3)*4")), State()).result == 32)
    val state = State()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=( y=( 5+3)* 4)")), state).result == 32)
    assert(state.vars("x") == 32)
    assert(state.vars("y") == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("++y")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y--")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y--")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("--y")), state).result == 30)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("--y+--x")), state).result == 60)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y--+x--")), state).result == 60)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y+x ")), state).result == 58)

    val state2 = State()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=1")), state2).result == 1)
    assert(state2.vars("x") == 1)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("2*x+++3*x++ ")), state2).result == 8)

    //    assert(Expression(new Tokenizer(new CharStream("x = 4 + y = 32")), state) == null)
  }

  it should "throw exception when string can't be parsed" in {

    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5)")), State()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("(5")), State()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("++5")), State()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("--5)")), State()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5++5")), State()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5")), State()).result
  }
}



