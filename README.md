# Foo

Replacing shotgun validation with different datastructures reduces the amount of code you write, possible test cases
and means it's impossible for a client or another developer to create a broken state.

## What is the repo about?

## Why does it exist?

## How to use it?

The repo is broken into packages that run through an example of applying a model change to make
it impossible to create invalid states.

- Each package has a package object roughly outlining the domain modelling problem the package tackles.
- Each package will contain a more "standard" approach that allows for both valid and invalid states.
- Each package will contain a different approach that only allows valid states.
- Each package will contain a mapping from the valid only approach back to the standard approach.

The packages in increasing complexity order (roughly) are:
- `primitive`
- `lists`
- `survey`
- `contiguous`



## Inefficiencies

There are more efficient/idiomatic ways of implementing certain things in this repo, but I have chosen the slightly
less standard approach to illustrate a point in each case. I feel the points would be hidden by the idiomatic approach. For example
in the `NonEmptyList` example:

- The sum of a list doesn't require a match on the empty list, but we are handling the invalid state of empty list in every 
function, this is more apparent for `min` & `max` and is made explicit in `sum` for this reason. When moving to the NEL we
can literally see a code path removed from every function.

- `Option` composition can be done with a for comprehension. Composing the `Option`s this way hides the fact that we know we _should_ 
never have `None, None`, so why do we need to compose them at all?

## External Links

Links to talks and blog posts on similar topics.

- [Parse Don't Validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/) A blog post on type driven
 design highlighting the principles applied in this repo.
- [Making Impossible States Impossible](https://youtu.be/IcgmSRJHu_8) A great talk by Richard Feldman on using different 
data structures in Elm to enforce constraints. This is where the survey example comes from.
- [Elm Accessible HTML](https://github.com/tesk9/accessible-html) A library that does not allow you to trigger events from 
non-interactive elements and enforces accessibility where possible.

<!-- TODO more links -->

## Contribution

If you come up with any other new examples feel free to open a PR with a new package following
the same general layout as the others.

Also feel free to add any related links you feel are a good representation of these principles.

- [ ] Come up with name for project: Validation is for suckers?? Parsing is great. Simplifying your state. 
    Always Valid, mostly correct. Should not => Can not. Replacing tests with types! Some kind of shotgun validation pun
- [ ] Consistency in the package presentation
- [ ] Tests and property tests
