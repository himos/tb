import org.apache.logging.log4j.scala.Logging

sealed trait Expression {
  def result: Double
}


case class BinaryExpression(operator: (Double, Double) => Double, left: Expression, right: Expression) extends Expression {
  override def result: Double = operator(left.result, right.result)
}

case class UnaryExpression(operator: Double => Double, expr: Expression) extends Expression {
  override def result: Double = operator(expr.result)
}

case class PrefixExpression(operator: Double => Double, varExpression: VarExpression, state: State) extends Expression {
  override def result: Double = {
    state.vars(varExpression.varname) = operator(varExpression.result)
    println(state.vars(varExpression.varname))
    varExpression.result
  }
}

case class PostfixExpression(operator: Double => Double, varExpression: VarExpression, state: State) extends Expression {
  override def result: Double = {
    val result = varExpression.result
    state.vars(varExpression.varname) = operator(varExpression.result)
    result
  }
}

case class VarExpression(varname: String, state: State) extends Expression {
  override def result: Double = state.vars(varname)
}

case class AssignmentExpression(expr: Expression, varname: String, state: State) extends Expression {
  override def result: Double = {
    state.vars(varname) = expr.result
    state.vars(varname)
  }
}

case class SimpleExpression(result: Double) extends Expression

object Expression extends Logging {
  def apply(tokenizer: Tokenizer, state: State): Expression = {
    logger.info(s"Received new token stream: $tokenizer")
    val token = tokenizer.next()
    token match {
      case EndToken => null
      case otherToken => expectExpr(token, tokenizer, state)
    }
  }


  private[this] def expectAfterExpr(token: Token, tokenizer: Tokenizer, state: State): Expression = {
    token match {
      case EndToken => state.result
      case OperatorToken(op) =>
        op match {
          case "++" | "--" =>
            pushOp(Operator.getUni(op, UnaryOperatorType.Postfix), state)
            expectAfterExpr(tokenizer.next(), tokenizer, state)
          case otherOp =>
            pushOp(Operator.getBi(op), state)
            expectExpr(tokenizer.next(), tokenizer, state)
        }
      case AssignmentToken =>
        if(state.operatorStack.forall(op => op == OpenBracket || op == AssignmentOperator)){
          state.operatorStack.push(AssignmentOperator)
          expectExpr(tokenizer.next(), tokenizer, state)
        } else {
          throw new IllegalArgumentException("Illegal place for assignment.")
        }
      case CloseBracketToken =>
        while (state.operatorStack.head != OpenBracket) popOp(state)
        state.operatorStack.pop()
        expectAfterExpr(tokenizer.next(), tokenizer, state)

    }

  }

  private[this] def expectVar(token: Token, tokenizer: Tokenizer, state: State): Expression = {
    token match {
      case VariableToken(varname) =>
        processVariable(tokenizer, state, varname)
      case x => throw new IllegalArgumentException(s"Variable expected, but $token found")
    }
  }

  private def processVariable(tokenizer: Tokenizer, state: State, varname: String) = {
    pushExpression(state, VarExpression(varname, state))
    expectAfterExpr(tokenizer.next(), tokenizer, state)
  }

  private[this] def expectExpr(token: Token, tokenizer: Tokenizer, state: State): Expression = {
      token match {
      case OpenBracketToken =>
        state.operatorStack.push(OpenBracket)
        expectExpr(tokenizer.next(), tokenizer, state)
      case OperatorToken(op) =>
        op match {
          case "++" | "--"  =>
            pushOp(Operator.getUni(op, UnaryOperatorType.Prefix), state)
            expectVar(tokenizer.next(), tokenizer, state)

          case otherOp =>
            pushOp(Operator.getUni(op, UnaryOperatorType.None), state)
            expectExpr(tokenizer.next(), tokenizer, state)

        }


      case VariableToken(varname) =>
        processVariable(tokenizer, state, varname)

      case NumberToken(double) =>
        val expression = SimpleExpression(double)
        pushExpression(state, expression)
        expectAfterExpr(tokenizer.next(), tokenizer, state)
      case x => throw new IllegalArgumentException(s"Unexpected start of expression: $x")


    }
  }

  private def pushExpression(state: State, expression: Expression) = {
    logger.info(s"Pushing expression ${expression.getClass}")
    state.exprStack.push(expression)
  }

  private[this] def pushOp(op: Operator, state: State): Unit = {
    val opStack = state.operatorStack
    while (opStack.nonEmpty && opStack.head != OpenBracket && opStack.head.precedence > op.precedence) {
      popOp(state)
    }
    logger.info(s"Pushing operator: $op")
    opStack.push(op)
  }


  private[this] def popOp(state: State): Unit = {
    val exprStack = state.exprStack
    state.operatorStack.pop() match {
      case AssignmentOperator =>
        val operand = exprStack.pop()
        val varExpression = exprStack.pop().asInstanceOf[VarExpression]
        pushExpression(state, AssignmentExpression(operand, varExpression.varname, varExpression.state))
      case UnaryOperator(op, opType, _) =>
        val operand = exprStack.pop()
        val expr = opType match {
          case UnaryOperatorType.Prefix => PrefixExpression(op, operand.asInstanceOf[VarExpression], state)
          case UnaryOperatorType.Postfix => PostfixExpression(op, operand.asInstanceOf[VarExpression], state)
          case UnaryOperatorType.None => UnaryExpression(op, operand)
        }
        pushExpression(state, expr)
      case BinaryOperator(op, _) =>
        val right = exprStack.pop()
        val left = exprStack.pop()
        pushExpression(state, BinaryExpression(op, left, right))
    }
  }

}
