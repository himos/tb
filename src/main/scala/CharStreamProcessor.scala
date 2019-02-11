trait CharStreamProcessor{
  def processWhile(f: Char => Boolean): String
  def processString(s: String): Boolean
  def currentChar: Char
  def isFinished: Boolean
}

class CharStreamProcessorImpl(originalStr: String) extends CharStreamProcessor {

  assert(originalStr.length > 0, "String should not be empty")

  private[this] val limit = originalStr.length - 1
  private[this] var currentPos = 0

  override def processString(s: String): Boolean = {
    val res = (currentPos + s.length <= originalStr.length) &&
      (originalStr.slice(currentPos, currentPos + s.length) == s)
    if(res) currentPos = currentPos + s.length
    res
  }

  override def processWhile(f: Char => Boolean): String = {
    val sb = new StringBuilder
    while(currentPos < originalStr.length && f(originalStr(currentPos))){
      sb += originalStr(currentPos)
      currentPos += 1
    }
    sb.toString()
  }

  override def isFinished: Boolean = currentPos > limit

  override def toString: String = {
    val sb = new StringBuilder
    sb ++= originalStr.slice(0, currentPos)
    sb += '^'
    if (currentPos < originalStr.length)
      sb ++= originalStr.slice(currentPos, originalStr.length)
    sb.result()
  }

  override def currentChar: Char =
    if(currentPos < originalStr.length)
      originalStr(currentPos)
    else
      null.asInstanceOf[Char]
}
