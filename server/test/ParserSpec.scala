import com.itegulov.parser.{Parser, Tree}
import org.specs2.mutable.Specification
import org.specs2.matcher.Matchers
import org.specs2.specification.Scope

/**
  * @author Daniyar Itegulov
  */
class ParserSpec extends Specification with Matchers {
  trait WithParser extends Scope {
    lazy val parser: Parser = new Parser
  }

  "Parser" should {
    "parse unit numbers" in new WithParser {
      parser.parse("1") must beRight
      parser.parse("782396") must beRight
      parser.parse("-47362") must beRight
    }

    "parse add operation" in new WithParser {
      parser.parse("1 2 +") must beRight
      parser.parse("3 4 5 + +") must beRight
    }

    "parse sub operation" in new WithParser {
      parser.parse("378 47 -") must beRight
      parser.parse("62 -7382 923 - -") must beRight
      parser.parse("48 -462 - 273 -") must beRight
      parser.parse("2 -1 -") must beRight
    }

    "parse mul operation" in new WithParser {
      parser.parse("4 7 *") must beRight
      parser.parse("298 364 * 47 *") must beRight
    }

    "parse complex expressions" in new WithParser {
      parser.parse("1 2 + 3 * 8 - 14 +") must beRight
      parser.parse("1 2 3 4 5 6 - - - - -") must beRight
    }

    "fail on empty expression" in new WithParser {
      parser.parse("") must beLeft
    }

    "fail on incomplete expression" in new WithParser {
      parser.parse("1 2") must beLeft
      parser.parse("1 2 3 -") must beLeft
      parser.parse("1 2 3 *") must beLeft
      parser.parse("1 2 3 +") must beLeft
    }

    "fail on overcomplete expression" in new WithParser {
      parser.parse("1 +") must beLeft
      parser.parse("1 2 - +") must beLeft
      parser.parse("1 2 3 * + -") must beLeft
    }
  }
}
