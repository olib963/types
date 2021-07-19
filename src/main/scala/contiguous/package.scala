package object contiguous {

  /* Our application needs to represent a contiguous list of time periods. Our use case for this was billing periods.
   *
   * It's invalid for a period to end before it starts. It's also invalid for periods to have gaps between them or
   * for any two periods to overlap.
   *
   * The standard representation is to model each period as an independent entity that can be manipulated.
   *
   * We can eliminate the invalid states by modelling the whole timeline as one interconnected entity.
   */

}
