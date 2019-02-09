import StringsTransformer.StringFunction

import scala.collection.JavaConverters._

object Question3Transformations extends App{

  val st = new StringsTransformer(List("A","B","C").asJava)
  st.transform(List(new StringFunction {
    override def transform(str: String): String = str.toLowerCase()
  }, new StringFunction {
    override def transform(str: String): String = str.toUpperCase()
  }).asJava)

}
