import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class ExpressionTest extends FlatSpec with Matchers {
  behavior of "calculator"

  it should "compute result for simple calculations" in {
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5")), CalcState()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-5")), CalcState()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-+5")), CalcState()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("+-5")), CalcState()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-+-+-+-5")), CalcState()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("+ + + + + +-5")), CalcState()).result == -5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5 + +5")), CalcState()).result == 10)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-5+ +5")), CalcState()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+ +50")), CalcState()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+-50")), CalcState()).result == -100)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-50+ +- -+ + + + + + +- -51")), CalcState()).result == 1)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("5+ + + + +5")), CalcState()).result == 10)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("-10+5+ + + + +5")), CalcState()).result == 0)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4")), CalcState()).result == 12)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4/3")), CalcState()).result == 4)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("3*4")), CalcState()).result == 12)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("1+2*3")), CalcState()).result == 7)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("(1+2)*3")), CalcState()).result == 9)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("((1+2)*3)")), CalcState()).result == 9)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("((1+2)*3)+((3+2)*2)")), CalcState()).result == 19)

  }

  it should "compute results for simple assignments" in {
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5")), CalcState()).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5+3")), CalcState()).result == 8)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5+3*4")), CalcState()).result == 17)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=(5+3)*4")), CalcState()).result == 32)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=x=(5+3)*4")), CalcState()).result == 32)
    val state = CalcState()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=( y=( 5+3)* 4)")), state).result == 32)
    assert(state.vars("x") == 32)
    assert(state.vars("y") == 32)
  }

  it should "compute results for postfix and prefix increments/decrements" in {
    val state = CalcState()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=y=32")), state).result == 32)
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

    val state2 = CalcState()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=1")), state2).result == 1)
    assert(state2.vars("x") == 1)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("2*x+++3*x++ ")), state2).result == 8)
  }

  it should "compute results for assignments with operands" in {
    val state = CalcState()
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x=5")), state).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y=5")), state).result == 5)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("x+=5")), state).result == 10)
    assert(Expression(new Tokenizer(new CharStreamProcessorImpl("y*=x+=5")), state).result == 75)
    assert(state.vars("x") == 15)
    assert(state.vars("y") == 75)

  }

  it should "throw exception when string can't be parsed" in {

    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5=5")), CalcState()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5)")), CalcState()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("(5")), CalcState()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("++5")), CalcState()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("--5)")), CalcState()).result
    an [IllegalArgumentException] should be thrownBy Expression(new Tokenizer(new CharStreamProcessorImpl("5++5")), CalcState()).result

  }
}



