import org.scalatest.{FlatSpec, Matchers}

class TokenizerTest extends FlatSpec with Matchers {
  behavior of "Tokenizer"

  it should "parse string to tokens" in{
    assert(new Tokenizer(new CharStream("")).size == 1)
    val tokenizer = new Tokenizer(new CharStream("(+++1+-*/5)x123+5"))
    assert(tokenizer.next() == OpenBracketToken)
    assert(tokenizer.next() == OperatorToken("++"))
    assert(tokenizer.next() == OperatorToken("+"))
    assert(tokenizer.next() == NumberToken(1))
    assert(tokenizer.next() == OperatorToken("+"))
    assert(tokenizer.next() == OperatorToken("-"))
    assert(tokenizer.next() == OperatorToken("*"))
    assert(tokenizer.next() == OperatorToken("/"))
    assert(tokenizer.next() == NumberToken(5))
    assert(tokenizer.next() == CloseBracketToken)
    assert(tokenizer.next() == VariableToken("x123"))
    assert(tokenizer.next() == OperatorToken("+"))
    assert(tokenizer.next() == NumberToken(5))

  }
}



