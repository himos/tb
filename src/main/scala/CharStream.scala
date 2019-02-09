class CharStream(originalStr: String) {


  private[this] val limit = originalStr.length - 1
  private[this] var currentPos = 0

  def next(): Option[Char] = {
    currentPos = math.min( currentPos + 1, originalStr.length)
    current()
  }

  def current(): Option[Char] = {
    if(currentPos <= limit){
      Some(originalStr.charAt(currentPos))
    } else {
      None
    }
  }

  def containsFromCurrPos(findStr: String): Boolean = {
    val res = (currentPos + findStr.length <= limit + 1) &&
      (originalStr.slice(currentPos, currentPos + findStr.length) == findStr)
    if(res) currentPos += findStr.length
    res
  }
}
