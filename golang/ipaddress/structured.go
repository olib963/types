package ipaddress

type IPAddress struct {
	first  uint8 // could be turned into binary if we wanted
	second uint8
	third  uint8
	fourth uint8
}

func MakeIPAddress(first, second, third, fourth uint8) IPAddress {
	return IPAddress{first, second, third, fourth}
}

type BytePredicate = func(uint8) bool
type Mask struct {
	firstMask  BytePredicate
	secondMask BytePredicate
	thirdMask  BytePredicate
	fourthMask BytePredicate
}

var always = BytePredicate(func(_ uint8) bool { return true })

func equal(byte uint8) BytePredicate {
	return func(addressByte uint8) bool { return byte == addressByte }
}

func MaskFirstEightBits(byte uint8) Mask {
	return MakeMask(equal(byte), always, always, always)
}

func MakeMask(firstMask, secondMask, thirdMask, fourthMask BytePredicate) Mask {
	return Mask{firstMask, secondMask, thirdMask, fourthMask}
}

func IsIn(address IPAddress, mask Mask) bool {
	return mask.firstMask(address.first) &&
		mask.secondMask(address.second) &&
		mask.thirdMask(address.third) &&
		mask.fourthMask(address.fourth)
}

var (
	ValidIP   = MakeIPAddress(10, 12, 10, 127)
	ValidMask = MaskFirstEightBits(10)
	isIn      = IsIn(ValidIP, ValidMask)
)
