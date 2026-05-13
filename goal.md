# Goal

## Project
epoxy — a Java library for building and efficiently updating complex lists of items.

## Description
Epoxy is a library for building complex lists with multiple item types. It provides a declarative model-based architecture where each list item is represented by a model object with a unique ID and properties. A controller declares what items to show via a buildModels() method. When data changes, the controller automatically diffs the old and new model lists and computes the minimal set of insertions, removals, moves, and updates needed to transform one into the other. The library handles view type mapping, view holder management, state save/restore, model lifecycle (bind/unbind), interceptors for post-build modification, drag-and-drop and swipe support, visibility tracking, and model groups for compositing multiple models into a single item.

## Scope
- ~25 production source files to implement
- ~12 test files to write
- Reproduce core model system, diffing, controllers, adapters, annotations, touch support, visibility tracking, and model groups
