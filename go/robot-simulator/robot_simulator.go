package robot

import (
	"fmt"
	// "sync" // Removed as it's not needed for the core implementation
)

// See defs.go for other definitions

// Step 1
// Define N, E, S, W here.
const (
	N Dir = 0
	E Dir = 1
	S Dir = 2
	W Dir = 3
)

// Right turns the robot 90 degrees clockwise.
func Right() {
	Step1Robot.Dir = (Step1Robot.Dir + 1) % 4
}

// Left turns the robot 90 degrees counter-clockwise.
func Left() {
	Step1Robot.Dir = (Step1Robot.Dir - 1 + 4) % 4
}

// Advance moves the robot one step forward in the direction it is facing.
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

// String returns the string representation of the direction.
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
		return "?" // Should not happen
	}
}

// Step 2
// Define Action type here.
type Action Command // Re-using Command type for simplicity in Step 2

// StartRobot runs the robot logic. It receives commands from the command channel
// and sends actions to the action channel. It shuts down when the command channel is closed.
func StartRobot(command chan Command, action chan Action) {
	defer close(action) // Close action channel when robot shuts down
	for cmd := range command {
		action <- Action(cmd) // Send the command as an action
	}
}

// Room simulates the room environment. It receives actions from the robot,
// updates the robot's state, and ensures it stays within bounds.
// When the action channel closes, it sends the final state via the report channel.
func Room(extent Rect, robot Step2Robot, action chan Action, report chan Step2Robot) {
	currentRobot := robot // Make a mutable copy

	for act := range action {
		switch Command(act) { // Convert Action back to Command
		case 'R':
			currentRobot.Dir = (currentRobot.Dir + 1) % 4
		case 'L':
			currentRobot.Dir = (currentRobot.Dir - 1 + 4) % 4
		case 'A':
			nextPos := currentRobot.Pos
			switch currentRobot.Dir {
			case N:
				nextPos.Northing++
			case E:
				nextPos.Easting++
			case S:
				nextPos.Northing--
			case W:
				nextPos.Easting--
			}
			// Check bounds (inclusive Max)
			if nextPos.Easting >= extent.Min.Easting && nextPos.Easting <= extent.Max.Easting &&
				nextPos.Northing >= extent.Min.Northing && nextPos.Northing <= extent.Max.Northing {
				currentRobot.Pos = nextPos
			}
		}
	}
	report <- currentRobot // Send final state
}

// Step 3
// Define Action3 type here.
type Action3 struct {
	Name   string
	Action Command // 'R', 'L', 'A'
}

// StartRobot3 runs a robot based on a script.
func StartRobot3(name, script string, action chan Action3, log chan string) {
	if name == "" {
		log <- "A robot without a name"
		// We might choose to stop here, but the tests expect the robot to run
		// even without a name, potentially logging other errors later.
	}
	for _, cmd := range script {
		switch Command(cmd) {
		case 'R', 'L', 'A':
			action <- Action3{Name: name, Action: Command(cmd)}
		default:
			log <- fmt.Sprintf("An undefined command in a script: %c", cmd)
			// Stop processing script on invalid command? Test behavior implies continue.
		}
	}
	// Signal completion by sending a special action or just closing the channel?
	// The instructions imply the Room detects shutdown when the action channel closes.
	// So, individual robots don't need to signal completion explicitly here.
}

