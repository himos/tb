import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable

sealed trait Token

class Tokenizer(charStream: CharStreamProcessorImpl) extends Iterator[Token] with Logging {

  private[this] val tokens = mutable.Buffer.empty[Token]


  def next(): Token = {

    tokens.lastOption.filter(_ == EndToken).foreach(_ => throw new NoSuchElementException("Next on empty token iterator: " + toString()))
    if (charStream.isFinished) {
      tokens += EndToken
    } else {

      val currChar = charStream.currentChar

      logger.trace(s"Current char: $currChar")

      val currentToken: Token =
        if (currChar.isLetter) {
          VariableToken(charStream)
        } else if (currChar.isDigit) {
          NumberToken(charStream)
        } else if (currChar == '(') {
          charStream.processString("(")
          OpenBracketToken
        } else if (currChar == ')') {
          charStream.processString(")")
          CloseBracketToken
        } else if (OperatorToken.OpsChars.contains(currChar)) {
          OperatorToken(charStream)
        } else if (currChar == ' ') {
          charStream.processString(" ")
          next()
        } else throw new IllegalArgumentException(s"Found unexpected character: $charStream")
      tokens += currentToken
    }
    tokens.last
  }

  def head: Token = if (tokens.isEmpty) null else tokens.last

  def hasNext: Boolean = tokens.isEmpty || tokens.last != EndToken

  override def toString(): String = charStream.toString
}

case class VariableToken(varname: String) extends Token

case class OperatorToken(op: String) extends Token

case class NumberToken(double: Double) extends Token

case object OpenBracketToken extends Token

case object CloseBracketToken extends Token

//case object AssignmentToken extends Token

case object EndToken extends Token

object OperatorToken {

  val Ops = List("++", "--", "+", "-", "*", "/", "=")
  val OpsChars: Set[Char] = OperatorToken.Ops.mkString.toList.toSet


  def apply(charStream: CharStreamProcessorImpl): OperatorToken = {
    Ops.find(charStream.processString).map(opStr => if(charStream.processString("=")) {
      OperatorToken(opStr + "=")
    } else {
      OperatorToken(opStr)
    }) match {
      case Some(op) => op
      case None => throw new IllegalArgumentException(s"Unknown operator: $charStream")
    }
  }
}

object NumberToken {
  def apply(charStream: CharStreamProcessorImpl): NumberToken =
    NumberToken(charStream.processWhile(_.isDigit).toDouble)
}

object VariableToken {

  private[this] val AllowedChars = Set('_', '$')

  def apply(charStream: CharStreamProcessorImpl): VariableToken =
    VariableToken(charStream.processWhile(c => {
      c.isLetterOrDigit || AllowedChars.contains(c)
    }))

}
