# Acceptance Criteria

## Task 1: Core item model and controller with automatic list diffing

### Acceptance Criteria
- [x] ItemModel assigns auto-decremented negative IDs starting from -1 for each new instance
- [x] ItemModel supports explicit ID setting via id(long), id(CharSequence), and composite id(long, long)
- [x] ItemModel.id() throws InvalidUsageException when called after the model has been added to an adapter
- [x] ItemModel.equals/hashCode compares id, viewType, and visibility; subclasses extend with their own fields
- [x] ItemModel supports show/hide toggling with isVisible() reflecting current state
- [x] ItemModel has bind/unbind lifecycle methods
- [x] TrackedModelList notifies observer on insertions and removals with correct positions and counts
- [x] TrackedModelList supports pausing/resuming notifications
- [x] TrackedModelList.set() fires removal+insertion when model IDs differ, no notification when IDs match
- [x] TrackedModelList.subList().clear() fires a single batch removal notification
- [x] FrozenModelList throws IllegalStateException when modified after freeze()
- [x] OperationCollector batches consecutive ADD operations at adjacent positions into single operations
- [x] OperationCollector batches consecutive REMOVE operations at same/adjacent positions into single operations
- [x] OperationCollector batches consecutive UPDATE operations at adjacent positions
- [x] OperationCollector does not batch MOVE operations
- [x] DiffCalculator correctly computes no-op diff when lists are identical
- [x] DiffCalculator correctly computes insertions when items are added
- [x] DiffCalculator correctly computes removals when items are deleted
- [x] DiffCalculator correctly computes moves when items change position
- [x] DiffCalculator correctly computes updates when item content changes but ID stays the same
- [x] DiffCalculator handles all permutations of a list up to size 5 correctly
- [x] DiffCalculator handles random combinations of insert/remove/change/move
- [x] ListController.buildModels() is called when requestModelBuild() is invoked
- [x] ListController dispatches diff operations to a ListChangeObserver
- [x] ListController interceptors can add and modify models after buildModels()
- [x] ListController interceptors run in the order they were added
- [x] ListController duplicate filtering removes models with duplicate IDs
- [x] ListController model build listeners are called after build completes with the diff result
- [x] ListController.moveModel() correctly moves items and dispatches a move notification
- [x] ListController throws InvalidUsageException when requestModelBuild is called inside buildModels
- [x] ListController throws InvalidUsageException when adding a model with default (unset) ID

## Task 2: List adapter with view holder management and view type mapping

### Acceptance Criteria
- [ ] ViewTypeRegistry assigns stable integer view types from model layout IDs
- [ ] ViewTypeRegistry auto-assigns negative view types for models with zero layout IDs
- [ ] ViewTypeRegistry reuses view types for the same model class across calls
- [ ] ViewTypeRegistry is shared as a static instance across all adapters
- [ ] ListItemHolder wraps a view reference and tracks the currently bound model
- [ ] ListItemHolder.bind() calls the model's bind lifecycle method
- [ ] ListItemHolder.bind() with a previous model calls the differential bind overload
- [ ] ListItemHolder.unbind() calls the model's unbind method and clears references
- [ ] BoundHolderTracker tracks currently bound holders keyed by model ID
- [ ] BoundHolderTracker supports add, remove, get, getByModel, and iteration
- [ ] HolderState saves holder state for models with shouldSaveViewState() true
- [ ] HolderState restores saved state when a holder is bound to a model with matching ID
- [ ] HolderState skips save/restore for models where shouldSaveViewState() is false
- [ ] PlaceholderModel returns zero span size and has no-op bind/unbind
- [ ] PayloadWrapper wraps the previous model for differential binding
- [ ] PayloadWrapper supports single-model and multi-model storage with lookup by ID
- [ ] BaseListAdapter.getItemCount() returns the number of current models
- [ ] BaseListAdapter.getItemId() returns the model's ID at the given position
- [ ] BaseListAdapter.getItemViewType() returns the view type from ViewTypeRegistry
- [ ] BaseListAdapter.bindHolder() dispatches bind lifecycle and tracks the holder
- [ ] BaseListAdapter.unbindHolder() dispatches unbind, saves state, and untracks the holder
- [ ] BaseListAdapter.saveState()/restoreState() persists and recovers all holder state
- [ ] DirectAdapter supports addModel/removeModel/removeAllModels with observer notifications
- [ ] DirectAdapter supports insertModelBefore and insertModelAfter
- [ ] DirectAdapter.showModel/hideModel toggles visibility and dispatches change notification
- [ ] DirectAdapter.getModelForPosition() returns PlaceholderModel for hidden models
- [ ] DirectAdapter.notifyModelsChanged() uses DiffCalculator to compute minimal updates
- [ ] SimpleDirectAdapter exposes all DirectAdapter mutations as public methods
- [ ] ControllerAdapter receives model lists from ListController and forwards change notifications
- [ ] ControllerAdapter enforces that models are built through the controller, not modified directly

## Task 3: Annotation definitions for model attributes, views, and package configuration