// Room3 manages multiple robots concurrently within defined bounds.
func Room3(extent Rect, robots []Step3Robot, action chan Action3, rep chan []Step3Robot, log chan string) {
	// Use a map for efficient lookup and update of robot states
	robotStates := make(map[string]*Step3Robot)
	// Keep track of occupied positions for collision detection
	occupied := make(map[Pos]string) // Position -> Robot Name

	// Initial placement and validation
	for i := range robots {
		r := &robots[i] // Get pointer to modify the original slice element

		// Check for unnamed robots
		if r.Name == "" {
			log <- "A robot without a name"
			// Let unnamed robots proceed for now, they might generate unknown action logs later
		}

		// Check for duplicate names
		if _, exists := robotStates[r.Name]; exists && r.Name != "" { // Only log duplicates if name is not empty
			log <- fmt.Sprintf("Duplicate robot names: %s", r.Name)
			continue // Skip duplicate robots
		}

		// Check placement bounds
		if r.Pos.Easting < extent.Min.Easting || r.Pos.Easting > extent.Max.Easting ||
			r.Pos.Northing < extent.Min.Northing || r.Pos.Northing > extent.Max.Northing {
			log <- fmt.Sprintf("A robot placed outside of the room: %s", r.Name)
			continue // Skip robots placed outside
		}

		// Check for duplicate positions
		if existingRobotName, exists := occupied[r.Pos]; exists {
			log <- fmt.Sprintf("Robots placed at the same place: %s and %s", existingRobotName, r.Name)
			// Which robot gets the spot? The test seems to imply the first one keeps it.
			continue // Skip the second robot trying to occupy the same spot
		}

		// Only add named robots to the state and occupied map
		if r.Name != "" {
			robotStates[r.Name] = r
			occupied[r.Pos] = r.Name
		}
	}

	// Process actions from robots
	for act := range action {
		robot, exists := robotStates[act.Name]
		if !exists {
			// It's possible a robot was skipped during initialization (e.g., duplicate name, unnamed)
			// but its StartRobot3 goroutine still sent actions.
			// Or, it could be an action from a truly unknown robot.
			if act.Name != "" { // Don't log unknown for the unnamed robot we allowed earlier
				log <- fmt.Sprintf("An action from an unknown robot: %s", act.Name)
			}
			continue
		}

		currentPos := robot.Pos
		nextPos := currentPos
		nextDir := robot.Dir

		switch act.Action {
		case 'R':
			nextDir = (robot.Dir + 1) % 4
			robot.Dir = nextDir // Update direction immediately
		case 'L':
			nextDir = (robot.Dir - 1 + 4) % 4
			robot.Dir = nextDir // Update direction immediately
		case 'A':
			switch robot.Dir {
			case N:
				nextPos.Northing++
			case E:
				nextPos.Easting++
			case S:
				nextPos.Northing--
			case W:
				nextPos.Easting--
			}

			// Check wall collision
			if nextPos.Easting < extent.Min.Easting || nextPos.Easting > extent.Max.Easting ||
				nextPos.Northing < extent.Min.Northing || nextPos.Northing > extent.Max.Northing {
				log <- fmt.Sprintf("A robot attempting to advance into a wall: %s", robot.Name)
				// Do not update position
			} else if occupyingRobot, collision := occupied[nextPos]; collision {
				// Check robot collision
				log <- fmt.Sprintf("A robot attempting to advance into another robot: %s into %s", robot.Name, occupyingRobot)
				// Do not update position
			} else {
				// Valid move: update position in map and state
				delete(occupied, currentPos)
				occupied[nextPos] = robot.Name
				robot.Pos = nextPos
			}
		}
	}

	// Action channel closed, prepare and send report
	// The report should contain the final states of the robots initially provided,
	// updated with their final positions and directions.
	// We need to iterate through the *original* robots slice and update based on robotStates map.
	finalReport := make([]Step3Robot, 0, len(robots))
	processedNames := make(map[string]bool) // Track names already added to avoid duplicates in report

	for _, initialRobot := range robots {
		if finalState, ok := robotStates[initialRobot.Name]; ok {
			// Only include robots that were successfully initialized and processed
			// And ensure we don't add the same named robot twice if it appeared multiple times initially
			if !processedNames[initialRobot.Name] {
				finalReport = append(finalReport, *finalState)
				processedNames[initialRobot.Name] = true
			}
		}
		// Robots that were skipped initially (bad name, position etc) are not included.
	}

	rep <- finalReport
}
