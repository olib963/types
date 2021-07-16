package primitive

package object option {
  /* An example of how using Options everywhere can lead to invalid states that you need to handle. This tends to
   * be a carry over from an approach that would have been taken in a more dynamic language e.g. Clojure -> Scala or
   * JavaScript -> TypeScript
   *
   * Given the above I've picked the standard Redux <-> React integration approach as a problem to solve. This problem
   * however frequently occurs in our Avro schemas where we define multiple nullable fields that do not make sense together
   * or when all are null.
   *
   * We can eliminate the invalid states using a custom union type that better represents our domain
   */

}
