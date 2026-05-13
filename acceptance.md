# Acceptance Criteria

## Task 1: Core item model and controller with automatic list diffing

### Acceptance Criteria
- [ ] ItemModel assigns auto-decremented negative IDs starting from -1 for each new instance
- [ ] ItemModel supports explicit ID setting via id(long), id(CharSequence), and composite id(long, long)
- [ ] ItemModel.id() throws InvalidUsageException when called after the model has been added to an adapter
- [ ] ItemModel.equals/hashCode compares id, viewType, and visibility; subclasses extend with their own fields
- [ ] ItemModel supports show/hide toggling with isVisible() reflecting current state
- [ ] ItemModel has bind/unbind lifecycle methods
- [ ] TrackedModelList notifies observer on insertions and removals with correct positions and counts
- [ ] TrackedModelList supports pausing/resuming notifications
- [ ] TrackedModelList.set() fires removal+insertion when model IDs differ, no notification when IDs match
- [ ] TrackedModelList.subList().clear() fires a single batch removal notification
- [ ] FrozenModelList throws IllegalStateException when modified after freeze()
- [ ] OperationCollector batches consecutive ADD operations at adjacent positions into single operations
- [ ] OperationCollector batches consecutive REMOVE operations at same/adjacent positions into single operations
- [ ] OperationCollector batches consecutive UPDATE operations at adjacent positions
- [ ] OperationCollector does not batch MOVE operations
- [ ] DiffCalculator correctly computes no-op diff when lists are identical
- [ ] DiffCalculator correctly computes insertions when items are added
- [ ] DiffCalculator correctly computes removals when items are deleted
- [ ] DiffCalculator correctly computes moves when items change position
- [ ] DiffCalculator correctly computes updates when item content changes but ID stays the same
- [ ] DiffCalculator handles all permutations of a list up to size 5 correctly
- [ ] DiffCalculator handles random combinations of insert/remove/change/move
- [ ] ListController.buildModels() is called when requestModelBuild() is invoked
- [ ] ListController dispatches diff operations to a ListChangeObserver
- [ ] ListController interceptors can add and modify models after buildModels()
- [ ] ListController interceptors run in the order they were added
- [ ] ListController duplicate filtering removes models with duplicate IDs
- [ ] ListController model build listeners are called after build completes with the diff result
- [ ] ListController.moveModel() correctly moves items and dispatches a move notification
- [ ] ListController throws InvalidUsageException when requestModelBuild is called inside buildModels
- [ ] ListController throws InvalidUsageException when adding a model with default (unset) ID
