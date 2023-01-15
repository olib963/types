package ipaddress

func MaskString(address string, mask string) {
	// Where do we even start???
}

var (
	ValidStates = []string{
		"10.1.12.10",
		"10.0.0.0/8",
	}
	InvalidStates = []string{
		"foo",
		"only IP adresses starting with 10",
	}
)
