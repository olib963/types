package lists

import "fmt"

type ListDescription struct {
	Min   int
	Max   int
	Total int
}

func (d ListDescription) String() string {
	return fmt.Sprintf("%+v", d)
}
