package dev.epoxy;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class ListControllerTest {

    @Test
    public void testBasicBuildModels() {
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
            }
        };
        controller.setObserver(observer);
        controller.requestModelBuild();

        assertEquals(2, controller.getModelCount());
        assertTrue(observer.events.size() > 0);
    }

    @Test
    public void testBuildDispatchesInsertions() {
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.setObserver(observer);
        controller.requestModelBuild();

        assertEquals(1, observer.events.size());
        assertTrue(observer.events.get(0).startsWith("INSERT"));
    }

    @Test
    public void testRebuildDetectsChanges() {
        List<TestItem> items = new ArrayList<>();
        items.add(new TestItem(1, 0));
        items.add(new TestItem(2, 0));

        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                for (TestItem item : items) {
                    new TestItem(item.id(), item.value).addTo(this);
                }
            }
        };
        controller.setObserver(observer);
        controller.requestModelBuild();
        observer.events.clear();

        items.add(new TestItem(3, 0));
        controller.requestModelBuild();

        assertEquals(3, controller.getModelCount());
        assertTrue(observer.events.stream().anyMatch(e -> e.startsWith("INSERT")));
    }

    @Test
    public void testInterceptorAddsModels() {
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.addInterceptor(models -> {
            TestItem extra = new TestItem(99);
            models.add(extra);
        });
        controller.setObserver(observer);
        controller.requestModelBuild();

        assertEquals(2, controller.getModelCount());
    }

    @Test
    public void testInterceptorModifiesModels() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1, 10).addTo(this);
            }
        };
        controller.addInterceptor(models -> {
            TestItem model = (TestItem) models.get(0);
            model.value = 42;
        });
        controller.requestModelBuild();

        TestItem result = (TestItem) controller.getModel(0);
        assertEquals(42, result.value);
    }

    @Test
    public void testInterceptorOrder() {
        List<String> order = new ArrayList<>();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.addInterceptor(m -> order.add("first"));
        controller.addInterceptor(m -> order.add("second"));
        controller.addInterceptor(m -> order.add("third"));
        controller.requestModelBuild();

        assertEquals(3, order.size());
        assertEquals("first", order.get(0));
        assertEquals("second", order.get(1));
        assertEquals("third", order.get(2));
    }

    @Test
    public void testDuplicateFilteringOff() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                new TestItem(1).addTo(this);
            }
        };
        controller.setFilterDuplicates(true);
        controller.requestModelBuild();

        assertEquals(1, controller.getModelCount());
    }

    @Test
    public void testDuplicateFilteringKeepsFirst() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1, 10).addTo(this);
                new TestItem(1, 20).addTo(this);
            }
        };
        controller.setFilterDuplicates(true);
        controller.requestModelBuild();

        assertEquals(1, controller.getModelCount());
        assertEquals(10, ((TestItem) controller.getModel(0)).value);
    }

    @Test
    public void testBuildListenerCalledAfterBuild() {
        AtomicBoolean called = new AtomicBoolean(false);
        AtomicReference<List<ChangeOperation>> result = new AtomicReference<>();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.addBuildListener(ops -> {
            called.set(true);
            result.set(ops);
        });
        controller.requestModelBuild();

        assertTrue(called.get());
        assertNotNull(result.get());
    }

    @Test
    public void testRemoveBuildListener() {
        AtomicBoolean called = new AtomicBoolean(false);
        ListController.BuildListener listener = ops -> called.set(true);

        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.addBuildListener(listener);
        controller.removeBuildListener(listener);
        controller.requestModelBuild();

        assertFalse(called.get());
    }

    @Test
    public void testMoveModel() {
        RecordingObserver observer = new RecordingObserver();
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                new TestItem(2).addTo(this);
                new TestItem(3).addTo(this);
            }
        };
        controller.setObserver(observer);
        controller.requestModelBuild();
        observer.events.clear();

        controller.moveModel(0, 2);

        assertEquals(1, observer.events.size());
        assertEquals("MOVE(0,2)", observer.events.get(0));
        assertEquals(2, controller.getModel(0).id());
        assertEquals(3, controller.getModel(1).id());
        assertEquals(1, controller.getModel(2).id());
    }

    @Test(expected = InvalidUsageException.class)
    public void testRequestBuildInsideBuildThrows() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
                requestModelBuild();
            }
        };
        controller.requestModelBuild();
    }

    @Test(expected = InvalidUsageException.class)
    public void testAddModelWithDefaultIdThrows() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new ItemModel<Object>() {
                    @Override
                    public int getDefaultLayoutId() { return 0; }
                }.addTo(this);
            }
        };
        controller.requestModelBuild();
    }

    @Test(expected = InvalidUsageException.class)
    public void testAddHiddenModelThrows() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                TestItem item = new TestItem(1);
                item.hide();
                item.addTo(this);
            }
        };
        controller.requestModelBuild();
    }

    @Test(expected = InvalidUsageException.class)
    public void testAddModelOutsideBuildThrows() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {}
        };
        controller.requestModelBuild();
        new TestItem(1).addTo(controller);
    }

    @Test
    public void testHasPendingBuildDuringBuild() {
        AtomicBoolean pendingDuringBuild = new AtomicBoolean(false);
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                pendingDuringBuild.set(hasPendingModelBuild());
                new TestItem(1).addTo(this);
            }
        };

        assertFalse(controller.hasPendingModelBuild());
        controller.requestModelBuild();
        assertTrue(pendingDuringBuild.get());
        assertFalse(controller.hasPendingModelBuild());
    }

    @Test
    public void testIsBuildingModelsDuringBuild() {
        AtomicBoolean buildingDuringBuild = new AtomicBoolean(false);
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                buildingDuringBuild.set(isBuildingModels());
                new TestItem(1).addTo(this);
            }
        };

        assertFalse(controller.isBuildingModels());
        controller.requestModelBuild();
        assertTrue(buildingDuringBuild.get());
        assertFalse(controller.isBuildingModels());
    }

    @Test
    public void testGetCurrentModelsIsUnmodifiable() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.requestModelBuild();
        try {
            controller.getCurrentModels().add(new TestItem(2));
            fail("Should throw UnsupportedOperationException");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    @Test
    public void testAddIfTrue() {
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addIf(true, this);
                new TestItem(2).addIf(false, this);
            }
        };
        controller.requestModelBuild();
        assertEquals(1, controller.getModelCount());
        assertEquals(1, controller.getModel(0).id());
    }

    @Test
    public void testModelsMarkedAddedAfterBuild() {
        TestItem[] items = new TestItem[1];
        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                items[0] = new TestItem(1);
                items[0].addTo(this);
            }
        };
        controller.requestModelBuild();
        assertTrue(items[0].addedToAdapter);
    }

    @Test
    public void testGlobalDuplicateFilteringDefault() {
        try {
            ListController.setGlobalDuplicateFilteringDefault(true);
            ListController controller = new ListController() {
                @Override
                protected void buildModels() {
                    new TestItem(1).addTo(this);
                    new TestItem(1).addTo(this);
                }
            };
            controller.requestModelBuild();
            assertEquals(1, controller.getModelCount());
        } finally {
            ListController.setGlobalDuplicateFilteringDefault(false);
        }
    }

    @Test
    public void testRemoveInterceptor() {
        AtomicBoolean called = new AtomicBoolean(false);
        ListController.Interceptor interceptor = m -> called.set(true);

        ListController controller = new ListController() {
            @Override
            protected void buildModels() {
                new TestItem(1).addTo(this);
            }
        };
        controller.addInterceptor(interceptor);
        controller.removeInterceptor(interceptor);
        controller.requestModelBuild();

        assertFalse(called.get());
    }

    private static class RecordingObserver implements ListChangeObserver {
        final List<String> events = new ArrayList<>();

        @Override
        public void onItemsInserted(int positionStart, int count) {
            events.add("INSERT(" + positionStart + "," + count + ")");
        }

        @Override
        public void onItemsRemoved(int positionStart, int count) {
            events.add("REMOVE(" + positionStart + "," + count + ")");
        }

        @Override
        public void onItemMoved(int fromPosition, int toPosition) {
            events.add("MOVE(" + fromPosition + "," + toPosition + ")");
        }

        @Override
        public void onItemsChanged(int positionStart, int count, Object payload) {
            events.add("CHANGE(" + positionStart + "," + count + ")");
        }
    }
}
