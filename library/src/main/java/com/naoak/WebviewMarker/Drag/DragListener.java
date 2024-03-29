package com.naoak.WebviewMarker.Drag;

/**
 * Interface to receive notifications when a drag starts or stops
 */
public interface DragListener {
    void onDragStart(DragSource source, Object info, DragController.DragBehavior dragBehavior);
    void onDragEnd();
}
