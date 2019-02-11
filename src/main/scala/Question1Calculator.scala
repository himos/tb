import java.io._

import scala.io.Source

object Question1Calculator extends App {

    if(args.length < 1) throw new IllegalArgumentException("Expecting file path as program argument")

    var file = new File(args(0))

    val source =  Source.fromFile(file)

    val state = CalcState()

    source.getLines().foreach(l => {
        if(l.nonEmpty) Expression(new Tokenizer(new CharStreamProcessorImpl(l)), state).result
    })

    println(state.vars.map(kv => kv._1 + "=" + kv._2).mkString("(", ",", ")"))

}
