package contiguous

// Golang does not have a set implementation so I have written one here

type Set[A comparable] struct {
	internal map[A]struct{}
}

func SetOf[A comparable](values ...A) Set[A] {
	set := Set[A]{internal: make(map[A]struct{})}
	for _, v := range values {
		set = set.Add(v)
	}
	return set
}

func (set Set[A]) Add(a A) Set[A] {
	c := set.copy()
	c.internal[a] = struct{}{}
	return c
}

func (set Set[A]) Remove(a A) Set[A] {
	c := set.copy()
	delete(c.internal, a)
	return c
}

func (set Set[A]) Contains(a A) bool {
	_, exists := set.internal[a]
	return exists
}

func (set Set[A]) toSlice() []A {
	s := make([]A, len(set.internal))
	index := 0
	for a := range set.internal {
		s[index] = a
		index++
	}
	return s
}

func (set Set[A]) copy() Set[A] {
	c := make(map[A]struct{})
	for k, v := range set.internal {
		c[k] = v
	}
	return Set[A]{
		internal: c,
	}
}