### Acceptance Criteria
- [ ] ModelAttribute has FIELD target and CLASS retention with value() returning Option[] defaulting to empty
- [ ] ModelAttribute.Option enum defines NoGetter, NoSetter, DoNotHash, IgnoreRequireHashCode, DoNotUseInToString
- [ ] ViewModelSpec has TYPE target and CLASS retention
- [ ] ViewModelSpec.Size enum defines NONE, MANUAL, and four width/height size combinations
- [ ] ViewModelSpec has autoLayout(), defaultLayout(), baseModelClass(), saveViewState(), fullSpan() elements
- [ ] ModelProperty has METHOD and FIELD targets with CLASS retention
- [ ] ModelProperty.Option enum defines DoNotHash, IgnoreRequireHashCode, GenerateStringOverloads, NullOnRecycle
- [ ] ModelProperty has options(), defaultValue(), group() elements with correct defaults
- [ ] TextProperty has METHOD and FIELD targets, CLASS retention, and defaultRes() element
- [ ] CallbackProperty is a marker annotation on METHOD and FIELD with CLASS retention
- [ ] AfterPropertiesSet is a marker annotation on METHOD with CLASS retention
- [ ] OnRecycled is a marker annotation on METHOD with CLASS retention
- [ ] VisibilityChanged is a marker annotation on METHOD with CLASS retention
- [ ] VisibilityStateChanged is a marker annotation on METHOD with CLASS retention
- [ ] AutoModel is a marker annotation on FIELD with CLASS retention
- [ ] GeneratedModelClass has TYPE target and CLASS retention with layout() defaulting to 0
- [ ] DataBindingLayouts has TYPE target and CLASS retention with value() int[] and enableDoNotHash() defaulting to true
- [ ] DataBindingPattern has TYPE target and CLASS retention with rClass(), layoutPrefix(), and enableDoNotHash()
- [ ] PackageConfig has TYPE target and CLASS retention with requireHashCode, requireAbstractModels, implicitlyAddAutoModels
- [ ] PackageViewConfig has TYPE target and CLASS retention with rClass(), defaultLayoutPattern(), defaultBaseModelClass()
- [ ] PackageViewConfig.Option enum defines Default, Enabled, Disabled
- [ ] PackageViewConfig has generatedModelSuffix() defaulting to "Model_"
- [ ] PackageViewConfig has disableGenerateBuilderOverloads, disableGenerateGetters, disableGenerateReset options
- [ ] ViewModelSpec.Size enum has exactly 6 constants with correct names
- [ ] ModelAttribute.Option enum has exactly 5 constants with correct names
- [ ] ModelProperty.Option enum has exactly 4 constants with correct names
- [ ] PackageViewConfig.Option enum has exactly 3 constants with correct names
- [ ] All annotation elements with Class<?> type use Void.class as sentinel default
- [ ] All annotation elements with String type use "" as sentinel default
- [ ] Annotations can be applied to test classes/fields/methods and compile successfully

## Task 4: Model groups for compositing multiple child models into a single list item

### Acceptance Criteria
- [ ] ModelGroup wraps multiple child models into a single item with a layout ID
- [ ] ModelGroup defaults its ID to the first child model's ID
- [ ] ModelGroup delegates bind to all child models via ModelGroupHolder
- [ ] ModelGroup delegates unbind to all child models
- [ ] ModelGroup.bind with previousModel passes matching child for incremental binding
- [ ] ModelGroup.bind with previousModel uses fresh bind for non-matching children
- [ ] ModelGroup.equals includes child models list in comparison
- [ ] ModelGroup.hashCode includes child models list
- [ ] ModelGroup.getSpanSize delegates to first child model
- [ ] ModelGroup.shouldSaveViewState returns true if any child has it
- [ ] ModelGroup supports explicit ID override after construction
- [ ] ModelGroup.getChildModels returns unmodifiable list of children
- [ ] ModelGroupHolder creates child holders matching each child model's view type
- [ ] ModelGroupHolder reuses existing child holders when view types match on rebind
- [ ] ModelGroupHolder recycles excess holders when new group has fewer children
- [ ] ModelGroupHolder replaces holders when child view types change
- [ ] ModelGroupHolder.unbindGroup unbinds all children and clears state
- [ ] ModelGroup supports addModel for incremental child construction
- [ ] ModelGroup supports both varargs and collection constructors
- [ ] ModelGroup child visibility is tracked during bind
- [ ] ModelGroup correctly diffs when used with a ListController
- [ ] ModelGroup reports content change when any child's content changes
- [ ] ModelGroup reports no change when all children are identical
- [ ] ModelGroupHolder.getChildCount returns the number of child holders
- [ ] ModelGroupHolder.getChildHolder returns holder at given index
- [ ] ModelGroup throws on construction with empty model collection
- [ ] ItemModel.createView returns a default Object for view creation
- [ ] ModelGroup.createView returns a ModelGroupHolder instance
- [ ] ModelGroup bind correctly handles more children than previous group
- [ ] ModelGroup bind correctly handles fewer children than previous group
