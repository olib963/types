# Foo

- [ ] Come up with name for project: Validation is for suckers?? Parsing is great. Simplifying your state. Always Valid, mostly correct. Should not => Can not. Replacing tests with types!
- [ ] Is it ZipList? OR something else?? Watch vid again
- [ ] Consistency in the package presentation
- [ ] Tests and property tests
- [ ] High level overview of what you want to achieve
- [ ] Write a talk for each section
- [ ] Links to related posts and talks, PNV, Elm survey, Elm Accessibility, Making Impossible states irrepresentable, Decoders? What else?
- [ ] Slides? For OTF
- [ ] Example Gifs from Games



I know there are more efficient/idiomatic ways of implementing certain things in this repo, but I have chosen the slightly
less standard approach to illustrate a point in each case. I feel the points would be hidden by the idiomatic approach. For example
in the `NonEmptyList` example:
- The sum of a list doesn't require a match on the empty list, but we are handling the invalid state of empty list in every 
function, this is more apparent for `min` & `max` and is made explicit in `sum` for this reason. When moving to the NEL we
can literally see a code path being removed from every function.
- `Option` composition can be done with a for comprehension. Composing the `Option`s this way hides the fact that we know we _should_ 
never have `None, None`, so why do we need to compose them at all?
