import org.apache.logging.log4j.scala.Logging
import scala.collection.JavaConverters._

sealed trait Expression {
  def result: Double
}


case class BinaryExpression(operator: (Double, Double) => Double, left: Expression, right: Expression) extends Expression {
  override def result: Double = operator(left.result, right.result)
}

case class UnaryExpression(operator: Double => Double, expr: Expression) extends Expression {
  override def result: Double = operator(expr.result)
}

case class PrefixExpression(operator: Double => Double, varExpression: VarExpression, state: CalcState) extends Expression {
  override def result: Double = {
    state.vars(varExpression.varname) = operator(varExpression.result)
    varExpression.result
  }
}

case class PostfixExpression(operator: Double => Double, varExpression: VarExpression, state: CalcState) extends Expression {
  override def result: Double = {
    val result = varExpression.result
    state.vars(varExpression.varname) = operator(varExpression.result)
    result
  }
}

case class VarExpression(varname: String, state: CalcState) extends Expression {
  override def result: Double = state.vars(varname)
}

case class AssignmentExpression(expr: Expression,
                                operator: Option[(Double, Double) => Double],
                                varname: String,
                                state: CalcState) extends Expression {
  override def result: Double = {
    state.vars(varname) = operator.fold(expr.result)(f => f(state.vars(varname), expr.result))
    state.vars(varname)
  }
}

case class SimpleExpression(result: Double) extends Expression

object Expression extends Logging {
  def apply(tokenizer: Tokenizer, state: CalcState): Expression = {
    logger.debug(s"Received new token stream: $tokenizer")
    if(tokenizer.hasNext){
      expectExpr(tokenizer.next(), tokenizer, state)
    } else {
      null
    }
  }


  private[this] def expectAfterExpr(token: Token, tokenizer: Tokenizer, state: CalcState): Expression = {
    token match {
      case EndToken => state.result
      case OperatorToken(op) =>
        op match {
          case assignment if assignment.contains("=") =>
            val biOpSplit = assignment.split("=")
            val biOp =
              if(biOpSplit.nonEmpty)
                Some(Operator.getBi(biOpSplit(0)).op)
              else
                None

            if(state.operatorStack.asScala.forall(op => op == OpenBracket || op.isInstanceOf[AssignmentOperator]) &&
            state.exprStack.getFirst.isInstanceOf[VarExpression]){
              state.pushOp(AssignmentOperator(biOp))
              expectExpr(tokenizer.next(), tokenizer, state)
            } else {
              throw new IllegalArgumentException("Illegal place for assignment: " + tokenizer.toString())
            }

          case "++" | "--" =>
            state.exprStack.getFirst match {
              case VarExpression(varname, _) =>
                state.pushOp(Operator.getUni(op, UnaryOperatorType.Postfix))
                expectAfterExpr(tokenizer.next(), tokenizer, state)
              case _ => throw new IllegalArgumentException("Variable expected: " + tokenizer.toString())
            }

          case _ =>
            state.pushOp(Operator.getBi(op))
            expectExpr(tokenizer.next(), tokenizer, state)
        }

      case CloseBracketToken =>
        try {
          while (state.operatorStack.getFirst != OpenBracket) state.popOp()
          state.operatorStack.pop()
          expectAfterExpr(tokenizer.next(), tokenizer, state)
        } catch {
          case e: NoSuchElementException => throw new IllegalArgumentException("Unbalanced close bracket: " + tokenizer.toString())
        }

    }

  }

  private[this] def expectVar(token: Token, tokenizer: Tokenizer, state: CalcState): Expression = {
    token match {
      case VariableToken(varname) =>
        processVariable(tokenizer, state, varname)
      case x => throw new IllegalArgumentException(s"Variable expected, but $token found")
    }
  }

  private def processVariable(tokenizer: Tokenizer, state: CalcState, varname: String) = {
    state.pushExpression(VarExpression(varname, state))
    expectAfterExpr(tokenizer.next(), tokenizer, state)
  }

  private[this] def expectExpr(token: Token, tokenizer: Tokenizer, state: CalcState): Expression = {
      token match {
      case OpenBracketToken =>
        state.operatorStack.push(OpenBracket)
        expectExpr(tokenizer.next(), tokenizer, state)
      case OperatorToken(op) =>
        op match {
          case "++" | "--"  =>
            state.pushOp(Operator.getUni(op, UnaryOperatorType.Prefix))
            expectVar(tokenizer.next(), tokenizer, state)

          case otherOp =>
            state.pushOp(Operator.getUni(op, UnaryOperatorType.None))
            expectExpr(tokenizer.next(), tokenizer, state)

        }


      case VariableToken(varname) =>
        processVariable(tokenizer, state, varname)

      case NumberToken(double) =>
        val expression = SimpleExpression(double)
        state.pushExpression(expression)
        expectAfterExpr(tokenizer.next(), tokenizer, state)
      case x => throw new IllegalArgumentException(s"Unexpected start of expression: $x in " + tokenizer.toString())


    }
  }


}
