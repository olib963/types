package primitive

package object boolean {
  /* An example of how primitive obsession can lead to invalid states that you need to handle.
   *
   * It's invalid in our application for our state to both be active and deleted in Status
   *
   * We can eliminate the invalid states using a custom union type that better represents our domain
   */

}
