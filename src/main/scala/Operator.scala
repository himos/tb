
trait Operator {
  def precedence: Int
}

abstract class Bracket extends Operator {
  override val precedence: Int = 120
}

case class UnaryOperator(op: Double => Double, opType: UnaryOperatorType.Value, override val precedence: Int) extends Operator

case class BinaryOperator(op: (Double, Double) => Double, override val precedence: Int) extends Operator

object UnaryOperatorType extends Enumeration {
  val None, Prefix, Postfix = Value
}

object Operator {
  def getUni(op: String, opType: UnaryOperatorType.Value): UnaryOperator = {
    op match {
      case "++" => UnaryOperator(x => x + 1, opType, 111)
      case "--" => UnaryOperator(x => x - 1, opType, 110)
      case "+" => UnaryOperator(x => x, opType, 100)
      case "-" => UnaryOperator(x => -x, opType, 100)
      case x => throw new IllegalArgumentException(s"Expecting unary operator, but got $x")
    }
  }

  def getBi(op: String): BinaryOperator = {
    op match {
      case "+" => BinaryOperator((x, y) => x + y, 80)
      case "-" => BinaryOperator((x, y) => x - y, 80)
      case "*" => BinaryOperator((x, y) => x * y, 90)
      case "/" => BinaryOperator((x, y) => x / y, 90)
    }
  }
}

case object AssignmentOperator extends Operator {
  override val precedence: Int = 10
}

case object OpenBracket extends Bracket

case object CloseBracket extends Bracket