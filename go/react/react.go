package react

type reactor struct{}

type cell struct {
	value      int
	isInput    bool
	compute1   func(int) int
	compute2   func(int, int) int
	deps       []*cell
	dependents []*cell

	callbacks        map[int]func(int)
	callbackIDCounter int
}

type canceler struct {
	c       *cell
	cbID    int
}

func New() Reactor {
	return &reactor{}
}

func (r *reactor) CreateInput(initial int) InputCell {
	c := &cell{
		value:      initial,
		isInput:    true,
		callbacks:  make(map[int]func(int)),
	}
	return c
}

func (r *reactor) CreateCompute1(dep Cell, compute func(int) int) ComputeCell {
	depCell := dep.(*cell)
	c := &cell{
		compute1:   compute,
		deps:       []*cell{depCell},
		callbacks:  make(map[int]func(int)),
	}
	c.value = compute(depCell.Value())
	depCell.dependents = append(depCell.dependents, c)
	return c
}

func (r *reactor) CreateCompute2(dep1, dep2 Cell, compute func(int, int) int) ComputeCell {
	depCell1 := dep1.(*cell)
	depCell2 := dep2.(*cell)
	c := &cell{
		compute2:   compute,
		deps:       []*cell{depCell1, depCell2},
		callbacks:  make(map[int]func(int)),
	}
	c.value = compute(depCell1.Value(), depCell2.Value())
	depCell1.dependents = append(depCell1.dependents, c)
	depCell2.dependents = append(depCell2.dependents, c)
	return c
}

func (c *cell) Value() int {
	return c.value
}

func (c *cell) SetValue(value int) {
	if !c.isInput {
		return
	}
	if c.value == value {
		return
	}
	c.value = value
	c.propagate()
}

func (c *cell) propagate() {
	queue := []*cell{}
	visited := map[*cell]bool{}
	changed := map[*cell]bool{}

	for _, dep := range c.dependents {
		queue = append(queue, dep)
	}

	for len(queue) > 0 {
		curr := queue[0]
		queue = queue[1:]

		if visited[curr] {
			continue
		}
		visited[curr] = true

		oldVal := curr.value
		var newVal int
		if curr.compute1 != nil {
			newVal = curr.compute1(curr.deps[0].value)
		} else if curr.compute2 != nil {
			newVal = curr.compute2(curr.deps[0].value, curr.deps[1].value)
		}

		if oldVal != newVal {
			curr.value = newVal
			changed[curr] = true
			for _, dep := range curr.dependents {
				queue = append(queue, dep)
			}
		}
	}

	// After propagation, call callbacks on changed compute cells
	for cell := range changed {
		for _, cb := range cell.callbacks {
			cb(cell.value)
		}
	}
}

func (c *cell) AddCallback(callback func(int)) Canceler {
	c.callbackIDCounter++
	id := c.callbackIDCounter
	c.callbacks[id] = callback
	return &canceler{
		c:    c,
		cbID: id,
	}
}

func (c *canceler) Cancel() {
	delete(c.c.callbacks, c.cbID)
}
