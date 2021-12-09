# Impossible States

## What is the repo about?

By changing the datastructures we represent our state with we can remove the possibility of having our application in 
an invalid state. This repo contains examples of the application of this principle.

## Why does it exist?

Applying this to our codebases means there are fewer code paths, possible operations and consequently fewer tests to write.
Making it impossible for any client interaction to fundamentally break your state is a fantastic goal to achieve. This
is not something we widely talk about so this repository will serve as a hub for examples of such improvements, hopefully
inspiring us to recognise when we can apply this ourselves.

It is, of course, always possible for applications to get into an "incorrect" state e.g. by a user typing in the wrong
date, but this repo focussed on removing the possibility of "invalid" states. 

## How to use it?

The repo has been broken into packages that run through an example of applying a model change to make
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

The original repo was written for Scala, but Golang examples have been added in the `golang` directory where
possible. Due to Go's lack of sum types certain refactorings (e.g. the redux one) are not possible.

## Inefficiencies

There are more efficient/idiomatic ways of implementing certain things in this repo, but the slightly less standard approach
has been chosen to illustrate a point in each case that would be hidden by the idiomatic approach. Taking the `NonEmptyList` example:

- The `sum` of a `List[Int]` doesn't require an explicit match on the empty list, but we are handling the invalid state
of empty list in every function. This is more apparent for `min` & `max` and is made explicit in `sum` for this reason.
When moving to the `NonEmptyList` we can literally see a code path removed from every function, this would be hidden
by a simple `fold`.

- `Option` composition can be done with a `for` comprehension. Composing the `Option`s this way hides the fact that we know
we _should_ never have `None` returned from `maxOption` iff (if and only if) `minOption` returns a `Some`. Performing a 
`for` comprehension hides the fact that we are having to match on impossible cases.

## External Links

Links to talks and blog posts on similar topics.

- [Parse Don't Validate](https://lexi-lambda.github.io/blog/2019/11/05/parse-don-t-validate/) A blog post on type driven
 design highlighting the principles applied in this repo.
- [Making Impossible States Impossible](https://youtu.be/IcgmSRJHu_8) A great talk by Richard Feldman on using different 
data structures in Elm to enforce constraints. This is where the survey example comes from.
- [Elm Accessible HTML](https://github.com/tesk9/accessible-html) A library that does not allow you to trigger events from 
non-interactive elements and enforces accessibility where possible.

## Contribution

If you come up with any other new examples feel free to open a PR with a new package following
the same general layout as the others.

If you find any other external links on similar topics also feel free to add these in a PR.

