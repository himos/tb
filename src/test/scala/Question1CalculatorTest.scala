import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

class Question1CalculatorTest extends FlatSpec with Matchers {
  behavior of "calculator"

  it should "create CharStream from string" in {

      val stream = new CharStream("12345")
      assert(stream.current().get == '1')
      assert(stream.next().get == '2')
      assert(stream.current().get == '2')
      assert(stream.next().get == '3')
      assert(stream.current().get == '3')
      assert(stream.next().get == '4')
      assert(stream.current().get == '4')
      assert(stream.next().get == '5')
      assert(stream.current().get == '5')
      assert(stream.next().isEmpty)

//      for( i <- 0 to Int.MaxValue) {
//        assert(stream.next().isEmpty)
//        assert(stream.current().isEmpty)
//      }
  }

//  it should "parse string into expression" in {
//    assert(Expression(new Tokenizer(new CharStream("5")), State()).result == 5)
//    assert(Expression(new Tokenizer(new CharStream("-5")), State()).result == -5)
//    assert(Expression(new Tokenizer(new CharStream("-+5")), State()).result == -5)
//    assert(Expression(new Tokenizer(new CharStream("+-5")), State()).result == -5)
//    assert(Expression(new Tokenizer(new CharStream("-----+-5")), State()).result == 5)
//    assert(Expression(new Tokenizer(new CharStream("++++++-5")), State()).result == -5)
//    assert(Expression(new Tokenizer(new CharStream("5++5")), State()).result == 10)
//    assert(Expression(new Tokenizer(new CharStream("-5++5")), State()).result == 0)
//    assert(Expression(new Tokenizer(new CharStream("-50++50")), State()).result == 0)
//    assert(Expression(new Tokenizer(new CharStream("-50+-50")), State()).result == -100)
//    assert(Expression(new Tokenizer(new CharStream("-50++--+++++++--51")), State()).result == 1)
//    assert(Expression(new Tokenizer(new CharStream("5+++++5")), State()).result == 10)
//    assert(Expression(new Tokenizer(new CharStream("-10+5+++++5")), State()).result == 0)
//    assert(Expression(new Tokenizer(new CharStream("3*4")), State()).result == 12)
//    assert(Expression(new Tokenizer(new CharStream("3*4/3")), State()).result == 4)
//    assert(Expression(new Tokenizer(new CharStream("3*4")), State()).result == 12)
//    assert(Expression(new Tokenizer(new CharStream("1+2*3")), State()).result == 7)
//    assert(Expression(new Tokenizer(new CharStream("(1+2)*3")), State()).result == 9)
//    assert(Expression(new Tokenizer(new CharStream("((1+2)*3)")), State()).result == 9)
//    assert(Expression(new Tokenizer(new CharStream("((1+2)*3)+((3+2)*2)")), State()).result == 19)
//    assert(Expression(new Tokenizer(new CharStream("x=5")), State()).result == 5)
//    assert(Expression(new Tokenizer(new CharStream("x=5+3")), State()).result == 8)
//    assert(Expression(new Tokenizer(new CharStream("x=5+3*4")), State()).result == 17)
//    assert(Expression(new Tokenizer(new CharStream("x=(5+3)*4")), State()).result == 32)
//    assert(Expression(new Tokenizer(new CharStream("x=x=(5+3)*4")), State()).result == 32)
//    val state = State()
//    assert(Expression(new Tokenizer(new CharStream("x=( y=( 5+3)* 4)")), state).result == 32)
//    assert(state.vars("x") == 32)
//    assert(state.vars("y") == 32)
//    assert(Expression(new Tokenizer(new CharStream("x")), state).result == 32)
//    assert(Expression(new Tokenizer(new CharStream("y")), state).result == 32)
//    assert(Expression(new Tokenizer(new CharStream("x y")), state).result == 32)
//  }

  it should "parse string into expression" in {
    assert(Expression(new Tokenizer(new CharStream("")), State()) == null)
    assert(Expression(new Tokenizer(new CharStream("5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStream("-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStream("-+5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStream("+-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStream("-+-+-+-5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStream("+ + + + + +-5")), State()).result == -5)
    assert(Expression(new Tokenizer(new CharStream("5 + +5")), State()).result == 10)
    assert(Expression(new Tokenizer(new CharStream("-5+ +5")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStream("-50+ +50")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStream("-50+-50")), State()).result == -100)
    assert(Expression(new Tokenizer(new CharStream("-50+ +- -+ + + + + + +- -51")), State()).result == 1)
    assert(Expression(new Tokenizer(new CharStream("5+ + + + +5")), State()).result == 10)
    assert(Expression(new Tokenizer(new CharStream("-10+5+ + + + +5")), State()).result == 0)
    assert(Expression(new Tokenizer(new CharStream("3*4")), State()).result == 12)
    assert(Expression(new Tokenizer(new CharStream("3*4/3")), State()).result == 4)
    assert(Expression(new Tokenizer(new CharStream("3*4")), State()).result == 12)
    assert(Expression(new Tokenizer(new CharStream("1+2*3")), State()).result == 7)
    assert(Expression(new Tokenizer(new CharStream("(1+2)*3")), State()).result == 9)
    assert(Expression(new Tokenizer(new CharStream("((1+2)*3)")), State()).result == 9)
    assert(Expression(new Tokenizer(new CharStream("((1+2)*3)+((3+2)*2)")), State()).result == 19)
    assert(Expression(new Tokenizer(new CharStream("x=5")), State()).result == 5)
    assert(Expression(new Tokenizer(new CharStream("x=5+3")), State()).result == 8)
    assert(Expression(new Tokenizer(new CharStream("x=5+3*4")), State()).result == 17)
    assert(Expression(new Tokenizer(new CharStream("x=(5+3)*4")), State()).result == 32)
    assert(Expression(new Tokenizer(new CharStream("x=x=(5+3)*4")), State()).result == 32)
    val state = State()
    assert(Expression(new Tokenizer(new CharStream("x=( y=( 5+3)* 4)")), state).result == 32)
    assert(state.vars("x") == 32)
    assert(state.vars("y") == 32)
    assert(Expression(new Tokenizer(new CharStream("x")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStream("y")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStream("++y")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStream("y")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStream("y--")), state).result == 33)
    assert(Expression(new Tokenizer(new CharStream("y--")), state).result == 32)
    assert(Expression(new Tokenizer(new CharStream("--y")), state).result == 30)
    assert(Expression(new Tokenizer(new CharStream("--y+--x")), state).result == 60)
    assert(Expression(new Tokenizer(new CharStream("y--+x--")), state).result == 60)
    assert(Expression(new Tokenizer(new CharStream("y+x ")), state).result == 58)

    val state2 = State()
    assert(Expression(new Tokenizer(new CharStream("x=1")), state2).result == 1)
    assert(state2.vars("x") == 1)
    assert(Expression(new Tokenizer(new CharStream("2*x+++3*x++ ")), state2).result == 8)

    //    assert(Expression(new Tokenizer(new CharStream("x = 4 + y = 32")), state) == null)


  }
}



