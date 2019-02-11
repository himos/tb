import org.apache.logging.log4j.scala.Logging

import scala.collection.mutable.{Map => MMap, Stack => MStack}



case class State(vars: MMap[String, Double] = MMap.empty,
                 exprStack: MStack[Expression] = MStack(),
                 operatorStack: MStack[Operator] = MStack()) extends Logging{
  override def toString: String = {
    exprStack.toString() + "\n" + operatorStack.toString()
  }

  def pushExpression(expression: Expression): Unit = {
    logger.debug(s"Pushing expression ${expression.getClass}")
    exprStack.push(expression)
  }

  def pushOp(op: Operator): Unit = {
    while (operatorStack.nonEmpty &&
      operatorStack.head != OpenBracket &&
      operatorStack.head.precedence > op.precedence) {
      popOp()
    }
    logger.debug(s"Pushing operator: $op")
    operatorStack.push(op)
  }


  def popOp(): Unit = {
    operatorStack.pop() match {
      case AssignmentOperator(biOp) =>
        val operand = exprStack.pop()
        val varExpression = exprStack.pop().asInstanceOf[VarExpression]
        pushExpression(AssignmentExpression(operand, biOp, varExpression.varname, varExpression.state))
      case UnaryOperator(op, opType, _) =>
        val operand = exprStack.pop()
        val expr = opType match {
          case UnaryOperatorType.Prefix => PrefixExpression(op, operand.asInstanceOf[VarExpression], this)
          case UnaryOperatorType.Postfix => PostfixExpression(op, operand.asInstanceOf[VarExpression], this)
          case UnaryOperatorType.None => UnaryExpression(op, operand)
        }
        pushExpression(expr)
      case BinaryOperator(op, _) =>
        val right = exprStack.pop()
        val left = exprStack.pop()
        pushExpression(BinaryExpression(op, left, right))
      case OpenBracket =>
        throw new IllegalArgumentException("Unbalanced brackets.")
    }
  }

}




