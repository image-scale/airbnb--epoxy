# Todo

## Plan
Implement the core Epoxy library top-down, starting with the most important user-facing feature: the model+controller+adapter system with automatic diffing. Then layer on annotations, model groups, typed controllers, touch support, and visibility tracking. Each task delivers independently testable functionality.

## Tasks
- [x] Task 1: Implement the core item model and controller with automatic list diffing
- [x] Task 2: Implement the list adapter with view holder management and view type mapping — Create an adapter that manages view holders, maps view types to model classes, saves/restores view state, and dispatches model lifecycle events. Include both a direct-manipulation adapter (where users modify models list directly and call notify methods) and a controller-backed adapter that integrates with the diff algorithm for automatic updates.
- [x] Task 3: Implement annotation definitions for model attributes, views, and package configuration
- [x] Task 4: Implement model groups for compositing multiple child models into a single list item
- [x] Task 5: Implement typed controllers for data-driven model building with 1-4 type parameters
- [>] Task 6: Implement touch interaction support for drag-and-drop reordering and swipe-to-dismiss
- [ ] Task 7: Implement visibility tracking to monitor item visibility state changes during scrolling
