import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable

sealed trait Token

class Tokenizer(charStream: CharStream) extends Iterator[Token] with Logging {


  private[this] val tokens = mutable.Buffer.empty[Token]

  while (charStream.current().isDefined) {

    val currChar = charStream.current().get

    logger.trace(s"Processing char: $currChar")

    if (currChar.isLetter) {
      tokens += VariableToken(charStream)
    } else if (currChar.isDigit) {
      tokens += NumberToken(charStream)
    } else if (currChar == '(') {
      tokens += OpenBracketToken
      charStream.next()
    } else if (currChar == ')') {
      tokens += CloseBracketToken
      charStream.next()
    } else if (currChar == '=') {
      tokens += AssignmentToken
      charStream.next()
    } else if (OperatorToken.OpsChars.contains(currChar)) {
      tokens += OperatorToken.parse(charStream)
    } else if (currChar == ' ') {
      charStream.next()
    } else throw new IllegalArgumentException(s"Found unexpected character: $charStream")

  }

  tokens += EndToken

  logger.trace(s"Processed all chars: $tokens")

  private[this] var position = -1
  private[this] val limit = tokens.length - 1


  def next(): Token = {
    position = math.min(position + 1, limit + 1)
    head
  }

  def head: Token = tokens(position)

  def hasNext: Boolean = position < limit

  def notFinished: Boolean = position <= limit

  override def size = tokens.size

}

case class VariableToken(varname: String) extends Token

case class OperatorToken(op: String) extends Token

case class NumberToken(double: Double) extends Token

case object OpenBracketToken extends Token

case object CloseBracketToken extends Token

case object AssignmentToken extends Token

case object EndToken extends Token

object OperatorToken {

  val Ops = List("++", "--", "+", "-", "*", "/")
  val OpsChars: Set[Char] = OperatorToken.Ops.mkString.toList.toSet


  def parse(charStream: CharStream): OperatorToken = {
    Ops.find(charStream.containsFromCurrPos).map(OperatorToken.apply) match {
      case Some(op) => op
      case None => throw new IllegalArgumentException(s"Unknown operator: $charStream")
    }
  }
}

object NumberToken {
  def apply(charStream: CharStream): NumberToken = {

    val sb = new StringBuilder

    while (charStream.current().isDefined && charStream.current().get.isDigit) {
      sb += charStream.current().get

      charStream.next()
    }

    NumberToken(sb.toDouble)
  }
}

object VariableToken {

  private[this] val AllowedChars = Set('_', '$')
  def apply(charStream: CharStream): VariableToken = {
    val sb = new StringBuilder

    while(charStream.current().isDefined &&
      (charStream.current().get.isLetterOrDigit ||
      AllowedChars.contains(charStream.current().get))){
      sb += charStream.current().get
      charStream.next()
    }

    VariableToken(sb.result())
  }
}
