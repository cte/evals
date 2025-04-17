package robot

// See defs.go for other definitions

// Step 1
const (
	N Dir = iota
	E
	S
	W
)

func Right() {
	Step1Robot.Dir = Dir((int(Step1Robot.Dir) + 1) % 4)
}

func Left() {
	Step1Robot.Dir = Dir((int(Step1Robot.Dir) + 3) % 4)
}

func Advance() {
	switch Step1Robot.Dir {
	case N:
		Step1Robot.Y++
	case E:
		Step1Robot.X++
	case S:
		Step1Robot.Y--
	case W:
		Step1Robot.X--
	}
}

func (d Dir) String() string {
	switch d {
	case N:
		return "N"
	case E:
		return "E"
	case S:
		return "S"
	case W:
		return "W"
	default:
		return "?"
	}
}

// Step 2
// Define Action type here.

func StartRobot(command chan Command, action chan Action) {
	// step 2 not implemented yet
}

func Room(extent Rect, robot Step2Robot, action chan Action, report chan Step2Robot) {
	// step 2 not implemented yet
}

// Step 3
// Define Action3 type here.

func StartRobot3(name, script string, action chan Action3, log chan string) {
	// step 3 not implemented yet
}

func Room3(extent Rect, robots []Step3Robot, action chan Action3, rep chan []Step3Robot, log chan string) {
	// step 3 not implemented yet
}
