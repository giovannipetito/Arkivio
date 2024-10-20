package it.giovanni.arkivio.fragments.detail.drag;

import android.view.DragEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.giovanni.arkivio.R;

public class DragListener implements View.OnDragListener {

    private boolean isDropped = false;
    private final Listener listener;

    public DragListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean onDrag(View view, DragEvent event) {
        if (event.getAction() == DragEvent.ACTION_DROP) {
            isDropped = true;
            int positionTarget = -1;

            View viewSource = (View) event.getLocalState();
            int viewId = view.getId();
            final int cvItem = R.id.cv_grid;
            final int tvEmptyListTop = R.id.tvEmptyListTop;
            final int tvEmptyListBottom = R.id.tvEmptyListBottom;
            final int rvTop = R.id.rvTop;
            final int rvBottom = R.id.rvBottom;

            switch (viewId) {
                case cvItem:
                case tvEmptyListTop:
                case tvEmptyListBottom:
                case rvTop:
                case rvBottom:

                    RecyclerView target;
                    switch (viewId) {
                        case tvEmptyListTop:
                        case rvTop:
                            target = view.getRootView().findViewById(rvTop);
                            break;
                        case tvEmptyListBottom:
                        case rvBottom:
                            target = view.getRootView().findViewById(rvBottom);
                            break;
                        default:
                            target = (RecyclerView) view.getParent();
                            positionTarget = (int) view.getTag();
                    }

                    if (viewSource != null) {
                        RecyclerView source = (RecyclerView) viewSource.getParent();

                        MainAdapter adapterSource = (MainAdapter) source.getAdapter();
                        int positionSource = (int) viewSource.getTag();
                        int sourceId = source.getId();

                        String list = adapterSource.getList().get(positionSource);
                        List<String> listSource = adapterSource.getList();

                        listSource.remove(positionSource);
                        adapterSource.updateList(listSource);
                        adapterSource.notifyDataSetChanged();

                        MainAdapter adapterTarget = (MainAdapter) target.getAdapter();
                        List<String> customListTarget = adapterTarget.getList();
                        if (positionTarget >= 0) {
                            customListTarget.add(positionTarget, list);
                        } else {
                            customListTarget.add(list);
                        }
                        adapterTarget.updateList(customListTarget);
                        adapterTarget.notifyDataSetChanged();

                        if (sourceId == rvBottom && adapterSource.getItemCount() < 1) {
                            listener.setEmptyListBottom(true);
                        }
                        if (sourceId == rvTop && adapterSource.getItemCount() < 1) {
                            listener.setEmptyListTop(true);
                        }
                        if (viewId == tvEmptyListBottom) {
                            listener.setEmptyListBottom(false);
                        }
                        if (viewId == tvEmptyListTop) {
                            listener.setEmptyListTop(false);
                        }
                    }
                    break;
            }
        }

        if (!isDropped && event.getLocalState() != null) {
            ((View) event.getLocalState()).setVisibility(View.VISIBLE);
        }
        return true;
    }
}